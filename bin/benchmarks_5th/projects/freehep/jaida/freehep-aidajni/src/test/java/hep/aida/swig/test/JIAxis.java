package hep.aida.swig.test;

import hep.aida.IAxis;

public class JIAxis implements IAxis {

    public JIAxis() {
    }

/*
	public double binCenter(int arg0) {
		return arg0/2;
	}
*/
	public double binLowerEdge(int arg0) {
		return (double)arg0/2.0;
	}

	public double binUpperEdge(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double binWidth(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int bins() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int coordToIndex(double arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isFixedBinning() {
		// TODO Auto-generated method stub
		return false;
	}

	public double lowerEdge() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double upperEdge() {
		// TODO Auto-generated method stub
		return 0;
	}

}
