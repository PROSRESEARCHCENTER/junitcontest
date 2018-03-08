
#include <iostream>

#include "CAIDA.h"
#include "AIDAJNI.h"

#include <AIDA/IAnalysisFactory.h>
#include <AIDA/ITreeFactory.h>
#include <AIDA/ITree.h>
#include <AIDA/IHistogramFactory.h>
#include <AIDA/IHistogram1D.h>

#include <AIDAJNI/JIAnalysisFactory.h>

using namespace std;
using namespace AIDA;
using namespace AIDAJNI;

int main(int argc, char **args) {
	IAnalysisFactory* af = AIDA_createAnalysisFactory();
	if (af == NULL) {
    	cerr << "Could not create JIAnalysisFactory" << endl;
    	return 1;
    }
	ITreeFactory* tf = af->createTreeFactory();
	if (tf == NULL) {
    	cerr << "Could not create ITreeFactory" << endl;
    	return 1;
    }	
	ITree* t = tf->create();
	if (t == NULL) {
    	cerr << "Could not create ITree" << endl;
    	return 1;
    }
	IHistogramFactory* hf = af->createHistogramFactory(*t);
	if (hf == NULL) {
    	cerr << "Could not create IHistogramFactory" << endl;
    	return 1;
    }
	
    IHistogram1D* h1d = hf->createHistogram1D("/test", 0, 100, 1000);
    if (h1d == NULL) {
    	cerr << "Could not create IHistogram1D" << endl;
    	return 1;
    }
    if (h1d->title() != "JHistogram") {
    	cerr << "title() returned \"" << h1d->title() << "\" should have been \"JHistogram\"" << endl;
    	return 1;
    }
    if (h1d->binMean(50) != 100) {
    	cerr << "binMean(50) returned " << h1d->binMean(50) << " should have been 100" << endl;
    	return 1;
    }
    const IAxis& axis = h1d->axis();
    if (axis.binLowerEdge(8) != 4) {
    	cerr << "binLowerEdge(8) returned " << axis.binLowerEdge(8) << " should have been 4" << endl;
    	return 1;
    }
    IHistogram1D* h1a = new CHistogram1D();
    if (h1a == NULL) {
    	cerr << "Could not create CHistogram1D" << endl;
    	return 1;
    }
    if (h1a->title() != "CHistogram") {
    	cerr << "title() returned \"" << h1a->title() << "\" should have been \"CHistogram\"" << endl;
    	return 1;
    }
    
    h1d->add(*h1a);

    if (h1d->binMean(25) != 150) {
    	cerr << "binMean(25) returned " << h1d->binMean(25) << " should have been 150" << endl;
    	return 1;
    }
    return 0;
}
