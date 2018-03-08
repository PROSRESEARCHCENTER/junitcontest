#ifndef JAID_JAIDREF_H
#define JAID_JAIDREF_H

#ifdef WIN32
#ifndef GNU_GCC
// Disable warning C4786: identifier was truncated to '255' characters in the debug information
  #pragma warning ( disable : 4786 )
#endif
#endif

#include <string>
#include <cstdlib>
#include <cassert>

#include <jni.h>

namespace JAID {

class JAIDRef {

    protected:
        JNIEnv* env;
        jobject ref;

        inline JAIDRef() { };
        inline JAIDRef(const JAIDRef& r) { };
        inline JAIDRef& operator=(const JAIDRef&) { return *this; };

    public:
        JAIDRef(JNIEnv *jenv, jobject obj) : env(jenv), ref(obj) {
#ifndef DEBUG
            assert(env != NULL);
            assert(obj != NULL);
#endif
        }

        ~JAIDRef() {};

        jobject getRef() const { return ref; }
        JNIEnv* getEnv() const { return env; }
};  // class
}  // namespace

#endif
