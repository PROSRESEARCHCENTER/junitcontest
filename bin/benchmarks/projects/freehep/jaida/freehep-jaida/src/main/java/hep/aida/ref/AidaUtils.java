package hep.aida.ref;

import hep.aida.IAnnotation;
import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ref.tree.Tree;
import hep.aida.ref.xml.AidaXMLWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.freehep.util.FreeHEPLookup;

/**
 *
 * @author turri
 * @version $Id: AidaUtils.java 10700 2007-04-19 21:13:38Z serbo $
 */
public abstract class AidaUtils {
    
    private static Pattern stringPattern = Pattern.compile("\\s*((\"(.*?)\")|([^,;$]*))\\s*?(,|;|$)");
    private static Pattern pattern = Pattern.compile("([\\w|\\.]+)\\s*((=\\s*(\"(.*?)\"|([^,;$]*)))\\s*)?(,|;|$)");
    
    public static void fillPath(ITree tree, IManagedObject mo) {
        if (tree != null && mo != null) {
            IAnnotation an = null;
            if (mo instanceof IBaseHistogram) {
                an = ((IBaseHistogram) mo).annotation();
            } else if (mo instanceof IDataPointSet) {
                an = ((IDataPointSet) mo).annotation();
            } else if (mo instanceof IFunction) {
                an = ((IFunction) mo).annotation();
            } else if (mo instanceof ITuple) {
                an = ((ITuple) mo).annotation();
            }
            
            String path = tree.findPath(mo);
            if (an == null) return;
            try {
                if (an.hasKey(Annotation.aidaPathKey)) {
                    an.setValue(Annotation.aidaPathKey, path);
                } else {
                    an.addItem(Annotation.aidaPathKey, path, true);
                }
            } catch (Exception e) { e.printStackTrace(); }
            
            String name = tree.name();
            if (name == null || name.trim().equals("")) name = tree.storeName();
            String fullPath = "/"+name+path;
            
            try {
                Tree aidaMasterTree = ( (Tree) FreeHEPLookup.instance().lookup(ITree.class) );
                if (aidaMasterTree != null) fullPath = aidaMasterTree.findPath(mo);
                if (an.hasKey(Annotation.fullPathKey)) {
                    an.setValue(Annotation.fullPathKey, fullPath);
                } else {
                    an.addItem(Annotation.fullPathKey, fullPath, true);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    
    
    /**
     * Parse AIDA options. Accepts values of the form:
     * <pre>
     * a=b;c=d
     * a="Some Options",c="My , Funny Value"
     * testCase     (equivalent to testCase=true)
     * </pre>
     * @param options The options string to be parsed
     * @return A map containing all the found options
     * @throws IllegalArgumentException if the options string is invalid
     */
    public static Map parseOptions(String options) {
        if (options == null || options.trim().length() == 0) return Collections.EMPTY_MAP;
        Map hashValues = new HashMap();
        Matcher matcher = pattern.matcher(options);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(5);
            if (value == null) {
                value = matcher.group(6);
                //This is to remove the trailing spaces in group(6)
                if ( value != null )
                    value = value.trim();
            }
            if (value == null) value = "true";
            hashValues.put(key,value);
            if (matcher.end()  == options.length()) return hashValues;
        }
        throw new IllegalArgumentException("Invalid options: "+options);
    }
    
    public static String createOptionsString(Map options) {
        String tmp = "";
        if (options == null || options.size() == 0) return tmp;
        
        Iterator it = options.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Object value = options.get(key);
            tmp += ", "+key.toString()+"=\""+value.toString()+"\"";
        }
        return tmp;
    }
    
    public static String[] parseString(String options) {
        if (options == null || options.trim().length() == 0) return new String[0];
        ArrayList list = new ArrayList();
        Matcher matcher =stringPattern.matcher(options);
        while (matcher.find()) {
            int g = matcher.groupCount();
            String value = matcher.group(3);
            if (value == null) value = matcher.group(1);
            if (value != null) {
                list.add(value.trim());
            }
            if (matcher.end()  == options.length()) {
                String[] array = new String[list.size()];
                if (list.size() > 0) array = (String[]) list.toArray(array);
                return array;
            }
        }
        throw new IllegalArgumentException("Invalid options: "+options);
    }
    
    
    // Creates path array from String. Path should start with "/"
    // Does not take escaped "/"  ("\\/") as a path element separator
    public static String[] stringToArray(String path) {
        String[] result = null;
        if ( path == null || path.equals("")) return result;
        
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        ArrayList list = new ArrayList(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            while (token.endsWith("\\")) {
                token = token.substring(0, token.length()-1)+ "/";
                if (tokenizer.hasMoreTokens()) token = token  + tokenizer.nextToken();
            }
            list.add(token);
        }
        result = new String[list.size()];
        list.toArray(result);
        
        return result;
    }
    
    // Escape all "/" -> "\\/"
    public static String modifyName(String name) {
        if (name.indexOf('/') < 0) return name;
        String newName = "";
        
        int index = -1;
        for (int i=0; i<name.length(); i++) {
            if (name.charAt(i) == '/') {
                if (i > 0 && name.charAt(i-1) == '\\') continue;
                if (i > index) {
                    newName = newName + name.substring(index+1, i) + "\\/";
                    index = i;
                } else if (i == index) {
                    newName = newName + "\\/";
                }
            }
        }
        newName += name.substring(index+1);
        
        return newName;
    }
    
    public static String parseName(String pathString) {
        if (pathString == null || pathString.equals("/") || pathString.endsWith("/")) return "";
        if (pathString.indexOf("/") < 0) return pathString;
        String[] path = stringToArray(pathString);
        String name = path[path.length-1];
        return name;
    }
    
    public static String parseDirName(String pathString) {
        if (pathString == null || pathString.equals("/") || pathString.endsWith("/")) return pathString;
        if (pathString.indexOf("/") < 0) return "";
        String dirName = "";
        if (pathString.startsWith("/")) dirName = "/";
        String[] path = stringToArray(pathString);
        if (path.length <= 1) {
            return dirName;
        } else {
            for (int i=0; i<path.length-1; i++) { dirName += path[i] + "/"; }
        }
        return dirName;
    }
    
    // returns -1 if nothing was found
    public static int findInArray(String string, String[] array) {
        int index = -1;
        if (string == null || array == null || array.length == 0) return index;
        
        for (int i=0; i<array.length; i++) {
            if (string.equals(array[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    // Determines if "path" is direct child of "dir"
    public static boolean isPathInDir(String dir, String path) {
        if (dir.indexOf("//") >= 0)  dir.replaceAll("//", "/");
        if (path.indexOf("//") >= 0) path.replaceAll("//", "/");
        if (path.equals(dir)) return false;
        if (!path.startsWith(dir)) return false;
        
        String tmp = path.substring(dir.length());
        if (tmp.startsWith("/")) tmp = tmp.substring(1);
        if (tmp.trim().equals("")) return false;
        int index = tmp.indexOf("/");
        if (index >= 0 && index < (tmp.length()-1)) return false;
        else return true;
    }
    
    
    /**
     * Round number down (closer to Negative Infinity):
     * "order" defines which significant digit is rounded, order >= 0
     *
     * roundDown(234.5, 0) -> 200.0
     * roundDown(234.5, 1) -> 230.0
     * roundDown(234.5, 2) -> 234.0
     *
     */
    public static double roundDown(double x, int order) {
        if (Double.isNaN(x) || Double.isInfinite(x) || x == Double.MIN_VALUE) return x;
        else if (x < 0) {
            return (-1.)*roundUp(Math.abs(x), order);
        } else if (x == 0) return x;
        
        double mant = Math.floor(Math.log(x)/Math.log(10.));
        double factor = Math.pow(10., (order-mant));
        double tmp = Math.floor(x*factor)/factor;
        return tmp;
    }
    
    /**
     * Round number up (closer to Positive Infinity),
     * "order" defines which significant digit is rounded, order >= 0
     *
     * roundUp(234.5, 0) -> 300.0
     * roundUp(234.5, 1) -> 240.0
     * roundUp(234.5, 2) -> 235.0
     *
     */
    public static double roundUp(double x, int order) {
        if (Double.isNaN(x) || Double.isInfinite(x) || x == Double.MAX_VALUE) return x;
        else if (x < 0) {
            return (-1.)*roundDown(Math.abs(x), order);
        } else if (x == 0) return x;
        
        double mant = Math.floor(Math.log(x)/Math.log(10.));
        double factor = Math.pow(10., (order-mant));
        double tmp = Math.ceil(x*factor)/factor;
        return tmp;
    }
    
}

