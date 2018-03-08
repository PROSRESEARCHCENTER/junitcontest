package hep.aida.ref.pdf;

import hep.aida.IRangeSet;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * This class handles multiple ranges along one axis.
 * Overlapping ranges are merged in one bigger range.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class RangeSet implements IRangeSet {

    private ArrayList set;
    private boolean rangeChanged;
    private Random r = new Random();
    
    private ArrayList listeners = new ArrayList();

    /**
     * Creates RangeSet with (-infinity, +infinity) valid interval, borders excluded
     */
    public RangeSet() {
	rangeChanged = true;
	set = new ArrayList();

	Range r = new Range();
	set.add(r);
    }

    /*
     * Creates RangeSet with a valid interval (xMin, xMax), borders excluded.
     */
    public RangeSet(double xMin, double xMax) {
	rangeChanged = true;
	set = new ArrayList();

	Range r = new Range(xMin, xMax);;
	set.add(r);
    }

    /*
     * Creates RangeSet with valid intervals:
     * (xMin[0], xMax[0]), (xMin[1], xMax[1]), ... , borders excluded.
     */
    public RangeSet(double[] xMin, double[] xMax) {
	rangeChanged = true;
	if (xMin.length != xMax.length) 
	    throw new IllegalArgumentException("xMin and xMax arrays must have the same size.");
	set = new ArrayList();

	Range r = new Range(xMin[0], xMax[0]);
	set.add(r);
	if (xMin.length > 1)
	    for (int i=1; i<xMin.length; i++) { include(xMin[i], xMax[i]); }
    }

    public double  PLUS_INF() { return Double.POSITIVE_INFINITY; }

    public double MINUS_INF() { return Double.NEGATIVE_INFINITY; }

    /** Returns current number of disjoint ranges (non-overlapping intervals) */
    public int size() { return set.size(); }
    
    public double length() {
        if ( size() == 0 ) throw new RuntimeException("No length for this Range set");
        double length = 0;
	for (int i=0; i<size(); i++)
            length += ((Range) set.get(i)).upperBound() - ((Range) set.get(i)).lowerBound();
	return length;
    }
    
    public double generatePoint() {
//        double rnd = r.nextDouble();
        double rnd = Math.random();
        double l = length();
        double pl = 0;
	for (int i=0; i<size(); i++) {
            double rub = ((Range) set.get(i)).upperBound();
            double rlb = ((Range) set.get(i)).lowerBound();
            pl += rub - rlb;
            if ( rnd < pl/l ) 
                return rlb + ((pl/l)-rnd)*l;
        }
        throw new RuntimeException("Problem with generating a point in the RangeSet");
    }
        

    /** Return array of lower or upper Bounds for the current set of ranges */
    public double[] lowerBounds() {
	if (size() == 0) return null;

	double[] tmp = new double[size()];
	for (int i=0; i<size(); i++) tmp[i] = ((Range) set.get(i)).lowerBound();
	return tmp;
    }
    public double[] upperBounds() {
	if (size() == 0) return null;

	double[] tmp = new double[size()];
	for (int i=0; i<size(); i++) tmp[i] = ((Range) set.get(i)).upperBound();
	return tmp;
    }

    /**
     * Set the range from xMin to xMax
     * This method first does "excludeAll", then "includeRange"
     */
    /*
    public void setRange(double xMin, double xMax) {
	rangeChanged = true;
	set = new ArrayList();

	Range r = new Range(xMin, xMax);
	set.add(r);
    }
    */

    /**
     * Additive include/exclude of the range in given axis.
     */  
    public void include(double xMin, double xMax) {
	rangeChanged = true;
	if (xMin > xMax)
	    throw new IllegalArgumentException("xMax must be bigger tnan xMin.");

	int r1 = -1;
	int r2 = -1;
	boolean range1 = true;
	boolean range2 = true;
	double min = Double.NaN;
	double max = Double.NaN;

	// No ranges added before
	if (set.size() == 0) {
	    set.add(new Range(xMin, xMax));
            notifyRangeSetChanged();
            return;
	}

	// New range cover all valid intervals or more
	if (((Range) set.get(set.size()-1)).upperBound()<=xMax && ((Range) set.get(0)).lowerBound()>=xMin) {
	    //setRange(xMin, xMax);
	    excludeAll();
	    include(xMin, xMax);
            notifyRangeSetChanged();
            return;
	}

	// New range is above highest valid point
	if (((Range) set.get(set.size()-1)).upperBound()<xMin) {
	    set.add(new Range(xMin, xMax));
            notifyRangeSetChanged();
	    return;
	}

	// New range is below lowest valid point
	if (((Range) set.get(0)).lowerBound()>xMax) {
	    insert(0, new Range(xMin, xMax));
            notifyRangeSetChanged();
	    return;
	}

	// All other cases
	range1 = true;
	r1 = inRange(xMin);
	if (r1<0) {
	    range1 = false;
	    r1 = inGap(xMin);
	    min = xMin;
	} else {
	    min = ((Range) set.get(r1)).lowerBound();
	}

	range2 = true;
	r2 = inRange(xMax);
	if (r2<0) {
	    range2 = false;
	    r2 = inGap(xMax);
	    max = xMax;
	} else {
	    max = ((Range) set.get(r2)).upperBound();
	}

	// New range is inside one of existing ranges - do nothing
	if (range1 && range2 && (r1 == r2)) return;

	// New range edges fall in the gap between existing ranges 
	if (!range1 && !range2 && (r1 == r2)) {
	    insert(r1, new Range(min, max));
            notifyRangeSetChanged();
	    return;
	}

	// New range bridges several ranges - replace them with one big range
	if (range2) {
	    remove(r1, r2);
	    insert(r1, new Range(min, max));
            notifyRangeSetChanged();
	    return;
	} else if (!range2) {
	    remove(r1, r2-1);
	    insert(r1, new Range(min, max));
	    return;
	}	
    }

    public void exclude(double xMin, double xMax) {
	rangeChanged = true;
	if (xMin > xMax)
	    throw new IllegalArgumentException("xMax must be bigger than xMin.");

	int r1 = -1;
	int r2 = -1;
	boolean range1 = true;
	boolean range2 = true;
	double min = Double.NaN;
	double max = Double.NaN;

	// No ranges added before - everything is already excluded, do nothing
	if (set.size() == 0) {
            notifyRangeSetChanged();
	    return;
	}

	// Exclude range cover all valid intervals or more - exclude all
	if (((Range) set.get(set.size()-1)).upperBound()<=xMax && ((Range) set.get(0)).lowerBound()>=xMin) {
	    excludeAll();
            notifyRangeSetChanged();
	    return;
	}

	// New exclude range is above highest valid point - do nothing
	if (((Range) set.get(set.size()-1)).upperBound()<xMin) {
	    return;
	}

	// New exclude range is below lowest valid point - do nothing
	if (((Range) set.get(0)).lowerBound()>xMax) {
	    return;
	}

	// All other cases
	range1 = true;
	r1 = inRange(xMin);
	if (r1<0) {
	    range1 = false;
	    r1 = inGap(xMin);
	    min = xMin;
	} else {
	    min = ((Range) set.get(r1)).lowerBound();
	}

	range2 = true;
	r2 = inRange(xMax);
	if (r2<0) {
	    range2 = false;
	    r2 = inGap(xMax);
	    max = xMax;
	} else {
	    max = ((Range) set.get(r2)).upperBound();
	}

	//  New exclude range starts and ends in the gap
	if (!range1 && !range2) {
	    if (r1 == r2) return;
	    else {
		remove(r1, r2-1);
                notifyRangeSetChanged();
		return;
	    }
	} else {
	    // New exclude range starts on the valid include range
	    if (range1) {
		insert(r1, new Range(min, xMin));
		r1++;
		r2++;
	    }
	    // New exclude range ends on the valid include range
	    if (range2) {
		insert(r2+1, new Range(xMax, max));
	    } else r2--;

	    remove(r1, r2);

	}
        notifyRangeSetChanged();    
    }

    /**
     * Set full range in all axes.
     */
    public void includeAll() {
	rangeChanged = true;
	set = new ArrayList();

	Range r = new Range();
	set.add(r);
        notifyRangeSetChanged();
    }
    
    /**
     * Set empty range in all axes.
     */
    public void excludeAll() {
	rangeChanged = true;
	set = new ArrayList();
        notifyRangeSetChanged();
    }
 
    public boolean isInRange(double point) {
	if (set.size() == 0) return false;
	for (int i=0; i<set.size(); i++) { 
	    if (((Range) set.get(i)).isInRange(point)) 
                return true;
            else
                throw new IllegalArgumentException("Out of range "+point+" "+((Range) set.get(i)).lowerBound()+" "+((Range) set.get(i)).upperBound());
	}
	return false;
    }

    private int inRange(double point) {
	//System.out.println("inRange  point="+point);
	if (set.size() == 0) return -1;
	for (int i=0; i<set.size(); i++) { 
	    if (((Range) set.get(i)).isInRange(point)) return i;
	}
	return -1;
    }

    private int inGap(double point) {
	//System.out.println("inGap    point="+point+"   set.size() = "+set.size());
	if (set.size() == 0) return -1;
	if (((Range) set.get(0)).lowerBound()>point) return 0;
	if (((Range) set.get(set.size()-1)).upperBound()<point) return set.size();
	for (int i=0; i<set.size()-1; i++) { 
	    //System.out.println("     upper: "+((Range) set.get(i)).upperBound());
	    //System.out.println("     lower: "+((Range) set.get(i+1)).lowerBound());
	    if (((Range) set.get(i)).upperBound()<point && ((Range) set.get(i+1)).lowerBound()>point) { return i+1; }
	}
	//System.out.println("     Gap End"); 
	return -1;
    }

    /*
     * Inserts Range into the specified position of the Set
     * Indexing runs [0, set.size()]
     */
    private void insert(int index, Range r) {
	if (index>set.size() || index<0 ) 
	    throw new IllegalArgumentException("Wrong index: "+index+", Set size="+set.size());
	//System.out.println("insert1:Size: "+set.size()+" index="+index);
	if (index == set.size()) {
	    set.add(r);
	    return;
	}

	ArrayList tmp = new ArrayList();
      
	for (int i=0; i<set.size()+1; i++) {
	    if      (i < index) tmp.add((Range) set.get(i));
	    else if (i == index) tmp.add(r);
	    else if (i > index) tmp.add((Range) set.get(i-1));
	}
	//System.out.println("insert2:Size: "+tmp.size());
	set = tmp;
    }

    /* 
     * Removes ranges from the set, (index1 - index2), inclusive.
     * Indexing runs [0, set.size()-1]
     */ 
    private void remove(int index1, int index2) {
	//System.out.println("remove: Size: "+set.size()+" index1="+index1+",  index2="+index2);
	if (index1>index2 || index1>=set.size() || index2>=set.size() || index1<0 || index2<0) 
	    throw new IllegalArgumentException("Wrong index: index1="+index1+
					       ", index2="+index2+", Set size="+set.size());
	ArrayList tmp = new ArrayList();
	
	for (int i=0; i<set.size(); i++) if (i<index1 || i>index2) tmp.add((Range) set.get(i));
	
	//System.out.println("remove: Size: "+tmp.size());
	set = tmp;
    }

    void addRangeSetListener( RangeSetListener listener ) {
        listeners.add(listener);
    }
    
    void removeRangeSetListener( RangeSetListener listener ) {
        listeners.remove(listener);
    }
    
    void notifyRangeSetChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            RangeSetListener listener = (RangeSetListener) listeners.get(i);
            listener.rangeSetChanged();
        }
    }
    
    // Test RangeSet here
    public static void main(String[] args) {
	RangeSet set = new RangeSet();

	set.print();

	set.excludeAll();
	set.print();

	set.include(5.5, 7.6);
	set.print();

	set.include(14.5, 17.6);
	set.print();

	set.include(9.5, 12.6);
	set.print();
    
	set.include(6.5, 10.6);
	set.print();
    
	set.include(1.5, 3.6);
	set.print();

	set.include(5.1, 18.6);
	set.print();

	System.out.println("\n ****************** Start Excluding ******************\n");

	set.exclude(14.2, 16.7);
	set.print();

	set.exclude(7.2, 10.3);
	set.print();

	set.exclude(4.9, 11.4);
	set.print();

	set.exclude(2.1, 12.1);
	set.print();

	set.exclude(17.0, 18.0);
	set.print();

    }

    private void print() {
	System.out.println("\n\n Size: "+set.size());
	for (int i=0; i<set.size(); i++) { System.out.println(i + "\t lowerBound = " + ((Range) set.get(i)).lowerBound() +
							      ",\t upperBound = " + ((Range) set.get(i)).upperBound()); }
	System.out.println("");
	for (int i=0; i<20; i++) {
	    System.out.println(i +"\t "+ isInRange(i*1.));
	}
    }
}
