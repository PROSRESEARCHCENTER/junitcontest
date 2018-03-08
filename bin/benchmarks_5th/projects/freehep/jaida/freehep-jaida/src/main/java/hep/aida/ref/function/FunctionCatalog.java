/*
 * FunctionCatalog.java
 *
 * Created on September 18, 2002, 2:01 PM
 */

package hep.aida.ref.function;
import hep.aida.IAnalysisFactory;
import hep.aida.IFunction;
import hep.aida.IFunctionCatalog;
import hep.aida.IFunctionFactory;
import hep.aida.IModelFunction;
import hep.aida.IRangeSet;
import hep.aida.ITree;
import hep.aida.dev.IDevFunctionCatalog;
import hep.aida.ext.IManagedFunction;
import hep.aida.ref.ManagedObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author  serbo
 */
public class FunctionCatalog implements IDevFunctionCatalog, Serializable {

    private static Pattern pattern = Pattern.compile("\\s*(\\w+)\\s*\\+??");
    
    public static String[] defaultNames = { "g2", "g", "e", 
                                            "moyal", "lorentzian", "landau",
                                            "p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9" };
    public static String prefix = "codelet:";

    protected static FunctionCatalog catalog = null;
    protected TreeMap hash;
    protected TreeMap userHash;
    protected FunctionCreator creator;

    // Constructors
    public static FunctionCatalog getFunctionCatalog() {
	if (catalog == null) catalog = new FunctionCatalog();
	return catalog;
    }
    public static FunctionCatalog getFunctionCatalog(String fileName) throws IOException {
	try {
	    if (catalog == null) catalog = new FunctionCatalog(fileName);
	} catch (IOException ioe) { 
	    throw ioe; 
	} 
	return catalog;
    }

    protected FunctionCatalog() {
	creator = new FunctionCreator();
	hash = new TreeMap();
	userHash = new TreeMap();
	for (int i=0; i<defaultNames.length; i++) { 
	    hash.put(defaultNames[i], new String("codelet:"+defaultNames[i]+":catalog"));
	}
    }

    protected FunctionCatalog(String fileName) throws IOException {
	creator = new FunctionCreator();
	hash = new TreeMap();
	userHash = new TreeMap();
	for (int i=0; i<defaultNames.length; i++) { 
	    hash.put(defaultNames[i], new String("codelet:"+defaultNames[i]+":catalog"));
	}
	loadAll(fileName);
    }

    // IFunctionCatalog and IDevFunctionCatalog methods

    public boolean add(String nameId, IFunction f) {
	return add(nameId, f.codeletString());
    }

    public boolean add(String nameId, String codelet) {
	if (hash.containsKey(nameId.toLowerCase())) return false;
	if (userHash.containsKey(nameId)) return false;

	userHash.put(nameId, codelet);
	return true;
    }

    public String[] list() {
	String[] array = new String[hash.size()+userHash.size()];
	Object[] entries = (hash.keySet()).toArray();
	Object[] userEntries = (userHash.keySet()).toArray();

	for (int i=0; i<hash.size(); i++) { array[i] = (String) entries[i]; }
	for (int i=0; i<userHash.size(); i++) { array[i+hash.size()] = (String) userEntries[i]; }

	return array;
    }

    private String codeletForName(String catalogName) {
	String tmp = catalogName.trim();
	String str = (String) hash.get(tmp.toLowerCase());
	if (str == null)  str = (String) userHash.get(tmp);
	return str;
    }

    public IFunction create(String model) {
        return create(null, model);
    }

    public IFunction create(String name, String model) {
        if ( model.indexOf("*") > 0 || model.indexOf("-") > 0 || model.indexOf("/") >0 )
            throw new IllegalArgumentException("Unsupported operation. We currently only support adding functions.");
        
        ArrayList functions = new ArrayList();
        Matcher matcher = pattern.matcher(model);
        while (matcher.find()) {
            String m = matcher.group(1);
            String codeletString = codeletForName(m);
            functions.add( creator.createFromCodelet(name, codeletString) );
        }
        if ( functions.size() == 0 )
            throw new IllegalArgumentException("Problem parsing model "+model+". Please report this problem.");
        else if ( functions.size() == 1 )
            return (IFunction) functions.get(0);
        else
            return new SumOfFunctions(name,functions);
    }

    public IFunction clone(String nameInATree, IFunction f) {
	IFunction clone;
        String codeletString = f.codeletString();
        if ( CodeletUtils.isCodeletFromCatalog(codeletString) ) {
            String functionModel = CodeletUtils.modelFromCodelet(f.codeletString());
            clone = create(nameInATree,functionModel);
        } else if (f instanceof AbstractIFunction) {
	    try {
                clone = (IFunction) ((AbstractIFunction) f).clone();
		if (clone instanceof ManagedObject) ((ManagedObject) clone).setName(nameInATree);
		if (clone instanceof IManagedFunction) ((IManagedFunction) clone).setName(nameInATree);
            } catch (Exception e) { 
		throw new RuntimeException("Can not create clone of AbstractIFunction", e);
	    }
        } else if (f instanceof Cloneable) {
	    try {
                Class[] tmpC = new Class[0];
                Method m = f.getClass().getMethod("clone", tmpC);               
                clone = (IFunction) m.invoke(f, tmpC);
		if (clone instanceof ManagedObject) ((ManagedObject) clone).setName(nameInATree);
		if (clone instanceof IManagedFunction) ((IManagedFunction) clone).setName(nameInATree);
            } catch (Exception ec) { 
		throw new RuntimeException("Can not create clone of user function", ec);
	    }
        } else {
            clone = creator.createFromCodelet(nameInATree,codeletString);
        }

        if ( !(clone instanceof IModelFunction) )
            clone = new BaseModelFunction(nameInATree, clone.title(), clone);
        
	if (f instanceof IModelFunction) {
	    ((IModelFunction) clone).normalize(((IModelFunction) f).isNormalized());
	    IRangeSet c_rs = null;
	    IRangeSet f_rs = null;
	    double[] upper = null;
	    double[] lower = null;
	    ((IModelFunction) clone).excludeNormalizationAll();

	    for (int i=0; i<f.dimension(); i++) {
		f_rs = ((IModelFunction) f).normalizationRange(i);
		c_rs = ((IModelFunction) clone).normalizationRange(i);
		upper = f_rs.upperBounds();
		lower = f_rs.lowerBounds();
		for (int n=0; n<f_rs.size(); n++) {
		    c_rs.include(lower[n], upper[n]); 
		}
	    }
	} //else if (f instanceof IFunction) {
          //  BaseModelFunction bmf = new BaseModelFunction(nameInATree, nameInATree, clone);
          //  clone = bmf;
        //}
	    
	if (clone instanceof BaseModelFunction) {
	    ((BaseModelFunction) clone).setParameterNames(f.parameterNames());
	}
	
	clone.setParameters(f.parameters());
	return clone;
    }

    // Other methods
    public String[] defaultFunctions() { return defaultNames; }

    public void loadAll(String nameOnDisk) throws IOException { 
	try {
	    FileInputStream in = new FileInputStream(nameOnDisk); 
	    ObjectInputStream s = new ObjectInputStream(in);
	    userHash = (TreeMap)s.readObject();
	} catch (IOException ioe) { 
	    throw ioe; 
	} catch (Exception e) { 
	    throw new RuntimeException("Can not load FunctionCatalog. Please check file:"+nameOnDisk, e);
	}
    }

    public void storeAll(String nameOnDisk)  throws IOException { 
	try {
	    FileOutputStream out = new FileOutputStream(nameOnDisk); 
	    ObjectOutputStream s = new ObjectOutputStream(out);
	    s.writeObject(userHash);
	    s.flush();
	} catch (IOException ioe) { 
	    throw ioe; 
	} catch (Exception e) { 
	    throw new RuntimeException("Can not save FunctionCatalog. Please check file:"+nameOnDisk, e);
	}
    }

    public void remove(String nameId) {
	if (hash.containsKey(nameId)) hash.remove(nameId);
	else
	    throw new IllegalArgumentException("Catalog does not contain function \""+nameId+"\"");
    }

    public FunctionCreator getFunctionCreator() {
	return creator;
    }

    public String toString() {
	String str = "";
	str += "\nFunctionCatalog:            prefix = "+prefix+"\n";
	str +=   "------------------\n";

	if (!hash.isEmpty()) {
	    str += "\n\tDefault Functions:\n"; 
	    Object[] keys = (hash.keySet()).toArray();
	    for (int i=0; i<keys.length; i++) {
		str += "\t\t" + ((String) keys[i]) + "\t" + ((String) hash.get(keys[i])) +"\n";
	    }
	}

	if (!userHash.isEmpty()) {
	    str += "\n\tAdditional Functions:\n"; 
	    Object[] keys = (userHash.keySet()).toArray();
	    for (int i=0; i<keys.length; i++) {
		str += "\t\t" + ((String) keys[i]) + "\t" + ((String) userHash.get(keys[i])) +"\n";
	    }
	}
	return str;
    }


    public static void main(String[] args)  throws ClassNotFoundException, FileNotFoundException, IOException {
	
	test1();
	test2();


    }

    public static void test1()  throws ClassNotFoundException, FileNotFoundException, IOException {
        IAnalysisFactory af = IAnalysisFactory.create();     //jas.aida.gui.JASGUIAnalysisFactory.create(); 
        ITree tree = af.createTreeFactory().create();
        IFunctionFactory ff = af.createFunctionFactory( tree );
	IModelFunction f1 = (IModelFunction) ff.createFunctionFromScript("name1", 1, "a*(1+c*sin(x[0]-d))", "a,c,d", "f1");
	IModelFunction f2 = (IModelFunction) ff.createFunctionByName("name-2", "P5");


	IFunctionCatalog cat = ff.catalog();

	System.out.println(cat+"\n");

	cat.add("script1", f1);
	cat.add("poly-5",  f2);
	String[] names = cat.list();
	for(int i=0; i<names.length; i++) System.out.println(i+"\t "+names[i]);


	System.out.println(cat+"\n");
	String codelet = "codelet:hep.aida.ref.TestUserFunction:file://afs/slac.stanford.edu/u/ey/serbo/java-tests/freehep-hep.jar";

	cat.add("Victor-test", codelet);
	System.out.println(cat+"\n");

	cat.storeAll("Cat");
    }

    public static void test2()  throws ClassNotFoundException, FileNotFoundException, IOException {
        IAnalysisFactory af = IAnalysisFactory.create();     //jas.aida.gui.JASGUIAnalysisFactory.create(); 
        ITree tree = af.createTreeFactory().create();
        IFunctionFactory ff = af.createFunctionFactory( tree );


	IFunctionCatalog cat = ff.catalog();
	cat.loadAll("Cat");

	System.out.println(cat+"\n");

	IFunction f = ff.createFunctionByName("TreeName", "Victor-test");
	System.out.println(FunctionCreator.toString(f));
    }

}
