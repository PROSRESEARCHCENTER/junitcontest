package org.freehep.util.template;

import java.util.List;
import java.util.Properties;

/** A value provider that returns values based on a properties object.
 * <p>
 * Example of use:
 * <PRE>
 * Hello {v:user.name}
 * </PRE>
 * @author tonyj
 * @version $Id: PropertiesValueProvider.java 8584 2006-08-10 23:06:37Z duns $
 */

public class PropertiesValueProvider implements ValueProvider
{
   private Properties props;
   /** Builds a PropertiesValueProvider which takes its values from
    * the system properties.
    * @see java.lang.System#getProperties()
    */   
   public PropertiesValueProvider()
   {
      this(null);
   }
   /** Builds a PropertiesValueProvider which takes its values from
    * the specified Properties object.
    * @param props The properties to use.
    */   
   public PropertiesValueProvider(Properties props)
   {
      this.props = props;
   }
   public String getValue(String name)
   {
      return props == null ? System.getProperty(name) : props.getProperty(name);
   }
   public List getValues(String name)
   {
      return null;
   }
}