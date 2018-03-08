package hep.aida.ref.xml.converters;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.freehep.util.argv.ArgumentFormatException;
import org.freehep.util.argv.ArgumentParser;
import org.freehep.util.argv.BooleanOption;
import org.freehep.util.argv.MissingArgumentException;
import org.freehep.util.argv.StringParameter;
import org.xml.sax.SAXException;

public class ConvertHandler implements CommandHandler {

	public void run(List args) {
		BooleanOption help = new BooleanOption("-help", "-h",
				"Show this help page", true);
		BooleanOption binary = new BooleanOption("-binary", "-b",
				"Write destination (zip)file in 'binary' format");
		BooleanOption zip = new BooleanOption("-zip", "-b",
				"Write destination file in 'zip' format");
		StringParameter in = new StringParameter("from",
				"Source file to convert from");
		StringParameter out = new StringParameter("to",
				"Destination file to convert to");

		ArgumentParser parser = new ArgumentParser("aida convert");
		parser.add(help);
		parser.add(binary);
		parser.add(zip);
		parser.add(in);
		parser.add(out);

		List extra;
		try {
			extra = parser.parse(args);

			if (!extra.isEmpty() || help.getValue()) {
				parser.printUsage(System.out);
				return;
			}
		} catch (MissingArgumentException e) {
			parser.printUsage(System.out);
			return;
		} catch (ArgumentFormatException e) {
			parser.printUsage(System.out);
			return;
		}

		try {
			InputStream ins = new FileInputStream(in.getValue());
			byte[] magic = new byte[4];

			// Check filetype
			PushbackInputStream pin = new PushbackInputStream(ins, magic.length);

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
			ins = pin;

			// WBXML ?
			boolean isBinary = ((magic[0] & 0xff) == 0x03)
					&& ((magic[1] & 0xff) == 0x00)
					&& ((magic[2] & 0xff) == 0x00)
					&& ((magic[3] & 0xff) == 0x6a);

			ins = new BufferedInputStream(ins);
			OutputStream outs = new FileOutputStream(out.getValue());

			if (isBinary) {
				if (zip.getValue()) {
					BinaryToZipHandler handler = new BinaryToZipHandler();
					handler.convert(in.getValue(), out.getValue(), binary
							.getValue());
				} else {
					FromBinaryHandler handler = new FromBinaryHandler();
					handler.convert(ins, outs, binary.getValue());
				}
			} else {
				if (zip.getValue()) {
					AsciiToZipHandler handler = new AsciiToZipHandler();
					handler.convert(in.getValue(), out.getValue(), binary
							.getValue());
				} else {
					FromAsciiHandler handler = new FromAsciiHandler();
					handler.convert(ins, outs, binary.getValue());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
