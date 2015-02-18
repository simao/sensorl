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
  char *dst = malloc(sizeof(char) * (strlen(cStr) + 1));
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

void check_rrd_error(JNIEnv* env, int rrd_result) {
  if(rrd_result == -1)
    throwRuntimeException(env, rrd_get_error());
}

// jargs is an Array of Strings
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdcreate
(JNIEnv * env, jclass cls, jstring jfilename, jint step, jint start, jobjectArray jargs)
{
  struct s_command cmd = build_command(env, jfilename, jargs);

  /* int i; */
  /* for (i=0;i < argc+1;i++) { */
  /*   printf("%s\n",args[i]); */
  /* } */

  int res = rrd_create_r(cmd.filename, step, (time_t)NULL, cmd.argc, (const char**)cmd.args);

  free_command(cmd);

  check_rrd_error(env, res);

  return res;
}

JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdupdate
(JNIEnv * env, jclass cls, jstring jfilename, jobjectArray jargs)
{
  struct s_command cmd = build_command(env, jfilename, jargs);

  int res = rrd_update_r(cmd.filename, NULL, cmd.argc, (const char**)cmd.args);

  free_command(cmd);

  check_rrd_error(env, res);

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

JNIEXPORT jobjectArray JNICALL Java_io_simao_librrd_LibRRD_rrdfetch
(JNIEnv * env, jclass cls, jstring jfilename, jstring jcf, jlong jstart, jlong jend, jlong step)
{
  // TODO: Needs free
  char *cfilename = copyJavaStr(env, jfilename);
  char *cf = copyJavaStr(env, jcf);

  rrd_value_t *data;
  unsigned long ds_cnt;
  char **ds_namv;

  int res = rrd_fetch_r(cfilename, cf, &jstart, &jend, &step, &ds_cnt, &ds_namv, &data);

  // TODO: use rrd_test_error_instead
  check_rrd_error(env, res);
  
  int row_cnt = (jend - jstart)/step + 1;
  int valuesLen = ds_cnt * row_cnt;

  printf("%d\n", row_cnt);
  printf("%d\n", valuesLen);
  // printf("%s\n", ds_cnt);

  jclass dataPointCls = (*env)->FindClass(env, "[J"); // long[]
  jobjectArray jResult = (*env)->NewObjectArray(env, row_cnt, dataPointCls, 0);

  int i;
  for(i = 0; i < row_cnt; i++) {
    jlongArray longArray = (*env)->NewLongArray(env, valuesLen);
    (*env)->SetLongArrayRegion(env, longArray,
                               (jsize) 0,
                               (jsize) valuesLen,
                               (jlong*) &data[i]);
    (*env)->SetObjectArrayElement(env, jResult, (jsize) i, longArray);
    (*env)->DeleteLocalRef(env, longArray);
  }

  // free all the shit rrd_tool created, included nested data
  //  free(data);
  //  free(cfilename);
  //  free(cf);

  return jResult;
}
