package org.freehep.math.minuit;

/**
 *
 * @version $Id: Pair.java 8584 2006-08-10 23:06:37Z duns $
 */
class Pair<T1,T2>
{
   Pair(T1 f, T2 s)
   {
      first = f;
      second = s;
   }
   public T1 first;
   public T2 second;
}
