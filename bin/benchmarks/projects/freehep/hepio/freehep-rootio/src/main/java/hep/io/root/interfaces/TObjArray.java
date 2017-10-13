/*
 * TObjArray.java
 *
 * Created on January 12, 2001, 3:45 PM
 */
package hep.io.root.interfaces;


/**
 *
 * @author tonyj
 * @version $Id: TObjArray.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface TObjArray extends hep.io.root.RootObject, TSeqCollection, java.util.List
{
   int getLowerBound();

   int getUpperBound();
}
