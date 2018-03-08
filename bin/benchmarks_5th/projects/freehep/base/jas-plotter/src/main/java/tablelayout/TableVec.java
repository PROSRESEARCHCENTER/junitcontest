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


// TableVector Methods
//=======================
// Each TableLayout Manager has two TableVectors: 
// one describes the columns, and the other describes the rows.
// 
// The TableVector is an abstract class. For the column use TableCol, for the 
// row use TableRow.
//
// The table vectors are created based on information in the layout
// of the Manager, hence they must be created after the layout, and they
// must be updated when the layout changes. The layout data upon
// which the vectors depend is: number of cols, number of rows, options (only
// W and H).
//
// This class used to be abstract, but since MSIE could not handle it
// I removed the "abstract" notation.

/**
 * Each TableLayout Manager has two
 * TableVectors:
 * one describes the columns, and the other describes the rows.
 * 
 * The <b>TableVector</b> is an abstract class. For the column use
 * <b>TableCol</b>, for the row use <b>TableRow</b>.
 * 
 * The table vector is created, based on information of the 
 * layout
 * of the TableLayout Manager, hence they must be created after the
 * layout, and they must be updated when the layout changes.
 * 
 * The TableVec is an array of TableVecRec, which
 * contains the size information of each component in a row of column.
 * 
 * @see tablelayout.TableVecRec
 * @see tablelayout.TableLoc
 * @see tablelayout.TableCol
 * @see tablelayout.TableRow
 *
 * @see tablelayout.TableLayout
 * @author Birgit Arkesteijn
 * @version $Revision: 11553 $ $Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $
 */

abstract public class TableVec extends Object
{
  private static final String     version_id =
      "@(#)$Id: TableVec.java 11553 2007-06-05 22:06:23Z duns $ Copyright West Consulting bv";


// Table Vector Structs
//========================
// A table has two of these vectors: one for columns, and one for rows.
//
  static final int TBL_VEC_MINIMIZE 	= 0x01;
  static final int TBL_VEC_LOCK 	= 0x02;
  static final int TBL_VEC_NOGROW 	= (TBL_VEC_MINIMIZE | TBL_VEC_LOCK);

  static final boolean DO_ACTUAL = true;
  static final boolean DO_PREFERRED = false;
   
  /**
   * Vector with the sizes of the elements (columns or rows)
   */
  public TableVecRec 	vec[];
   
  /**
   * The layout parent
   */
  public TableLayout	parent;
   
  /**
   * Number of elements (columns or rows)
   */
  public int 		size;

  /**
   * Changes the cells to its minimum size. 
   */
  abstract public void minimize();

  /**
   * Returns the total (minimum) size 
   */
  abstract public int totalSize();

  /**
   * Returns the preferred size 
   */
  abstract public int getPreferredSize();


  /**
   * Calculates the size of the layout
   */
  abstract public int layoutSize(boolean do_actual);


  /**
   * Returns the TableVecRec size information on a given position
   *
   * @exception java.lang.ArrayIndexOutOfBoundsException if the index if out of
   * bound
   * @param index the index of the information
   * @see tablelayout.TableVecRec
   */
  public TableVecRec elementAt(int index)
  {
    if (index < 0 || index > size-1)
    {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    else
      return (TableVecRec) vec[index];
  }


// Adjust rows or columns
//==========================
// When a parent re-sizes a Table, it can make it larger or smaller. The
// adjustment is distributed as a ratio of the preferred sizes of the
// col/row, so small ones change slower than larger ones.
//
// Nowhere in the logic below is there any mechanism which prevents things 
// from shrinking smaller than the preferred size. 
// There is, however, mechanisms to prevent any col or row from becoming 
// smaller than 1.
//
// If resize makes the Table larger than before: First, all col/row
// smaller that preferred size are stretched up until their preferred
// size. The rest of the change is distributed evenly to un-locked col/row,
// but if all are locked, then all are stretched.
//
// If the table is being made smaller, then the inverse is applied: all
// unlocked (or all if all are locked) are made smaller down to their
// preferred sizes, then all are made smaller by the same amount.
//
// Adjustments to the vectors are made on a relative basis. Big slots
// change more than small slots. Therefore, the adjustment delta is
// computed for each slot which might change.
//
// While adjusting the vectors, there are too things to watch out for: lots of
// slots to change, yet not much change, integer truncation then leaves the
// delta zero. In this case we make the delta 1, which means the change gets
// used up before all the slots are seen. We use the same algorithm for 
// growing and shrinking, so there should be no perceivable problems. The
// second problem is when the delta would consume too much change, again due
// to integer truncation. In this case, we must simply make the delta equal
// to the remaining change.
//
// Notice that there are two resize algorithms used: one applies when 
// everything is smaller than preferred, and another applies when everything 
// is bigger.
// When smaller, everything gets changed relatively. When larger, change
// is influenced by the table slot being locked (TBL_VEC_LOCK). Slots which
// are locked are not adjusted unless ALL slots are locked, then all are
// adjusted relatively.
//

  /**
   * Adjust the row or column after a resize
   *
   * @param change the number of pixel the row or column should grow or
   * shrink
   */
  public void adjust(int change)
  {
    int 	vec_inx, remaining, amt;
    int 	total_pref;
    int 	can_change, can_change_pref;
    int 	too_small;
    int 	too_big, too_big_pref;

    if (0 == change)
	return;

    total_pref = can_change = can_change_pref = 0;
    too_small  = too_big    = too_big_pref    = 0;

    for (vec_inx=0; vec_inx < size; vec_inx++)
    {
      // NOTE: total_pref can be zero if all pref_value are 0!
      total_pref += vec[vec_inx].pref_value;
      if (change > 0)
      {
	if (0 == (vec[vec_inx].options & TBL_VEC_LOCK))
	{
	  // NOTE: can_change_pref can be zero if all pref_value are 0!
	  can_change++;
	  can_change_pref += vec[vec_inx].pref_value;
	}
	if (vec[vec_inx].value < vec[vec_inx].pref_value)
	{
	  too_small++;
	}
      }
      else
      {
	if (vec[vec_inx].value > vec[vec_inx].pref_value)
	{
	  // NOTE: too_big_pref can be zero if all pref_value are 0!
	  too_big++;
	  too_big_pref += vec[vec_inx].pref_value;
	}
      }
    }

    if (change > 0)
    {
      //================= Make columns wider or rows taller =============== 
      int still_too_small;
      remaining = change;
      still_too_small = 1;
      while (still_too_small != 0)
      {
	// Expand everything smaller than preferred up to preferred
	still_too_small = 0;

	for (vec_inx=0; vec_inx < size;  vec_inx++)
	{
	  if (vec[vec_inx].value < vec[vec_inx].pref_value)
	  {
	    // Make this one bigger, up to preferred size
	    if (0 == total_pref)
	      amt = change / (too_small!=0?too_small:size);
	    else
	      amt = change * vec[vec_inx].pref_value / total_pref;
	    if (0 == amt)
	      amt = 1;
	    else if (remaining < amt)
	      amt = remaining;

	    if (vec[vec_inx].value + amt < vec[vec_inx].pref_value)
	    {
	      vec[vec_inx].value += amt;
	      ++still_too_small;
	    }
	    else
	    {
	      amt = vec[vec_inx].pref_value - vec[vec_inx].value;
	      vec[vec_inx].value = vec[vec_inx].pref_value;
	    }
	    remaining -= amt;

	    if (remaining <= 0)
		return;	// used up all change 
	  }
	}

	change = remaining;
      }

      // All are at least preferred size, and there is change remaining.
      // If none of the vector slots can stretch, then we still must
      // force them all to stretch.

      if (0 == can_change)
	can_change_pref = total_pref;	// maintain relative sizes 

      while (true) // until remaining goes to zero or negative above 
      {
	for (vec_inx = 0; vec_inx < size; vec_inx++)
	{
	  if (0 == can_change || 
	      0 == (vec[vec_inx].options & TBL_VEC_LOCK))
	  {
	    // Add relative amount to all which can change.
	    if (0 == can_change_pref)
	      amt = change / (can_change!=0?can_change:size);
	    else
	      amt = change * vec[vec_inx].pref_value/can_change_pref;
	    if (0 == amt)
	      amt = 1;
	    else if (remaining < amt)
	      amt = remaining;

	    vec[vec_inx].value += amt;
	    remaining -= amt;

	    if (remaining <= 0)
	      return; // used up all change 
	  }
	}

	// We have gone thru vector, adding space, but due to truncation
	// there may still be more change to distribute.

	change = remaining;
      }
    }
    else // (change < 0)
    {
      //================= Make columns narrower or rows shorter ==============
      int still_too_big, num_larger_than_1;

      // For conceptual clarity, switch the sign on change
      change = -change;
      remaining = change;

      still_too_big = too_big;
      while (still_too_big !=0)
      {
	// Shrink all which are larger than preferred
	still_too_big = 0;
	for (vec_inx = 0; vec_inx < size;  vec_inx++)
	{
	  if (vec[vec_inx].value > vec[vec_inx].pref_value)
	  {
	    if (0 == too_big_pref)
	      amt = change / (too_big !=0?too_big:size);
	    else
	      amt = change * vec[vec_inx].pref_value / too_big_pref;
	    if (0 == amt)
	      amt = 1;
	    else if (remaining < amt)
	      amt = remaining;

	    if (vec[vec_inx].value - amt < vec[vec_inx].pref_value)
	    {
	      amt = vec[vec_inx].value - vec[vec_inx].pref_value;
	      vec[vec_inx].value = vec[vec_inx].pref_value;
	    }
	    else
	    {
	      vec[vec_inx].value -= amt;
	      still_too_big++;
	    }

	    remaining -= amt;

	    if (remaining <= 0)
	      return; // used up all change 
	  }
	}

	// We have made a pass through all slots
	change = remaining;
      }

      // Now all stretchable are preferred sizes, or all were already smaller
      // than preferred sizes, yet more change is remaining to be absorbed.
      //
      // Shrink evenly, but since none can become smaller than 1, we may need
      // to make multiple passes over vector until total change is absorbed,
      // or all are of size 1.

      num_larger_than_1 = 1;
      while (num_larger_than_1 != 0)
      {
	num_larger_than_1 = 0;

	for (vec_inx = 0;  vec_inx < size;  vec_inx++)
	{
	  if (0 == total_pref)
	    amt = change / size;
	  else
	    amt = change * vec[vec_inx].pref_value / total_pref;
	  if (0 == amt)
	    amt = 1;
	  else if (remaining < amt)
	    amt = remaining;

	  if (amt < vec[vec_inx].value)
	  {
	    vec[vec_inx].value -= amt;
	    ++num_larger_than_1;
	  }
	  else
	  {
	    amt = vec[vec_inx].value - 1;
	    vec[vec_inx].value = 1;
	  }
	  remaining -= amt;
	  if (remaining <= 0)
	    return; // used up all change 
	}

	// We have made a pass through all slots
	change = remaining;
      }
      return; // all are shrunk to absolute minimum size (1)
    }
  } // end of adjust()


// Set Upper Left Corner Coordinates of Each Cell
//==================================================
// Note that it is not worth doing this until the actual correct size of
// the rows and columns have been computed.
//

  /**
   * Sets the upper left corner coordinate of each component within the
   * container
   * 
   * @param margin the margin of the container
   * @param gap the gap between each component in the container
   */

  public void computeOffsets(int margin, int gap)
  {
    int i;
    int offset = margin;

    for (i = 0; i < size; i++)
    {
      vec[i].offset = offset;
      offset = offset + vec[i].value + gap;
    }
  }

  /**
   * Returns the String representation
   */
  public String toString()
  {
    String st = new String (
      "TableVec [" +
      " size " + size);

    for (int i=0; i<size; i++)
    {
      st = st + "\n" + vec[i].toString();
    }
    st = st + " ]";

    return (st);
  }

}


