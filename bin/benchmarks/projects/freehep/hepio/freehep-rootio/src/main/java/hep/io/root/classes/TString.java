package hep.io.root.classes;

import hep.io.root.core.GenericRootClass;
import hep.io.root.core.StreamerInfo;
import org.apache.bcel.generic.Type;

/**
 *
 * @author Tony Johnson
 * @version $Id: TString.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class TString extends GenericRootClass
{
   /** Creates a new instance of TSTring */
   public TString(String name, StreamerInfo info)
   {
      super(name, info);
   }

   /**
    * The method used to convert the object to its method type.
    */
   protected String getConvertMethod()
   {
      return "toString";
   }

   /**
    * The type that will be used when this class is stored as a member, or as a return
    * type from a method.
    */
   protected Type getJavaTypeForMethod()
   {
      return Type.STRING;
   }
}
