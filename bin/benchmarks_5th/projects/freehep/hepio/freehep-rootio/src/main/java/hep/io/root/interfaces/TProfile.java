/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Aug 18 12:41:52 PDT 2003
 */

package hep.io.root.interfaces;

public interface TProfile extends hep.io.root.RootObject, hep.io.root.interfaces.TH1D
{
	/** number of entries per bin */
	double[] getBinEntries();
	/** Option to compute errors */
	int getErrorMode();
	/** Lower limit in Y (if set) */
	double getYmin();
	/** Upper limit in Y (if set) */
	double getYmax();

	public final static int rootIOVersion=3;
	public final static int rootCheckSum=2078824377;
}
