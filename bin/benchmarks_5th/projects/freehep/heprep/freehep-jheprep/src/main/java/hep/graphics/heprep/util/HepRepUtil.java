// Copyright 2003-2005, FreeHEP.
package hep.graphics.heprep.util;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepAttribute;
import hep.graphics.heprep.HepRepConverter;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepIterator;
import hep.graphics.heprep.HepRepProvider;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.ref.DefaultHepRepIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openide.util.Lookup;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepUtil.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepUtil {

    // Static class, not to be instantiated
    private HepRepUtil() {
    }

    /**
     * Print statements in equal methods
     * @return true if print should happen
     */
    public static boolean debug() {
        return false;
    }

    /**
     * Decodes a String into a Double. The string can have the following formats:
     * <pre>
     *      0ds<number> :   where number is the DoubleToLongBits encoding in special format
     *      0d0x<number>:   where number is the DoubleToLongBits encoding in hex
     *      0d#<number> :   where number is the DoubleToLongBits encoding in hex
     *      0d<number>  :   where number is the DoubleToLongBits encoding in decimal
     *      0x<number>  :   where number is the Hex encoding of a Long
     *      <number>    :   where number contains (.Ee) and is parsed into a Double
     *      <number>    :   where number contains no (.Ee) and is parsed into a Long
     * </pre>
     * @param s number as string
     * @return decoded double
     */
    public static double decodeNumber(String s) {
        double d = 0;
        try {
        if (s.startsWith("0ds")) {
            d = Double.longBitsToDouble(decodeSpecial(s.substring(3)));
        } else if (s.startsWith("0d0x")) {
            d = Double.longBitsToDouble(decodeHex(s.substring(4)));
        } else if (s.startsWith("0d#")) {
            d = Double.longBitsToDouble(decodeHex(s.substring(3)));
        } else if (s.startsWith("0d")) {
            d = Double.longBitsToDouble(decodeHex(s.substring(2)));
        } else if (s.startsWith("0x")) {
            d = Long.decode(s).doubleValue();
        } else if ((s.indexOf(".") < 0) && (s.indexOf("E") < 0) && (s.indexOf("e") < 0)) {
            d = Long.decode(s).doubleValue();
        } else {
            d = Double.valueOf(s).doubleValue();
        }
        } catch (NumberFormatException nfe) {
            System.err.println("decodeNumber: "+s);
            nfe.printStackTrace();
            throw nfe;
        }
        return d;
    }

    private static char[] hexChars = new char[16];

    /**
     * Decodes a non-negative Hex number into a long
     * @param s number as string
     * @return decoded number
     * @throws NumberFormatException in case string is malformed
     */
    public static long decodeHex(String s) throws NumberFormatException {
        long result = 0;
        int len = Math.min(hexChars.length, s.length());
        s.getChars(0, len, hexChars, 0);

        for (int i=0; i<len; i++) {
            result = result << 4;
            char c = hexChars[i];
            if ((48 <= c) && (c <= 57)) {
                // 0..9
                result += (c-48);
            } else if ((65 <= c) && (c <= 70)) {
                result += (c-65+10);
            } else if ((97 <= c) && (c <= 102)) {
                result += (c-97+10);
            } else {
                throw new NumberFormatException("Not a hex number: "+s);
            }
        }

        return result;
    }

    // NOTE this should be less than 16
    private static char[] specialChars = new char[16];

    /**
     * Decodes a non-negative Special number into a long
     * @param s special number string
     * @return long number
     * @throws NumberFormatException in case string is malformed
     */
    public static long decodeSpecial(String s) throws NumberFormatException {
        long result = 0;
        int len = Math.min(specialChars.length, s.length());
        s.getChars(0, len, specialChars, 0);

        for (int i=0; i<len; i++) {
            result = result << 6;
            char c = specialChars[i];
            if ((48 <= c) && (c <= 57)) {
                // 0..9
                result += (c-48);
            } else if ((65 <= c) && (c <= 90)) {
                // A..Z
                result += (c-65+10);
            } else if ((97 <= c) && (c <= 124)) {
                // a..z,{,|
                result += (c-97+10+26);
            } else {
                throw new NumberFormatException("Not a special number: "+s);
            }
        }

        return result;
    }

    /**
     * Encodes a number into a special
     * @param d number
     * @return special number string
     */
    public static String encodeSpecial(long d) {

        for (int i=10; i>=0; i--) {     // 64 bits in 6 bit parts, 11 parts
            int c = (int)(d & 0x3f);        // 6 bits
            if (c < 10) {
                specialChars[i] = (char)('0'+c);
            } else if (c < 10+26) {
                specialChars[i] = (char)('A'+c-10);
            } else if (c < 10+26+26+2) {
                specialChars[i] = (char)('a'+c-10-26);
            } else {
                System.err.println("encodeSpecial: this looks bad.");
            }
            d = d >>> 6;
        }

        return new String(specialChars, 0, 11);
    }

    /**
     * Some tests for encoding and decoding
     * @param args ignored
     */
    public static void main(String[] args) {
        System.out.println(""+Long.toHexString(decodeSpecial("0123456789ABCDEF")));
        System.out.println(""+Long.toHexString(decodeSpecial("GHIJKLMNOPQRSTUV")));
        System.out.println(""+Long.toHexString(decodeSpecial("WXYZabcdefghijkl")));
        System.out.println(""+encodeSpecial(0x59a7a29aabb2dbafL));
        System.out.println(""+Long.toHexString(decodeSpecial("mnopqrstuvwxyz{|")));
        System.out.println(""+encodeSpecial(0x5db7e39ebbf3dfbfL));
    }


    /**
     * copy all attributes (defs and values) from node src to node dst
     * @param src 
     * @param dst 
     * @throws CloneNotSupportedException 
     */
    public static void copyAttributes(HepRepAttribute src, HepRepAttribute dst) throws CloneNotSupportedException {

        // BUG FIX.  Do something special for layers, because these do not end
        // up in the normal iteration.
        HepRepAttValue layerAtt = src.getAttValueFromNode("layer");
        if (layerAtt!=null) {
            dst.addAttValue(layerAtt.copy());
        }

        // copy all att values
        for (Iterator i=src.getAttValuesFromNode().iterator(); i.hasNext(); ) {
            HepRepAttValue value = (HepRepAttValue)i.next();
            dst.addAttValue(value.copy());
        }
    }

    /** 
     * Look for hierarchical type name in Collection (Set) of types, checking its subtypes.
     * @param types collection of types to search
     * @param name type to look for
     * @return HepRepType or null if not found
     */
    public static HepRepType getType(Collection/*<HepRepType>*/ types, String name) {
        // remove leading slash
        if (name.startsWith("/")) name = name.substring(1);

        // split name
        int slash = name.indexOf("/");
        String rest = "";
        if (slash >= 0) {
            rest = name.substring(slash+1);
            name = name.substring(0,slash);
        }
        
        // search
        for (Iterator i=types.iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            if (type.getName().equals(name)) {
                if (rest.equals("")) return type;
                return getType(type.getTypeList(), rest);
            }
        }
        return null;
    }

    /**
     * Returns an iterator which walks the full list of instances within a list of HepRepInstanceTrees
     * limited by a list of layers, set of types and augmented by double passes for frame iteration.
     *
     * @param instanceTrees HepRepInstanceTrees to be iterated over
     * @param layers list of layers to iteratate over or null.
     * @param types set of types for which iteration returns instances
     * @param iterateFrames iterate separately over each layer for frames
     * @return HepRepIterator
     */
    public static HepRepIterator getInstances(List/*<HepRepInstanceTree>*/ instanceTrees, List/*<String>*/ layers, 
                                              Set/*<Object>*/ types, boolean iterateFrames) {
        return new DefaultHepRepIterator(instanceTrees, layers, types, iterateFrames);
    }

    /**
     * iterates two iterations in order
     * @param first first iterator
     * @param second second iterator
     * @return combined iterator
     */
    public static Iterator iterator(Iterator first, Iterator second) {
        final Iterator f = (first != null) ? first : Collections.EMPTY_LIST.iterator();
        final Iterator s = (second != null) ? second : Collections.EMPTY_LIST.iterator();
        return new Iterator() {

            public boolean hasNext() {
                return f.hasNext() || s.hasNext();
            }

            public Object next() {
                if (f.hasNext()) {
                    return f.next();
                }

                return s.next();
            }

            public void remove() {
                // for now, if implemented think about the semantics of the last entry in the first enum
                throw new UnsupportedOperationException();
            }
        };
    }
    
    /**
     * Finds the first HepRepProvider which converts object.
     * Return null if not found.
     * @param registry registry to look for provider
     * @param object object to convert
     * @return provider to convert object
     */
    public static HepRepProvider getHepRepProvider(Lookup registry, Object object) {
        Collection allConverters = registry.lookup(new Lookup.Template(HepRepProvider.class)).allInstances();
        for (Iterator i = allConverters.iterator(); i.hasNext(); ) {
            HepRepProvider converter = (HepRepProvider)i.next();
            if (converter.canConvert(object)) {
                return converter;
            }
        }
        return null;
    }

    /**
     * Finds the first HepRepConverter which handles object. For backwards compatibility.
     * Return null if not found.
     * @param registry registry to look for provider
     * @param cls class of object to convert
     * @return converter to convert object
     * @deprecated use getHepRepProvider(). 
     */
    public static HepRepConverter getHepRepConverter(Lookup registry, Class cls) {
        Collection allConverters = registry.lookup(new Lookup.Template(HepRepConverter.class)).allInstances();
        for (Iterator i = allConverters.iterator(); i.hasNext(); ) {
            HepRepConverter converter = (HepRepConverter)i.next();
            if (converter.canHandle(cls)) {
                return converter;
            }
        }
        return null;
    }
    
    /**
     * Returns the Unit from the extra info. Normally the string up to the first colon, or null if extra is null.
     * @param attDef attribute definition
     * @return unit string
     */
    public static String getUnit(HepRepAttDef attDef) {
        String extra = attDef.getExtra();
        if (extra == null) return null;
        int colon = extra.indexOf(":");
        return (colon >= 0) ? extra.substring(0, colon) : extra;
    }
    
    /**
     * Returns a Set of all layernames which are used, and possibly visible and pickable.
     * 
     * @param instanceTree instance tree to look in
     * @param checkVisible look only in visible instances
     * @param checkPickable look only in pickable instances
     * @return set of layers
     */
    public static Set/*<String>*/ getAllLayerNames(HepRepInstanceTree instanceTree, boolean checkVisible, boolean checkPickable) {
        Set names = new HashSet();
        List trees = new ArrayList();
        trees.add(instanceTree);
        HepRepIterator iterator = HepRepUtil.getInstances(trees, null, null, false);
        while (iterator.hasNext()) {
            HepRepInstance instance = iterator.nextInstance();
            if (checkPickable && !instance.getAttValue("ispickable").getBoolean()) continue;
            if (!checkVisible || instance.getAttValue("visibility").getBoolean()) {
                names.add(instance.getAttValue("layer").getString());
            }
        }                
        return names;
    }



}

