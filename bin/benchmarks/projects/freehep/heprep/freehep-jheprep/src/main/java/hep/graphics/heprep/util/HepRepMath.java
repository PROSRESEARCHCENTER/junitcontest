package hep.graphics.heprep.util;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepMath.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepMath {

    // Static class, not to be instantiated
    private HepRepMath() {
    }

    /**
     * Calculates delta x
     * @param x x
     * @param xVertex x vertex
     * @return dx
     */
    public static double getX(double x, double xVertex) {
        return x-xVertex;
    }

    /**
     * Calculates delta y
     * @param y y
     * @param yVertex y vertex
     * @return dy
     */
    public static double getY(double y, double yVertex) {
        return y-yVertex;
    }

    /**
     * Calculates delta z
     * @param z z
     * @param zVertex z vertex
     * @return dz
     */
    public static double getZ(double z, double zVertex) {
        return z-zVertex;
    }

    /**
     * Calculates Rho sqrt(dx^2+dy^2)
     * @param x x
     * @param y y
     * @param xVertex x vertex
     * @param yVertex y vertex
     * @return rho
     */
    public static double getRho(double x, double y, double xVertex, double yVertex) {
        double dx = getX(x, xVertex);
        double dy = getY(y, yVertex);
        return Math.sqrt(dx*dx+dy*dy);
    }

    /**
     * Calculates Rho sqrt(x^2+y^2)
     * @param x x
     * @param y y
     * @return rho
     */
    public static double getRho(double x, double y) {
        return Math.sqrt(x*x+y*y);
    }

    /**
     * Calculates Phi atan2(dy/dx)
     * @param x x
     * @param y y 
     * @param xVertex x vertex
     * @param yVertex y vertex
     * @return phi
     */
    public static double getPhi(double x, double y, double xVertex, double yVertex) {
        return Math.atan2(getY(y, yVertex), getX(x, xVertex));
    }

    /**
     * Calculates Phi atan2(y/x)
     * @param x x
     * @param y y 
     * @return phi
     */
    public static double getPhi(double x, double y) {
        return Math.atan2(y,x);
    }

    /**
     * Return the dip angle of the track given the z-vertex position.
     * @param x dx
     * @param y dy
     * @param z dz
     * @param xVertex x vertex
     * @param yVertex y vertex
     * @param zVertex z vertex
     * @return theta angle in radians
     */
    public static double getTheta(double x, double y, double z, double xVertex, double yVertex, double zVertex) {
        return Math.atan2(getRho(getX(x, xVertex), getY(y, yVertex)), getZ(z, zVertex));
    }

    /**
     * Return the dip angle of the track assuming a zero z-vertex
     * position.
     * @param x dx
     * @param y dy
     * @param z z
     * @return theta angle in radians
     */
    public static double getTheta(double x, double y, double z) {
        return Math.atan2(getRho(x, y), z);
    }

    /**
     * Return the spherical radius to the point given the z-vertex
     * position.
     * @param x dx
     * @param y dy
     * @param z dz
     * @param xVertex x vertex
     * @param yVertex y vertex
     * @param zVertex z vertex
     * @return radius
     */
    public static double getR(double x, double y, double z, double xVertex, double yVertex, double zVertex) {
        double rho = getRho(getX(x, xVertex), getY(y, yVertex));
        double dz = getZ(z, zVertex);
        return Math.sqrt(rho*rho+dz*dz);
    }

    /**
     * Return the spherical radius to the point assuming a z-vertex
     * position.
     * @param x dx
     * @param y dy
     * @param z z
     * @return radius
     */
    public static double getR(double x, double y, double z) {
        double rho = getRho(x, y);
        return Math.sqrt(rho*rho+z*z);
    }

    /**
     * Return the pseudorapidity of the point given the z-vertex
     * position.
     * @param x dx
     * @param y dy
     * @param z dz
     * @param xVertex x vertex
     * @param yVertex y vertex
     * @param zVertex z vertex
     * @return eta
     */
    public static double getEta(double x, double y, double z, double xVertex, double yVertex, double zVertex) {
        double ct = Math.cos(getTheta(x, y, z, xVertex, yVertex, zVertex));
        return -0.5*Math.log((1.-ct)/(1.+ct));
    }

    /**
     * Return the pseudorapidity of the point assuming zero z-vertex
     * position.
     * @param x dx
     * @param y dy
     * @param z z
     * @return eta
     */
    public static double getEta(double x, double y, double z) {
        double ct = Math.cos(getTheta(x, y, z));
        return -0.5*Math.log((1.-ct)/(1.+ct));
    }

}