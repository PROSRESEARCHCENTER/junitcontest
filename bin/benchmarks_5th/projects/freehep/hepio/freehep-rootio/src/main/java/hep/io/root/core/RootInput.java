package hep.io.root.core;

import hep.io.root.RootObject;
import java.io.DataInput;
import java.io.IOException;

/**
 * Extension of DataInput with root specific utilities
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootInput.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public interface RootInput extends DataInput
{
   /**
    * @return The RootClassFactory associated with this stream
    */
   RootClassFactory getFactory();

   void setMap(int offset) throws IOException;

   void setPosition(long pos) throws IOException;

   long getPosition() throws IOException;

   /**
    * Returns the Root version which wrote this file
    */
   int getRootVersion();

   /**
    * Returns the RootInput at the top of top of the heirarchy of slices
    */
   RootInput getTop();

   void checkLength(AbstractRootObject object) throws IOException;

   void clearMap() throws IOException;

   int readArray(int[] data) throws IOException;

   int readArray(byte[] data) throws IOException;

   int readArray(short[] data) throws IOException;

   int readArray(float[] data) throws IOException;

   int readArray(double[] data) throws IOException;

   void readFixedArray(int[] data) throws IOException;

   void readFixedArray(byte[] data) throws IOException;

   void readFixedArray(short[] data) throws IOException;

   void readFixedArray(float[] data) throws IOException;

   void readFixedArray(double[] data) throws IOException;

   void readFixedArray(long[] data) throws IOException;
   
   void readMultiArray(Object[] array) throws IOException;

   String readNullTerminatedString(int maxLength) throws IOException;

   RootObject readObject(String type) throws IOException;

   RootObject readObjectRef() throws IOException;

   String readString() throws IOException;

   int readVersion() throws IOException;

   int readVersion(AbstractRootObject object) throws IOException;

   /**
    * Returns a new RootInput stream which represents a slice of this
    * RootInput stream. The new RootInput maintains its own file position
    * independent of the parent stream. The slice starts from the current
    * file position and extends for size bytes.
    * @param size The size of the slice.
    * @return The slice
    */
   RootInput slice(int size) throws IOException;

   /**
    * Slice and decompress
    */
   RootInput slice(int inSize, int outSize) throws IOException;
   /**
    * Reads a double in strange byte order?
    */
   double readTwistedDouble() throws IOException;
   /**
    * For debugging
    */
   void dump() throws IOException;
   void close() throws IOException;
   /**
    * For skipping uninterpretable objects
    */
   void skipObject() throws IOException;
}
