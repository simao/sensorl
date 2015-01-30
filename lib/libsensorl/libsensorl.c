#include <unistd.h>
#include "io_simao_librrd_LibRRD.h"
#include <rrd.h>

jint throwRuntimeException( JNIEnv *env, char *message )
{
  jclass exClass;
  char *className = "java/lang/RuntimeException";
  exClass = (*env)->FindClass( env, className);
  return (*env)->ThrowNew( env, exClass, message );
}

JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdcreate
(JNIEnv *env, jclass cls)
{
  char *args[6];

  args[0] = "dummy";
  args[1] = "/home/simao/code/sensorl/jni_test.rrd";
  args[2] = "--start";
  args[3] = "920804400";
  args[4] = "DS:speed:COUNTER:600:U:U";
  args[5] = "RRA:AVERAGE:0.5:1:24";
  args[6] = "RRA:AVERAGE:0.5:6:10";

  int res = rrd_create(6, args);

  if(res == -1)
    throwRuntimeException(env, rrd_get_error());

  return res;
}
