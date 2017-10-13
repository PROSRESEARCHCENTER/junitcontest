
#include <stdlib.h>

#include "JNICUtil.h"
#include "JNIUtil.hh"

/**
 * Checks and clears the exception, if there is one.
 *
 * @returns false if no exception has occured, true otherwise.
 */
bool JNIUtil::exceptions(JNIEnv *env) {
    return (JNICexceptions(env) == 1);
}

/**
 * Checks, prints and clears the exception, if there is one.
 */
void JNIUtil::checkExceptions(JNIEnv *env) {
    JNICcheckExceptions(env);
}

/**
 * Registers a function for the jvm.
 *
 * @param env JNI Environment
 * @param cls class for which this function was declared native
 * @param name name of the function
 * @param signture of the function (to be retrieved with "javap -s ClassName.class"
 * @param fnptr pointer to the c-function
 *
 * @returns "0" on success; returns a negative value on failure
 */
int JNIUtil::registerFunction(JNIEnv *env, jclass cls, char *name, char *signature, void* fnptr) {
	JNINativeMethod nativeMethod;
	nativeMethod.name = name;
	nativeMethod.signature = signature;
	nativeMethod.fnPtr = fnptr;
	int rc = env->RegisterNatives(cls, &nativeMethod, 1);
	return rc;
}


/**
 * Creates a Java Virtual Machine
 *
 * The following Environment Variables are used:
 * <PRE>
 *      JVM_CLASSPATH   Directories and jar files separated by semicolon (windows) or colon (unix)
 *      CLASSPATH       Used when JVM_CLASSPATH not set, if neither set the default is used and status 1 is returned
 *      JVM_VERBOSE     Specifies if JVM runs in verbose mode, possible values: on, class, gc, jni
 * (not implemented)     JVM_ARGS        Other arguments for the JVM (space separated)
 * </PRE>
 * @returns 0 on success,
 *          < 0 if JVM could not be created,
 *          1 if CLASSPATH not set
 */
int JNIUtil::createJVM(JNIEnv **env, JavaVM **jvm) {
    return JNICcreateJVM(env, jvm);
}

/**
 * Destroys the jvm
 */
void JNIUtil::destroyJVM(JavaVM *jvm) {
    JNICdestroyJVM(jvm);
}
