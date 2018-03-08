// Copyright 2002-2005, FreeHEP.
package org.freehep.aid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.freehep.aid.cli.Aid;
import org.freehep.rtti.IClass;
import org.freehep.rtti.IPackage;
import org.freehep.util.UserProperties;
import org.freehep.util.io.IndentPrintWriter;

/**
 * Generates one header file which includes all other header files
 *
 * @author Mark Donszelmann
 * @version $Id: CPPPackageHeaderGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class CPPPackageHeaderGenerator extends AbstractGenerator {

    protected final static String language = "cpp";

    protected CPPTypeConverter converter;
    protected UserProperties includeProperties = new UserProperties();

    public CPPPackageHeaderGenerator(String propDir) {
        super();

        AidUtil.loadProperties(properties, getClass(), propDir, "aid.cpp.properties");
        AidUtil.loadProperties(includeProperties, getClass(), propDir, "aid.includes."+language+".properties");
        converter = new CPPTypeConverter(propDir);
    }

    protected String namespace(IClass clazz) {
        return converter.namespace(clazz.getPackageName());
    }

    public String directory(IClass clazz) {
        return namespace(clazz).replaceAll("::","_");
    }

    public String filename(IClass clazz) {
        String name = directory(clazz);
        if (name.equals("")) name = "DEFAULT";
        return name+".h";
    }

    public boolean print(File file, IClass clazz) throws IOException {
        IndentPrintWriter out = new IndentPrintWriter(new PrintWriter(new BufferedWriter(new FileWriter(file))));
        out.setIndentString("    ");

        out.println("// -*- C++ -*-");

        warning(out);

        String define = filename(clazz).toUpperCase().replaceAll("::", "_").replaceAll("\\.", "_");
        out.println("#ifndef "+define);
        out.println("#define "+define+" 1");

        out.println();
        // FIXME we need to use tha package names here...
        IPackage[] p = Aid.getRTTI().getPackages();
        for (int i=0; i<p.length; i++) {
            IClass[] c = p[i].getClasses();
            for (int j=0; j<c.length; j++ ) {
                IClass cls = c[j];
                String name = converter.qualifiedName(cls.getName(), "" );
                out.println("#include \""+includeProperties.getProperty(name, name)+"\"");
            }
        }

        out.println();
        out.println("#endif /* ifndef "+define+" */");
        out.close();
        
        return true;
    }

}

