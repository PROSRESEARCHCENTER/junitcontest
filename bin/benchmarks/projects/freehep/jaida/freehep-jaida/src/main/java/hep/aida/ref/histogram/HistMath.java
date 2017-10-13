/*
 * HistMath.java
 *
 * Created on February 22, 2001, 1:12 PM
 */

package hep.aida.ref.histogram;

import hep.aida.IAnalysisFactory;
import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IHistogramFactory;
import hep.aida.ref.Annotation;
import hep.aida.ref.histogram.binner.BinnerMath;

import java.util.ArrayList;

/**
 *
 * @author The AIDA team @ SLAC.
 * @version $Id: HistMath.java 10569 2007-03-08 00:47:34Z serbo $
 */
class HistMath {
    static final double relPrec = 1E-10;
    
    
    protected HistMath() {
    }
    
    private double errorAdd(double e1, double e2) {
        return Math.sqrt(e1*e1+e2*e2);
    }
    private double errorSub(double e1, double e2) {
        return errorAdd(e1,e2);
    }
    private double errorMul(double e1, double l1, double e2, double l2) {
        return Math.sqrt(Math.pow(e1*l2, 2) + Math.pow(l1*e2, 2));
    }
    private double errorDiv(double e1, double l1, double e2, double l2) {
        return Math.sqrt(Math.pow(e1/l2, 2) + Math.pow(e2*l1/(l2*l2), 2));
    }
    
    private double addMean( double mean1, double height1, double mean2, double height2 ) {
        mean1 = HistUtils.isValidDouble(mean1) ? mean1 : 0;
        mean2 = HistUtils.isValidDouble(mean2) ? mean2 : 0;
        return ( mean1*height1 + mean2*height2 )/(height1+height2);
    }
    private double addRms( double rms1, double mean1, double height1, double rms2, double mean2, double height2 ) {
        double m = addMean( mean1,height1,mean2,height2);
        double h = height1+height2;
        double r = ((rms1*rms1*height1 + mean1*mean1*height1)+(rms2*rms2*height2 + mean2*mean2*height2))/h - m*m;
        if ( r < 0 ) {
            if ( Math.abs(r) < relPrec ) {
                r = 0;
            } else {
                r = 0;
                //throw new RuntimeException("Problem with rx "+r);
            }
        }
        return Math.sqrt(r);
    }
    private double subMean( double mean1, double height1, double mean2, double height2 ) {
        return ( mean1*height1 - mean2*height2 )/(height1-height2);
    }
    private double subRms( double rms1, double mean1, double height1, double rms2, double mean2, double height2 ) {
        double m = subMean( mean1,height1,mean2,height2);
        double h = height1-height2;
        double r = ((rms1*rms1*height1 + mean1*mean1*height1)-(rms2*rms2*height2 + mean2*mean2*height2))/h - m*m;
        if ( r < 0 ) {
            if ( Math.abs(r) < relPrec ) {
                r = 0;
            } else {
                r = 0;
                //throw new RuntimeException("Problem with r "+r);
            }
        }
        return Math.sqrt(r);
    }
    
    private IAxis copy( IAxis axis ) {
        if ( axis.isFixedBinning())
            return new FixedAxis( axis.bins(), axis.lowerEdge(),  axis.upperEdge() );
        else {
            double[] edges = new double[ axis.bins() + 1 ];
            edges[0] = axis.binLowerEdge(0);
            for ( int i = 0; i < axis.bins(); i ++ )
                edges[i+1] = axis.binUpperEdge(i);
            return new VariableAxis( edges );
        }
    }
    
    private void copy(IAnnotation newAn, IAnnotation an1, IAnnotation an2) {
        int size1 = an1.size();
        int size2 = an2.size();
        ArrayList list = new ArrayList(size1);
        
        // Fill only entries that are the same in both annotations
        for (int i=0; i<size1; i++) {
            String key = an1.key(i);
            String val = an1.value(key);
            if (key.equals(Annotation.titleKey))    continue;
            if (key.equals(Annotation.aidaPathKey)) continue;
            if (key.equals(Annotation.fullPathKey)) continue;
            if (an2.hasKey(key) && an2.value(key).equals(val)) {
                boolean sticky = an1.isSticky(key);
                if (newAn.hasKey(key)) {
                    newAn.setValue(key, val);
                    newAn.setSticky(key, sticky);
                } else {
                    newAn.addItem(key, val, sticky);
                }
            }
        }
    }

    
    //----------------------------------------------------------------1D case------------------
    /**
     *Checks for compatability of two axes
     */
    static void checkCompatibility(IAxis axis1, IAxis axis2) throws IllegalArgumentException {
        String message = null;
        if ( (axis1 instanceof FixedAxis && axis2 instanceof FixedAxis) ||
             (axis1 instanceof VariableAxis && axis2 instanceof VariableAxis) ) {
            if (!axis1.equals(axis2)) message = "Incompatible Axis";
            else return;
        } else if ( axis1.bins() != axis2.bins() ) message = "Different number of bins: "+axis1.bins()+ ", "+axis2.bins();
        else { 
            for ( int i = 0; i < axis1.bins(); i ++ ) {
                if ( axis1.binUpperEdge(i) != axis2.binUpperEdge(i) ||  axis1.binLowerEdge(i) != axis2.binLowerEdge(i) ) {
                    message = "Different edges for bin "+i;
                    break;
                }
            }
        }
        if (message != null) throw new IllegalArgumentException("Incompatible histogram binning: \n\t"+message);
    }
    
    private void checkValidity(IHistogram1D h1, IHistogram1D h2) throws IllegalArgumentException {
        checkCompatibility(h1.axis(),h2.axis());
    }
    
    /**
     * Adds two 1D Histogram
     *
     * @return h1 + h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram1D add(String name, IHistogram1D h1, IHistogram1D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram1D);
        boolean h2Aida = !(h2 instanceof Histogram1D);
        
        String options = null;
        if (!h1Aida) options = ((Histogram1D) h1).options();
        Histogram1D hist = new Histogram1D(name, name, copy( h1.axis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());
        
        if (!h1Aida && !h2Aida) {
            BinnerMath.add(hist.binner(), ((Histogram1D) h1).binner(), ((Histogram1D) h2).binner());
            hist.initHistogram1D(hist.binner());            
        } else {
            int bins = h1.axis().bins()+2;
            double[] newHeights = new double[bins];
            double[] newErrors  = new double[bins];
            double[] newMeans   = new double[bins];
            double[] newRmss    = new double[bins];
            int[]    newEntries = new int   [bins];
            
            double rms1 = 0;
            double rms2 = 0;
            for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
                double height1 = h1.binHeight(i);
                double height2 = h2.binHeight(i);
                double h       = height1+height2;
                double mean1   = h1.binMean(i);
                double mean2   = h2.binMean(i);
                double m    = 0;
                
                if (h1Aida) rms1 = (h1.axis().binUpperEdge(i)-h1.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms1 = ((Histogram1D) h1).binRms(i);
                
                if (h2Aida) rms2 = (h2.axis().binUpperEdge(i)-h2.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms2 = ((Histogram1D) h2).binRms(i);
                
                double r    = 0;
                if ( h != 0 ) {
                    m = addMean(mean1,height1,mean2,height2);
                    r = addRms(rms1,mean1,height1,rms2,mean2,height2);
                }
                
                int bin = hist.mapBinNumber(i,h1.axis());
                newHeights[bin] = h;
                newErrors [bin] = errorAdd(h1.binError(i),h2.binError(i));
                newEntries[bin] = h1.binEntries(i)+h2.binEntries(i);
                newMeans  [bin] = m;
                newRmss   [bin] = r;
            }
            hist.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
        }
        return hist;
    }
     
    /**
     * Subtracts two 1D Histogram
     *
     * @return h1 - h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram1D sub(String name, IHistogram1D h1, IHistogram1D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram1D);
        boolean h2Aida = !(h2 instanceof Histogram1D);
        
        String options = null;
        if (!h1Aida) options = ((Histogram1D) h1).options();
        Histogram1D hist = new Histogram1D(name, name, copy( h1.axis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        if (!h1Aida && !h2Aida) {
            BinnerMath.sub(hist.binner(), ((Histogram1D) h1).binner(), ((Histogram1D) h2).binner());
            hist.initHistogram1D(hist.binner());            
        } else {
            int bins = h1.axis().bins()+2;
            double[] newHeights = new double[bins];
            double[] newErrors  = new double[bins];
            double[] newMeans   = new double[bins];
            double[] newRmss    = new double[bins];
            int[]    newEntries = new int   [bins];
            
            double rms1 = 0;
            double rms2 = 0;
            
            for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
                double height1 = h1.binHeight(i);
                double height2 = h2.binHeight(i);
                double h       = height1-height2;
                double mean1   = h1.binMean(i);
                double mean2   = h2.binMean(i);
                double m    = 0;
                
                if (h1Aida) rms1 = (h1.axis().binUpperEdge(i)-h1.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms1 = ((Histogram1D) h1).binRms(i);
                
                if (h2Aida) rms2 = (h2.axis().binUpperEdge(i)-h2.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms2 = ((Histogram1D) h2).binRms(i);
                
                double r    = 0;
                if ( h != 0 ) {
                    m = subMean(mean1,height1,mean2,height2);
                    r = subRms(rms1,mean1,height1,rms2,mean2,height2);
                }
                
                int bin = hist.mapBinNumber(i,h1.axis());
                newHeights[bin] = h;
                newErrors [bin] = errorSub(h1.binError(i),h2.binError(i));
                newEntries[bin] = h1.binEntries(i)-h2.binEntries(i);
                newMeans  [bin] = m;
                newRmss   [bin] = r;
            }
            hist.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
        }
        return hist;
    }
    
    /**
     * Multiplies two 1D Histogram
     *
     * @return h1 * h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram1D mul(String name, IHistogram1D h1, IHistogram1D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram1D);
        boolean h2Aida = !(h2 instanceof Histogram1D);
        
        String options = null;
        if (!h1Aida) options = ((Histogram1D) h1).options();
        Histogram1D hist = new Histogram1D(name, name, copy( h1.axis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());
        
        if (!h1Aida && !h2Aida) {
            BinnerMath.mul(hist.binner(), ((Histogram1D) h1).binner(), ((Histogram1D) h2).binner());
            hist.initHistogram1D(hist.binner());
        } else {
            int bins = h1.axis().bins()+2;
            double[] newHeights = new double[bins];
            double[] newErrors  = new double[bins];
            double[] newMeans   = new double[bins];
            double[] newRmss    = new double[bins];
            int[]    newEntries = new int   [bins];
            
            double rms1 = 0;
            for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
                double height1 = h1.binHeight(i);
                double height2 = h2.binHeight(i);
                double h       = height1*height2;
                double m = h1.binMean(i);
                
                if (h1Aida) rms1 = (h1.axis().binUpperEdge(i)-h1.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms1 = ((Histogram1D) h1).binRms(i);
                
                int bin = hist.mapBinNumber(i,h1.axis());
                newHeights[bin] = h;
                newErrors [bin] = errorMul(h1.binError(i),h1.binHeight(i),h2.binError(i),h2.binHeight(i));
                newEntries[bin] = h1.binEntries(i);
                newMeans  [bin] = m;
                newRmss   [bin] = rms1;
                
                //newErrors [bin] = Math.sqrt((Math.pow(h1.binError(i)*h2.binHeight(i), 2) + Math.pow(h2.binError(i)*h1.binHeight(i), 2)));
            }
            hist.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
        }
        return hist;
    }
    
    
    IHistogram1D div(String name, IHistogram1D h1, IHistogram1D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram1D);
        boolean h2Aida = !(h2 instanceof Histogram1D);
        
        String options = null;
        if (!h1Aida) options = ((Histogram1D) h1).options();
        Histogram1D hist = new Histogram1D(name, name, copy( h1.axis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());
        
        if (!h1Aida && !h2Aida) {
            BinnerMath.div(hist.binner(), ((Histogram1D) h1).binner(), ((Histogram1D) h2).binner());
            hist.initHistogram1D(hist.binner());
        } else {
            int bins = h1.axis().bins()+2;
            double[] newHeights = new double[bins];
            double[] newErrors  = new double[bins];
            double[] newMeans   = new double[bins];
            double[] newRmss    = new double[bins];
            int[]    newEntries = new int   [bins];
            
            double rms1 = 0;
            for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
                double height1 = h1.binHeight(i);
                double height2 = h2.binHeight(i);
                double h       = height1/height2;
                double m = h1.binMean(i);
                
                if (h1Aida) rms1 = (h1.axis().binUpperEdge(i)-h1.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms1 = ((Histogram1D) h1).binRms(i);
                
                int bin = hist.mapBinNumber(i,h1.axis());
                if ( height2 != 0 ) {
                    newHeights[bin] = h;
                    newErrors [bin] = errorDiv(h1.binError(i),h1.binHeight(i),h2.binError(i),h2.binHeight(i));
                    newEntries[bin] = h1.binEntries(i);
                    newMeans  [bin] = m;
                    newRmss   [bin] = rms1;
                }
            }
            hist.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
        }
        return hist;
    }
    
    
    //----------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------2D Case---------------------
    /**
     * Checks for compatibility of 2D Histogram
     */
    private void checkValidity(IHistogram2D h1, IHistogram2D h2) throws IllegalArgumentException {
        checkCompatibility(h1.xAxis(),h2.xAxis());
        checkCompatibility(h1.yAxis(),h2.yAxis());
    }
    
    /**
     * Adds two 2D Histogram
     *
     * @return h1 + h
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram2D add(String name, IHistogram2D h1, IHistogram2D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram2D);
        boolean h2Aida = !(h2 instanceof Histogram2D);

        String options = null;
        if (!h1Aida) options = ((Histogram2D) h1).options();
        Histogram2D hist = new Histogram2D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        double[][] newHeights = new double[xbins][ybins];
        double[][] newErrors  = new double[xbins][ybins];
        double[][] newMeanXs  = new double[xbins][ybins];
        double[][] newRmsXs   = new double[xbins][ybins];
        double[][] newMeanYs  = new double[xbins][ybins];
        double[][] newRmsYs   = new double[xbins][ybins];
        int[][] newEntries = new    int[xbins][ybins];
        double rmsx1 = 0;
        double rmsx2 = 0;
        double rmsy1 = 0;
        double rmsy2 = 0;
        
        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins(); i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins(); j++) {
                
                double height1 = h1.binHeight(i,j);
                double height2 = h2.binHeight(i,j);
                double h       = height1+height2;
                double meanx1  = h1.binMeanX(i,j);
                double meanx2  = h2.binMeanX(i,j);
                double mx      = 0;
                double rx      = 0;
                double meany1  = h1.binMeanY(i,j);
                double meany2  = h2.binMeanY(i,j);
                double my      = 0;
                
                if (h1Aida) {
                    rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                    rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
                } else {
                    rmsx1   = ((Histogram2D) h1).binRmsX(i,j);
                    rmsy1   = ((Histogram2D) h1).binRmsY(i,j);
                }
                
                if (h2Aida) {
                    rmsx2 = (h2.xAxis().binUpperEdge(i)-h2.xAxis().binLowerEdge(i))/Math.sqrt(12);
                    rmsy2 = (h2.yAxis().binUpperEdge(j)-h2.yAxis().binLowerEdge(j))/Math.sqrt(12);
                } else {
                    rmsx2   = ((Histogram2D) h2).binRmsX(i,j);
                    rmsy2   = ((Histogram2D) h2).binRmsY(i,j);
                }
                
                double ry      = 0;
                if ( h != 0 ) {
                    mx = addMean(meanx1,height1,meanx2,height2);
                    rx = addRms(rmsx1,meanx1,height1,rmsx2,meanx2,height2);
                    my = addMean(meany1,height1,meany2,height2);
                    ry = addRms(rmsy1,meany1,height1,rmsy2,meany2,height2);
                }
                
                int binx = hist.mapBinNumber(i,h1.xAxis());
                int biny = hist.mapBinNumber(j,h1.yAxis());
                newHeights[binx][biny] = h;
                newErrors [binx][biny] = errorAdd(h1.binError(i,j),h2.binError(i,j));
                newEntries[binx][biny] = h1.binEntries(i,j)+h2.binEntries(i,j);
                newMeanXs [binx][biny] = mx;
                newRmsXs  [binx][biny] = rx;
                newMeanYs [binx][biny] = my;
                newRmsYs  [binx][biny] = ry;
            }
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
        return hist;
    }
    /**
     * Subtracts two 2D Histogram
     *
     * @return h1 - h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram2D sub(String name, IHistogram2D h1, IHistogram2D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram2D);
        boolean h2Aida = !(h2 instanceof Histogram2D);

        String options = null;
        if (!h1Aida) options = ((Histogram2D) h1).options();
        Histogram2D hist =new Histogram2D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        double[][] newHeights=new double[xbins][ybins];
        double[][] newErrors=new double[xbins][ybins];
        double[][] newMeanXs  = new double[xbins][ybins];
        double[][] newRmsXs   = new double[xbins][ybins];
        double[][] newMeanYs  = new double[xbins][ybins];
        double[][] newRmsYs   = new double[xbins][ybins];
        int[][] newEntries = new    int[xbins][ybins];
        double rmsx1 = 0;
        double rmsx2 = 0;
        double rmsy1 = 0;
        double rmsy2 = 0;

        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++){
                
                double height1 = h1.binHeight(i,j);
                double height2 = h2.binHeight(i,j);
                double h       = height1-height2;
                double meanx1  = h1.binMeanX(i,j);
                double meanx2  = h2.binMeanX(i,j);
                double mx    = 0;
                double rx    = 0;
                double meany1   = h1.binMeanY(i,j);
                double meany2   = h2.binMeanY(i,j);
                double my    = 0;
                
                if (h1Aida) {
                    rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                    rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
                } else {
                    rmsx1   = ((Histogram2D) h1).binRmsX(i,j);
                    rmsy1   = ((Histogram2D) h1).binRmsY(i,j);
                }
                
                if (h2Aida) {
                    rmsx2 = (h2.xAxis().binUpperEdge(i)-h2.xAxis().binLowerEdge(i))/Math.sqrt(12);
                    rmsy2 = (h2.yAxis().binUpperEdge(j)-h2.yAxis().binLowerEdge(j))/Math.sqrt(12);
                } else {
                    rmsx2   = ((Histogram2D) h2).binRmsX(i,j);
                    rmsy2   = ((Histogram2D) h2).binRmsY(i,j);
                }
                
                double ry    = 0;
                if ( h != 0 ) {
                    mx = subMean(meanx1,height1,meanx2,height2);
                    rx = subRms(rmsx1,meanx1,height1,rmsx2,meanx2,height2);
                    my = subMean(meany1,height1,meany2,height2);
                    ry = subRms(rmsy1,meany1,height1,rmsy2,meany2,height2);
                }
                
                int binx = hist.mapBinNumber(i,h1.xAxis());
                int biny = hist.mapBinNumber(j,h1.yAxis());
                newHeights[binx][biny] = h;
                newErrors [binx][biny] = errorSub(h1.binError(i,j),h2.binError(i,j));
                newEntries[binx][biny] = h1.binEntries(i,j)-h2.binEntries(i,j);
                newMeanXs [binx][biny] = mx;
                newRmsXs  [binx][biny] = rx;
                newMeanYs [binx][biny] = my;
                newRmsYs  [binx][biny] = ry;
                
            }
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
        return hist;
    }
    
    /**
     * Multiplies two Histogram
     *
     * @return h1 * h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram2D mul(String name, IHistogram2D h1, IHistogram2D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram2D);
 
        String options = null;
        if (!h1Aida) options = ((Histogram2D) h1).options();
        Histogram2D hist =new Histogram2D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        double[][] newHeights=new double[xbins][ybins];
        double[][] newErrors=new double[xbins][ybins];
        double[][] newMeanXs  = new double[xbins][ybins];
        double[][] newRmsXs   = new double[xbins][ybins];
        double[][] newMeanYs  = new double[xbins][ybins];
        double[][] newRmsYs   = new double[xbins][ybins];
        int[][] newEntries = new    int[xbins][ybins];
        double rmsx1 = 0;
        double rmsy1 = 0;
         
        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++) {
            
            double height1 = h1.binHeight(i,j);
            double height2 = h2.binHeight(i,j);
            double h       = height1*height2;
            double mx = h1.binMeanX(i,j);
            double my = h1.binMeanY(i,j);
            
            if (h1Aida) {
                rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx1   = ((Histogram2D) h1).binRmsX(i,j);
                rmsy1   = ((Histogram2D) h1).binRmsY(i,j);
            }
            
            int binx = hist.mapBinNumber(i,h1.xAxis());
            int biny = hist.mapBinNumber(j,h1.yAxis());
            newHeights[binx][biny] = h;
            newErrors [binx][biny] = errorMul(h1.binError(i,j),h1.binHeight(i,j),h2.binError(i,j),h2.binHeight(i,j));
            newEntries[binx][biny] = h1.binEntries(i,j);
            newMeanXs [binx][biny] = mx;
            newRmsXs  [binx][biny] = rmsx1;
            newMeanYs [binx][biny] = my;
            newRmsYs  [binx][biny] = rmsy1;
            }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
        return hist;
    }

    /**
     * Divides two Histogram
     *
     * @return h1 / h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram2D div(String name, IHistogram2D h1, IHistogram2D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram2D);

        String options = null;
        if (!h1Aida) options = ((Histogram2D) h1).options();
        Histogram2D hist =new Histogram2D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        double[][] newHeights=new double[xbins][ybins];
        double[][] newErrors=new double[xbins][ybins];
        double[][] newMeanXs  = new double[xbins][ybins];
        double[][] newRmsXs   = new double[xbins][ybins];
        double[][] newMeanYs  = new double[xbins][ybins];
        double[][] newRmsYs   = new double[xbins][ybins];
        int[][] newEntries = new    int[xbins][ybins];
        double rmsx1 = 0;
        double rmsy1 = 0;
        
        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++) {
            double height1 = h1.binHeight(i,j);
            double height2 = h2.binHeight(i,j);
            double h       = height1/height2;
            double mx = h1.binMeanX(i,j);
            double my = h1.binMeanY(i,j);            
            
            if (h1Aida) {
                rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx1   = ((Histogram2D) h1).binRmsX(i,j);
                rmsy1   = ((Histogram2D) h1).binRmsY(i,j);
            }
            
            int binx = hist.mapBinNumber(i,h1.xAxis());
            int biny = hist.mapBinNumber(j,h1.yAxis());
            
            if ( height2 != 0 ) {
                newHeights[binx][biny] = h;
                newErrors [binx][biny] = errorDiv(h1.binError(i,j),h1.binHeight(i,j),h2.binError(i,j),h2.binHeight(i,j));
                newEntries[binx][biny] = h1.binEntries(i,j);
                newMeanXs [binx][biny] = mx;
                newRmsXs  [binx][biny] = rmsx1;
                newMeanYs [binx][biny] = my;
                newRmsYs  [binx][biny] = rmsy1;
            }
            }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
        return hist;
    }
    //---------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------3D Case----------------------------------------------------
    /**
     * Checks for compatibility of 3D Histogram
     */
    private void checkValidity(IHistogram3D h1, IHistogram3D h2) throws IllegalArgumentException {
        checkCompatibility(h1.xAxis(),h2.xAxis());
        checkCompatibility(h1.yAxis(),h2.yAxis());
        checkCompatibility(h1.zAxis(),h2.zAxis());
    }
    /**
     * Adds two Histogram
     *
     * @return h1 + h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram3D add(String name, IHistogram3D h1, IHistogram3D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram3D);
        boolean h2Aida = !(h2 instanceof Histogram3D);

        String options = null;
        if (!h1Aida) options = ((Histogram3D) h1).options();
        Histogram3D hist = new Histogram3D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), copy( h1.zAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        int zbins = h1.zAxis().bins()+2;
        double[][][] newHeights=new double[xbins][ybins][zbins];
        double[][][] newErrors=new double[xbins][ybins][zbins];
        double[][][] newMeanXs  = new double[xbins][ybins][zbins];
        double[][][] newRmsXs   = new double[xbins][ybins][zbins];
        double[][][] newMeanYs  = new double[xbins][ybins][zbins];
        double[][][] newRmsYs   = new double[xbins][ybins][zbins];
        double[][][] newMeanZs  = new double[xbins][ybins][zbins];
        double[][][] newRmsZs   = new double[xbins][ybins][zbins];
        int[][][] newEntries = new int[xbins][ybins][zbins];
        double rmsx1 = 0;
        double rmsx2 = 0;
        double rmsy1 = 0;
        double rmsy2 = 0;
        double rmsz1 = 0;
        double rmsz2 = 0;

        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++)
                for(int k=IAxis.UNDERFLOW_BIN; k<h1.zAxis().bins();k++) {
            
            double height1 = h1.binHeight(i,j,k);
            double height2 = h2.binHeight(i,j,k);
            double h       = height1+height2;
            double meanx1  = h1.binMeanX(i,j,k);
            double meanx2  = h2.binMeanX(i,j,k);
            double mx      = 0;
            double rx      = 0;
            double meany1  = h1.binMeanY(i,j,k);
            double meany2  = h2.binMeanY(i,j,k);
            double my      = 0;
            double ry      = 0;
            double meanz1  = h1.binMeanZ(i,j,k);
            double meanz2  = h2.binMeanZ(i,j,k);
            double mz      = 0;
            if (h1Aida) {
                rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsz1 = (h1.zAxis().binUpperEdge(j)-h1.zAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx1   = ((Histogram3D) h1).binRmsX(i,j,k);
                rmsy1   = ((Histogram3D) h1).binRmsY(i,j,k);
                rmsz1   = ((Histogram3D) h1).binRmsZ(i,j,k);
            }
            
            if (h2Aida) {
                rmsx2 = (h2.xAxis().binUpperEdge(i)-h2.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy2 = (h2.yAxis().binUpperEdge(j)-h2.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsz2 = (h2.zAxis().binUpperEdge(j)-h2.zAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx2   = ((Histogram3D) h2).binRmsX(i,j,k);
                rmsy2   = ((Histogram3D) h2).binRmsY(i,j,k);
                rmsz2   = ((Histogram3D) h2).binRmsZ(i,j,k);
            }
            
            double rz      = 0;
            if ( h != 0 ) {
                mx = addMean(meanx1,height1,meanx2,height2);
                rx = addRms(rmsx1,meanx1,height1,rmsx2,meanx2,height2);
                my = addMean(meany1,height1,meany2,height2);
                ry = addRms(rmsy1,meany1,height1,rmsy2,meany2,height2);
                mz = addMean(meanz1,height1,meanz2,height2);
                rz = addRms(rmsz1,meanz1,height1,rmsz2,meanz2,height2);
            }
            int binx = hist.mapBinNumber(i,h1.xAxis());
            int biny = hist.mapBinNumber(j,h1.yAxis());
            int binz = hist.mapBinNumber(k,h1.zAxis());
            newHeights[binx][biny][binz] = h;
            newErrors [binx][biny][binz] = errorAdd(h1.binError(i,j,k),h2.binError(i,j,k));
            newEntries[binx][biny][binz] = h1.binEntries(i,j,k)+h2.binEntries(i,j,k);
            newMeanXs [binx][biny][binz] = mx;
            newRmsXs  [binx][biny][binz] = rx;
            newMeanYs [binx][biny][binz] = my;
            newRmsYs  [binx][biny][binz] = ry;
            newMeanZs [binx][biny][binz] = mz;
            newRmsZs  [binx][biny][binz] = rz;
            
                }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs,newMeanZs,newRmsZs);
        return hist;
    }
    
    /**
     * Subtracts two Histogram
     *
     * @return h1 - h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram3D sub(String name, IHistogram3D h1, IHistogram3D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram3D);
        boolean h2Aida = !(h2 instanceof Histogram3D);

        String options = null;
        if (!h1Aida) options = ((Histogram3D) h1).options();
        Histogram3D hist = new Histogram3D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), copy( h1.zAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        int zbins = h1.zAxis().bins()+2;
        double[][][] newHeights=new double[xbins][ybins][zbins];
        double[][][] newErrors=new double[xbins][ybins][zbins];
        double[][][] newMeanXs  = new double[xbins][ybins][zbins];
        double[][][] newRmsXs   = new double[xbins][ybins][zbins];
        double[][][] newMeanYs  = new double[xbins][ybins][zbins];
        double[][][] newRmsYs   = new double[xbins][ybins][zbins];
        double[][][] newMeanZs  = new double[xbins][ybins][zbins];
        double[][][] newRmsZs   = new double[xbins][ybins][zbins];
        int[][][] newEntries = new int[xbins][ybins][zbins];
        double rmsx1 = 0;
        double rmsx2 = 0;
        double rmsy1 = 0;
        double rmsy2 = 0;
        double rmsz1 = 0;
        double rmsz2 = 0;

        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++)
                for(int k=IAxis.UNDERFLOW_BIN; k<h1.zAxis().bins();k++) {
            double height1 = h1.binHeight(i,j,k);
            double height2 = h2.binHeight(i,j,k);
            double h       = height1-height2;
            double meanx1  = h1.binMeanX(i,j,k);
            double meanx2  = h2.binMeanX(i,j,k);
            double mx    = 0;
            double rx    = 0;
            double meany1   = h1.binMeanY(i,j,k);
            double meany2   = h2.binMeanY(i,j,k);
            double my    = 0;
            double ry    = 0;
            double meanz1   = h1.binMeanZ(i,j,k);
            double meanz2   = h2.binMeanZ(i,j,k);
            double mz    = 0;
            if (h1Aida) {
                rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsz1 = (h1.zAxis().binUpperEdge(j)-h1.zAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx1   = ((Histogram3D) h1).binRmsX(i,j,k);
                rmsy1   = ((Histogram3D) h1).binRmsY(i,j,k);
                rmsz1   = ((Histogram3D) h1).binRmsZ(i,j,k);
            }
            
            if (h2Aida) {
                rmsx2 = (h2.xAxis().binUpperEdge(i)-h2.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy2 = (h2.yAxis().binUpperEdge(j)-h2.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsz2 = (h2.zAxis().binUpperEdge(j)-h2.zAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx2   = ((Histogram3D) h2).binRmsX(i,j,k);
                rmsy2   = ((Histogram3D) h2).binRmsY(i,j,k);
                rmsz2   = ((Histogram3D) h2).binRmsZ(i,j,k);
            }
            
            double rz    = 0;
            if ( h != 0 ) {
                mx = subMean(meanx1,height1,meanx2,height2);
                rx = subRms(rmsx1,meanx1,height1,rmsx2,meanx2,height2);
                my = subMean(meany1,height1,meany2,height2);
                ry = subRms(rmsy1,meany1,height1,rmsy2,meany2,height2);
                mz = subMean(meanz1,height1,meanz2,height2);
                rz = subRms(rmsz1,meanz1,height1,rmsz2,meanz2,height2);
            }
            
            int binx = hist.mapBinNumber(i,h1.xAxis());
            int biny = hist.mapBinNumber(j,h1.yAxis());
            int binz = hist.mapBinNumber(k,h1.zAxis());
            newHeights[binx][biny][binz] = h;
            newErrors [binx][biny][binz] = errorSub(h1.binError(i,j,k),h2.binError(i,j,k));
            newEntries[binx][biny][binz] = h1.binEntries(i,j,k)-h2.binEntries(i,j,k);
            newMeanXs [binx][biny][binz] = mx;
            newRmsXs  [binx][biny][binz] = rx;
            newMeanYs [binx][biny][binz] = my;
            newRmsYs  [binx][biny][binz] = ry;
            newMeanZs [binx][biny][binz] = mz;
            newRmsZs  [binx][biny][binz] = rz;
                }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs,newMeanZs,newRmsZs);
        return hist;
    }
    
    /**
     * Multiplies two Histogram
     *
     * @return h1 * h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram3D mul(String name, IHistogram3D h1, IHistogram3D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram3D);
 
        String options = null;
        if (!h1Aida) options = ((Histogram3D) h1).options();
        Histogram3D hist = new Histogram3D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), copy( h1.zAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        int zbins = h1.zAxis().bins()+2;
        double[][][] newHeights = new double[xbins][ybins][zbins];
        double[][][] newErrors  = new double[xbins][ybins][zbins];
        double[][][] newMeanXs  = new double[xbins][ybins][zbins];
        double[][][] newRmsXs   = new double[xbins][ybins][zbins];
        double[][][] newMeanYs  = new double[xbins][ybins][zbins];
        double[][][] newRmsYs   = new double[xbins][ybins][zbins];
        double[][][] newMeanZs  = new double[xbins][ybins][zbins];
        double[][][] newRmsZs   = new double[xbins][ybins][zbins];
        int[][][] newEntries = new int[xbins][ybins][zbins];
        double rmsx1 = 0;
        double rmsy1 = 0;
        double rmsz1 = 0;
        
        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++)
                for(int k=IAxis.UNDERFLOW_BIN; k<h1.zAxis().bins();k++){
            
            double height1 = h1.binHeight(i,j,k);
            double height2 = h2.binHeight(i,j,k);
            double h       = height1*height2;
            double mx = h1.binMeanX(i,j,k);
            double my = h1.binMeanY(i,j,k);
            double mz = h1.binMeanZ(i,j,k);
            
            if (h1Aida) {
                rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsz1 = (h1.zAxis().binUpperEdge(j)-h1.zAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx1   = ((Histogram3D) h1).binRmsX(i,j,k);
                rmsy1   = ((Histogram3D) h1).binRmsY(i,j,k);
                rmsz1   = ((Histogram3D) h1).binRmsZ(i,j,k);
            }
            
            int binx = hist.mapBinNumber(i,h1.xAxis());
            int biny = hist.mapBinNumber(j,h1.yAxis());
            int binz = hist.mapBinNumber(k,h1.zAxis());
            newHeights[binx][biny][binz] = h;
            newErrors [binx][biny][binz] = errorMul(h1.binError(i,j,k),h1.binHeight(i,j,k),h2.binError(i,j,k),h2.binHeight(i,j,k));
            newEntries[binx][biny][binz] = h1.binEntries(i,j,k);
            newMeanXs [binx][biny][binz] = mx;
            newRmsXs  [binx][biny][binz] = rmsx1;
            newMeanYs [binx][biny][binz] = my;
            newRmsYs  [binx][biny][binz] = rmsy1;
            newMeanZs [binx][biny][binz] = mz;
            newRmsZs  [binx][biny][binz] = rmsz1;
            
                }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs,newMeanZs,newRmsZs);
        return hist;
    }
    
    /**
     * Divides two Histogram
     *
     * @return h1 / h2
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    IHistogram3D div(String name, IHistogram3D h1, IHistogram3D h2) throws IllegalArgumentException {
        checkValidity(h1, h2);
        
        boolean h1Aida = !(h1 instanceof Histogram3D);

        String options = null;
        if (!h1Aida) options = ((Histogram3D) h1).options();
        Histogram3D hist =new Histogram3D(name, name, copy( h1.xAxis() ), copy( h1.yAxis() ), copy( h1.zAxis() ), options);
        copy(hist.annotation(), h1.annotation(), h2.annotation());

        int xbins = h1.xAxis().bins()+2;
        int ybins = h1.yAxis().bins()+2;
        int zbins = h1.zAxis().bins()+2;
        double[][][] newHeights=new double[xbins][ybins][zbins];
        double[][][] newErrors=new double[xbins][ybins][zbins];
        double[][][] newMeanXs  = new double[xbins][ybins][zbins];
        double[][][] newRmsXs   = new double[xbins][ybins][zbins];
        double[][][] newMeanYs  = new double[xbins][ybins][zbins];
        double[][][] newRmsYs   = new double[xbins][ybins][zbins];
        double[][][] newMeanZs  = new double[xbins][ybins][zbins];
        double[][][] newRmsZs   = new double[xbins][ybins][zbins];
        int[][][] newEntries = new int[xbins][ybins][zbins];
        double rmsx1 = 0;
        double rmsy1 = 0;
        double rmsz1 = 0;
        
        for(int i=IAxis.UNDERFLOW_BIN; i<h1.xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<h1.yAxis().bins();j++)
                for(int k=IAxis.UNDERFLOW_BIN; k<h1.zAxis().bins();k++){
            double height1 = h1.binHeight(i,j,k);
            double height2 = h2.binHeight(i,j,k);
            double h       = height1/height2;
            double mx = h1.binMeanX(i,j,k);
            double my = h1.binMeanY(i,j,k);
            double mz = h1.binMeanZ(i,j,k);
            
            
            if (h1Aida) {
                rmsx1 = (h1.xAxis().binUpperEdge(i)-h1.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy1 = (h1.yAxis().binUpperEdge(j)-h1.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsz1 = (h1.zAxis().binUpperEdge(j)-h1.zAxis().binLowerEdge(j))/Math.sqrt(12);
            } else {
                rmsx1   = ((Histogram3D) h1).binRmsX(i,j,k);
                rmsy1   = ((Histogram3D) h1).binRmsY(i,j,k);
                rmsz1   = ((Histogram3D) h1).binRmsZ(i,j,k);
            }
            
            int binx = hist.mapBinNumber(i,h1.xAxis());
            int biny = hist.mapBinNumber(j,h1.yAxis());
            int binz = hist.mapBinNumber(k,h1.zAxis());
            
            if ( height2 != 0 ) {
                newHeights[binx][biny][binz] = h;
                newErrors [binx][biny][binz] = errorDiv(h1.binError(i,j,k),h1.binHeight(i,j,k),h2.binError(i,j,k),h2.binHeight(i,j,k));
                newEntries[binx][biny][binz] = h1.binEntries(i,j,k);
                newMeanXs [binx][biny][binz] = mx;
                newRmsXs  [binx][biny][binz] = rmsx1;
                newMeanYs [binx][biny][binz] = my;
                newRmsYs  [binx][biny][binz] = rmsy1;
                newMeanZs [binx][biny][binz] = mz;
                newRmsZs  [binx][biny][binz] = rmsz1;
            }
                }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs,newMeanZs,newRmsZs);
        return hist;
        
    }
    
    //----------------------------------------------------------------------------------------------------------------
    // Slices
    IHistogram1D sliceX(IHistogram2D h, String name, int start, int stop) {
        int ybins = h.yAxis().bins();
        start = convertIndex(start, ybins);
        stop  = convertIndex(stop, ybins);
        if ( start > stop ) throw new IllegalArgumentException("Invalid indexes "+start+" "+stop);
        int xbins = h.xAxis().bins()+2;
        double[] newHeights = new double[xbins];
        double[] newErrors  = new double[xbins];
        double[] newMeans   = new double[xbins];
        double[] newRmss    = new double[xbins];
        int[] newEntries = new    int[xbins];
        double rmss = 0;
        boolean h1Aida = !(h instanceof Histogram2D);
        
        Histogram1D hist = new Histogram1D(name, name, copy( h.xAxis() ));
        for(int i=IAxis.UNDERFLOW_BIN; i<h.xAxis().bins();i++) {
            int binx = hist.mapBinNumber(i,h.xAxis());
            for(int jj=start; jj<=stop; jj++) {
                int j = convertBackIndex(jj, ybins);
                double meanB   = newMeans[binx];
                double rmsB    = newRmss[binx];
                double heightB = newHeights[binx];
                if (h1Aida) {
                    rmss = (h.xAxis().binUpperEdge(i)-h.xAxis().binLowerEdge(i))/Math.sqrt(12);
                } else {
                    rmss = ((Histogram2D) h).binRmsX(i,j);
                }
                if ( heightB+h.binHeight(i,j) != 0 ) {
                    newMeans[binx]   = addMean(meanB,heightB,h.binMeanX(i,j),h.binHeight(i,j));
                    newRmss[binx]    = addRms(rmsB,meanB,heightB,rmss,h.binMeanX(i,j),h.binHeight(i,j));
                }
                newHeights[binx] += h.binHeight(i,j);
                newErrors[binx]  += h.binError(i,j)*h.binError(i,j);
                newEntries[binx] += h.binEntries(i,j);
            }
            newErrors[binx] = Math.sqrt(newErrors[binx]);
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
        
        return hist;
    }
    
    IHistogram1D sliceY(IHistogram2D h, String name, int start, int stop) {
        int xbins = h.xAxis().bins();
        start = convertIndex(start, xbins);
        stop  = convertIndex(stop, xbins);
        if ( start > stop ) throw new IllegalArgumentException("Invalid indexes "+start+" "+stop);
        int ybins = h.yAxis().bins()+2;
        double[] newHeights = new double[ybins];
        double[] newErrors  = new double[ybins];
        double[] newMeans   = new double[ybins];
        double[] newRmss    = new double[ybins];
        int[] newEntries = new    int[ybins];
        double rmss = 0;
        boolean h1Aida = !(h instanceof Histogram2D);
                
        Histogram1D hist = new Histogram1D(name, name, copy( h.yAxis() ));
        
        for(int j=IAxis.UNDERFLOW_BIN; j<h.yAxis().bins(); j++) {
            int biny = hist.mapBinNumber(j,h.yAxis());
            for(int ii=start; ii<=stop; ii++) {
                int i = convertBackIndex(ii, xbins);
                double meanB   = newMeans[biny];
                double rmsB    = newRmss[biny];
                double heightB = newHeights[biny];
                if (h1Aida) {
                    rmss = (h.yAxis().binUpperEdge(i)-h.yAxis().binLowerEdge(i))/Math.sqrt(12);
                } else {
                    rmss = ((Histogram2D) h).binRmsY(i,j);
                }
                if ( heightB+h.binHeight(i,j) != 0 ) {
                    newMeans[biny]   = addMean(meanB,heightB,h.binMeanY(i,j),h.binHeight(i,j));
                    newRmss[biny]    = addRms(rmsB,meanB,heightB,rmss,h.binMeanY(i,j),h.binHeight(i,j));
                }
                newHeights[biny] += h.binHeight(i,j);
                newErrors[biny] += h.binError(i,j)*h.binError(i,j);
                newEntries[biny] += h.binEntries(i,j);
            }
            newErrors[biny] = Math.sqrt(newErrors[biny]);
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
        return hist;
    }
    
    IHistogram2D sliceXY(IHistogram3D h, String name, int start, int stop) {
        int zbins = h.zAxis().bins();
        start = convertIndex(start, zbins);
        stop  = convertIndex(stop, zbins);
        if ( start > stop ) throw new IllegalArgumentException("Invalid indexes "+start+" "+stop);
        
        Histogram2D hist = new Histogram2D(name, name, copy( h.xAxis() ), copy( h.yAxis() ));
        int xbins = h.xAxis().bins()+2;
        int ybins = h.yAxis().bins()+2;
        double[][] newHeights = new double[xbins][ybins];
        double[][]  newErrors = new double[xbins][ybins];
        double[][] newMeanXs  = new double[xbins][ybins];
        double[][] newRmsXs   = new double[xbins][ybins];
        double[][] newMeanYs  = new double[xbins][ybins];
        double[][] newRmsYs   = new double[xbins][ybins];
        int[][]    newEntries = new    int[xbins][ybins];
        double rmsx = 0;
        double rmsy = 0;
        boolean h1Aida = !(h instanceof Histogram3D);
        
        for(int i=IAxis.UNDERFLOW_BIN; i<h.xAxis().bins(); i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<h.yAxis().bins(); j++) {
                int binx = hist.mapBinNumber(i,h.xAxis());
                int biny = hist.mapBinNumber(j,h.yAxis());
                for(int kk=start; kk<=stop; kk++) {
                    int k = convertBackIndex(kk, zbins);
                    double meanXB   = newMeanXs[binx][biny];
                    double rmsXB    = newRmsXs[binx][biny];
                    double meanYB   = newMeanYs[binx][biny];
                    double rmsYB    = newRmsYs[binx][biny];
                    double heightB = newHeights[binx][biny];
                    if (h1Aida) {
                        rmsx = (h.xAxis().binUpperEdge(i)-h.xAxis().binLowerEdge(i))/Math.sqrt(12);
                        rmsy = (h.yAxis().binUpperEdge(j)-h.yAxis().binLowerEdge(j))/Math.sqrt(12);
                    } else {
                        rmsx = ((Histogram3D) h).binRmsX(i,j,k);
                        rmsy = ((Histogram3D) h).binRmsY(i,j,k);
                    }
                    if ( heightB+h.binHeight(i,j,k) != 0 ) {
                        newMeanXs[binx][biny]   = addMean(meanXB,heightB,h.binMeanX(i,j,k),h.binHeight(i,j,k));
                        newRmsXs[binx][biny]    = addRms(rmsXB,meanXB,heightB,rmsx,h.binMeanX(i,j,k),h.binHeight(i,j,k));
                        newMeanYs[binx][biny]   = addMean(meanYB,heightB,h.binMeanY(i,j,k),h.binHeight(i,j,k));
                        newRmsYs[binx][biny]    = addRms(rmsYB,meanYB,heightB,rmsy,h.binMeanY(i,j,k),h.binHeight(i,j,k));
                    }
                    newHeights[binx][biny] += h.binHeight(i,j,k);
                    newErrors [binx][biny] += h.binError(i,j,k)*h.binError(i,j,k);
                    newEntries[binx][biny] += h.binEntries(i,j,k);
                }
                newErrors [binx][biny] = Math.sqrt( newErrors [binx][biny] );
            }
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
        return hist;
    }
    
    IHistogram2D sliceYZ(IHistogram3D h, String name, int start, int stop) {
        int xbins = h.xAxis().bins();
        start = convertIndex(start, xbins);
        stop  = convertIndex(stop, xbins);
        if ( start > stop ) throw new IllegalArgumentException("Invalid indexes "+start+" "+stop);
        Histogram2D hist = new Histogram2D(name, name, copy( h.yAxis() ), copy( h.zAxis() ));
        
        int zbins = h.zAxis().bins()+2;
        int ybins = h.yAxis().bins()+2;
        double[][] newHeights = new double[ybins][zbins];
        double[][] newErrors  = new double[ybins][zbins];
        double[][] newMeanYs  = new double[ybins][zbins];
        double[][] newRmsYs   = new double[ybins][zbins];
        double[][] newMeanZs  = new double[ybins][zbins];
        double[][] newRmsZs   = new double[ybins][zbins];
        int[][]    newEntries = new    int[ybins][zbins];
        double rmsy = 0;
        double rmsz = 0;
        boolean h1Aida = !(h instanceof Histogram3D);
        
        for(int j=IAxis.UNDERFLOW_BIN; j<h.yAxis().bins(); j++) {
            for(int k=IAxis.UNDERFLOW_BIN; k<h.zAxis().bins(); k++) {
                int biny = hist.mapBinNumber(j,h.yAxis());
                int binz = hist.mapBinNumber(k,h.zAxis());
                for(int ii=start; ii<=stop; ii++) {
                    int i = convertBackIndex(ii, xbins);
                    double meanYB   = newMeanYs[biny][binz];
                    double rmsYB    = newRmsYs[biny][binz];
                    double meanZB   = newMeanZs[biny][binz];
                    double rmsZB    = newRmsZs[biny][binz];
                    double heightB = newHeights[biny][binz];
                    if (h1Aida) {
                        rmsy = (h.yAxis().binUpperEdge(i)-h.yAxis().binLowerEdge(i))/Math.sqrt(12);
                        rmsz = (h.zAxis().binUpperEdge(j)-h.zAxis().binLowerEdge(j))/Math.sqrt(12);
                    } else {
                        rmsy = ((Histogram3D) h).binRmsY(i,j,k);
                        rmsz = ((Histogram3D) h).binRmsZ(i,j,k);
                    }
                    if ( heightB+h.binHeight(i,j,k) != 0 ) {
                        newMeanYs[biny][binz]   = addMean(meanYB,heightB,h.binMeanY(i,j,k),h.binHeight(i,j,k));
                        newRmsYs[biny][binz]    = addRms(rmsYB,meanYB,heightB,rmsy,h.binMeanY(i,j,k),h.binHeight(i,j,k));
                        newMeanZs[biny][binz]   = addMean(meanZB,heightB,h.binMeanZ(i,j,k),h.binHeight(i,j,k));
                        newRmsZs[biny][binz]    = addRms(rmsZB,meanZB,heightB,rmsz,h.binMeanZ(i,j,k),h.binHeight(i,j,k));
                    }
                    newHeights[biny][binz] += h.binHeight(i,j,k);
                    newErrors [biny][binz] += h.binError(i,j,k)*h.binError(i,j,k);
                    newEntries[biny][binz] += h.binEntries(i,j,k);
                }
                newErrors [biny][binz] = Math.sqrt(newErrors [biny][binz]);
            }
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeanYs,newRmsYs,newMeanZs,newRmsZs);
        return hist;
    }
    
    IHistogram2D sliceXZ(IHistogram3D h, String name, int start, int stop) {
        int ybins = h.yAxis().bins();
        start = convertIndex(start, ybins);
        stop  = convertIndex(stop, ybins);
        if ( start > stop ) throw new IllegalArgumentException("Invalid indexes "+start+" "+stop);
        Histogram2D hist = new Histogram2D(name, name, copy( h.xAxis() ), copy( h.zAxis() ));
        int xbins = h.xAxis().bins()+2;
        int zbins = h.zAxis().bins()+2;
        double[][] newHeights = new double[xbins][zbins];
        double[][]  newErrors = new double[xbins][zbins];
        double[][] newMeanXs  = new double[xbins][zbins];
        double[][] newRmsXs   = new double[xbins][zbins];
        double[][] newMeanZs  = new double[xbins][zbins];
        double[][] newRmsZs   = new double[xbins][zbins];
        int[][]    newEntries = new    int[xbins][zbins];
        double rmsx = 0;
        double rmsz = 0;
        boolean h1Aida = !(h instanceof Histogram3D);
        
        for(int i=IAxis.UNDERFLOW_BIN; i<h.xAxis().bins(); i++) {
            for(int k=IAxis.UNDERFLOW_BIN; k<h.zAxis().bins(); k++) {
                int binx = hist.mapBinNumber(i,h.xAxis());
                int binz = hist.mapBinNumber(k,h.zAxis());
                for(int jj=start; jj<=stop; jj++) {
                    int j = convertBackIndex(jj, ybins);
                    double meanXB   = newMeanXs[binx][binz];
                    double rmsXB    = newRmsXs[binx][binz];
                    double meanZB   = newMeanZs[binx][binz];
                    double rmsZB    = newRmsZs[binx][binz];
                    double heightB = newHeights[binx][binz];
                    if (h1Aida) {
                        rmsx = (h.xAxis().binUpperEdge(i)-h.xAxis().binLowerEdge(i))/Math.sqrt(12);
                        rmsz = (h.zAxis().binUpperEdge(j)-h.zAxis().binLowerEdge(j))/Math.sqrt(12);
                    } else {
                        rmsx = ((Histogram3D) h).binRmsX(i,j,k);
                        rmsz = ((Histogram3D) h).binRmsZ(i,j,k);
                    }
                    if ( heightB+h.binHeight(i,j,k) != 0 ) {
                        newMeanXs[binx][binz]   = addMean(meanXB,heightB,h.binMeanX(i,j,k),h.binHeight(i,j,k));
                        newRmsXs[binx][binz]    = addRms(rmsXB,meanXB,heightB,rmsx,h.binMeanX(i,j,k),h.binHeight(i,j,k));
                        newMeanZs[binx][binz]   = addMean(meanZB,heightB,h.binMeanZ(i,j,k),h.binHeight(i,j,k));
                        newRmsZs[binx][binz]    = addRms(rmsZB,meanZB,heightB,rmsz,h.binMeanZ(i,j,k),h.binHeight(i,j,k));
                    }
                    newHeights[binx][binz] += h.binHeight(i,j,k);
                    newErrors [binx][binz] += h.binError(i,j,k)*h.binError(i,j,k);
                    newEntries[binx][binz] += h.binEntries(i,j,k);
                }
                newErrors [binx][binz] = Math.sqrt(newErrors [binx][binz]);
            }
        }
        hist.setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanZs,newRmsZs);
        return hist;
    }
    
    public static void main(String[] argv) {
        IAnalysisFactory af = IAnalysisFactory.create();
        IHistogramFactory hf = af.createHistogramFactory(af.createTreeFactory().create());
        
        IHistogram1D h1 = hf.createHistogram1D("test 1d",50,-3,6);
        IHistogram1D h2 = hf.createHistogram1D("test 2d",50,-3,6);
        
        java.util.Random r = new java.util.Random();
        for (int i=0; i<10000; i++) {
            h1.fill(r.nextGaussian());
            h2.fill(3+r.nextGaussian());
        }
        IHistogram1D plus = hf.add("h1+h2",h1,h2);
        IHistogram1D minus = hf.subtract("h1-h2",h1,h2);
        IHistogram1D mul = hf.multiply("h1*h2",h1,h2);
        IHistogram1D div = hf.divide("h1 over h2",h1,h2);
        
      /*
      IPlotter plotter = af.createPlotterFactory().create("Plot");
      plotter.createRegions(2,2,0);
      plotter.region(0).plot(plus);
      plotter.region(1).plot(minus);
      plotter.region(2).plot(mul);
      plotter.region(3).plot(div);
      plotter.show();
       */
    }
    
    /**
     * Convert indexes from the AIDA bins convention: Underflow = -2,
     * Overflow = -1, bins between 0 and nBins-1
     * bins being between 0 and nBins+1 where 0 is the underflow and nBins+1 is the overflow
     *
     */
    private int convertIndex( int index, int nBins )  {
        if (index >= 0 && index < nBins) return index+1;
        if (index == IAxis.UNDERFLOW_BIN) return 0;
        if (index == IAxis.OVERFLOW_BIN) return nBins+1;
        throw new IllegalArgumentException("Illegal argument "+index);
    }
    
    //Opposite conversion
    private int convertBackIndex( int index, int nBins )  {
        if ( index == 0 ) return IAxis.UNDERFLOW_BIN;
        if ( index == nBins+1 ) return IAxis.OVERFLOW_BIN;
        return index-1;
    }
    
}
