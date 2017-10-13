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

/**
 * Controls the rows of the table
 *
 * @see tablelayout.TableLayout
 * @author Birgit Arkesteijn
 * @version $Revision: 11553 $ $Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $
 */

public class TableRow extends TableVec
{
  private static final String     version_id =
      "@(#)$Id: TableRow.java 11553 2007-06-05 22:06:23Z duns $ Copyright West Consulting bv";

  /**
   * @param tw the corresponding TableLayout manager
   */

  public TableRow (TableLayout tw)
  {
    TableLoc  	loc;
    int		index;

    parent = tw;
    loc = parent.layout;
    if ((TableLoc)null == loc)
	return;

    size = loc.numRows();

    if (0 == size)
	return;

    vec = new TableVecRec [size];
    for (index=0; index < size; index++)
    {
      vec[index] = new TableVecRec();
    }

    minimize();
  }


// Minimize Row Heights
//==========================================
// Change the vector to be its minimum size in the direction indicated.
// If TBL_VEC_MINIMIZE (i.e., H) then the widget is kept to its 
// original size. 
// TBL_VEC_LOCK (i.e., h) is not checked, because such widgets DO 
// grow to the minimum size of the row.
//
  /**
   * Changes the row to its minimum size. This method is called every time 
   * the layout changes, before the container is laid out
   */

  public void minimize()
  {
    int		i;
    boolean	minimize, dontStretch; 
    int		first_slot, last_slot, slot;
    int 	index, loc_sz;

    TableLoc  	loc;
    TableLocRec rec;

    // Determine which slots need to be minimized
    loc = parent.layout;
    loc_sz = loc.size();
    for (index=0; index < loc_sz; index++)
    {
      rec = loc.recElementAt(index);

      minimize		= rec.options.H;
      dontStretch	= rec.options.h;
      first_slot	= rec.row;
      last_slot		= rec.row + rec.row_span;

      if (minimize)
	for (slot = first_slot; slot < last_slot; slot++)
	  vec[slot].options |= TBL_VEC_MINIMIZE;

      if (dontStretch)
	for (slot = first_slot; slot < last_slot; slot++)
	  vec[slot].options |= TBL_VEC_LOCK;
    }

    // Sort layout (in-place) by the number of rows each component
    // spans so we first compute sizes of individual rows, then 
    // compute the spanned rows.
    //
    loc.qsort(0, (loc_sz-1), false);

    // Reset all width|heights to zero, then expand to fit
    for (i=0; i < size; i++)
    {
      vec[i].value = 0;
    }

    for (index=0; index < loc_sz; index++)
    {
      int pref;
      rec = loc.recElementAt(index);

      // Check for simple case (span of 1), where row just becomes
      // large enough for largest component in that row.

      if (rec.row_span == 1)
      {
	pref = rec.preferredHeight();

	if (pref > vec[rec.row].value)
	  vec[rec.row].value = pref;
      }
      else
      {
	// Spans multiple rows. We have already set each
	// row to the individual size requirements, now we can
	// see which spanned rows need to be stretched.  The
	// span width includes inter-row spacing.

	int to_stretch, span_size, stop_before;
	int can_stretch = 0;

	span_size   = parent.row_spacing * (rec.row_span-1);
	first_slot  = rec.row;
	stop_before = rec.row + rec.row_span;

	for (slot = first_slot;  slot < stop_before;  slot++)
	{
	  if (0 == (vec[slot].options & TBL_VEC_LOCK))
	      can_stretch++;
	  span_size += vec[slot].value;
	}

	// If none of the slots can stretch, then we still must force
	// them all to stretch at least to the orig_size of this widget.

	if (0 == can_stretch)
	{
	  to_stretch = rec.row_span;
	  pref = rec.orig_height;
	}
	else
	{
	  to_stretch = can_stretch;
	  pref = rec.preferredHeight();
	}

	if (span_size < pref)
	{
	  // Increase size of some or all slots: if nothing
	  // can stretch, expand every slot, else expand only
	  // those which are not locked small.

	  int excess	= pref - span_size;
	  int amt	= excess / to_stretch;
	  int truncated	= excess - amt*to_stretch;

	  for (slot = first_slot;  slot < stop_before;  slot++)
	  {
	    if (0 == can_stretch || 
	        0 == (vec[slot].options & TBL_VEC_LOCK))
	    {
	      if (truncated!=0)
	      {
		vec[slot].value += amt + 1;
		--truncated;
	      }
	      else
		vec[slot].value += amt;
	    }
	  }
	}
      }
    }

    // The vector is minimized: set pref_value from value
    for (i=0; i < size; i++)
      vec[i].pref_value = vec[i].value;
  }

  /**
   * Returns the total (minimum) size of the row
   *
   * @see #layoutSize
   */

  public int totalSize()
  {
      return layoutSize(DO_ACTUAL);
  }

  /**
   * Returns the preferred size of the row.
   *
   * @see #layoutSize
   */

  public int getPreferredSize()
  {
      return layoutSize(DO_PREFERRED);
  }

  /**
   * Calculates the size of the layout
   *
   * @param do_actual actual size or the preferred size
   */
  public int layoutSize(boolean do_actual)
  {
    int slot, space;
    int laysize = 0;

    space = parent.row_spacing;
    laysize += 2*parent.margin_height;

    if (do_actual)
    {
      laysize -= space;
      for (slot = 0;  slot < size;  slot++)
	laysize += vec[slot].value + space;
    }
    else
    {
      laysize -= space;
      for (slot = 0;  slot < size;  slot++)
	laysize += vec[slot].pref_value + space;
    }

    if (laysize > 0)
      return laysize;
    else
      return 1;	// minimum laysize 
  }

  /**
   * Returns the String representation
   */

  public String toString()
  {
    String st = new String (
      "TableRow [" + super.toString() + " ] ");
    return (st);
  }
}
