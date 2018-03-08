package hep.io.root.reps;
import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TMap;
import hep.io.root.interfaces.TObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TMapRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TMapRep extends AbstractRootObject implements TMap
{
   private String fName;
   private TObject fObject;
   private int fNobjects;
   private Map map;

   public void readMembers(RootInput in) throws IOException
   {
      int v = in.readVersion(this);
      if (v > 2) fObject = (TObject) in.readObject("TObject");
      if (v > 1) fName = in.readObject("TString").toString();
      fNobjects = in.readInt();
      map = new HashMap();
      for (int i = 0; i < fNobjects; i++)
      {
         Object key = in.readObjectRef();
         Object value = in.readObjectRef();
         if (key != null) 
         {
             map.put(key,value);
         }
      }
      in.checkLength(this);
   }

    public Object remove (Object key)
    {
        throw new UnsupportedOperationException();
    }

    public Object get (Object key)
    {
        return map.get(key);
    }

    public boolean containsValue (Object value)
    {
        return map.containsValue(value);
    }

    public boolean containsKey (Object key)
    {
        return map.containsKey(key);
    }

    public void putAll (Map t)
    {
        throw new UnsupportedOperationException();
    }

    public java.util.Collection values ()
    {
        return map.values();
    }

    public int size ()
    {
        return map.size();
    }

    public Object put (Object key, Object value)
    {
        throw new UnsupportedOperationException();
    }

    public java.util.Set keySet ()
    {
        return map.keySet();
    }

    public boolean isEmpty ()
    {
        return map.isEmpty();
    }

    public java.util.Set entrySet ()
    {
        return map.entrySet();
    }

    public void clear ()
    {
        throw new UnsupportedOperationException();
    }
}
