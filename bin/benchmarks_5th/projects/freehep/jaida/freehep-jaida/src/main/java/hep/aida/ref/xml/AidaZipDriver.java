// Copyright 2007, FreeHEP
package hep.aida.ref.xml;

import java.io.IOException;
import java.io.OutputStream;

import de.schlichtherle.io.archive.Archive;
import de.schlichtherle.io.archive.zip.JarDriver;
import de.schlichtherle.io.archive.zip.Zip32InputArchive;
import de.schlichtherle.io.archive.zip.Zip32OutputArchive;

/**
 * Class to override compression level for writing (and updating) aida files.
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class AidaZipDriver extends JarDriver {

	private static final long serialVersionUID = -7312165879930787449L;
	
	// NOTE: default compression was undefined and set internally to BEST_COMPRESSION (1), being quite slow
	// we set it to the 5 (center) as BEST_SPEED will produce a slightly bigger file.
	protected Zip32OutputArchive createZip32OutputArchive(Archive arg0,
			OutputStream arg1, Zip32InputArchive arg2) throws IOException {
		Zip32OutputArchive archive = super.createZip32OutputArchive(arg0, arg1, arg2);
		archive.setLevel(5);
		return archive;
	}
}
