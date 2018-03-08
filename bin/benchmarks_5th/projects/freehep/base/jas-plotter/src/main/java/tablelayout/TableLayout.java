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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.util.Vector;

// Edit History
//
// 25Oct92		david	Geometry management re-work
// 01Feb92		david	Re-Implementation


/**
 * TableLayout is a layout manager which allows components to be
 * arranged
 * in a tabular form. The TableLayout component has a layout
 * resource which is used to specify the column and row position of each
 * component. Components can span rows and/or columns.  Layout options
 * are available to control the initial sizes, justification, and dynamic
 * resizing
 *
 * @author Birgit Arkesteijn
 * @version $Revision: 11553 $ $Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $
 */

public class TableLayout extends Object implements LayoutManager
{
  private static final String     version_id =
      "@(#)$Id: TableLayout.java 11553 2007-06-05 22:06:23Z duns $ Copyright West Consulting bv";

  boolean	force_shrink;		// Shrink smaller than pref'd	
  int		col_spacing;		// Space between columns	
  int		row_spacing;		// Space between rows		
  TableLoc  	layout;			// Layout spec 

  // Lists of lists of components whose preferred dimensions
  // should be considered to be the maximum of any individual named on the
  // lists.  

  // Vector of Vector of Component
  Vector	same_width;		// kids with same width		
  Vector	same_height;		// kids with same height	
  int		margin_width;
  int		margin_height;

  // internally computed members
  TableCol	cols;			// Widths and opts of each col	
  TableRow	rows;			// Heights and opts of each row	

  /**
   *
   */

  public TableLayout()
  {
    layout = new TableLoc();

    force_shrink = true;
    col_spacing = 1;
    row_spacing = 1;
    same_width = new Vector();
    same_height = new Vector();
    margin_width = 0;
    margin_height = 0;
    cols = (TableCol)null;
    rows = (TableRow)null;
  }

  /**
   * Adds the specified component with the layout string to the layout
   *
   * @param layout represents the wanted layout of the component
   * @param comp the component to be added
   * @see tablelayout.TableLocRec
   */

  public void addLayoutComponent(String name, Component comp)
  {
    try
    {
      TableLocRec rec = new TableLocRec(name);
      layout.addElement(rec, comp);
    }
    catch(Exception e)
    {
      System.out.println("TableLayout: Syntax error in component: "
      		+ name);
      return;
    }
  }

  /**
   * Removes the specified component from the layout
   *
   * @param comp the component to be removed
   */

  public void removeLayoutComponent(Component comp)
  {
    layout.removeElement(comp);
  }

  /**
   * Calculates the preferred size dimensions for the specified panel,
   * given the components in the specified target container
   *
   * @param target the component to be laid out
   */

  public Dimension preferredLayoutSize(Container target)
  {
    int	nmembers = target.getComponentCount();
    for (int i=0; i<nmembers; i++)
    {
      Component current = target.getComponent(i);
	  if (!current.isVisible()) continue;
	  
      TableLocRec rec = layout.element(current);
      if (rec == null) continue;

      rec.orig_width = current.getPreferredSize().width;
      rec.orig_height = current.getPreferredSize().height;
      rec.same_width = 0;
      rec.same_height = 0;

    }
    considerSameWidth();
    considerSameHeight();

    cols = new TableCol(this);
    rows = new TableRow(this);

    int width = cols.getPreferredSize();
    int height = rows.getPreferredSize();
	
	// Is it the layout managers job to allow for the insets?
	
	Insets insets = target.getInsets();
	width += insets.left + insets.right;
	height += insets.top + insets.bottom;

    return new Dimension (width, height);
  }

  /**
   * Calculates the minimum size dimensions for the specified panel,
   * given the components in the specified target container
   *
   * @param target the component to be laid out
   */

  public Dimension minimumLayoutSize(Container target)
  {
    int	nmembers = target.getComponentCount();
    for (int i=0; i<nmembers; i++)
    {
      Component current = target.getComponent(i);
	  if (!current.isVisible()) continue;

      TableLocRec rec = layout.element(current);
      if (rec == null) continue;

      rec.orig_width = current.getPreferredSize().width;
      rec.orig_height = current.getPreferredSize().height;
      rec.same_width = 0;
      rec.same_height = 0;
    }
    considerSameWidth();
    considerSameHeight();

    cols = new TableCol(this);
    rows = new TableRow(this);

    int width = cols.totalSize ();
    int height = rows.totalSize ();

    return new Dimension (width, height);
  }

  /**
   * Lays out the container in the specified panel
   *
   * @param target the component to be laid out
   */

  public void layoutContainer(Container target)
  {
    Dimension 	dim = target.getSize();
    Insets 	insets = target.getInsets();

    int	nmembers = target.getComponentCount();
    for (int i=0; i<nmembers; i++)
    {
      Component current = target.getComponent(i);
	  if (!current.isVisible()) continue;

      TableLocRec rec = layout.element(current);
      if (rec == null) continue;

      current.doLayout();

      rec.orig_width = current.getPreferredSize().width;
      rec.orig_height = current.getPreferredSize().height;
      rec.same_width = 0;
      rec.same_height = 0;
    }
    considerSameWidth();
    considerSameHeight();

    cols = new TableCol(this);
    rows = new TableRow(this);

    cols.minimize();
    rows.minimize();

    tableMakeColsFitWidth(dim.width - insets.left - insets.right);
    tableMakeRowsFitHeight(dim.height - insets.top - insets.bottom);

    tableSetGeometryOfChildren(insets);
  }

  /**
   * This resource is used to specify the names of components
   * which will be constrained to remain the same width as
   * the table shrinks and grows
   *
   * @param v the vector of component with the same width
   */

  public void sameWidth(Vector v)
  {
    same_width.addElement(v);
  }

  /**
   * This resource is used to specify the names of components
   * which will be constrained to remain the same heigth as
   * the table shrinks and grows
   *
   * @param v the vector of component with the same heigth
   */

  public void sameHeight(Vector v)
  {
    same_height.addElement(v);
  }

  /**
   * The minimum spacing between the left and right edges of the
   * components in the Container
   *
   * @param i the spacing
   */

  public void marginWidth(int i)
  {
    margin_width = i;
  }

  /**
   * The minimum spacing between the top and bottom edges of the
   * components in the Container
   *
   * @param i the spacing
   */

  public void marginHeight(int i)
  {
    margin_height = i;
  }

  /**
   * Specifies if components should be made smaller than their "preferred"
   * sizes.
   * The TableLayout component tries to respect the preferred geometries of
   * its components.
   * 
   * Components which are locked using options including any of
   * "whWH" will continue to be excluded from
   * stretching, but others will be
   * stretched and then can be shrunk back to their initial preferred sizes
   * from the time they were last managed.
   * When the table is shrunk further, all
   * components are shrunk an equal number of pixels until they are of size 1
   * (the smallest legal size of a Components).
   * 
   * By default, this resource is <em>true</em>.
   * @param force  boolean to indicate shrink should be forced
   * @see tablelayout.TableOpts
   */

  public void forceShrink(boolean force)
  {
    force_shrink = force;
  }

  /**
   * Specifies the number of pixels between columns
   *
   * @param sp the spacing between columns
   */

  public void columnSpacing(int sp)
  {
    col_spacing = sp;
  }

  /**
   * Specifies the number of pixels between rows
   *
   * @param sp the spacing between rows
   */

  public void rowSpacing(int sp)
  {
    row_spacing = sp;
  }


  private void considerSameWidth()
  {
    int nlist = same_width.size();
    for (int ind1=0; ind1<nlist; ind1++)
    {
      Vector compv = (Vector) same_width.elementAt(ind1);
      int max=0;

      // find the maximum width
      int ncomp = compv.size();
      for (int ind2=0; ind2<ncomp; ind2++)
      {
	Component comp = (Component) compv.elementAt(ind2);

	TableLocRec rec = layout.element(comp);
	if (rec == null)
	  break;

        if (rec.orig_width > max)
	  max = rec.orig_width;
      }

      // set the maximum width
      for (int ind2=0; ind2<ncomp; ind2++)
      {
	Component comp = (Component) compv.elementAt(ind2);

	TableLocRec rec = layout.element(comp);
	if (rec == null)
	  break;

        rec.same_width = max;
      }
    }
  }

  private void considerSameHeight()
  {
    int nlist = same_height.size();
    for (int ind1=0; ind1<nlist; ind1++)
    {
      Vector compv = (Vector) same_height.elementAt(ind1);
      int max=0;

      // find the maximum height
      int ncomp = compv.size();
      for (int ind2=0; ind2<ncomp; ind2++)
      {
	Component comp = (Component) compv.elementAt(ind2);

	TableLocRec rec = layout.element(comp);
	if (rec == null)
	  break;

        if (rec.orig_height > max)
	  max = rec.orig_height;
      }

      // find the maximum height
      for (int ind2=0; ind2<ncomp; ind2++)
      {
	Component comp = (Component) compv.elementAt(ind2);

	TableLocRec rec = layout.element(comp);
	if (rec == null)
	  break;

        rec.same_height = max;
      }
    }
  }

  private void tableMakeColsFitWidth(int width)
  {
    int 	change, current, prefer;

    current = cols.totalSize ();
    prefer  = cols.getPreferredSize ();

    if (width < prefer && force_shrink == false)
    {
        // Smallest size is preferred size.  Excess clipped.
        change = prefer - current;
    }
    else
    {
        change = width - current;
    }

    if (change != 0)
        cols.adjust (change);
  }

  private void tableMakeRowsFitHeight(int height)
  {
    int change, current, prefer;

    current = rows.totalSize ();
    prefer  = rows.getPreferredSize ();

    if (height < prefer && force_shrink == false)
    {
        // Smallest size is preferred size.  Excess clipped.
        change = prefer - current;
    }
    else
    {
        change = height - current;
    }

    if (change != 0)
        rows.adjust (change);
  }


  private void tableSetGeometryOfChildren (Insets insets)
  {
    TableLocRec	rec;
    Component   comp;
    int		sz, index;

    if (layout		== (TableLoc)null
	|| cols		== (TableCol)null
	|| rows		== (TableRow)null)
      return;

    cols.computeOffsets(insets.left, col_spacing);
    rows.computeOffsets(insets.top, row_spacing);

    sz = layout.size();
    for (index=0; index < sz; index++)
    {
      rec = layout.recElementAt(index);
      comp = layout.compElementAt(index);

      TableComputeChildPosition(rec, comp);
    }
  }

  private void TableComputeChildPosition(TableLocRec rec, Component comp)
  {
    int cell_w, cell_h;
    int cell_x, x;
    int cell_y, y;
    int width, prefer, height;
    int i, pad;

    // cell width may well span cols and spacing

    pad = col_spacing;
    cell_w = -pad;
    for (i = 0;  i < rec.col_span;  i++)
	cell_w += cols.elementAt(rec.col + i).value + pad;

    // If size growth is prevented due to (W)
    // then use the lesser of the cell size or the preferred size.
    // Otherwise, use the cell size.

    prefer = rec.preferredWidth();
    if (rec.options.W && cell_w > prefer)
    {
	width = prefer;
    }
    else
    {
	width = cell_w;
    }
    // Be certain that the size does not go to zero, or negative!
    if (width <= 0) width = 1;

    pad = row_spacing;
    cell_h = -pad;
    for (i = 0;  i < rec.row_span;  i++)
	cell_h += rows.elementAt(rec.row + i).value + pad;

    prefer = rec.preferredHeight();
    if (rec.options.H && cell_h > prefer)
    {
	height = prefer;
    }
    else
    {
	height = cell_h;
    }
    if (height <= 0) height = 1;

    cell_x = cols.elementAt(rec.col).offset;
    if (rec.options.l)
	x = cell_x;				// left justify in cell
    else if (rec.options.r)
	x = cell_x + cell_w - width;		// right justify in cell
    else
	x = cell_x + (cell_w - width)/2; 	// center in cell

    cell_y = rows.elementAt(rec.row).offset;
    if (rec.options.t)
	y = cell_y;				// top justify in cell
    else if (rec.options.b)
	y = cell_y + cell_h - height;		// bottom justify in cell
    else
	y = cell_y + (cell_h - height)/2; 	// center in cell


    comp.setSize(width, height);
    Point p = comp.getLocation();
    if (x != p.x || y != p.y)
    {
	comp.setLocation(x, y);
    }
  }

  /**
   * Returns the String representation
   */

  public String toString()
  {
    return (
      "TableLayout [" +
      "\nlayout: " 		+ layout.toString() +
      "\ncols: " 		+ cols.toString() +
      "\nrows: " 		+ rows.toString() +
      "\nsame_width: " 		+ same_width.toString() +
      "\nsame_height: " 	+ same_height.toString() +
      "\ncol_spacing: " 	+ col_spacing +
      "\nrow_spacing: " 	+ row_spacing +
      "\nforce_shrink: " 	+ force_shrink +
      "\nmargin_width: " 	+ margin_width +
      "\nmargin_height: " 	+ margin_height +
      " ]");
  }

  /**
   * Creates a clone of the object. A new instance is allocated and all
   * the variables of the class are cloned
   */

  public Object clone()
  {
    TableLayout elem = new TableLayout();

    elem.force_shrink = force_shrink;
    elem.col_spacing = col_spacing;
    elem.row_spacing = row_spacing;
    elem.layout = (TableLoc) layout.clone();

    elem.same_width = (Vector) same_width.clone();
    elem.same_height = (Vector) same_height.clone();
    elem.margin_width = margin_width;
    elem.margin_height = margin_height;

    elem.considerSameWidth();
    elem.considerSameHeight();

    elem.cols = new TableCol(this);
    elem.rows = new TableRow(this);

    return ((Object) elem);
  }
}
