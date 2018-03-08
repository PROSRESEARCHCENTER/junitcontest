package hep.io.root.core;

import org.apache.bcel.classfile.JavaClass;


/**
 * Interface implemented by all class builders
 * @author tonyj
 * @version $Id: ClassBuilder.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public interface ClassBuilder
{
   String getStem();

   JavaClass build(GenericRootClass name);
}
