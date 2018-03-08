/*
 * NAME
 *	$RCSfile$ - 
 * DESCRIPTION
 *	[given below in javadoc format]
 * DELTA
 *	$Revision: 11553 $
 * CREATED
 *	$Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $ by birgit
 * COPYRIGHT
 *	West Consulting bv
 * TO DO
 *	
 */

package tablelayout;
import java.util.StringTokenizer;


// Parse Layout String
//=======================
// Parse a layout string, allocating and setting the values. 
// 
// A layout is a string of a location specification.
// Each location specification has the form:
// 
// 	column  row  col_span  row_span  opt_list
// 
// where the meaning of each field is:
// 
// 	column		Integer >= 0 descibing column in array
// 	row		Row >= 0 describing row in array
// 
// optional:
// 	col_span	Integer >= 1 describing horizontal widget span
// 	row_span	Integer >= 1 describing vertical widget span
// 	opt_list	Series of characters each representing an option:
// 				l:  TBL_LEFT
// 				r:  TBL_RIGHT
// 				t:  TBL_TOP
// 				b:  TBL_BOTTOM
// 				w:  TBL_LK_WIDTH
// 				h:  TBL_LK_HEIGHT
// 				W:  TBL_SM_WIDTH
// 				H:  TBL_SM_HEIGHT
// 
// The options are interpreted in the TableOpts() method.
//


/**
 * This class parses the string which specifies the layout for a component.
 * 
 * The layout string specifies the column, row, column span, row span,
 * and options for each component. The options field is optional.
 * The options are parsed by the TableOpts
 * class.
 * 
 * Components which are not named in the layout specification are
 * positioned
 * in column 0, row 0, with colum and row spans of 1, all the options will
 * be
 * <em>false</em>. If the layout is 
 * changed after the TableLocRec is created, then a complete
 * re-layout is performed.
 * 
 * Each layout specification is of the form:
 * <pre>
 * name col row [opts]
 * </pre>
 *
 * @see tablelayout.TableLayout
 * @see tablelayout.TableOpts
 * @author Birgit Arkesteijn
 * @version $Revision: 11553 $ $Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $
 */

public class TableLocRec extends Object 
{
  private static final String     version_id =
      "@(#)$Id: TableLocRec.java 11553 2007-06-05 22:06:23Z duns $ Copyright West Consulting bv";

  /**
   * Position of column in table (>=0)
   */
  public int		col; 

  /**
   * Position of row in table (>=0)
   */
  public int		row; 

  /**
   * Horizontal widget span (>=1)
   */
  public int		col_span; 

  /**
   * Vertical widget span (>=1)
   */
  public int		row_span;

  /**
   * Original width of the cell
   */
  public int		orig_width; 

  /**
   * Original heigth of the cell
   */
  public int		orig_height;

  /**
   * The width of the cell, changed because of sameWidth constraint
   */
  public int		same_width; 

  /**
   * The height of the cell, changed because of sameHeight constraint
   */
  public int		same_height;

  /**
   * Justify, grow and shrink constraint options
   *
   * @see tablelayout.TableOpts
   */
  public TableOpts	options;

  static final String CHAR_BLANC      = " ";

  /**
   *
   */

  public TableLocRec()
  {
    col = 0;
    row = 0; 
    col_span = 1; 
    row_span = 1;
    orig_width  = 0;
    orig_height  = 0;
    same_width  = 0;
    same_height  = 0;
    options = new TableOpts();
  }

  /**
   * @param layout the string that specifies the layout
   */

  public TableLocRec(String layout)
  {
    String next;
    orig_width  = 0;
    orig_height  = 0;
    same_width  = 0;
    same_height  = 0;

    StringTokenizer st = new StringTokenizer(layout, CHAR_BLANC, false);

    next = st.nextToken();
    col = Integer.valueOf(next).intValue();

    next = st.nextToken();
    row = Integer.valueOf(next).intValue();

    // is there a next token?
    try 
    {
      next = st.nextToken();
    }
    catch (Exception e) 
    {
      // No next token
      col_span = 1;
      row_span = 1;
      options = new TableOpts ();
      return;
    }

    // is the token a number (col_span) or not (option)
    try
    {
      col_span = Integer.valueOf(next).intValue();
      if (col_span < 1) col_span = 1;
    } 
    catch (NumberFormatException e) 
    {
      col_span = 1;
      row_span = 1;
      options = new TableOpts (next);
      return;
    }

    // is there a next token?
    try 
    {
      next = st.nextToken();
    }
    catch (Exception e) 
    {
      // No next token
      row_span = 1;
      options = new TableOpts ();
      return;
    }

    // is the token a number (row_span) or not (option)
    try
    {
      row_span = Integer.valueOf(next).intValue();
      if (row_span < 1) row_span = 1;
    } 
    catch (NumberFormatException e) 
    {
      row_span = 1;
      options = new TableOpts (next);
      return;
    }

    try 
    {
      next = st.nextToken();
      options = new TableOpts (next);
    } 
    catch (Exception e) 
    {
      options = new TableOpts ();
    }
  }

  /**
   * Creates a clone of the object. A new instance is allocated and all
   * the variables of the class are cloned
   */

  public Object clone()
  {
    TableLocRec elem = new TableLocRec();
    elem.options 	= options;
    elem.col 		= col;
    elem.row 		= row;
    elem.col_span 	= col_span;
    elem.row_span	= row_span;
    elem.orig_width 	= orig_width;
    elem.orig_height 	= orig_height;
    elem.same_width 	= same_width;
    elem.same_height 	= same_height;
    return ((Object) elem);
  }

  /**
   * Returns the String representation
   */

  public String toString()
  {
    return (
	"TableLocRec [" +
    	"\n\tcol " + col +
	" row " + row +
	" col_span " + col_span +
	" row_span " + row_span +
	"\n\torig_width " + orig_width +
	" orig_height " + orig_height +
	" same_width " + same_width +
	" same_height " + same_height +
	"\n\toptions " + options.toString() +
	"]");
  }


  // Used by qsort when the layout table is sorted by span 
  // before doing distribution of space to rows or columns.
  //
  /**
   * Compaires the span width of two columns, returns the difference in
   * span width
   *
   * @param loc1 the first column
   * @param loc2 the second column
   */

  public static int compareColSpan(TableLocRec loc1, TableLocRec loc2)
  {
      if (loc1.col_span == loc2.col_span)
	  return loc1.col - loc2.col;

      return loc1.col_span - loc2.col_span;
  }

  /**
   * Compaires the span height of two rows, returns the difference in
   * span height
   *
   * @param loc1 the first row
   * @param loc2 the second row
   */

  public static int compareRowSpan (TableLocRec loc1, TableLocRec loc2)
  {
      if (loc1.row_span == loc2.row_span) 
        return loc1.row - loc2.row;

      return loc1.row_span - loc2.row_span;
  }

  /**
   * Returns the preferred width of the layout component
   */

  public int preferredWidth ()
  {
    // First take care of situations where SameSize resources apply
    if (same_width != 0)
    {
      return same_width;
    }
    else 
    {
      return orig_width;
    }
  }

  /**
   * Returns the preferred heigth of the layout component
   */

  public int preferredHeight ()
  {
    if (same_height != 0)
    {
      return same_height;
    }
    else 
    {
      return orig_height;
    }
  }

}
