/*
 * BinnerMath.java
 *
 * Created on March 5, 2007, 10:49 AM
 *
 */

package hep.aida.ref.histogram.binner;

/**
 *
 * @author serbo
 */
public class BinnerMath {
    
    public static void add(Binner1D newBinner, Binner1D b1, Binner1D b2) throws IllegalArgumentException{
        checkCompatibility(b1, b2);
        checkCompatibility(newBinner, b2);
        
        for (int i=0; i<b1.bins(); i++) {
            int e = b1.entries(i) + b2.entries(i);
            double h = b1.height(i) + b2.height(i);
            double sumWW  = b1.sumWW(i) + b2.sumWW(i);
            double sumXW  = b1.sumXW(i) + b2.sumXW(i);
            double sumXXW = b1.sumXXW(i) +b2.sumXXW(i);
            double d = b1.binCenter(i) - b2.binCenter(i);
            if (d != 0) {
                sumXXW -= d*(2*b2.sumXW(i) - d*b2.height(i));
                sumXW  -= d*b2.height(i);
            }
            double ep = Math.sqrt(b1.plusError(i)*b1.plusError(i) + b2.plusError(i)*b2.plusError(i));
            double em = Math.sqrt(b1.minusError(i)*b1.plusError(i) + b2.minusError(i)*b2.plusError(i));
            newBinner.setBinContent(i, b1.binCenter(i), e, h, ep, em, sumWW, sumXW, sumXXW);
        }
    }
    
    // Assume uncorrelated statistics: sumWW = sumWW1 + sumWW2
    public static void sub(Binner1D newBinner, Binner1D b1, Binner1D b2) throws IllegalArgumentException{
        checkCompatibility(b1, b2);
        checkCompatibility(newBinner, b2);
        
        for (int i=0; i<b1.bins(); i++) {
            int e = b1.entries(i) - b2.entries(i);
            double h = b1.height(i) - b2.height(i);
            double sumWW  = b1.sumWW(i) + b2.sumWW(i);
            double sumXW  = b1.sumXW(i) - b2.sumXW(i);
            double sumXXW = b1.sumXXW(i) - b2.sumXXW(i);
            double d = b1.binCenter(i) - b2.binCenter(i);
            if (d != 0) {
                sumXXW += d*(2*b2.sumXW(i) - d*b2.height(i));
                sumXW  += d*b2.height(i);
            }
            double ep = Math.sqrt(b1.plusError(i)*b1.plusError(i) + b2.plusError(i)*b2.plusError(i));
            double em = Math.sqrt(b1.minusError(i)*b1.plusError(i) + b2.minusError(i)*b2.plusError(i));
            newBinner.setBinContent(i, b1.binCenter(i), e, h, ep, em, sumWW, sumXW, sumXXW);
        }
    }
    
    
    // Treat multiplication as bin-dependent scaling
    // Scaling factor has an error
    public static void mul(Binner1D newBinner, Binner1D b1, Binner1D b2) throws IllegalArgumentException{
        checkCompatibility(b1, b2);
        checkCompatibility(newBinner, b2);
        
        for (int i=0; i<b1.bins(); i++) {
            int e = b1.entries(i);
            double h = b1.height(i)*b2.height(i);
            double sumXW  = b1.sumXW(i)*b2.height(i);
            double sumXXW = b1.sumXXW(i)*b2.height(i);
            //double sumWW  = b1.sumWW(i)*b2.height(i)*b2.height(i);
            double sumWW  = sumWW_Mul(b1.height(i), b1.sumWW(i), b2.height(i), b2.sumWW(i));
            double ep = errorMul(b1.plusError(i), b1.height(i), b2.plusError(i), b2.height(i));
            double em = errorMul(b1.minusError(i) ,b1.height(i), b2.minusError(i), b2.height(i));
            newBinner.setBinContent(i, b1.binCenter(i), e, h, ep, em, sumWW, sumXW, sumXXW);
        }
    }
    
    // Treat division as bin-dependent scaling
    // Scaling factor has an error
    public static void div(Binner1D newBinner, Binner1D b1, Binner1D b2) throws IllegalArgumentException{
        checkCompatibility(b1, b2);
        checkCompatibility(newBinner, b2);
        
        for (int i=0; i<b1.bins(); i++) {
            int e = b1.entries(i);
            double h = 0;
            double sumXW  = b1.sumXW(i);
            double sumXXW = b1.sumXXW(i);
            double sumWW  = Double.NaN;
            double ep = Double.NaN;
            double em = Double.NaN;
            
            if (b2.height(i) != 0) {
                h = b1.height(i)/b2.height(i);
                sumXW  = b1.sumXW(i)/b2.height(i);
                sumXXW = b1.sumXXW(i)/b2.height(i);
                sumWW  = sumWW_Div(b1.height(i), b1.sumWW(i), b2.height(i), b2.sumWW(i));
                ep = errorDiv(b1.plusError(i), b1.height(i), b2.plusError(i), b2.height(i));
                em = errorDiv(b1.minusError(i) ,b1.height(i), b2.minusError(i), b2.height(i));
            }
            newBinner.setBinContent(i, b1.binCenter(i), e, h, ep, em, sumWW, sumXW, sumXXW);
        }
    }
    
    
    public static boolean isValidDouble(double d) {
        if ( Double.isNaN(d) )
            return false;
        if ( Double.isInfinite(d) )
            return false;
        return true;
    }
    
    static void checkCompatibility(Binner1D binner1, Binner1D binner2) throws IllegalArgumentException {
        if (binner1.bins() != binner2.bins()) {
            String message = "Different number of bins: n1="+binner1.bins()+", n2="+binner2.bins();
            throw new IllegalArgumentException(message);
        }
    }
    
    private static double errorMul(double e1, double l1, double e2, double l2) {
        return Math.sqrt(Math.pow(e1*l2, 2) + Math.pow(l1*e2, 2));
    }
    private static double sumWW_Mul(double h1, double sumWW1, double h2, double sumWW2) {
        return sumWW1*h2*h2 + sumWW2*h1*h1;
    }
    private static double errorDiv(double e1, double l1, double e2, double l2) {
        return Math.sqrt(Math.pow(e1/l2, 2) + Math.pow(e2*l1/(l2*l2), 2));
    }
    private static double sumWW_Div(double h1, double sumWW1, double h2, double sumWW2) {
        return (sumWW1 +sumWW2*Math.pow(h1/h2, 2))/(h2*h2);
    }
    
}
