#ifndef JNIMACROS
#define JNIMACROS

#include <iostream>
#include <string>

#include <jni.h>

#include "JNIUtil.hh"

#define GETCLASS(name,classID,classname) \
	classID = env->FindClass(classname);\
	JNIUtil::checkExceptions(env);\
	if (classID == NULL) {\
        std::cerr << name << ": Could not find class: " << classname << std::endl;\
	}

#define GETMETHOD(name,methodID,method,parameters) \
	methodID = env->GetMethodID(cls, method, parameters);\
	JNIUtil::checkExceptions(env);\
	if (methodID == NULL) {\
        std::cerr << name << ": Could not find method: " << method << parameters << std::endl;\
	}

#define GETSTATICMETHOD(name,methodID,method,parameters) \
	methodID = env->GetStaticMethodID(cls, method, parameters);\
	JNIUtil::checkExceptions(env);\
	if (methodID == NULL) {\
        std::cerr << name << ": Could not find static method: " << method << parameters << std::endl;\
	}

#define GETPRIMITIVECLASS(result,name) \
    {\
        jclass nameClass;\
        GETCLASS("internal",nameClass,name)\
        jfieldID nameClassFieldID = env->GetStaticFieldID(nameClass, "TYPE", "Ljava/lang/Class;");\
	    JNIUtil::checkExceptions(env);\
        result = (jclass)env->GetStaticObjectField(nameClass, nameClassFieldID);\
    }

#define NEWSTRING(stringID,string) \
	stringID = env->NewStringUTF(string.c_str());\
	JNIUtil::checkExceptions(env);\
	if (stringID == NULL) {\
	    std::cerr << "Out of memory while allocation: " << string.c_str() << std::endl;\
	}

#define DELSTRING(string) env->DeleteLocalRef(string);

#define NEWSTRINGARRAY(stringArray, source) \
    {\
        jclass stringClass123 = env->FindClass("java/lang/String");\
	    JNIUtil::checkExceptions(env);\
        stringArray = (jobjectArray)env->NewObjectArray(source.size(), stringClass123, NULL);\
	    JNIUtil::checkExceptions(env);\
        for (unsigned int index=0; index<source.size(); index++) {\
            jobject jtmp;\
            NEWSTRING(jtmp, source[index]);\
            env->SetObjectArrayElement(stringArray, index, jtmp);\
	        JNIUtil::checkExceptions(env);\
            DELSTRING(jtmp);\
        }\
    }

#define DELSTRINGARRAY(stringArray) env->DeleteLocalRef(stringArray);

#define NEWDOUBLEARRAY(dst,src) \
    dst = env->NewDoubleArray(src.size());\
	JNIUtil::checkExceptions(env);\
    {\
        for (unsigned int index=0; index<src.size(); index++) {\
            env->SetDoubleArrayRegion(dst, index, 1, (jdouble*)(&(src[index])) );\
	        JNIUtil::checkExceptions(env);\
        }\
    }

#define DELDOUBLEARRAY(array) env->DeleteLocalRef(array);

#define NEWFLOATARRAY(dst,src) \
    dst = env->NewFloatArray(src.size());\
	JNIUtil::checkExceptions(env);\
    {\
        for (unsigned int index=0; index<src.size(); index++) {\
            env->SetFloatArrayRegion(dst, index, 1, (jfloat*)(&(src[index])) );\
	        JNIUtil::checkExceptions(env);\
        }\
    }

#define DELFLOATARRAY(array) env->DeleteLocalRef(array);

#define NEWINTARRAY(dst,src) \
    dst = env->NewIntArray(src.size());\
	JNIUtil::checkExceptions(env);\
    {\
        for (unsigned int index=0; index<src.size(); index++) {\
            env->SetIntArrayRegion(dst, index, 1, (jint*)(&(src[index])) );\
	        JNIUtil::checkExceptions(env);\
        }\
    }

#define DELINTARRAY(array) env->DeleteLocalRef(array);

#define MOVETOINTVECTOR(intVector, jsrc) \
    {\
        unsigned int len = env->GetArrayLength(jsrc);\
        for (unsigned int i=0; i<len; i++) {\
            jint d;\
            env->GetIntArrayRegion(jsrc, i, 1, &d);\
	        JNIUtil::checkExceptions(env);\
            ((std::vector<int>&)intVector).push_back(d);\
        }\
        env->DeleteLocalRef(jsrc);\
    }

#define MOVETODOUBLEVECTOR(doubleVector, jsrc) \
    {\
        unsigned int len = env->GetArrayLength(jsrc);\
        for (unsigned int i=0; i<len; i++) {\
            jdouble d;\
            env->GetDoubleArrayRegion(jsrc, i, 1, &d);\
	        JNIUtil::checkExceptions(env);\
            ((std::vector<double>&)doubleVector).push_back(d);\
        }\
        env->DeleteLocalRef(jsrc);\
    }

#define MOVETOSTRINGVECTOR(stringVector, jsrc) \
    {\
        unsigned int len = env->GetArrayLength(jsrc);\
        for (unsigned int i=0; i<len; i++) {\
            jboolean isCopy;\
            jstring s = (jstring)env->GetObjectArrayElement(jsrc, i);\
	        JNIUtil::checkExceptions(env);\
            const char *c = env->GetStringUTFChars(s, &isCopy);\
            stringVector.push_back(c);\
            if (isCopy == JNI_TRUE) \
                env->ReleaseStringUTFChars(s, c);\
            env->DeleteLocalRef(s);\
        }\
        env->DeleteLocalRef(jsrc);\
    }

#endif

