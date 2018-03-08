
#include <CAIDA.h>
#include <AIDAJNI/JIAnalysisFactory.h>

extern "C" {	
	AIDA::IHistogram1D* getTestHistogram() {
      CHistogram1D* h = new CHistogram1D();
//      std::cerr << "binMean(c++): " << h->binMean(30) << std::endl;
      return h;
   }
	
	AIDA::IAnalysisFactory* AIDA_createAnalysisFactory() {
		JNIEnv* jenv = AIDAJNI_module_init();
		if (jenv == NULL) {
			std::cerr << "failed to initialize AIDAJNI" << std::endl;
			return NULL;
		}

		return new AIDAJNI::JIAnalysisFactory(jenv);
	}
}

