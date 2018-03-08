#ifndef JNICUTIL
#define JNICUTIL

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

int JNICexceptions(JNIEnv *env);
void JNICcheckExceptions(JNIEnv *env);
int JNICcreateJVM(JNIEnv **env, JavaVM **jvm);
void JNICdestroyJVM(JavaVM *jvm);
void JNICfatal(char* msg, ...);
void JNICerror(char* msg, ...);

int JNICdebug();

#ifdef __cplusplus
}
#endif

#endif

