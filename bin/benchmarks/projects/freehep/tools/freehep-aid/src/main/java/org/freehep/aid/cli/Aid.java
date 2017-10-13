// Copyright 2002-2006, FreeHEP.
package org.freehep.aid.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.freehep.aid.parser.AidParser;
import org.freehep.rtti.Generator;
import org.freehep.rtti.IClass;
import org.freehep.rtti.IPackage;
import org.freehep.rtti.RTTI;
import org.freehep.util.argv.ArgumentFormatException;
import org.freehep.util.argv.ArgumentParser;
import org.freehep.util.argv.BooleanOption;
import org.freehep.util.argv.ListParameter;
import org.freehep.util.argv.MissingArgumentException;
import org.freehep.util.argv.StringOption;
import org.freehep.util.argv.StringParameter;
import org.freehep.util.io.ConditionalInputStream;

/**
 * @author Mark Donszelmann
 * @version $Id: Aid.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Aid {

    private static final String aidDescription = "Abstract Interface Definition compiler";

    private static RTTI rtti;

    public static RTTI getRTTI() {
        return rtti;
    }

    public int run(String[] args) throws Exception {
        rtti = new RTTI();
        
  		BooleanOption help = new BooleanOption("-help", "-h", "Show this help page", true);
  		BooleanOption version = new BooleanOption("-version", "-v", "Show product version", true);
  		BooleanOption rtti = new BooleanOption("-rtti", "-r", "Print RTTI to stdout");
  		BooleanOption noGenerate = new BooleanOption("-nogenerate", "-n", "Do not generate any code");
  		StringOption directory = new StringOption("-directory", "-d", "output dir", ".", "Output into directory instead of current directory");
  		StringOption property = new StringOption("-property", "-p", "property directory", ".", "Read user property files from directory instead of current directory");
  		BooleanOption force = new BooleanOption("-force", "-f", "Force overwriting of output files");
        BooleanOption ignore = new BooleanOption("-ignore", "-i", "Ignore errors from the parser");
        BooleanOption verbose = new BooleanOption("-verbose", "-V", "Verbose");
        StringParameter generatorParameter = new StringParameter("generator", "A generator, one of JavaInterfaceGenerator, JavaClassGenerator, CPPHeaderGenerator, PythonClassGenerator, ...");
  		ListParameter files = new ListParameter("files", "AID files");

        Properties mavenProperties = new Properties();
        InputStream is = Aid.class.getResourceAsStream("/META-INF/maven/org.freehep/freehep-aid/pom.properties");
        if (is != null) mavenProperties.load(is);
        String versionNumber = "("+mavenProperties.getProperty("version", "undefined")+")";
        String aid = "AID - "+aidDescription+" "+versionNumber;
        
        ArgumentParser cl = new ArgumentParser(aid);
        cl.add(help);
        cl.add(version);
        cl.add(rtti);
        cl.add(noGenerate);
        cl.add(directory);
        cl.add(property);
        cl.add(force);
        cl.add(ignore);
        cl.add(verbose);
        cl.add(generatorParameter);
        cl.add(files);

        try {
            List extra = cl.parse(args);
            
            if( !extra.isEmpty() || help.getValue()) {
                cl.printUsage( System.out );
                return 0;
            }
            
            if (version.getValue()) {
                System.out.println(aid);
                return 0;
            }
            
        } catch (MissingArgumentException mae) {
            System.out.println(mae.getMessage());
            return 0;
        } catch (ArgumentFormatException afe) {
            System.out.println(afe.getMessage());
            return 0;
        }

        String generatorName = generatorParameter.getValue();
        Generator generator = null;
        try {
            String propDir = property.getValue();
            // generate output
            Class cls = null;
            try {
                cls = Class.forName(generatorName);
            } catch (ClassNotFoundException cnfe) {
                cls = Class.forName("org.freehep.aid."+generatorName);
            }
            Constructor constructor = cls.getConstructor(new Class[] { java.lang.String.class });
            generator = (Generator)constructor.newInstance(new Object[] { propDir });
        } catch (ClassNotFoundException cnfe) {
            System.err.println("ERROR: Generator class '"+generatorName+"' does not exist.");
            return 1;
        } catch (ClassCastException cce) {
            System.err.println("ERROR: Generator class '"+generatorName+"' does not implement org.freehep.jaco.generator.Generator.");
            return 1;
        } catch (NoSuchMethodException nsme) {
            System.err.println("ERROR: Generator class '"+generatorName+"' does not define a constructor with one String argument.");
            return 1;
        } catch (InstantiationException ie) {
            System.err.println("ERROR: Generator class '"+generatorName+"' cannot be instantiated.");
            return 1;
        } catch (IllegalAccessException iae) {
            System.err.println("ERROR: Generator class '"+generatorName+"' cannot be accessed.");
            return 1;
        } catch (InvocationTargetException ite) {
            System.err.println("ERROR: Generator class '"+generatorName+"' cannot be invoked.");
            ite.printStackTrace();
            return 1;
        }

        // parse files, skip first argument as it is the generator
        AidParser parser;
        for (Iterator i=files.getValue().iterator(); i.hasNext(); ) {
            String filename = (String)i.next();
            try {
                if (verbose.getValue()) System.out.println("Parsing AID: "+filename);
                InputStream in = new java.io.FileInputStream(filename);
                InputStream cin = new ConditionalInputStream(in, generator.getProperties());
                parser = new AidParser(cin);
                parser.parse();
                cin.close();
                in.close();
            } catch (java.io.FileNotFoundException e) {
                System.err.println("ERROR: File " + filename + " not found.");
                if (!ignore.getValue()) return 1;
//            } catch (ParseException e) {
//                System.err.println("ERROR: Encountered errors during parse.");
//                System.err.println(e.getMessage());
//                if (!ignore.getValue()) return 1;
            } catch (Exception e) {
                System.err.println("ERROR: "+e);
                if (!ignore.getValue()) return 1;
            }
        }

        // check to print out rtti
        if (rtti.getValue()) {
            System.out.println(Aid.getRTTI().toString());
        }

        // only generate
        if (noGenerate.getValue()) {
            System.out.println("No code generated.");
            return 0;
        }

        try {
            IPackage[] p = Aid.getRTTI().getPackages();

            boolean stop = false;
            for (int i=0; i<p.length; i++) {
                IClass[] c = p[i].getClasses();
                for (int j=0; (j<c.length) && !stop; j++ ) {
                    stop = writeClass(generator, c[j], directory.getValue(), force.getValue(), verbose.getValue());
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
            return 1;
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    // return true if we want to stop
    private boolean writeClass(Generator generator, IClass clazz,
                       String outDir, boolean overwrite, boolean verbose) throws IOException {

		File dir = new File(outDir+"/"+generator.directory(clazz));
        dir.mkdirs();
        File file = new File(dir, generator.filename(clazz));

        // Check if we are overwriting a class which was already generated by us
        if (!overwrite) {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                boolean found = false;
                // look in first 5 lines
                for (int i=0; !found && (i<5); i++) {
                    String line = reader.readLine();
                    if ((line != null) && (line.indexOf("AID-GENERATED") >= 0)) {
                        found = true;
                    }
                }
                if (!found) throw new IOException("Non AID-GENERATED file exists '"+file+"', cannot overwrite, use -force option");
                reader.close();
            }
        }

		if (verbose) System.out.println("Generating: "+file);

		boolean stop = generator.print(file, clazz);

        return stop;
    }
    
    public static void runMain(String[] args) throws Exception {
        Aid aid = new Aid();
        aid.run(args);
    }
    
    public static void main(String[] args) {
        try {  
            Aid aid = new Aid();
            System.exit(aid.run(args));
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
