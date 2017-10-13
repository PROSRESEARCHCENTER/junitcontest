package hep.io.root;


/**
 * Represents a single member of a RootClass
 * @author tonyj
 * @version $Id: RootMember.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface RootMember
{
   public int getArrayDim();

   public String getComment();

   public String getName();

   public RootClass getType();

   public Object getValue(RootObject object);
}
