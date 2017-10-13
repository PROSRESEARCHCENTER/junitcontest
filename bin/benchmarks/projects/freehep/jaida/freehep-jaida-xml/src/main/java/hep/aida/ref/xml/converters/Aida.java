package hep.aida.ref.xml.converters;

import java.util.List;

import org.freehep.util.argv.ArgumentFormatException;
import org.freehep.util.argv.ArgumentParser;
import org.freehep.util.argv.BooleanOption;
import org.freehep.util.argv.MissingArgumentException;
import org.freehep.util.argv.StringParameter;

public class Aida {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BooleanOption help = new BooleanOption("-help", "-h", "Show this help page", true);
		StringParameter command = new StringParameter("command", "Command to execute: 'convert', 'file', 'diff'");
		
		ArgumentParser parser = new ArgumentParser("aida");
		parser.add(help);
		parser.add(command);
		List extra;
		try {
		    extra = parser.parse(args);

			if(help.getValue()) {
                parser.printUsage( System.out );
                return;
            }
		} catch (MissingArgumentException e) {
			parser.printUsage( System.out );
            return;
		} catch (ArgumentFormatException e) {
            parser.printUsage( System.out );
            return;			
		}
		
		String cmd = command.getValue();
		CommandHandler handler;
		if (cmd.equalsIgnoreCase("convert")) {
			handler = new ConvertHandler();
		} else if (cmd.equalsIgnoreCase("file")) {
			handler = new FileHandler();
		} else if (cmd.equalsIgnoreCase("diff")) {
			handler = new DiffHandler();
		} else {
			parser.printUsage( System.out );
			return;
		}
		
		try {
			handler.run(extra);
		} catch (Throwable t) {
			while (t != null) {
				t.printStackTrace();
				t = t.getCause();
			}
		}
	}
}
