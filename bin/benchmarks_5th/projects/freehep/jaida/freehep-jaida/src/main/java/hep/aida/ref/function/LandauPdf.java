package hep.aida.ref.function;


/**
 * <p>
 * Landau Probability Distribution
 * <p>
 * Copied from math/mathcore/src/ProbFuncMathCore.cxx::landau_pdf in ROOT (version 5.34.18).
 */
public class LandauPdf {

    static final double[] p1 = { 0.4259894875, -0.1249762550, 0.03984243700, -0.006298287635, 0.001511162253 };
    static final double[] q1 = { 1.0, -0.3388260629, 0.09594393323, -0.01608042283, 0.003778942063 };

    static final double[] p2 = { 0.1788541609, 0.1173957403, 0.01488850518, -0.001394989411, 0.0001283617211 };
    static final double[] q2 = { 1.0, 0.7428795082, 0.3153932961, 0.06694219548, 0.008790609714 };

    static final double[] p3 = { 0.1788544503, 0.09359161662, 0.006325387654, 0.00006611667319, -0.000002031049101 };
    static final double[] q3 = { 1.0, 0.6097809921, 0.2560616665, 0.04746722384, 0.006957301675 };

    static final double[] p4 = { 0.9874054407, 118.6723273, 849.2794360, -743.7792444, 427.0262186 };
    static final double[] q4 = { 1.0, 106.8615961, 337.6496214, 2016.712389, 1597.063511 };

    static final double[] p5 = { 1.003675074, 167.5702434, 4789.711289, 21217.86767, -22324.94910 };
    static final double[] q5 = { 1.0, 156.9424537, 3745.310488, 9834.698876, 66924.28357 };

    static final double[] p6 = { 1.000827619, 664.9143136, 62972.92665, 475554.6998, -5743609.109 };
    static final double[] q6 = { 1.0, 651.4101098, 56974.73333, 165917.4725, -2815759.939 };

    static final double[] a1 = { 0.04166666667, -0.01996527778, 0.02709538966 };
    static final double[] a2 = { -1.845568670, -4.284640743 };
    
    protected double sigma = 0;
    protected double mean = 0;
    
    public LandauPdf(double mean, double sigma) {
        this.mean = mean;
        this.sigma = sigma;
    }
    
    public LandauPdf() {
    }
    
    public double getValue(double x) {
        return getValue(x, mean, sigma);
    }
    
    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
    
    public void setMean(double mean) {
        this.mean = mean;
    }
                 
    /**
     * 
     * @param x The free variable.
     * @param sigma The sigma (width) of the distribution.
     * @param mean The mean of the distribution.
     * @return The value of the Landau distribution at x.
     */
    static double getValue(double x, double mean, double sigma) {
        // LANDAU pdf : algorithm from CERNLIB G110 denlan same algorithm is used in GSL
        if (sigma <= 0)
            return 0;
        double v = (x - mean) / sigma;
        double u, ue, us, denlan;
        if (v < -5.5) {
            u = Math.exp(v + 1.0);
            if (u < 1e-10)
                return 0.0;
            ue = Math.exp(-1 / u);
            us = Math.sqrt(u);
            denlan = 0.3989422803 * (ue / us) * (1 + (a1[0] + (a1[1] + a1[2] * u) * u) * u);
        } else if (v < -1) {
            u = Math.exp(-v - 1);
            denlan = Math.exp(-u) * Math.sqrt(u) * (p1[0] + (p1[1] + (p1[2] + (p1[3] + p1[4] * v) * v) * v) * v) / (q1[0] + (q1[1] + (q1[2] + (q1[3] + q1[4] * v) * v) * v) * v);
        } else if (v < 1) {
            denlan = (p2[0] + (p2[1] + (p2[2] + (p2[3] + p2[4] * v) * v) * v) * v) / (q2[0] + (q2[1] + (q2[2] + (q2[3] + q2[4] * v) * v) * v) * v);
        } else if (v < 5) {
            denlan = (p3[0] + (p3[1] + (p3[2] + (p3[3] + p3[4] * v) * v) * v) * v) / (q3[0] + (q3[1] + (q3[2] + (q3[3] + q3[4] * v) * v) * v) * v);
        } else if (v < 12) {
            u = 1 / v;
            denlan = u * u * (p4[0] + (p4[1] + (p4[2] + (p4[3] + p4[4] * u) * u) * u) * u) / (q4[0] + (q4[1] + (q4[2] + (q4[3] + q4[4] * u) * u) * u) * u);
        } else if (v < 50) {
            u = 1 / v;
            denlan = u * u * (p5[0] + (p5[1] + (p5[2] + (p5[3] + p5[4] * u) * u) * u) * u) / (q5[0] + (q5[1] + (q5[2] + (q5[3] + q5[4] * u) * u) * u) * u);
        } else if (v < 300) {
            u = 1 / v;
            denlan = u * u * (p6[0] + (p6[1] + (p6[2] + (p6[3] + p6[4] * u) * u) * u) * u) / (q6[0] + (q6[1] + (q6[2] + (q6[3] + q6[4] * u) * u) * u) * u);
        } else {
            u = 1 / (v - v * Math.log(v) / (v + 1));
            denlan = u * u * (1 + (a2[0] + a2[1] * u) * u);
        }
        return denlan / sigma;
    }
}
