/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Tue May 29 18:40:48 PDT 2001
 */

package hep.io.root.interfaces;

public interface Moyennes extends RootObject, TObject
{
	/**  */
	int getSize();
	/**  */
	double[] getMoy();
	/**  */
	double[] getSig();
	/**  */
	int[] getNEntries();
	/**  */
	int getBid();

	public final static int rootIOVersion=1;
}
