// Copyright FreeHEP, 2007
package hep.aida.ref.xml;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPointSet;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITuple;
import hep.aida.ref.AidaUtils;
import hep.aida.test.AidaTestCase;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.freehep.util.argv.ArgumentFormatException;
import org.freehep.util.argv.ArgumentParser;
import org.freehep.util.argv.BooleanOption;
import org.freehep.util.argv.MissingArgumentException;
import org.freehep.util.argv.StringOption;
import org.freehep.util.argv.StringParameter;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileOutputStream;
import de.schlichtherle.io.FileWriter;

/**
 * Writes AIDA Tree to zip archive.
 * 
 * @author serbo
 * @author Mark Donszelmann
 * @version $Id: AidaZipXMLWriter.java 13360 2007-10-02 23:13:06Z serbo $
 */
public class AidaZipXMLWriter {
	private static String dirType = "dir";
	protected String[] skip;
	protected boolean binary;
	protected List list;
	protected File root;

	public static void main(String[] args) throws Exception {
		BooleanOption helpOpt = new BooleanOption("-help", "-h",
				"Show this help page", true);
		BooleanOption verboseOpt = new BooleanOption("-verbose", "-v",
				"Set verbose output", false);
		BooleanOption overWriteOpt = new BooleanOption("-overwrite", "-o",
				"Overwrite existing output file", false);
		BooleanOption binaryOpt = new BooleanOption("-binary", "-b",
				"Write in binary format (if available)", false);
		BooleanOption zipOpt = new BooleanOption("-zip", "-z",
				"Write in zip format (.zip)", false);
		BooleanOption compressOpt = new BooleanOption("-compress", "-c",
				"Write in compressed format (.gz or .zip)", false);
		BooleanOption verifyOpt = new BooleanOption("-verify", "-d",
				"Check for diffs between input and output", false);

		StringOption storeType = new StringOption("-type", "-t", "Store_Type",
				"xml", "Type of the file: xml, hbook, root, etc.");
		StringOption skipTypes = new StringOption("-skip", "-s",
				"ITuple,ICloud2D", null,
				"AIDA Object Types to skip during conversion: comma-separated list, no spaces");

		StringParameter sourceFile = new StringParameter("inputFile",
				"Path to the input file (aida, hbook, root)");
		StringParameter dstFile = new StringParameter("outputFile",
				"Path to the output file");

		String originalFileType = "xml";
		String[] skip = null;
		boolean overWrite = false;
		boolean verbose = false;
		boolean binary = false;
		boolean zip = false;
		boolean compress = false;
		boolean verify = false;

		ArgumentParser cl = new ArgumentParser("AidaZipXMLWriter ");
		cl.add(helpOpt);
		cl.add(verboseOpt);
		cl.add(overWriteOpt);
		cl.add(binaryOpt);
		cl.add(zipOpt);
		cl.add(compressOpt);
		cl.add(verifyOpt);
		cl.add(storeType);
		cl.add(skipTypes);
		cl.add(sourceFile);
		cl.add(dstFile);

		try {
			List extra = cl.parse(args);

			if (!extra.isEmpty() || helpOpt.getValue()) {
				cl.printUsage(System.out);
				return;
			}
			verbose = verboseOpt.getValue();
			if (storeType.getValue() != null
					&& !storeType.getValue().trim().equals(""))
				originalFileType = storeType.getValue();

			String skipString = skipTypes.getValue();
			if (skipString != null && !skipString.trim().equals("")) {
				Map map = AidaUtils.parseOptions(skipString);
				if (map.size() > 0) {
					skip = new String[map.size()];
					skip = (String[]) map.keySet().toArray(skip);
				}
			}

			overWrite = overWriteOpt.getValue();
			binary = binaryOpt.getValue();
			compress = compressOpt.getValue();
			zip = zipOpt.getValue();
			verify = verboseOpt.getValue();

			// End of argument parsing

			File inFile = new File(sourceFile.getValue());
			if (!inFile.exists()) {
				throw new IOException("Input file does not exist: "
						+ sourceFile.getValue());
			}

			File outFile = new File(dstFile.getValue());
			if (!overWrite && outFile.exists()) {
				throw new IOException("Output file already exists: "
						+ dstFile.getValue());
			}

			if (verbose) {
				System.out.println("Will convert: " + originalFileType
						+ " file into AIDA " + (binary ? "(Binary) " : "")
						+ "XML");
				System.out.println("\tInput File:      " + inFile.getPath());
				System.out.println("\tInput File Type: " + originalFileType);
				System.out.println("\tOutput File:     " + outFile.getPath());
				String tmpSkip = null;
				if (skip != null && skip.length > 0) {
					tmpSkip = "\n";
					for (int i = 0; i < skip.length; i++)
						tmpSkip += "\t\t\t    " + skip[i] + "\n";
				}
				System.out.println("\tSkip AIDA types: " + tmpSkip);
			}

			long t0 = System.currentTimeMillis();
			IAnalysisFactory af = IAnalysisFactory.create();
			ITreeFactory tf = af.createTreeFactory();
			ITree srcTree = tf.create(sourceFile.getValue(), originalFileType, true, false);
			int srcLength = srcTree.listObjectTypes("/", true).length;
			System.err.println("Reading "+srcLength+" records.");
			System.err.println("Total time to read: "
					+ ((System.currentTimeMillis() - t0) / 1000) + " s.");
			t0 = System.currentTimeMillis();

			AidaXMLStore store = new AidaXMLStore();
			store.commit(srcTree, outFile, skip, zip, compress, binary);
			System.err.println("Total time to write: "
					+ ((System.currentTimeMillis() - t0) / 1000) + " s.");

			if (verify) {
				t0 = System.currentTimeMillis();
				ITree dstTree = tf.create(dstFile.getValue(), originalFileType);
				System.err.println("Verifying "+dstTree.listObjectNames("/", true).length+" records.");
//				System.err.println("And again "+dstTree.listObjectNames("/", true).length+" records.");
//				assertEquals(srcTree, dstTree);
				System.err.println("Total time to verify: "
						+ ((System.currentTimeMillis() - t0) / 1000) + " s.");
			}
		} catch (MissingArgumentException mae) {
			System.out.println(mae.getMessage());
			System.exit(1);
		} catch (ArgumentFormatException afe) {
			System.out.println(afe.getMessage());
			System.exit(1);
		} catch (Exception e) {
			if (verbose) {
				System.out.println("ERROR:  " + e.getMessage());
				e.printStackTrace();
			} else
				throw e;
		}
	}

	// FIXME, move to aida-test
	private static void assertEquals(ITree dstTree, ITree srcTree) {
		String[] names = srcTree.listObjectNames("/", true);

		// same number of objects ?
		TestCase.assertEquals(dstTree.listObjectNames("/", true).length,
				names.length);

		// objects the same ?
		for (int i = 0; i < names.length; i++) {
			if (!names[i].endsWith("/")) {
				IManagedObject dstObject = dstTree.find(names[i]);
				IManagedObject srcObject = srcTree.find(names[i]);
				AidaTestCase.assertEquals(dstObject.getClass(), srcObject
						.getClass());
				AidaTestCase.assertEquals(dstObject, srcObject);
				if (dstObject instanceof ITuple) {
					AidaTestCase.assertEquals((ITuple) dstObject,
							(ITuple) srcObject);
				} else if (dstObject instanceof IDataPointSet) {
					AidaTestCase.assertEquals((IDataPointSet) dstObject,
							(IDataPointSet) srcObject);
				} else if (dstObject instanceof IHistogram1D) {
					AidaTestCase.assertEquals((IHistogram1D) dstObject,
							(IHistogram1D) srcObject);
				} else if (dstObject instanceof IHistogram2D) {
					AidaTestCase.assertEquals((IHistogram2D) dstObject,
							(IHistogram2D) srcObject);
				} else if (dstObject instanceof IHistogram3D) {
					AidaTestCase.assertEquals((IHistogram3D) dstObject,
							(IHistogram3D) srcObject);
				} else if (dstObject instanceof ICloud1D) {
					AidaTestCase.assertEquals((ICloud1D) dstObject,
							(ICloud1D) srcObject);
				} else if (dstObject instanceof ICloud2D) {
					AidaTestCase.assertEquals((ICloud2D) dstObject,
							(ICloud2D) srcObject);
				} else if (dstObject instanceof ICloud3D) {
					AidaTestCase.assertEquals((ICloud3D) dstObject,
							(ICloud3D) srcObject);
				} else if (dstObject instanceof IProfile1D) {
					AidaTestCase.assertEquals((IProfile1D) dstObject,
							(IProfile1D) srcObject);
				} else if (dstObject instanceof IProfile2D) {
					AidaTestCase.assertEquals((IProfile2D) dstObject,
							(IProfile2D) srcObject);
				} else {
					AidaTestCase.fail("IManagedObject of unknown class: "
							+ dstObject.getClass());
				}
			}
		}
	}

	AidaZipXMLWriter(File file) throws IOException {
		this(file, null);
	}

	AidaZipXMLWriter(File file, String[] skip) throws IOException {
		this(file, true, skip);
	}

	AidaZipXMLWriter(File file, boolean binary, String[] skip)
			throws IOException {
		this.root = file;
		this.binary = binary;
		this.skip = skip;
		list = new ArrayList();
	}

	void toXML(ITree tree) {
		String[] objNames = tree.listObjectNames("/", true);
		String[] objTypes = null;
		if (skip != null && skip.length > 0)
			objTypes = tree.listObjectTypes("/", true);

		for (int i = 0; i < objNames.length; i++) {

			// Skip writing some types to file
			if (skip != null && skip.length > 0
					&& AidaUtils.findInArray(objTypes[i], skip) >= 0)
				continue;

			int pos = objNames[i].lastIndexOf('/');
			String objPath = null;
			IManagedObject mo = null;

			// For directories
			if ((pos + 1) == objNames[i].length()) {
				String tmp = objNames[i].substring(0, pos);
				int pos2 = tmp.lastIndexOf('/');
				objPath = tmp.substring(0, pos2 + 1);
				String objName = objNames[i].substring(pos2 + 1);
				mo = new DirectoryObject(objName);

				// For other objects
			} else {
				objPath = objNames[i].substring(0, pos + 1);
				mo = tree.find(objNames[i]);
			}

			toXML(mo, objPath);
		}
	}

	// "path" is the directory where to put this IManagedObject, must end with
	// "/"
	void toXML(IManagedObject mo, String path) {
		// Skip writing some types to file
		if (mo != null && skip != null && skip.length > 0
				&& AidaUtils.findInArray(mo.type(), skip) >= 0)
			return;

		String fullPath = path + mo.name();
		boolean isDirectory = mo.type().equalsIgnoreCase(dirType);

		// handle types
		if (isDirectory) {
			if (fullPath.endsWith("/")) {
				fullPath = fullPath.substring(0, fullPath.length()-1);
			}
		} else {
			fullPath = fullPath + "." + mo.type();
		}
		String zipPath = fullPath;
		if (zipPath.startsWith("/"))
			zipPath = zipPath.substring(1);

//		System.err.println(zipPath);
		// write data
		try {
			File file = new File(root, zipPath);

			if (isDirectory) {
				file.mkdirs();
			} else {
				AidaXMLWriter axw = null;
				try {
					if (binary) {
						OutputStream os = new BufferedOutputStream(
								new FileOutputStream(file));
						list.add(os);
						axw = new AidaXMLWriter(new DataOutputStream(os));
					} else {
						Writer writer = new BufferedWriter(new FileWriter(file));
						list.add(writer);
						axw = new AidaXMLWriter(writer);
					}
					axw.toXML(mo, path);
				} catch (Exception e) {
					System.out.println("Error writing element: " + fullPath
							+ ", " + mo.type());
					e.printStackTrace();
				}
				if (axw != null) {
					axw.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void close() throws IOException {

		root = null;

		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if (obj instanceof Writer)
				((Writer) obj).close();
			else if (obj instanceof OutputStream)
				((OutputStream) obj).close();
		}
		list.clear();
	}

	class DirectoryObject extends hep.aida.ref.ManagedObject {

		DirectoryObject(String name) {
			super(name);
		}

		public String type() {
			return dirType;
		}
	}
}