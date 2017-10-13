/*
 * NAME
 *	$RCSfile$ - 
 * DESCRIPTION
 *	[given below in javadoc format]
 * DELTA
 *	$Revision: 11550 $
 * CREATED
 *	$Date: 2007-06-05 23:44:14 +0200 (Tue, 05 Jun 2007) $ by birgit
 * COPYRIGHT
 *	West Consulting bv
 * TO DO
 *	
 */

package tablelayout;

/**
 * Contains information of the size of one table cell
 *
 * @see tablelayout.TableLayout
 * @author Birgit Arkesteijn
 * @version $Revision: 11550 $ $Date: 2007-06-05 23:44:14 +0200 (Tue, 05 Jun 2007) $
 */
public class TableVecRec extends Object
{
  private static final String     version_id =
      "@(#)$Id: TableVecRec.java 11550 2007-06-05 21:44:14Z duns $ Copyright West Consulting bv";

  /**
   * the preferred value of the cell
   */
  public int	pref_value;	

  /**
   * the upper left corner of the cell
   */
  public int	offset;		

  /**
   * the options that should be applied to the entire column or row
   */
  public int  	options;	

  /**
   * the width of column, or the height of the row
   */
  public int	value;		

  /**
   *
   */

  public TableVecRec ()
  {
    pref_value = 0;
    offset     = 0;
    options    = 0;
    value      = 0;
  }

  /**
   * Returns the String representation
   */
  public String toString()
  {
    return (
	"TableVecRec [" +
    	"options " 	+ options +
	" value " 	+ value +
	" pref_value " 	+ pref_value +
	" offset " 	+ offset +
	"]");
  }
}
