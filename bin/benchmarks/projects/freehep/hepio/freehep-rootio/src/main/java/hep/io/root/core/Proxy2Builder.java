package hep.io.root.core;


/**
 * @author tonyj
 * @version $Id: Proxy2Builder.java 13617 2009-04-09 22:48:46Z tonyj $
 */
class Proxy2Builder extends ProxyBuilder
{
   public Proxy2Builder()
   {
      super(false);
   }

   public String getStem()
   {
      return "hep.io.root.proxy2";
   }
}
