
#include "FreeHepTypes.h"

#include <iostream>
#include <string>

#include <CUtil.h>

using namespace std;

JNIEXPORT jclass JNICALL Java_hep_aida_jni_convert(JNIEnv *env, string type) {
    if (type == "int") return env->FindClass("java/lang/Integer");
    if (type == "short") return env->FindClass("java/lang/Short");
    if (type == "long") return env->FindClass("java/lang/Long");
    if (type == "float") return env->FindClass("java/lang/Float");
    if (type == "double") return env->FindClass("java/lang/Double");
    if (type == "boolean") return env->FindClass("java/lang/Boolean");
    if (type == "byte") return env->FindClass("java/lang/Byte");
    if (type == "char") return env->FindClass("java/lang/Character");
    if (type == "string") return env->FindClass("java/lang/String");
    if (type == "java.lang.Object") return env->FindClass("java/lang/Object");
    if (type == "ITuple") return env->FindClass("hep/aida/ITuple");

    cerr << "CITuple.convert(type) could not find jclass for type: " << type << endl;

    return NULL;
}
