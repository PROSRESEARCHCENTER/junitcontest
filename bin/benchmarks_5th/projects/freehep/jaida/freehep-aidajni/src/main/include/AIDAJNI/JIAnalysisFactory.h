#ifndef AIDAJNI_JIAnalysisFactory
#define AIDAJNI_JIAnalysisFactory

#include <AIDAJNI.h>

namespace AIDAJNI {

class JIAnalysisFactory : public SwigDirector_IAnalysisFactory {

// FIXME, seems to be necessary to avoid overwriting the swig-override array.
// Removing the array gives intermittent failures
private:
    bool extra[100];
    JNIEnv *jenv;
	
public:
    JIAnalysisFactory(JNIEnv *jenv);
    ~JIAnalysisFactory();
};

}

#endif
