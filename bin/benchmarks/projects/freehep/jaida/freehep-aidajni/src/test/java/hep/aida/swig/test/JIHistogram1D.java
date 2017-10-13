package hep.aida.swig.test;

import hep.aida.IAxis;

public class JIHistogram1D implements hep.aida.IHistogram1D {

    private double k;
    private IAxis axis;
    
    public JIHistogram1D() {
        k = 0;
        axis = new JIAxis();
    }
    
    public void add(hep.aida.IHistogram1D a) throws IllegalArgumentException {
        k += a.binMean(50);
    }

    public IAxis axis() {
        // TODO Auto-generated method stub
        return axis;
    }

    public int binEntries(int arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return 0;
    }

    public double binError(int arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return 0;
    }

    public double binHeight(int arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return 0;
    }

    public double binMean(int a) throws IllegalArgumentException {
        return 2*a + k;
    }

    public int coordToIndex(double arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void fill(double arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public void fill(double arg0, double arg1) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public double mean() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double rms() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int allEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double equivalentBinEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int extraEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double maxBinHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double minBinHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void scale(double arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public double sumAllBinHeights() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double sumBinHeights() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double sumExtraBinHeights() {
        // TODO Auto-generated method stub
        return 0;
    }

    public hep.aida.IAnnotation annotation() {
        // TODO Auto-generated method stub
        return null;
    }

    public int dimension() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int entries() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int nanEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void reset() throws RuntimeException {
        // TODO Auto-generated method stub

    }

    public void setTitle(String arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    public String title() {
        // TODO Auto-generated method stub
        return "JHistogram";
    }

}
