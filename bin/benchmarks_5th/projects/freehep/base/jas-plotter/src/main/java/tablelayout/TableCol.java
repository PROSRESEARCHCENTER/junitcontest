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
 * Controls the colums of the table
 *
 * @see tablelayout.TableLayout
 * @author Birgit Arkesteijn
 * @version $Revision: 11553 $ $Date: 2007-06-06 00:06:23 +0200 (Wed, 06 Jun 2007) $
 */

public class TableCol extends TableVec
{
  private static final String     version_id =
      "@(#)$Id: TableCol.java 11553 2007-06-05 22:06:23Z duns $ Copyright West Consulting bv";

  /**
   * @param tw the corresponding TableLayout manager
   */

  public TableCol (TableLayout tw)
  {
    TableLoc  	loc;
    int		index;

    parent = tw;

    loc = parent.layout;
    if ((TableLoc)null == loc)
	return;

    size = loc.numCols();	

    if (0 == size)
	return;

    vec = new TableVecRec [size];
    for (index=0; index < size; index++)
    {
      vec[index] = new TableVecRec();
    }
    
    minimize();
  }


// Minimize Column Widths 
//==========================================
// Change the vector to be its minimum size in the direction indicated.
// If TBL_VEC_MINIMIZE (i.e., W) then the widget is kept to its 
// original size. 
// TBL_VEC_LOCK (i.e., w) is not checked, because such widgets DO 
// grow to the minimum size of the column.
//
  /**
   * Changes the column to its minimum size. This method is called every time 
   * the layout changes, before the container is laid out
   */

  public void minimize()
  {
    int		i;
    boolean	minimize, dontStretch; 
    int		first_slot, last_slot, slot;
    int 	index, loc_sz;

    TableLoc 	loc;
    TableLocRec rec;

    // Determine which slots need to be minimized
    loc = parent.layout;
    loc_sz = loc.size();
    for (index=0; index < loc_sz; index++)
    {
      rec = loc.recElementAt(index);

      minimize		= rec.options.W;
      dontStretch	= rec.options.w;
      first_slot	= rec.col;
      last_slot		= rec.col + rec.col_span;

      if (minimize)
	for (slot = first_slot; slot < last_slot; slot++)
	  vec[slot].options |= TBL_VEC_MINIMIZE;

      if (dontStretch)
	for (slot = first_slot; slot < last_slot; slot++)
	  vec[slot].options |= TBL_VEC_LOCK;
    }

    // Sort layout (in-place) by the number of columns each component
    // spans so we first compute sizes of individual columns, then 
    // compute the spanned columns.
    //
    loc.qsort(0, (loc_sz-1), true);

    // Reset all width|heights to zero, then expand to fit
    for (i=0; i < size; i++)
    {
      vec[i].value = 0;
    }

    for (index=0; index < loc_sz; index++)
    {
      int pref;
      rec = loc.recElementAt(index);

      // Check for simple case (span of 1), where col just becomes
      // large enough for largest component in that col.

      if (rec.col_span == 1)
      {
	pref = rec.preferredWidth();

	if (pref > vec[rec.col].value)
	  vec[rec.col].value = pref;
      }
      else
      {
	// Spans multiple columns. We have already set each
	// column to the individual size requirements, now we can
	// see which spanned columns need to be stretched.  The
	// span width includes inter-column spacing.

	int to_stretch, span_size, stop_before;
	int can_stretch = 0;

	span_size   = parent.col_spacing * (rec.col_span-1);
	first_slot  = rec.col;
	stop_before = rec.col + rec.col_span;

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
	  to_stretch = rec.col_span;
	  pref = rec.orig_width;
	}
	else
	{
	  to_stretch = can_stretch;
	  pref = rec.preferredWidth();
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
   * Returns the total (minimum) size of the column
   *
   * @see #layoutSize
   */

  public int totalSize()
  {
      return layoutSize(DO_ACTUAL);
  }

  /**
   * Returns the preferred size of the column.
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

    space = parent.col_spacing;
    laysize += 2*parent.margin_width;

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
      "TableCol [" + super.toString() + " ] ");
    return (st);
  }
}
