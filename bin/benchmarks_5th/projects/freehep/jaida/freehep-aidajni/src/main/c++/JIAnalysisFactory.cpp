#include <AIDAJNI/JIAnalysisFactory.h>

using namespace AIDAJNI;

JIAnalysisFactory::JIAnalysisFactory(JNIEnv *jenv) :
	SwigDirector_IAnalysisFactory(jenv) {
	// FIXME, we still refer to the real implementation version of the class...
	// because SWIG seems to thing that director methods are not implemented
	// if it checks for method ids from the real class (JIAnalysisFactory)
	// returned by the create method and the method ids from interface (IAnalysisFactory).
	char* className = "hep/aida/IAnalysisFactory";
/*
	jclass jcls = jenv->FindClass(className);
	if (jcls == NULL) {
		std::cerr << "Cannot find class "<< className << std::endl;
		jenv->ExceptionDescribe();
		exit(1);
	}
*/
	
	// We check for test class first, then the jaida implementation
	char* testClassName = "hep/aida/swig/test/JIAnalysisFactory";
	char* jaidaClassName = "hep/aida/ref/AnalysisFactory";
	jclass jcls = jenv->FindClass(testClassName);
	if (jcls == NULL) {
		jenv->ExceptionClear();
		jcls = jenv->FindClass(jaidaClassName);
		if (jcls == NULL) {
			std::cerr << "Cannot find class "<< testClassName << " or " << jaidaClassName << std::endl;
			jenv->ExceptionDescribe();
			exit(1);
		} else {
			className = jaidaClassName;
		}
	} else {
		className = testClassName;
	}
	
	std::cerr << "Using: " << className << std::endl;

/* FIXME did not seem to work...
	char* methodName = "create";
	char* methodSignature = "()Lhep/aida/IAnalysisFactory;";
	jmethodID mid = jenv->GetStaticMethodID(jcls, methodName, methodSignature);
	if (mid == NULL) {
		std::cerr << "Cannot find "<< methodName << methodSignature
				<< " in class "<< className << std::endl;
		jenv->ExceptionDescribe();
		exit(1);
	}

	jobject jobj = jenv->CallStaticObjectMethod(jcls, mid);
	if (jobj == NULL) {
		std::cerr << "Cannot call "<< className << "."<< methodName << "();"
				<< std::endl;
		jenv->ExceptionDescribe();
		exit(1);
	}
*/
	
	// create using constructor
	char* methodName = "<init>";
	char* methodSignature = "()V";
	jmethodID mid = jenv->GetMethodID(jcls, methodName, methodSignature);
	if (mid == NULL) {
		std::cerr << "Cannot find "<< methodName << methodSignature
				<< " in class "<< className << std::endl;
		jenv->ExceptionDescribe();
		exit(1);
	}	
	
	jobject jobj = jenv->NewObject(jcls, mid);
	if (jobj == NULL) {
		std::cerr << "Cannot call new "<< className << "();"
				<< std::endl;
		jenv->ExceptionDescribe();
		exit(1);
	}
	
	swig_connect_director(jenv, jobj, jcls, false, false);
}

JIAnalysisFactory::~JIAnalysisFactory() {
	if (AIDAJNI_module_teardown(jenv) != 0) {
		std::cerr << "Failed to teardown AIDAJNI"<< std::endl;
	}
}
