// Copyright 2002-2005, FreeHEP.
package org.freehep.aid;

import org.freehep.rtti.IClass;
import org.freehep.util.io.IndentPrintWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: JavaClassGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JavaClassGenerator extends AbstractJavaGenerator {

    public JavaClassGenerator(String propDir) {
        super(propDir);

        properties.setProperty("java.class", "true");
    }

    public String filename(IClass clazz) {
        return "Abstract" + clazz.getName() + ".java";
    }

    protected boolean isClass(IClass clazz) {
        return true;
    }

    protected void printClassHeader(IndentPrintWriter out, IClass clazz) {
        out.println();
        out.print("public abstract class ");
        out.print("Abstract" + clazz.getName());
        
        // for templates
        String[] templateParameters = clazz.getTemplateParameters();
        if (templateParameters.length > 0) {
            out.print("<");
            out.print(templateParameters[0]);
            for (int i=1; i<templateParameters.length; i++) {
                out.print(", ");
                out.print(templateParameters[i]);
            }
            out.print("> ");
        }
        
        out.println(" implements " + clazz.getName() + " {");
    }
}
