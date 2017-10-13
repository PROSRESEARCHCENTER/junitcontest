// Copyright 2002-2007, FreeHEP.
package hep.aida.ref.xml;

import hep.aida.ITree;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IOnDemandStore;
import hep.aida.ref.AidaUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import hep.aida.IManagedObject;
import hep.aida.dev.IAddable;
import hep.aida.util.Addable;

/**
 * Store associated to XML file, directory structure or archive.
 * 
 * @author tonyj
 * @author Mark Donszelmann
 * @version $Id: AidaXMLStore.java 13360 2007-10-02 23:13:06Z serbo $
 */
public class AidaXMLStore implements IOnDemandStore {
	protected File root;
        protected boolean createNew = false;
        protected boolean useProxies = false;

	public boolean isReadOnly() {
		return false;
	}

	int n = 0;
	
	// IOnDemandStore methods
        // This method is only called for a root file with structure: "zip" or "dir"
	public void read(IDevTree tree, String path)
			throws IllegalArgumentException, IOException {
//	    System.err.println("--> "+root+" "+path);
		if (root == null)
			return;

		path = path.startsWith("/") ? path.substring(1) : path;

		File file = new File(root, path);
		//System.err.println("file dir="+file.isDirectory()+", path="+file.getInnerEntryName());
		if (file.isDirectory()) {
			tree.mkdirs("/"+path);
			File[] files = (File[]) file.listFiles();
			int rootLength = root.getPath().length()+1;
			for (int i = 0; i < files.length; i++) {
				String tmpPath = files[i].getInnerEntryName();
				if (tmpPath == null) tmpPath = files[i].getPath().substring(rootLength);
//				System.err.println(i+"\t "+tmpPath);
				if (files[i].isDirectory()) {
					tree.mkdirs("/"+tmpPath);
					n++;
				} else {
					read(tree, "/"+tmpPath);
				}
			}
			tree.hasBeenFilled("/"+path);
                } else if (useProxies) {
                    String treePath = file.getInnerEntryName();
                    if (treePath == null) treePath = file.getPath();
                    
                    String type = "IManagedObject";
                    int index = treePath.lastIndexOf(".");
                    if (index >= 0) {
                        type = treePath.substring(index+1);
                        treePath = treePath.substring(0, index);
                    }
                    IManagedObject mo = AidaObjectProxy.createProxy(this, path, type);
                    
                    tree.add(AidaUtils.parseDirName(treePath), mo);
                } else {
			InputStream in = new FileInputStream(file);
			try {
				parse(tree, false, in, true);
				n++;
			} catch (Exception x) {
				IOException xx = new IOException("Error reading "
						+ tree.storeName() + " " + path);
				xx.initCause(x);
				throw xx;
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
//		System.err.println(n);
	}

	// IStore methds
	public void read(IDevTree tree, Map optionsMap, boolean readOnly,
			boolean createNew) throws IOException {

		File file = new File(tree.storeName());
		file.isDirectory();
                this.createNew = createNew;
		boolean exists = file.exists();
		if (!exists && (readOnly || !createNew)) {
			throw new IOException("File " + file + " does not exist.");
		}
		if (exists && !readOnly && !createNew && !file.canWrite()) {
			throw new IOException("File " + file + " is a read-only file.");
		}
                if (exists) {
			// FIXME ???
			readOnly |= file.canWrite();

			if (file.isFile()) {
				// we select a plain (xml) file 
				// (recognized archive files are NOT files)
				root = null;
				InputStream in = new FileInputStream(file);
				try {
					String validateString = (String) optionsMap.get("validate");
					boolean validate = (validateString == null)
							|| validateString.equalsIgnoreCase("true");
					parse(tree, true, in, validate);
				} catch (Exception x) {
					IOException xx = new IOException("Error reading "
							+ tree.storeName());
					xx.initCause(x);
					System.err.println(x);
					x.printStackTrace();
					throw xx;
				} finally {
					if (in != null) {
						in.close();
					}
				}
			} else {
				// archive and/or directory
				root = file;
                                useProxies = toBoolean(optionsMap,"useProxies", true);
				read(tree, "/");
			}
		}
	}

	public void commit(IDevTree tree, Map optionsMap) throws IOException {
		String createString = (String) optionsMap.get("createNew");
                if (createString != null &&  createString.equalsIgnoreCase("true"))
                    createNew = true;
                
		String cString = (String) optionsMap.get("compress");
		boolean zip = cString != null && cString.equalsIgnoreCase("zip");
		boolean compress = (cString == null) || cString.equalsIgnoreCase("yes")
				|| cString.equalsIgnoreCase("true")
				|| cString.equalsIgnoreCase("gzip");
		String bString = (String) optionsMap.get("binary");
		boolean binary = (bString != null)
				&& (bString.equalsIgnoreCase("yes") || bString
						.equalsIgnoreCase("true"));
		String[] skip = null;
		if (optionsMap.get("skip") != null)
			skip = AidaUtils.parseString((String) optionsMap.get("skip"));
                
                java.io.File f = new java.io.File(tree.storeName());
                if (createNew && f.exists() && !f.isDirectory()) f.delete();
		commit(tree, new File(tree.storeName()), skip, zip,
				compress, binary);
	}

	public void commit(ITree tree, File file, String[] skip, boolean zip,
			boolean compress, boolean binary) throws IOException {
		if (file.isDirectory() || zip) {
			AidaZipXMLWriter zw = new AidaZipXMLWriter(file, binary, skip);
			zw.toXML(tree);
			zw.close();
		} else {
			OutputStream os = new FileOutputStream(file);
			if (compress) {
				os = new GZIPOutputStream(os);
			}
			AidaXMLWriter out;
			if (binary) {
				out = new AidaXMLWriter(new DataOutputStream(
						new BufferedOutputStream(os)));
			} else {
				out = new AidaXMLWriter(new BufferedWriter(
						new OutputStreamWriter(os)));
			}
			out.toXML(tree);
			out.close();
		}
	}

	public void close() {
		root = null;
	}

        IManagedObject readManagedObject(String path) throws IOException {
            Addable addable = new Addable();
            File file = new File(root, path);
            InputStream in = new FileInputStream(file);
            try {
                parse(addable, false, in, true);
                n++;
            } catch (Exception x) {
                IOException xx = new IOException("Error creating managed object for "+ path);
                xx.initCause(x);
                throw xx;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
             return addable.object();
        }
        
	// NOTE: we need to change this to handle partial reads...
	protected void parse(IAddable tree, boolean markAsFilled, InputStream in,
			boolean validate) throws SAXException,
			ParserConfigurationException, IOException {
		byte[] magic = new byte[4];

		// Check filetype
		PushbackInputStream pin = new PushbackInputStream(in, magic.length);

		// peek first four bytes
		int b = pin.read(magic);
		if (b != magic.length)
			throw new IOException("Unexpected EOF");
		pin.unread(magic);

		// gzipped stream ?
		if (((magic[0] & 0xff) == 0x1f) && ((magic[1] & 0xff) == 0x8b)) {
			pin = new PushbackInputStream(new GZIPInputStream(pin),
					magic.length);
			b = pin.read(magic);
			if (b != magic.length)
				throw new IOException("Unexpected EOF");
			pin.unread(magic);
		}
		in = pin;

		// WBXML ?
		boolean binary = ((magic[0] & 0xff) == 0x03)
				&& ((magic[1] & 0xff) == 0x00)
				&& ((magic[2] & 0xff) == 0x00)
				&& ((magic[3] & 0xff) == 0x6a);

		in = new BufferedInputStream(in);
		AidaHandlerImpl handler = new AidaHandlerImpl(tree, markAsFilled);
		if (binary) {
			// Binary XML
			AidaWBXMLParser parser = new AidaWBXMLParser(handler);
			parser.parse(in);
		} else {
			// Plain XML
			EntityResolver er = new AIDAEntityResolver(AidaParser.class,
					"http://aida.freehep.org/");
			AidaParser parser = new AidaParser(handler, er);
			parser.setValidate(validate);

			InputSource is = new InputSource(in);
                        String id = (tree instanceof ITree) ? ((ITree) tree).storeName() : "AidaXMLStore";
			is.setSystemId(id);
			parser.parse(is);
		}
	}

   private boolean toBoolean(Map options, String key) {
       return toBoolean(options, key, false);
   }
   private boolean toBoolean(Map options, String key, boolean def)
   {
      Object value = options.get(key);
      if (value == null) return def;
      return Boolean.valueOf(value.toString()).booleanValue();
   }
}
