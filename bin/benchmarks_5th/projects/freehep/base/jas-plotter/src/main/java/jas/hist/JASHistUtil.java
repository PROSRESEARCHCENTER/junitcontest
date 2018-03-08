package jas.hist;

public class JASHistUtil {
 
        
    /**
     * Round number down (closer to Negative Infinity):
     * "order" defines which significant digit is rounded, order >= 0
     *
     * roundDown(234.5, 0) -> 200.0
     * roundDown(234.5, 1) -> 230.0
     * roundDown(234.5, 2) -> 234.0
     *
     */
    public static double roundDown(double x, int order) {
        if (Double.isNaN(x) || Double.isInfinite(x) || x == Double.MIN_VALUE) return x;
        else if (x < 0) {
	    return (-1.)*roundUp(Math.abs(x), order);
	} else if (x == 0) return x;

	double mant = Math.floor(Math.log(x)/Math.log(10.));
	double factor = Math.pow(10., (order-mant));
	double tmp = Math.floor(x*factor)/factor;
	return tmp;  
    }

     /**
     * Round number up (closer to Positive Infinity), 
     * "order" defines which significant digit is rounded, order >= 0
     *
     * roundUp(234.5, 0) -> 300.0
     * roundUp(234.5, 1) -> 240.0
     * roundUp(234.5, 2) -> 235.0
     *
     */
    public static double roundUp(double x, int order) {
        if (Double.isNaN(x) || Double.isInfinite(x) || x == Double.MAX_VALUE) return x;
        else if (x < 0) {
	    return (-1.)*roundDown(Math.abs(x), order);
	} else if (x == 0) return x;
        
	double mant = Math.floor(Math.log(x)/Math.log(10.));
	double factor = Math.pow(10., (order-mant));
	double tmp = Math.ceil(x*factor)/factor;
	return tmp; 
    }
    
}
