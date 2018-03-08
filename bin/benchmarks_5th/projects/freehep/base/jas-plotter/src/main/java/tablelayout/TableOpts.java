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

// Parse Layout String
//=======================
// opt_list: Series of characters each representing an option:
// l:  left justify component in table cell
// r:  right justify component in table cell
// t:  top justify component in table cell
// b:  bottom justify component in table cell
// w:  do not stretch width of column in which component is
//	positioned during resizing. Column width will remain wide enough to
//	contain the widest component in the column
// h:  do not stretch height of row in which component is positioned
//	during resizing. Row height will emain high enough to contain the
//	tallest component in the row
// W:  do not stretch width of component during resizing. The width
//	of the column may grow (unless constrained by w), and the 
//	component will be justified according to its layout options
// H:  do not stretch height of component during resizing. The height
//	of the row may grow (unless constrained by h), and the 
//	component will be justified according to its layout options
// 
// The options are interpreted in the constructor.
//


/**
 * This class parses the options string which specifies the layout options
 * for an component.
 * 
 * The layout options allow a component to be justified within the table
 * cell, and allow the initial size and dynamic resizing of components to
 * be
 * constrained. Combinations of options can be used: <b>tr</b> means top
 * and right, or north-east justification.
 * 
 * Note that the justification does not do anything special with the labels
 * of a
 * Label components: it simply sets the positioning
 * of the entire component within the cell of the <b>TableLayout</b>.
 *
 * If no options are specified for a component, all options will be
 * <em>false</em>.
 * 
 *
 * @see tablelayout.TableLayout
 * @author Birgit Arkesteijn
 * @version $Revision: 11550 $ $Date: 2007-06-05 23:44:14 +0200 (Tue, 05 Jun 2007) $
 */

public class TableOpts extends Object 
{
  private static final String     version_id =
      "@(#)$Id: TableOpts.java 11550 2007-06-05 21:44:14Z duns $ Copyright West Consulting bv";

 /**
  * left justify component in table cell.
  */
  public boolean	l; 

 /**
  * right justify component in table cell.
  */
  public boolean	r; 

 /**
  * top justify component in table cell.
  */
  public boolean	t; 

 /**
  * bottom justify component in table cell.
  */
  public boolean	b; 

 /**
  * do not stretch width of column in which component is
  * positioned during resizing. Column width will remain wide enough to
  * contain the widest component in the column.
  */
  public boolean	w; 

 /**
  * do not stretch height of row in which component is
  * positioned
  * during resizing. Row height will emain high enough to contain the
  * tallest component in the row.
  */
  public boolean	h; 

 /**
  * do not stretch width of component during resizing. The
  * width
  * of the column may grow (unless constrained by <em>w</em>), and the
  * component
  * will be justified according to its layout options.
  */
  public boolean	W; 

 /**
  * do not stretch height of component during resizing. The
  * height
  * of the row may grow (unless constrained by <em>h</em>), and the
  * component
  * will be justified according to its layout options.
  * 
  */
  public boolean	H;


  /**
   *
   */

  public TableOpts()
  {
    l = false;
    r = false;
    t = false;
    b = false;
    w = false;
    h = false;
    W = false;
    H = false;
  }

  /**
   * @param layout the string that specifies the options
   */

  public TableOpts(String layout)
  {
    if (layout.indexOf ('l') > -1) l = true;
    else l = false;
    if (layout.indexOf ('r') > -1) r = true;
    else r = false;
    if (layout.indexOf ('t') > -1) t = true;
    else t = false;
    if (layout.indexOf ('b') > -1) b = true;
    else b = false;
    if (layout.indexOf ('w') > -1) w = true;
    else w = false;
    if (layout.indexOf ('h') > -1) h = true;
    else h = false;
    if (layout.indexOf ('W') > -1) W = true;
    else W = false;
    if (layout.indexOf ('H') > -1) H = true;
    else H = false;
  }

  /**
   * Creates a clone of the object. A new instance is allocated and all
   * the variables of the class are cloned
   */

  public Object clone()
  {
    TableOpts elem = new TableOpts();
    elem.r	= r;
    elem.l	= l;
    elem.b	= b;
    elem.t	= t;
    elem.w	= w;
    elem.h	= h;
    elem.W	= W;
    elem.H	= H;
    return ((Object) elem);
  }


  /**
   * Returns the String representation
   */

  public String toString()
  {
    return (
	"TableOpts [" +
	" r " + r +
	" l " + l +
	" b " + b +
	" t " + t +
	" w " + w +
	" h " + h +
	" W " + W +
	" H " + H +
	"]");
  }

}
