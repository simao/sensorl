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
(JNIEnv * env, jclass cls, jstring jfilename, jstring jcf, jlong jstart, jlong end, jlong step, jlong ds_cnt, jobjectArray ds_namv)
{
  rrd_value_t *data;

  char *cfilename = copyJavaStr(env, jfilename);
  char *cf = copyJavaStr(env, jcf);
  time_t start = (time_t) jstart;

  int res = rrd_fetch_r(cfilename, cf, &start, &end, &step, &ds_cnt, NULL, &data);

  // TODO: use rrd_test_error_instead
  check_rrd_error(env, res);

  jclass dataPointCls = (*env)->FindClass(env, "io/simao/librrd/RRDDataPoint");
  jsize len = sizeof(data);
  jobjectArray jResult = (*env)->NewObjectArray(env, len, dataPointCls, 0);

  


  free(data);
  free(cfilename);
  free(cf);

  return NULL;
}
