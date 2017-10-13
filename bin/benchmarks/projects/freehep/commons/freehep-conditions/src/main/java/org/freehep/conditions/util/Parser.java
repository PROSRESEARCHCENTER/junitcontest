package org.freehep.conditions.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utilities for handling conditions names and data location templates.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class Parser {
  
// -- Private parts : ----------------------------------------------------------
  
  static final private Pattern _pPar = Pattern.compile("\\$\\{([^}]+)\\}");
  
// -- Handling parameters in templates : ---------------------------------------
  
  /**
   * Replaces parameters in the <tt>${key}</tt> format found in the template with values from specified maps.
   * Returns <tt>null</tt> if at least one of the parameters cannot be resolved.
   * 
   * @param template Template.
   * @param par Map of parameter names to values (values are converted to <tt>String</tt> by calling their <tt>toString()</tt>
   *            methods if necessary.
   * @param parDefault Map of parameter names to default values (may be <tt>null</tt>).
   * @return The string with parameter place holders replaced by their values.
   */
  public static String resolveParameters(String template, Map<String, ? extends Object> par, Map<String, ? extends Object> parDefault) {
    return resolveParameters(template, par, parDefault, false, null);
  }
  
  /**
   * Replaces parameters in the <tt>${key}</tt> format found in the template with values from specified maps.
   * 
   * @param template Template.
   * @param par Map of parameter names to values (values are converted to <tt>String</tt>
   *            by calling their <tt>toString()</tt> methods if necessary.
   * @param parDefault Map of parameter names to default values (may be <tt>null</tt>).
   * @param globalDefault Default value for parameters not found in the maps. 
   *                      If <tt>null</tt>, unresolved parameter place holders are left unchanged.
   * @return The string with parameter place holders replaced by their values.
   */
  public static String resolveParameters(String template, Map<String, ? extends Object> par, Map<String, ? extends Object> parDefault, String globalDefault) {
    return resolveParameters(template, par, parDefault, true, globalDefault);
  }
  
  /**
   * Replaces parameters in the <tt>${name}</tt> format found in the template with values from specified maps.
   * 
   * @param template Template.
   * @param par Map of parameter names to values (values are converted to <tt>String</tt> by calling their <tt>toString()</tt>
   *            methods if necessary.
   * @param parDefault Map of parameter names to default values
   * @param mode Defines behavior when some parameters cannot be resolved. If <tt>null</tt>, unresolved parameter 
   *             place holders are left unchanged. If empty string, an empty string is returned whenever there is at least one
   *             unresolved parameter. If non-empty string, the value is used as a replacement for unresolved parameters.
   * @return The string with parameter place holders replaced by their values.
   */
  private static String resolveParameters(String template, Map<String, ? extends Object> par, Map<String, ? extends Object> parDefault, boolean useGlobalDefault, String globalDefault) {
      Matcher m = _pPar.matcher(template);
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        String key = m.group(1);
        Object value = null;
        if (par != null) {
          value = par.get(key);
        }
        if (value == null && parDefault != null) {
          value = parDefault.get(key);
          if (value != null) value = value.toString();
        }
        if (value == null) {
          if (useGlobalDefault) {
            if (globalDefault == null) {
              m.appendReplacement(sb, "\\$\\{");
              sb.append(key).append("}");
            } else {
              m.appendReplacement(sb, globalDefault);
            }
          } else {
            return null;
          }
        } else {
          m.appendReplacement(sb, value.toString());
        }
      }
      m.appendTail(sb);
      return sb.toString();
  }
  
  public static Set<String> getParameterNames(String template) {
    Matcher m = _pPar.matcher(template);
    Set<String> out = new HashSet<>();
    while (m.find()) {
      out.add(m.group(1));
    }
    return out;
  }

// -- Parsing conditions names : -----------------------------------------------

  public static ConditionsName splitName(String name) {
    return new ConditionsName(name);
  }
  
  public static String stripPrefix(String name) {
    int i = name.indexOf(":");
    return i == -1 ? name : name.substring(i+1);
  }
  
  public static class ConditionsName {
    
    final private String prefix;
    final private String id;
    final private Map<String,String> query;
    
    ConditionsName(String name) {
      int i = name.indexOf(":");
      if (i == -1) {
        prefix = "";
      } else {
        prefix = name.substring(0, i);
        name = name.substring(i+1);
      }
      i = name.lastIndexOf("?");
      if (i == -1) {
        id = name;
        query = Collections.emptyMap();
      } else {
        id = name.substring(0, i);
        String[] pairs = name.substring(i+1).split("&");
        query = new LinkedHashMap<>(pairs.length*2);
        for (String pair : pairs) {
          i = pair.indexOf("=");
          if (i == -1) {
            query.put(pair, "");
          } else {
            query.put(pair.substring(0, i), pair.substring(i+1));
          }
        }
      }
    }
    
    public String getPrefix() {
      return prefix;
    }

    public String getId() {
      return id;
    }

    public Map<String,String> getQuery() {
      return query;
    }
    
  }
  
}
