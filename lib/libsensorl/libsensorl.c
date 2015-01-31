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

  args[0] = "dummy";

  for(i = 0; i < argc; i++) {
    jstring s = (*env)->GetObjectArrayElement(env, jargs, i);
    const char *cString = (*env)->GetStringUTFChars(env, s, NULL);
    args[i+1] = (char*)cString;
  }
}

// jargs is an Array of Strings
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdcreate
(JNIEnv *env, jclass cls, jobjectArray jargs)
{
  int argc = (*env)->GetArrayLength(env, jargs);
  char *args[argc+1];
  copyRrdArgs(env, jargs, argc, args);

  int res = rrd_create(argc+1, args); // TODO: Use _r

  if(res == -1)
    throwRuntimeException(env, rrd_get_error());

  return res;
}

JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdupdate
(JNIEnv *env, jclass cls, jobjectArray jargs)
{
  int argc = (*env)->GetArrayLength(env, jargs);
  char *args[argc+1];
  copyRrdArgs(env, jargs, argc, args);
  
  int res = rrd_update(argc+1, args); // TODO: Use _r

  if(res == -1)
    throwRuntimeException(env, rrd_get_error());

  return res;
}
