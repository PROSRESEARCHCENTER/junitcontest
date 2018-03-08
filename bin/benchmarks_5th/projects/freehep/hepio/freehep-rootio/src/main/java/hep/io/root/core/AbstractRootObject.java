package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootObject;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 *
 * @author tonyj
 */
public abstract class AbstractRootObject implements RootObject
{
   private static boolean debug = System.getProperty("debugRoot") != null;
   private int length;
   private long expectedEnd = 0;

   public RootClass getRootClass()
   {
      Class klass = getClass();
      RootClassLoader loader = (RootClassLoader) klass.getClassLoader();
      return loader.getRootClass(klass);
   }

   public void dump(PrintStream out)
   {
      out.println("Dump of " + getRootClass().getClassName());

      // Use reflection to dump all member variables
      Field[] fields = this.getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++)
      {
         out.print(fields[i].getName() + "=");
         try
         {
            fields[i].setAccessible(true);
            out.print(fields[i].get(this));
         }
         catch (IllegalAccessException x)
         {
            out.print("No Access");
         }
         out.println();
      }
   }

   public void read(RootInput in) throws IOException
   {
      try
      {
         readMembers(in);
      }
      catch (IOException x)
      {
         if (debug)
         {
            dump(System.out);
            x.printStackTrace();
         }
         throw x;
      }
      catch (RuntimeException x)
      {
         if (debug)
         {
            dump(System.out);
            x.printStackTrace();
         }
         throw x;
      }
      catch (Error x)
      {
         if (debug)
         {
            dump(System.out);
            x.printStackTrace();
         }
         throw x;
      }
   }

   protected void readMembers(RootInput in) throws IOException {}

   /*
    * Used by the version unpacking routine to store/retrieve
    * the expected length (actually end position) of a class
    * in the RootInput buffer.
    */
   void setExpectedLength(long start, int len)
   {
      length = len;
      expectedEnd = start + len;
   }

   void checkLength(long end) throws IOException
   {
      //System.out.println("checkLength "+getRootClass().getClassName()+" "+length);
      if ((expectedEnd != 0) && (expectedEnd != end))
         throw new IOException("Error reading " + getRootClass().getClassName() + ": Unexpected length (expected " + length + " got " + (end - expectedEnd + length) + ")");
   }
}
