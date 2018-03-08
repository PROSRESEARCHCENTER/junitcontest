package hep.aida.ref.pdf;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public abstract class FunctionDerivative {
    
    /**
     * Computes the derivative of a function with respect to one argument.
     * Adapted from Numerical Recipes in FORTRAN dfridr, pp. 182-183.
     * @param f a JAIDA IFunction
     * @param x a double[] with the point where the derivative is evaluated
     * @param ind the index of the argument of f we differentiate wrto
     * @param h a typical step size for x[ind]
     * @return the value of df/dx[ind] at x[]
     */
    public static double derivative(Function f, Variable x, double h) {
        final double BIG  = Double.MAX_VALUE;
        final double CON  = 1.4;
        final double CON2 = CON*CON;
        final double SAFE = 2.;
        
        final int NTAB = 10;
        double[][] a = new double[NTAB][NTAB];
        
        double res = Double.NaN;
        double err = BIG;
                
        a[0][0] = evaluateMatrixElement(f,x,h);

        for (int i=1; i<NTAB; i++) {
            h /= CON;
            a[0][i] = evaluateMatrixElement(f,x,h);
            double fac = CON2;
            for (int j=1; j<=i; j++) {
                a[j][i] = (a[j-1][i]*fac - a[j-1][i-1]) / (fac - 1.);
                fac *= CON2;
                double errt = Math.max( Math.abs(a[j][i]-a[j-1][i]  ),
                Math.abs(a[j][i]-a[j-1][i-1]) );
                if (errt <= err) {
                    err = errt;
                    res = a[j][i];
                }
            }
            if (Math.abs(a[i][i]-a[i-1][i-1]) >= SAFE*err) {
                return res;
            }
        }
        return res;
    }
    
    private static double evaluateMatrixElement(Function f, Variable x, double increment) {
        double xVal = x.value();
        
        double result = 0;
        
        double xPlus = xVal + increment;
        double xMinus = xVal - increment;
        
        x.setValue(xPlus);
        result += f.value();
        
        x.setValue(xMinus);
        result -= f.value();

        result /= 2*increment;
        
        // Set the dependents back to its original value
        x.setValue(xVal);
        return result;
    }
    
    
}
