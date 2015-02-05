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

struct s_command build_command(JNIEnv *env, jstring jfilename, jobjectArray jargs)
{
  struct s_command command;

  command.argc = (*env)->GetArrayLength(env, jargs);
  command.args = malloc(sizeof(char*) * (command.argc+1));

  const char *cFilename = (*env)->GetStringUTFChars(env, jfilename, JNI_FALSE);
  command.filename = malloc(sizeof(char) * (strlen(cFilename) + 1));
  strcpy(command.filename, cFilename);
  
  int i;
  for(i = 0; i < command.argc; i++) {
    jstring s = (*env)->GetObjectArrayElement(env, jargs, i);
    const char *cString = (*env)->GetStringUTFChars(env, s, JNI_FALSE);
    command.args[i] = malloc(sizeof(char) * (strlen(cString) + 1));
    strcpy(command.args[i], (char*)cString);
    (*env)->ReleaseStringUTFChars(env, s, cString);
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
  free(cmd.filename);
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
