#include <unistd.h>
#include "io_simao_librrd_LibRRD.h"
#include <rrd.h>

jint throwRuntimeException( JNIEnv *env, char *message )
{
  jclass exClass;
  char *className = "java/lang/RuntimeException";
  exClass = (*env)->FindClass(env, className);
  return (*env)->ThrowNew(env, exClass, message);
}

void copyRrdArgs(JNIEnv *env, jobjectArray jargs, int argc, char** args)
{
  int i;

  for(i = 0; i < argc; i++) {
    jstring s = (*env)->GetObjectArrayElement(env, jargs, i);
    const char *cString = (*env)->GetStringUTFChars(env, s, NULL); // TODO: LEAK?
    args[i] = (char*)cString;
  }
}

// jargs is an Array of Strings
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdcreate
(JNIEnv * env, jclass cls, jstring jfilename, jint step, jint start, jobjectArray jargs)
{
  int argc = (*env)->GetArrayLength(env, jargs);
  char *args[argc];
  copyRrdArgs(env, jargs, argc, args);

  /* int i; */
  /* for (i=0;i < argc+1;i++) { */
  /*   printf("%s\n",args[i]); */
  /* } */

  const char *filename = (*env)->GetStringUTFChars(env, jfilename, 0);

  int res = rrd_create_r(filename, step, (time_t)NULL, argc, (const char**) args);

  (*env)->ReleaseStringUTFChars(env, jfilename, filename);

  if(res == -1)
    throwRuntimeException(env, rrd_get_error());

  return res;
}

JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdupdate
(JNIEnv * env, jclass cls, jstring jfilename, jobjectArray jargs)
{
  int argc = (*env)->GetArrayLength(env, jargs);
  char *args[argc];
  copyRrdArgs(env, jargs, argc, args);
  const char *filename = (*env)->GetStringUTFChars(env, jfilename, 0);
  
  int res = rrd_update_r(filename, NULL, argc, (const char**)args);

  (*env)->ReleaseStringUTFChars(env, jfilename, filename);

  if(res == -1)
    throwRuntimeException(env, rrd_get_error());

  return res;
}
