#ifndef AIDAJNIUtil
#define AIDAJNIUtil

#include <jni.h>

extern "C" {
JNIEXPORT jclass JNICALL Java_hep_aida_jni_convert
  (JNIEnv *env, std::string type);
}

#endif
