package hep.physics.filter;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that filters an input iterator to only accept items that are
 * accepted by a given predicate.
 */
public class Filter implements Iterator
{
   public Filter(Iterator e, Predicate p)
   {
      this.source = e;
      this.predicate = p;
      findNextElement();
   }
   public Object next()
   {
      Object result = next;
      if (result == null) throw new NoSuchElementException();
      findNextElement();
      return result;
   }
   public boolean hasNext()
   {
      return next != null;
   }
   public void remove()
   {
      throw new UnsupportedOperationException();
   }
   private void findNextElement()
   {
      while (source.hasNext())
      {
         next = source.next();
         if (predicate.accept(next)) return;
      }
      next = null;
   }
   private Iterator source;
   private Predicate predicate;
   private Object next;
}