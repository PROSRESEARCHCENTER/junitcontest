package hep.io.root.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controls name mangling when building Java classes corresponding to Root classes.
 * @author tonyj
 * @version $Id: NameMangler.java 13618 2009-04-10 00:02:54Z tonyj $
 */
public class NameMangler
{
   private final static NameMangler theNameMangler = new NameMangler();
   private Pattern pattern = Pattern.compile("^(hep\\.io\\.root\\.(?:\\w+))\\.(.+)$");

   public static NameMangler instance()
   {
      return theNameMangler;
   }

   /**
    * Given a root class name, returns the name of the corresponding java interface.
    */
   public String mangleInterfaceName(String rootClassName)
   {
      return mangleFullClassName("hep.io.root.interfaces",rootClassName);
   }
   /**
    * Given a full java class name, returns the corresponding root class.
    */
   String getClassForJavaClass(String name)
   {
      Matcher matcher = pattern.matcher(name);
      if (matcher.matches())
      {
         return decodeClassName(matcher.group(2));
      }
      else throw new RuntimeException("Java class name "+name+" illegal for root");   }
   /**
    * Given a full java class name, returns the stem.
    */
   String getStemForJavaClass(String name)
   {
      Matcher matcher = pattern.matcher(name);
      if (matcher.matches())
      {
         return matcher.group(1);
      }
      else throw new RuntimeException("Java class name "+name+" illegal for root");
   }

   String mangleFullClassName(String stem, String in)
   {
      return stem+"."+encodeClassName(in);
   }
   
   /**
    * Name mangling applied to root member variables.
    * By default:
    * <ul>
    * <li>If the name begins with f followed by an uppercase letter, we remove the f
    * <li>If the variable begins with m_ we remove it
    * <li>We uppercase the initial letter
    * <li>Prepend get
    * </ul>
    */
   public String mangleMember(String in)
   {
      if (in.length() >= 2)
      {
         if ((in.charAt(0) == 'f') && Character.isUpperCase(in.charAt(1)))
            in = in.substring(1);
         else if (in.startsWith("m_"))
            in = in.substring(2);
      }
      if (in.length() >= 1)
      {
         if (Character.isLowerCase(in.charAt(0)))
            in = Character.toUpperCase(in.charAt(0)) + in.substring(1);
      }
      return "get" + in;
   }

   private String encodeClassName(String in) {
      in = in.replace("<", "$LT$");
      in = in.replace(">", "$GT$");
      in = in.replace("::", ".");
      return in;
   }
   private String decodeClassName(String in) {
      in = in.replace("$LT$","<");
      in = in.replace("$GT$",">");
      in = in.replace(".","::");
      return in;
   }
}
