#ifndef CAIDA
#define CAIDA

#include <iostream>

#include <AIDA/IAnalysisFactory.h>
#include <AIDA/IAnnotation.h>
#include <AIDA/IAxis.h>
#include <AIDA/IHistogram1D.h>

extern "C" AIDA::IHistogram1D* getTestHistogram();
extern "C" AIDA::IAnalysisFactory* AIDA_createAnalysisFactory();

class CAxis : virtual public AIDA::IAxis {

private:

public:
    CAxis() {
    };

	bool isFixedBinning() const { return true; }
	double lowerEdge() const { return 0.0; }
	double upperEdge() const { return 10.0; }
	int bins() const { return 19; }
	double binLowerEdge(int x) const { return (double)x/2.0; }
	double binUpperEdge(int) const { return 1.0; }
	double binWidth(int) const { return 2.0; }
//	double binCenter(int a) const { return a/2.0; }
	int coordToIndex(double) const { return 1; }
};


class CHistogram1D : virtual public AIDA::IHistogram1D {

private: 
    AIDA::IAnnotation* theAnnotation;
    AIDA::IAxis* theAxis;
    double k;
    
public:
    CHistogram1D() {
        k = 0;
        theAxis = new CAxis();
    };
    
    std::string title() const { return "CHistogram"; }
    void setTitle(const std::string&) {};
	AIDA::IAnnotation& annotation() { return *theAnnotation; }
	const AIDA::IAnnotation& annotation() const { return *theAnnotation; }
	int dimension() const { return 1; }
	void reset() {};
	int entries() const { return 100; }
	int nanEntries() const { return 0; }
	void* cast(const std::string&) const { return 0; }
	int allEntries() const { return 100; }
	int extraEntries() const { return 0; }
	double equivalentBinEntries() const { return 0.0; }
	double sumBinHeights() const { return 0.0; }
	double sumAllBinHeights() const { return 0.0; }
	double sumExtraBinHeights() const { return 0.0; }
	double minBinHeight() const { return 0.0; }
	double maxBinHeight() const { return 0.0; }
	void scale(double) {};
	void fill(double, double) {};
	double binMean(int a) const { 
		return 2*a+k; 
	}
	int binEntries(int) const { return 0; }
	double binHeight(int) const { return 0.0; }
	double binError(int) const { return 0.0; }
    double mean() const { return 0.0; }
	double rms() const { return 0.0; }
	const AIDA::IAxis& axis() const { return *theAxis; }
	int coordToIndex(double) const { return 1; }
	void add(const AIDA::IHistogram1D& a) { 
	   k += a.binMean(50); 
	}
};

#endif

