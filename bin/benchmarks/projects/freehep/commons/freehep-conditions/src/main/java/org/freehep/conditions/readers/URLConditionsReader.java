package org.freehep.conditions.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.*;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsInvalidException;
import org.freehep.conditions.base.AbstractConditionsSet;
import org.freehep.conditions.base.ConditionsReader;
import org.freehep.conditions.base.DefaultConditions;
import org.freehep.conditions.base.DefaultConditionsManager;
import org.freehep.conditions.util.Parser;
import org.freehep.conditions.util.cache.FileCache;

/**
 * {@link ConditionsReader} that provides access to conditions data contained in 
 * archive files, directories, or classpath resources. 
 * It is also capable of reading data from arbitrary URLs. The reader can be configured
 * to cache remote archives for subsequent use.
 * <p>
 * An instance of this reader is constructed with a list of URL templates that may contain
 * parameters in the <tt>${xxx}</tt> format. Whenever the reader updates itself in response
 * to a triggering event, it goes through the list, attempting to resolve parameters using the
 * current configuration based on previously processed events.
 * The first successfully resolved and validated URL is set as a context URL. Whenever the
 * context URL changes, listeners of all conditions handled by this reader are notified
 * of possible changes.
 * <p>
 * See {@link AbstractConditionsReader} for details on how the configuration is maintained.
 * By default, this reader will only maintain configuration parameters whose name appear
 * in templates, and it will only use values from the latest processed update triggering event
 * (no accumulation). This behavior can be changed with a call to 
 * {@link #setConfiguration setConfiguration(...)} method. 
 * <p>
 * Note that, by default, <tt>null</tt> is considered to be a valid context. Therefore,
 * {@link #update(ConditionsEvent)} method will not throw {@link ConditionsUpdateException} 
 * even if none of the templates can be resolved. This ensures that conditions 
 * whose names can be parsed into valid URLs with <tt>null</tt>, as well as those handled by other
 * readers, remain accessible. To modify this behavior, use a context validator that
 * rejects <tt>null</tt> URLs.
 * <p>
 * When this reader is asked to create or update a conditions object with a given name, it
 * strips prefix from the name (substring terminated with the first occurrence of ":", if any),
 * then resolves the name against the context URL to produce conditions URL.
 * The conditions URL is then used by {@link #open} and {@link update(Conditions)} methods
 * to access the conditions data. <tt>ConditionsSet</tt> objects created by this reader 
 * attempt to load <tt>Properties</tt> object from the stream provided by {@link #open} method.
 * <p>
 * {@link DefaultContextValidator} class implements several useful methods for validating URLs.
 * <p>
 * The demo in the <tt>org.freehep.conditions.demo</tt> package provides a couple of examples
 * that use <tt>URLConditionsReader</tt>.
 *
 * @version $Id: $
 * @author Tony Johnson
 * @author Dmitry Onoprienko
 */
public class URLConditionsReader extends AbstractConditionsReader {

// -- Private parts : ----------------------------------------------------------

  private boolean _cacheEnabled = true;
  private File _cacheDir;
  private FileCache _cache;
  
  protected URL _context;
  protected ArrayList<String> _contextTemplates;
  protected URLValidator _contextValidator;
  protected HashMap<String,URLStreamHandler> _handlers;


// -- Construction : -----------------------------------------------------------
  
  /**
   * Creates an instance of <tt>URLConditionsReader</tt>.
   * @param manager ConditionsManager that will be using this reader.
   * @param templates URL templates.
   */
  public URLConditionsReader(DefaultConditionsManager manager, String... templates) {
    super(manager);
    _contextTemplates = new ArrayList<>(Arrays.asList(templates));
    Map<String,? extends Object> config = new HashMap<>();
    for (String template : templates) {
      for (String parameter : Parser.getParameterNames(template)) {
        config.put(parameter, null);
      }
    }
    setConfiguration(config, true, false);
  }

  
// -- Setters and getters : ----------------------------------------------------
  
  public void setContextValidator(URLValidator validator) {
    _contextValidator = validator;
  }
  
  /**
   * Enables or disables caching of remote zip files.
   * By default, caching is enabled.
   */
  public void setCacheEnabled(boolean enabled) {
    _cacheEnabled = enabled;
  }
  
  /**
   * Sets cache directory and enables caching of remote zip files.
   * @param path Path to cache directory.
   */
  public void setCacheDirectory(String path) {
    _cacheDir = new File(path);
    if (_cacheDir.exists()) {
      if (!_cacheDir.isDirectory()) throw new IllegalArgumentException(path +" is not a directory");
    } else {
      if (!_cacheDir.mkdirs()) throw new IllegalArgumentException("Cache directory "+ path +" cannot be created");
    }
    _cacheEnabled = true;
  }
  
  /**
   * Adds a URL stream handler to be used by this reader when connecting to URLs.
   */
  public void addURLHandler(String protocol, URLStreamHandler handler) {
    if (_handlers == null) _handlers = new HashMap(4);
    _handlers.put(protocol, handler);
  }


// -- Implementing ConditionsReader : ------------------------------------------
  
  @Override
  public InputStream open(String name) throws IOException, ConditionsInvalidException {
    URL url = resolveConditionsName(name);
    return url.openStream();
  }
  
  protected URL resolveConditionsName(String conditionsName) throws ConditionsInvalidException {
    try {
      String name = Parser.stripPrefix(conditionsName);
      return new URL(_context, name);
    } catch (MalformedURLException x) {
      throw new ConditionsInvalidException("Unable to resolve conditions name "+ conditionsName, x);
    }
  }

  /**
   * Updates this reader in response to the given event.
   * 
   * @param event Update triggering event.
   * @return True if context URL has changed as a result of the reader update.
   * @throws ConditionsUpdateException if no template can be resolved into a valid context URL,
   *                                   and context validator that does not accept <tt>null</tt>
   *                                   URLs is set on this reader.
   */
  @Override
  public boolean update(ConditionsEvent event) throws ConditionsUpdateException {
    
    if (!super.update(event)) return false;
    
    URL context = resolveTemplate();
    
    if ((context != null && context.equals(_context)) || (context == _context) ) return false;
    _context = context;
    return true;
  }
  
  /**
   * Finds the first resolvable template and constructs the context URL.
   * 
   * @return Context URL to be used for fetching conditions data.
   * @throws ConditionsUpdateException if no template can be resolved into a valid URL.
   */
  protected URL resolveTemplate() throws ConditionsUpdateException {
    
    for (String template : _contextTemplates) {
      
      // expand template using current configuration parameters
            
      String spec = Parser.resolveParameters(template, _config, null);
      if (spec == null) continue; // not all parameters resolved, try next template
      
      // see if remote zip file needs to be cached
      
      if (_cacheEnabled && spec.startsWith("jar:http:") && spec.endsWith(".zip!/")) {
        try {
          File file = getCache().getCachedFile(new URL(spec.substring(4, spec.length()-2)));
          URI uri = file.toURI();
          spec = "jar:"+ uri +"!/";
        } catch (IOException x) {
        }
      }
      
      // construct and validate URL
      
      URLStreamHandler handler = null;
      String[] tokens = spec.split(":");
      if (tokens.length > 1) {
        handler = _handlers == null ? null : _handlers.get(tokens[0]);
      }
      URL out;      
      try {
        out = new URL(null, spec, handler);
      } catch (MalformedURLException x) {
        continue; // not a URL, try next template
      }
      
      // validate URL
      
      if (_contextValidator == null || _contextValidator.isValid(out)) return out;
    }

    // None of the templates produced usable context URL;
    
    if (_contextValidator == null || _contextValidator.isValid(null)) {
      return null;
    } else {
      throw new ConditionsUpdateException("URLConditionsReader failed to construct a valid context URL");
    }
    
  }

  @Override
  public boolean update(DefaultConditions conditions) throws ConditionsInvalidException {
    if (conditions instanceof AbstractConditionsSet) {
      AbstractConditionsSet conSet = (AbstractConditionsSet) conditions;
      try {
        conSet.set(open(conSet.getName()));
        return true;
      } catch (FileNotFoundException x) {
        throw new ConditionsInvalidException("ConditionsSet " + conditions.getName() + ", data file not found.", x);
      } catch (IOException x) {
        throw new RuntimeException("ConditionsSet " + conditions.getName() + ", error updating", x);
      }
    } else {
      return super.update(conditions);
    }
  }


// -- Handling cache : ---------------------------------------------------------
  
  protected FileCache getCache() throws IOException {
    if (_cache == null) {
      if (_cacheDir == null) {
        _cacheDir = new File(getConditionsManager().getConditionsHome(), "cache");
      }
      _cache = new FileCache(_cacheDir);
    }
    return _cache;
  }

}
