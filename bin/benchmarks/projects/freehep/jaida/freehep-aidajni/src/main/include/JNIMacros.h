#ifndef JNIMACROS
#define JNIMACROS

#include <jni.h>

#include "FreeHepTypes.h"

#define GETCLASS(env,classID,classname) \
	classID = (*env)->FindClass(env, classname);\
	if (classID == NULL) {\
	    JNICerror("Could not find class: %s", classname);\
	}

#define GETMETHOD(env,methodID,classID,method,parameters) \
	methodID = (*env)->GetMethodID(env, classID, method, parameters);\
	if (methodID == NULL) {\
	    JNICerror("Could not find method: %s%s", method, parameters);\
	}

#define GETSTATICMETHOD(env,methodID,classID,method,parameters) \
	methodID = (*env)->GetStaticMethodID(env, classID, method, parameters);\
	if (methodID == NULL) {\
	    JNICerror("Could not find static method: %s%s", method, parameters);\
	}

#define NEWSTRING(env,stringID,string) \
	stringID = (*env)->NewStringUTF(env, string);\
	if (stringID == NULL) {\
	    JNICerror("Out of memory while allocation: %s", string);\
	}

#define DELSTRING(env,string) (*env)->DeleteLocalRef(env, string);

#define NEWSTRINGARRAY(env,objectID,numberOfObjects,objectClass,source,index,jtmp) \
    objectID = (*env)->NewObjectArray(env, numberOfObjects, objectClass, NULL);\
    for (index=0; index<numberOfObjects; index++) {\
        NEWSTRING(env, jtmp, source[index]);\
        (*env)->SetObjectArrayElement(env, objectID, index, jtmp);\
        DELSTRING(env, jtmp);\
    }

#define DELSTRINGARRAY(env,string) (*env)->DeleteLocalRef(env, string);

#define NEWFLOATARRAY(env,dst,n,src) \
    dst = (*env)->NewFloatArray(env, n);\
    (*env)->SetFloatArrayRegion(env, dst, 0, n, (jfloat*)src );

#define DELFLOATARRAY(env, string) (*env)->DeleteLocalRef(env, string);

#endif

