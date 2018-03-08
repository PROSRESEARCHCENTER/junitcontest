#include <jni.h>

#ifndef _Included_hep_aida_jni_CUtil
#define _Included_hep_aida_jni_CUtil
#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jclass JNICALL Java_hep_aida_jni_convert
  (JNIEnv *env, std::string type);
#ifdef __cplusplus
}
#endif
#endif
