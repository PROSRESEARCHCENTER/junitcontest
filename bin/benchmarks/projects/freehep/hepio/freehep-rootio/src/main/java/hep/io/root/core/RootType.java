package hep.io.root.core;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

/**
 *
 * @author  Tony Johnson
 */
public class RootType extends Type
{
   public final static ArrayType DOUBLEARRAY = new ArrayType(Type.DOUBLE, 1);
   public final static ArrayType FLOATARRAY = new ArrayType(Type.FLOAT, 1);
   public final static ArrayType INTARRAY = new ArrayType(Type.INT, 1);
   public final static ArrayType CHARARRAY = new ArrayType(Type.CHAR, 1);
   public final static Type ROOTINPUT = new ObjectType("hep.io.root.core.RootInput");
   
   RootType(byte t, String name)
   {
      super(t, name);
   }
}
