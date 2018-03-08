#ifndef JNIUTIL
#define JNIUTIL

#include <jni.h>

#include "FreeHepTypes.h"

class JNIUtil {

    public:
        static bool exceptions(JNIEnv *env);
        static void checkExceptions(JNIEnv *env);
        static int registerFunction(JNIEnv *env, jclass cls, char *name, char *signature, void* fnptr);
    	static int createJVM(JNIEnv **env, JavaVM **jvm);
    	static void destroyJVM(JavaVM *jvm);
};
#endif
