package hep.io.root.core;
import java.io.IOException;
/**
 *
 * @author tonyj
 * @version $Id: IOUtils.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IOUtils
{
   public static void readVariableMultiArray(RootInput in, float[][] source, int[] lengths) throws IOException
   {
      for (int i=0; i<lengths.length; i++)
      {
         byte b = in.readByte();
         if (b == 0) continue;
         int l = lengths[i];
         source[i] = new float[l];
         in.readFixedArray(source[i]);
      }
   }
   public static void readFixedArray(RootInput in, Object[] data, String type) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readObject(type);
   }
}
