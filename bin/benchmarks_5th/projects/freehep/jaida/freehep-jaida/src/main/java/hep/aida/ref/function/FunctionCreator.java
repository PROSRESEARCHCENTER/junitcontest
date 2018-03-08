/*
 * FunctionCreator.java
 *
 * Created on September 26, 2002, 11:57 AM
 */

package hep.aida.ref.function;

import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.ext.IManagedFunction;
import hep.aida.ref.ManagedObject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

/**
 * Singleton class that is used by FunctionFactory and FunctionCatalog to create functions from codelet strings. Use static method getFunctionCreator() to get an instance of this class.
 *
 * @author serbo
 */
public class FunctionCreator {

    public FunctionCreator() {
    }

    public IFunction createFromCodelet(String codeletString) {
        return createFromCodelet(null, codeletString);
    }

    public IFunction createFromCodelet(String nameInTree, String codeletString) {
        //System.out.println("createFromCodelet: " + nameInTree + ", " + codeletString);
        IFunction f = null;
        if (!codeletString.toLowerCase().startsWith(FunctionCatalog.prefix))
            throw new IllegalArgumentException("\"" + codeletString + "\" is not a codelet string (must start with \"" + FunctionCatalog.prefix + "\")");

        String name = CodeletUtils.modelFromCodelet(codeletString);
        String location = CodeletUtils.locationFromCodelet(codeletString);
        // System.out.println("Catalog:  index="+index+",  name="+name+",  location="+location);

        // Catalog entries for Build-in functions
        if (CodeletUtils.isCodeletFromCatalog(codeletString)) {
            if (name.toLowerCase().equals("e")) {
                f = new BaseModelFunction(name, name, new ExponentialCoreNotNorm(name), new ExponentialCoreNorm(name));
            } else if (name.toLowerCase().startsWith("p")) {
                f = new BaseModelFunction(name, name, new PolynomialCoreNotNorm(name), new PolynomialCoreNorm(name));
            } else if (name.toLowerCase().startsWith("g2")) {
                f = new BaseModelFunction(name, name, new GaussianCore2DNotNorm(name), new GaussianCore2DNorm(name));
            } else if (name.toLowerCase().startsWith("g")) {
                f = new BaseModelFunction(name, name, new GaussianCoreNotNorm(name), new GaussianCoreNorm(name));
            } else if (name.toLowerCase().startsWith("moyal")) {
                f = new BaseModelFunction(name, name, new MoyalCoreNotNorm(name), null);
            } else if (name.toLowerCase().startsWith("lorentzian")) {
                f = new BaseModelFunction(name, name, new LorentzianCoreNotNorm(name), null);
            } else {
                throw new UnsupportedOperationException("Can not create function: " + name);
            }
            ((BaseModelFunction) f).setCodeletString(codeletString);
            if (nameInTree == null)
                nameInTree = name;
            ((ManagedObject) f).setName(nameInTree);
        }

        // Create JEL-based scripted function. The codeletString explession after
        // "codelet:verbatim:jel:" MUST contain a full description of scripted function.
        // Currently use ":" as deliminators. Example:
        // codelet:Script_Function:verbatim:jel :1 : a*(1+c*sin(x[0]-d)) : a,c,d : a*c*cos(x[0]-d)
        else if (CodeletUtils.isCodeletFromScript(codeletString)) {
            // System.out.println("CODELETSTRING :: "+codeletString);
            StringTokenizer st = new StringTokenizer(codeletString, ":");
            String[] args = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                args[i] = st.nextToken().trim();
                // System.out.println(i+" "+args[i]);
                i++;
            }

            int dim = Integer.parseInt(args[4]);
            String valexpr = args[5];
            String parameters = args[6];
            String gradexpr = "";
            if (args.length > 7) {
                gradexpr = args[7];
            }
            if (gradexpr.trim().toLowerCase().equals("null") || gradexpr.trim().equals(""))
                gradexpr = null;
            // System.out.println("dim="+dim+",  valexpr="+valexpr+",  parameters="+parameters+",  gradexpr="+gradexpr);

            f = new JELBaseModelFunction(name, dim, valexpr, parameters, name, gradexpr);
            ((JELBaseModelFunction) f).setCodeletString(codeletString);
            ((ManagedObject) f).setName(nameInTree);
        }

        // Create function, given its full class name:
        // codelet:my.functions.MyFunction:classPath
        // No-argument constructor is used in such case: new MyFunction()
        // If optional list of variable names and parameter names is provided:
        // codelet:my.functions.MyFunction:classPath:x0, x1: a, b, c
        // MyFunction(String[] varNames, String[] parNames) constructor is used
        else if (CodeletUtils.isCodeletFromClass(codeletString)) {
            StringTokenizer st = new StringTokenizer(codeletString, ":");
            String[] args = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                args[i] = st.nextToken().trim();
                i++;
            }
            String[] varList = null;
            if (args.length > 3) {
                CodeletUtils.stringToArray(args[3]);
            }
            String[] parList = null;
            if (args.length > 4) {
                 parList = CodeletUtils.stringToArray(args[4]);
            }

            try {
                if (varList != null && parList != null) {
                    Class[] types = new Class[] { String[].class, String[].class };
                    Class cl = Class.forName(name);
                    f = (IFunction) cl.getConstructor(types).newInstance(new Object[] { varList, parList });
                } else {
                    Class cl = Class.forName(name);
                    f = (IFunction) cl.newInstance();
                }
                if (f instanceof ManagedObject)
                    ((ManagedObject) f).setName(nameInTree);
                if (f instanceof IManagedFunction)
                    ((IManagedFunction) f).setName(nameInTree);
                if (f != null)
                    f.setTitle(nameInTree);
            } catch (Exception ec) {
                throw new RuntimeException("Can not create user CLASS function:" + name, ec);
            }
        }

        // Load function from file. "name" MUST be just FULL name of the class
        // (e.g. "fitProject.functions.MyFunction"
        else if (CodeletUtils.isCodeletFromFile(codeletString)) {
            try {
                String urlString = location.substring(5).trim();
                if (urlString == null || urlString.equals("")) {
                    Class cl = Class.forName(name);
                    f = (IFunction) cl.newInstance();
                } else {
                    URL[] urlList = new URL[] { new File(urlString.substring(1)).toURL() };
                    URLClassLoader loader = new URLClassLoader(urlList);

                    System.out.println("Name: " + name + "\nFile Name: " + urlString);
                    System.out.println("URL: " + urlList[0].getFile());
                    Class cl = loader.loadClass(name);
                    f = (IFunction) cl.newInstance();
                }
                if (f instanceof ManagedObject)
                    ((ManagedObject) f).setName(nameInTree);
                if (f instanceof IManagedFunction)
                    ((IManagedFunction) f).setName(nameInTree);
            } catch (Exception ec) {
                throw new RuntimeException("Can not create user FILE function:" + name, ec);
            }
        }

        return f;
    }

    public static String toString(IFunction f) {

        String out = "Codelet: " + f.codeletString() + "\n";
        if (f instanceof IManagedObject)
            out += "   name: " + ((IManagedObject) f).name() + "\n";
        out += "\tDimensions: " + f.dimension() + "\n";
        for (int i = 0; i < f.dimension(); i++)
            out += "\t\t Variable " + i + "\t Name: " + f.variableName(i) + "\n";
        out += "\tParameters: " + f.numberOfParameters() + "\n";
        String[] par = f.parameterNames();
        for (int i = 0; i < f.numberOfParameters(); i++)
            out += "\t\t Parameter " + i + "\t Name: " + par[i] + "\n";

        out += "Provides gradiant: " + f.providesGradient();

        return out;
    }

}
