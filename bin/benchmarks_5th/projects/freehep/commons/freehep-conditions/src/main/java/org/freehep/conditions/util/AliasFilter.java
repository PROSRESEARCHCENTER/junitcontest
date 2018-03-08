package org.freehep.conditions.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.EventFilter;

/**
 * Filter that treats "detector" field in the event as an alias and replaces it with
 * whatever string that alias points to.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class AliasFilter implements EventFilter {

// -- Private parts : ----------------------------------------------------------
  
  final private Properties _aliases;

// -- Construction and initialization : ----------------------------------------
  
  public AliasFilter() {
    _aliases = new Properties();
  }
  
  public AliasFilter(Properties aliases) {
    _aliases = (Properties) aliases.clone();
  }

// -- Setters : ----------------------------------------------------------------

  public String addAlias(String alias, String target) {
    return (String) _aliases.setProperty(alias, target);
  }
  
  public boolean addAliases(Properties aliases) {
    boolean out = false;
    for (Map.Entry<Object,Object> e : aliases.entrySet()) {
      out = _aliases.setProperty(e.getKey().toString(), e.getValue().toString()) != null || out;
    }
    return out;
  }

  public void loadAliases(String location) {
    InputStream in = null;
    try {
      in = (location.contains(":")) ? new URL(location).openStream() : new FileInputStream(new File(location));
      _aliases.load(in);
        
    } catch (IOException x) {
      throw new IllegalArgumentException("Failed to load aliases from "+ location, x);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException x) {
        }
      }
    }
  }
  
// -- Filtering : --------------------------------------------------------------

  @Override
  public boolean pass(ConditionsEvent event) {
    String detector = event.getDetector();
    if (detector != null) {
      detector = resolveAlias(detector);
      event.put("detector", detector);
    }
    return true;
  }
  
// -- Local methods : ----------------------------------------------------------
  
  protected String resolveAlias(final String alias) {
    String target = alias;
    String name = alias;
    int maxTranslations = 100;
    while (name != null) {
      if (--maxTranslations < 0) throw new RuntimeException("Cyclic translation for alias "+ alias);
      target = name;
      name = _aliases.getProperty(name);
    }
    return target;
  }
  
}
