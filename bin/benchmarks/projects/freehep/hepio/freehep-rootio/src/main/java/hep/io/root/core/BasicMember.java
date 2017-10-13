package hep.io.root.core;

import hep.io.root.RootMember;
import hep.io.root.RootObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;


/**
 *
 * @author Tony Johnson
 */
public abstract class BasicMember implements RootMember
{
   static final NameMangler nameMangler = NameMangler.instance();

   public abstract Type getJavaType();

   public abstract int getMaxIndex(int index);

   public abstract void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className);

   public Object getValue(RootObject obj)
   {
      try
      {
         String methodName = nameMangler.mangleMember(getName());
         Method method = obj.getClass().getMethod(methodName, (Class[]) null);
         return method.invoke(obj, (Object[]) null);
      }
      catch (Throwable x)
      {
         if (x instanceof InvocationTargetException)
            x = ((InvocationTargetException) x).getTargetException();

         throw new RuntimeException("Unexpected error accessing value "+getName(),x);
      }
   }
   public String getVarCounter()
   {
      return null;
   }
}
