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
import java.util.Vector;


// TableLoc methods
//====================
// The TableLayout Manager keeps its layout as a TableLoc, which are 
// vectors of TableLocRecs.
//
// Each TableLocRec defines the component's location information (the
// row, col, spans, and layout options like justification and sizing controls).
// The layout describes where components may be placed if and when they
// become layout-ed. 
//
// The layout is created from a string by the TableLocRec constructor.

// Allocate, Grow, and Free Arrays of TableLocRec's
//====================================================
//

/**
 * The TableLayout Manager keeps its layout
 * description as a TableLoc, which contains 
 * a vector of TableLocRec and of the
 * components.
 * 
 * Each TableLocRec defines the component's location information (the
 * row, col, spans, and layout options like justification and sizing
 * controls).
 * The layout describes where components may be placed if and when they
 * become layout-ed.
 * 
 * The layout is created from a string by the TableLocRec constructor.
 * 
 * @see tablelayout.TableLayout
 * @see tablelayout.TableLocRec
 * @author Birgit Arkesteijn
 * @version $Revision: 11553 $ $Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $
 */

public class TableLoc extends Object 
{
  private static final String     version_id =
      "@(#)$Id: TableLoc.java 11553 2007-06-05 22:06:23Z duns $ Copyright West Consulting bv";

  Vector compinfo;
  Vector recinfo;

  static final String CHAR_SEMI       = ";";
  static final String CHAR_BLANC      = " ";
  static final String CHAR_ALL        = CHAR_SEMI + CHAR_BLANC;

  static final String COMPONENT_REC   = "rec";
  static final String COMPONENT_NEXT  = "next";
  static final String COMPONENT_PREV  = "prev";

  /**
   *
   */

  public TableLoc()
  {
    compinfo = new Vector();
    recinfo = new Vector();
  }

  /**
   * Adds a component with its layout
   *
   * @param rec the layout information of the component
   * @param comp the component
   */

  public void addElement (TableLocRec rec, Component comp)
  {
    compinfo.addElement((Object)comp);
    recinfo.addElement((Object)rec);
  }

  /**
   * Adds a TableLoc vector
   *
   * @param loc the TableLoc vector to add
   */

  public void addElement (TableLoc loc)
  {
    int index, sz;

    sz = loc.size();
    for (index=0; index<sz; index++)
    {
      compinfo.addElement(loc.compinfo.elementAt(index));
      recinfo.addElement(loc.recinfo.elementAt(index));
    }
  }

  /**
   * Removes the information about a component
   *
   * @param comp the component
   */

  public void removeElement(Component comp) 
  {
    int index;

    if ((index = compinfo.indexOf(comp)) > -1) 
    {
      compinfo.removeElementAt(index);
      recinfo.removeElementAt(index);
    }
  }

  /**
   * Returns the number of components in the layout
   */

  public int size()
  {
    return compinfo.size();
  }


// Find things in TableLocs
//============================
// Linear search of TableLoc array looking for various parameters
//

  /**
   * Returns the TableLocRec layout information on a given position
   *
   * @param index the index of the information
   */

  public TableLocRec recElementAt(int index)
  {
    return ((TableLocRec)recinfo.elementAt(index));
  }

  /**
   * Returns the component on a given position
   *
   * @param index the index of the information
   */

  public Component compElementAt(int index)
  {
    return ((Component)compinfo.elementAt(index));
  }

  /**
   * Returns the TableLocRec layout information of a given component
   *
   * @param w the component
   */

  public TableLocRec element(Component w)
  {
    int index;

    if ((index = compinfo.indexOf(w)) > -1) 
    {
      return ((TableLocRec)recinfo.elementAt(index));
    }
    return ((TableLocRec)null);
  }

  /**
   * Returns the number of columns of the layout
   */

  public int numCols()
  {
    int index, sz;
    TableLocRec rec = null;
    int cols = 0;

    index = 0;
    sz = compinfo.size();
    while (index < sz)
    {
      rec = (TableLocRec)recinfo.elementAt(index);
      if (cols < (rec.col + rec.col_span))
        cols = rec.col + rec.col_span;
      index ++;
    }
    return cols;
  }

  /**
   * Returns the number of rows of the layout
   */

  public int numRows()
  {
    int index, sz;
    TableLocRec rec = null;
    int rows = 0;

    index = 0;
    sz = compinfo.size();
    while (index < sz)
    {
      rec = (TableLocRec)recinfo.elementAt(index);
      if (rows < (rec.row + rec.row_span))
        rows = rec.row + rec.row_span;
      index ++;
    }
    return rows;
  }


  // Definitly ported from "C". Java does not allow function pointers to pass

  /**
   * Sorts the colums of row to their position
   *
   * @param left left element 
   * @param right right element
   * @param do_col are these columns?
   */
  public void qsort(int left, int right, boolean do_col)
  {
    int i, last;

    if (left >= right)
      return;
    swap (left, (left+right)/2);
    last = left;

    for (i=left+1; i<=right; i++)
    {
      if (do_col)
      {
	if (TableLocRec.compareColSpan((TableLocRec)recinfo.elementAt(i),
		   (TableLocRec)recinfo.elementAt(left)) < 0)
        swap (last, i);
      }
      else
      {
	if (TableLocRec.compareRowSpan((TableLocRec)recinfo.elementAt(i),
		   (TableLocRec)recinfo.elementAt(left)) < 0)
        swap (last, i);
      }
      last++;
    }
    swap(left, last);
    qsort(left, last-1, do_col);
    qsort(last+1, right, do_col);
  }

  /**
   * Swap two cells
   * 
   * @param one cell no. 1
   * @param two cell no. 2
   */

  public void swap(int one, int two)
  {
    TableLocRec trec = (TableLocRec)recinfo.elementAt(one);
    TableLocRec rec1 = (TableLocRec)recinfo.elementAt(one);
    TableLocRec rec2 = (TableLocRec)recinfo.elementAt(two);

    trec = rec2;
    rec2 = rec1;
    rec1 = trec;

    Component tcom = (Component)compinfo.elementAt(one);
    Component com1 = (Component)compinfo.elementAt(one);
    Component com2 = (Component)compinfo.elementAt(two);

    tcom = com2;
    com2 = com1;
    com1 = tcom;
  }

  /**
   * Creates a clone of the object. A new instance is allocated and all
   * the variables of the class are cloned
   */

  public Object clone()
  {
    TableLoc elem = new TableLoc();
    elem.compinfo = (Vector) compinfo.clone();
    elem.recinfo  = (Vector) recinfo.clone();
    return ((Object) elem);
  }

  /**
   * Returns the String representation
   */

  public String toString()
  {
    return (
      "TableLoc [" +
      "\ncompinfo " + compinfo.toString() +
      "\nrecinfo " + recinfo.toString() +
      "]");
  }

}
