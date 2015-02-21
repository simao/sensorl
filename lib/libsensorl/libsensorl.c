#include <unistd.h>
#include "io_simao_librrd_LibRRD.h"
#include <rrd.h>
#include <string.h>
#include <stdlib.h>

struct s_command {
  char *filename;
  char **args;
  int argc;
};

jint throwRuntimeException( JNIEnv *env, char *message )
{
  jclass exClass;
  char *className = "java/lang/RuntimeException";
  exClass = (*env)->FindClass(env, className);
  return (*env)->ThrowNew(env, exClass, message);
}

char* copyJavaStr(JNIEnv *env, jstring jstr) {
  const char *cStr = (*env)->GetStringUTFChars(env, jstr, JNI_FALSE);
  char *dst = malloc(sizeof(char) * strlen(cStr) + 1);
  strcpy(dst, cStr);
  (*env)->ReleaseStringUTFChars(env, jstr, cStr);
  return dst;
}

struct s_command build_command(JNIEnv *env, jstring jfilename, jobjectArray jargs)
{
  struct s_command command;

  command.argc = (*env)->GetArrayLength(env, jargs);
  command.args = malloc(sizeof(char*) * (command.argc+1));

  if(jfilename != NULL) {
    command.filename = copyJavaStr(env, jfilename);
  } else {
    command.filename = NULL;
  }

  int i;
  for(i = 0; i < command.argc; i++) {
    jstring jString = (*env)->GetObjectArrayElement(env, jargs, i);
    const char *c_string = (*env)->GetStringUTFChars(env, jString, JNI_FALSE);
    command.args[i] = malloc(sizeof(char) * (strlen(c_string) + 1));
    strcpy(command.args[i], (char*)c_string);
    (*env)->ReleaseStringUTFChars(env, jString, c_string);
  }

  return command;
}

void free_command(struct s_command cmd)
{
  int i;
  for(i = 0; i < cmd.argc; i++) {
    free(cmd.args[i]);
  }
  free(cmd.args);
  if(cmd.filename != NULL) {
    free(cmd.filename);
  }
}

void check_rrd_error(JNIEnv* env) {
  if (rrd_test_error())
    throwRuntimeException(env, rrd_get_error());
}

// jargs is an Array of Strings
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdcreate
(JNIEnv * env, jclass cls, jstring jfilename, jint step, jint start, jobjectArray jargs)
{
  struct s_command cmd = build_command(env, jfilename, jargs);

  int res = rrd_create_r(cmd.filename, step, (time_t)NULL, cmd.argc, (const char**)cmd.args);

  free_command(cmd);

  check_rrd_error(env);

  return res;
}

JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdupdate
(JNIEnv * env, jclass cls, jstring jfilename, jobjectArray jargs)
{
  struct s_command cmd = build_command(env, jfilename, jargs);

  int res = rrd_update_r(cmd.filename, NULL, cmd.argc, (const char**)cmd.args);

  free_command(cmd);

  check_rrd_error(env);

  return res;
}

JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdgraph
(JNIEnv * env, jclass cls, jobjectArray jargs)
{
  struct s_command cmd = build_command(env, NULL, jargs);

  rrd_clear_error();

  rrd_graph_v(cmd.argc, cmd.args);

  free_command(cmd);

  if (rrd_test_error()) {
    throwRuntimeException(env, rrd_get_error());
  }

  return 0;
}

JNIEXPORT jobject JNICALL Java_io_simao_librrd_LibRRD_rrdfetch
(JNIEnv * env, jclass cls, jstring jfilename, jstring jcf, jlong jstart, jlong jend, jlong step)
{
  char *cfilename = copyJavaStr(env, jfilename);
  char *cf = copyJavaStr(env, jcf);

  rrd_value_t *data;
  unsigned long ds_cnt;
  char **ds_names;

  int res = rrd_fetch_r(cfilename, cf, &jstart, &jend, &step, &ds_cnt, &ds_names, &data);

  check_rrd_error(env);

  unsigned long row_cnt = (jend - jstart)/step + 1;
  unsigned long totalValues = ds_cnt * row_cnt;

  jlongArray jData = (*env)->NewDoubleArray(env, totalValues);
  (*env)->SetDoubleArrayRegion(env, jData, 0, totalValues, data);

  jobjectArray jNames = (*env)->NewObjectArray(env, ds_cnt,
                                               (*env)->FindClass(env, "java/lang/String"),
                                               (*env)->NewStringUTF(env, ""));

  unsigned long i;
  for(i = 0; i < ds_cnt; i++) {
    jobject jName = (*env)->NewStringUTF(env, ds_names[i]);
    (*env)->SetObjectArrayElement(env, jNames, i, jName);
    free(ds_names[i]);
  }
  free(ds_names);

  jclass resultCls = (*env)->FindClass(env, "io/simao/librrd/RRDFetchResult");
  jmethodID constr = (*env)->GetMethodID(env, resultCls, "<init>",
                                         "(JJJJ[Ljava/lang/String;[D)V");

  jobject jResult = (*env)->NewObject(env, resultCls, constr,
                                      jstart,
                                      jend,
                                      step,
                                      ds_cnt,
                                      jNames,
                                      jData);

  free(cfilename);
  free(cf);
  free(data);

  return jResult;
}
