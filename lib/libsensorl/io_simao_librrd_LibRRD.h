/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class io_simao_librrd_LibRRD */

#ifndef _Included_io_simao_librrd_LibRRD
#define _Included_io_simao_librrd_LibRRD
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     io_simao_librrd_LibRRD
 * Method:    rrdcreate
 * Signature: (Ljava/lang/String;II[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdcreate
  (JNIEnv *, jclass, jstring, jint, jint, jobjectArray);

/*
 * Class:     io_simao_librrd_LibRRD
 * Method:    rrdupdate
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdupdate
  (JNIEnv *, jclass, jstring, jobjectArray);

/*
 * Class:     io_simao_librrd_LibRRD
 * Method:    rrdgraph
 * Signature: ([Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_io_simao_librrd_LibRRD_rrdgraph
  (JNIEnv *, jclass, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif
