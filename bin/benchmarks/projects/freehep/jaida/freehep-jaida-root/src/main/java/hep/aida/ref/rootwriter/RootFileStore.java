package hep.aida.ref.rootwriter;

import hep.aida.ref.rootwriter.converter.Converter;
import hep.aida.IManagedObject;
import hep.aida.ref.rootwriter.converter.ConverterException;
import hep.aida.ref.rootwriter.converter.DefaultConverter;
import hep.aida.ref.rootwriter.converter.ICloud1DConverter;
import hep.aida.ref.rootwriter.converter.ICloud2DConverter;
import hep.aida.ref.rootwriter.converter.IHistogram1DConverter;
import hep.aida.ref.rootwriter.converter.IHistogram2DConverter;
import hep.aida.ref.rootwriter.converter.IProfile1DConverter;
import hep.io.root.output.TDirectory;
import hep.io.root.output.TFile;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author onoprien
 */
public class RootFileStore extends Store {
  
  private String _path;
  private Map<String,Converter> _converters;
  private Converter _defaultConverter;
  
  private TFile _file;
  private TDirectory _currentDir;
  
// -- Construction : -----------------------------------------------------------
  
  public RootFileStore(String path) {
    _path = path;
    _converters = new HashMap<String,Converter>();
    _converters.put("IHistogram1D", new IHistogram1DConverter());
    _converters.put("ICloud1D", new ICloud1DConverter());
    _converters.put("IHistogram2D", new IHistogram2DConverter());
    _converters.put("ICloud2D", new ICloud2DConverter());
    _converters.put("IProfile1D", new IProfile1DConverter());
    _defaultConverter = new DefaultConverter();
  }
  
  
// -- Setters : ----------------------------------------------------------------
  
  public Converter setConverter(String type, Converter converter) {
    return _converters.put(type, converter);
  }
   
  public Converter setDefaultConverter(Converter converter) {
    Converter old = _defaultConverter;
    _defaultConverter = converter;
    return old;
  }
 
// -- Implementing Store : -----------------------------------------------------

  @Override
  public void open() throws IOException  {
    
    _file = new TFile(_path);
    _currentDir = _file;
  }

  @Override
  public void mkdir(String path) {
    makeDirectory(path);
  }

  @Override
  public void cd(String path) {
    if (_file == null) throw new IllegalStateException("Operating on a closed store");
    if (path == null) throw new IllegalArgumentException("Illegal path: "+ path);
    String[] dirNames = path.split("/");
    TDirectory dir = path.startsWith("/") ? _file : _currentDir;
    for (String name : dirNames) {
      if (!name.isEmpty()) {
        dir = dir.findDir(name);
        if (dir == null) throw new IllegalArgumentException("No such directory: "+ path);
      }
    }
    _currentDir = dir;
  }

  @Override
  public void add(Object object) {
    if (_file == null) throw new IllegalStateException("Operating on a closed store");
    add(object, _currentDir);
  }

  @Override
  public void add(Object object, String path) {
    TDirectory dir = makeDirectory(path);
    add(object, dir);
  }

  @Override
  public void close() throws IOException {
    if (_file != null) {
      _currentDir = null;
      _file.close();
      _file = null;
    }
  }
  
// -- Local methods : ----------------------------------------------------------
  
  private void add(Object object, TDirectory dir) {
    
    String key;
    try {
      key = ((IManagedObject)object).type();
    } catch (ClassCastException x) {
      key = object.getClass().getName();
    }
    
    Object converted = null;
    Set<String> triedKeys = null;
    while (key != null) {
      Converter converter = _converters.get(key);
      if (converter == null) converter = _defaultConverter;
      if (converter == null) throw new IllegalArgumentException("No converter for "+ key);
      try {
        converted = converter.convert(object);
        key = null;
      } catch (ConverterException x) {
        key = x.getKey();
        if (triedKeys == null) {
          triedKeys = new HashSet<String>();
          triedKeys.add(key);
        } else {
          if (triedKeys.contains(key)) {
            throw new IllegalArgumentException("No converter for "+ key);
          } else {
            triedKeys.add(key);
          }
        }
        object = x.getObject();
      }
    }
    
    if (converted != null) dir.add(converted);
  }
  
  private TDirectory makeDirectory(String path) {
    if (_file == null) throw new IllegalStateException("Operating on a closed store");
    if (path == null) throw new IllegalArgumentException("Illegal path: "+ path);
    String[] dirNames = path.split("/");
    TDirectory dir = path.startsWith("/") ? _file : _currentDir;
    for (String name : dirNames) {
      if (!name.isEmpty()) {
        TDirectory child = dir.findDir(name);
        if (child == null) {
          child = dir.mkdir(name);
        }
        dir = child;
      }
    }
    return dir;
  }
  
}
