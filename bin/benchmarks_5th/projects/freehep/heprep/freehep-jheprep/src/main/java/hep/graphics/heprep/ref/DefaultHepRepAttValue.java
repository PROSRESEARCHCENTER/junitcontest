// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.ref;

import java.awt.*;
import java.io.*;
import java.util.*;

import org.freehep.util.ScientificFormat;
import org.freehep.swing.ColorConverter;

import hep.graphics.heprep.*;
import hep.graphics.heprep.xml.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttValue.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepAttValue implements HepRepAttValue, Serializable {

    private static final ScientificFormat scientific = new ScientificFormat();
    private String name;
    private String lowerCaseName;

    // values implemented as separate items, so that they do not take up unnecessary space for an Object
    // only ONE of these is filled
    private String lowerCaseString;
    private Object objectValue;
    private long longValue;
    private double doubleValue;
    private boolean booleanValue;

    private int type;
    private int showLabel;

    private DefaultHepRepAttValue() {
        // Un-initialized, for clone
    }

    void replace(DefaultHepRepAttValue attValue) {
        name = attValue.name;
        lowerCaseName = attValue.lowerCaseName;
        
        lowerCaseString = attValue.lowerCaseString;
        objectValue = attValue.objectValue;
        longValue = attValue.longValue;
        doubleValue = attValue.doubleValue;
        booleanValue = attValue.booleanValue;
        type = attValue.type;
        showLabel = attValue.showLabel;
    }

    /**
     * Create an String Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, String value, int showLabel) {
        init(name, TYPE_STRING, showLabel);
        objectValue = (value == null) ? null : value.intern();
        lowerCaseString = (value == null) ? null : value.toLowerCase().intern();
    }

    /**
     * Create an Color Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, Color value, int showLabel) {
        init(name, TYPE_COLOR, showLabel);
        objectValue = value;
    }

    /**
     * Create an long Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, long value, int showLabel) {
        init(name, TYPE_LONG, showLabel);
        longValue = value;
    }

    /**
     * Create an int Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, int value, int showLabel) {
        init(name, TYPE_INT, showLabel);
        longValue = value;
    }

    /**
     * Create an double Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, double value, int showLabel) {
        init(name, TYPE_DOUBLE, showLabel);
        doubleValue = value;
    }

    /**
     * Create an boolean Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, boolean value, int showLabel) {
        init(name, TYPE_BOOLEAN, showLabel);
        booleanValue = value;
    }

    /**
     * Create a typed Attribute Value
     * @param name name of the value
     * @param value value
     * @param type tyoe of the value
     * @param showLabel code what to show as a label
     */
    public DefaultHepRepAttValue(String name, String value, String type, int showLabel) {
        int t = toType(type);
        init(name, t, showLabel);
        switch (t) {
            case TYPE_STRING:
                objectValue = value.intern();
                lowerCaseString = value.toLowerCase().intern();
                break;
            case TYPE_COLOR:
                objectValue = HepRepColor.get(value);
                break;
            case TYPE_LONG:
                longValue = Long.decode(value).longValue();
                break;
            case TYPE_INT:
                longValue = Integer.decode(value).intValue();
                break;
            case TYPE_DOUBLE:
                doubleValue = HepRepUtil.decodeNumber(value);
                break;
            case TYPE_BOOLEAN:
                booleanValue = Boolean.valueOf(value).booleanValue();
                break;
            default:
                System.err.println("Unknown type in DefaultHepRepAttValue: '"+type+"'");
                objectValue = value;
                break;
        }
    }

    private void init(String name, int type, int showLabel) {
        this.name = name.intern();
        this.lowerCaseName = name.toLowerCase().intern();
        this.type = type;
        this.showLabel = showLabel;
    }

    public HepRepAttValue copy() throws CloneNotSupportedException {
        DefaultHepRepAttValue copy = new DefaultHepRepAttValue();
        copy.init(name, type, showLabel);
        copy.lowerCaseString = lowerCaseString;
        copy.objectValue = objectValue;
        copy.longValue = longValue;
        copy.doubleValue = doubleValue;
        copy.booleanValue = booleanValue;
        return copy;
    }

    public String getName() {
        return name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public int getType() {
        return type;
    }

    // Use the toString method of object, so that this is well defined
    // for all data types.
    public String getTypeName() {
        return toString(type);
    }

    private static String labelStrings[] = {"NAME", "DESC", "VALUE", "EXTRA"};
    private static Map labelTable;
    static {
        labelTable = new HashMap(5);
        // FIXME: JHEPREP-4
        labelTable.put("NONE", new Integer(HepRepAttValue.SHOW_NONE));
        labelTable.put("NAME", new Integer(HepRepAttValue.SHOW_NAME));
        labelTable.put("DESC", new Integer(HepRepAttValue.SHOW_DESC));
        labelTable.put("VALUE", new Integer(HepRepAttValue.SHOW_VALUE));
        labelTable.put("EXTRA", new Integer(HepRepAttValue.SHOW_EXTRA));
    }

    /**
     * Convert showLabel string to a int code
     * @param labelString showLabel string
     * @return showLabel code
     */
    public static int toShowLabel(String labelString) {
        int showLabel = SHOW_NONE;
        if (labelString != null) {
            StringTokenizer st = new StringTokenizer(labelString, ", ");
            while (st.hasMoreTokens()) {
                String label = st.nextToken();
                Integer number = (Integer)labelTable.get(label);
                if (number != null) {
                    showLabel += number.intValue();
                } else {
                    showLabel += Integer.decode(label).intValue();
                }
            }
        }
        return showLabel;
    }

    /**
     * Converts a showLabel code to a string
     * @param showLabel code
     * @return showLabel string
     */
    public static String toShowLabel(int showLabel) {
        String label = null;
        if (showLabel == HepRepAttValue.SHOW_NONE) {
            label = "NONE";
        } else {
            for (int i=0; i<16; i++) {
                if (((showLabel >> i) & 0x0001) == 0x0001) {
                    if (label == null) {
                        label = "";
                    } else {
                        label += ", ";
                    }
                    if (i < labelStrings.length) {
                        label += labelStrings[i];
                    } else {
                        label += "0x"+Integer.toHexString(0x0001 << i);
                    }
                }
            }
        }
        return label;
    }

    /**
     * Converts a type code to a string
     * @param type code
     * @return type string
     */
    public static String toString(int type) {
        switch(type) {
            case TYPE_STRING: return("String");
            case TYPE_COLOR: return("Color");
            case TYPE_LONG: return("long");
            case TYPE_INT: return("int");
            case TYPE_DOUBLE: return("double");
            case TYPE_BOOLEAN: return("boolean");
            default: throw new RuntimeException("Unknown type stored in HepRepAttDef: '"+type+"'");
        }
    }

    private static final Map stringToType;
    static {
        stringToType = new HashMap(6);
        stringToType.put("String",      new Integer(TYPE_STRING));
        stringToType.put("Color",       new Integer(TYPE_COLOR));
        stringToType.put("long",        new Integer(TYPE_LONG));
        stringToType.put("int",         new Integer(TYPE_INT));
        stringToType.put("double",      new Integer(TYPE_DOUBLE));
        stringToType.put("boolean",     new Integer(TYPE_BOOLEAN));
    }

    /**
     * Converts type string to type code
     * @param type type string
     * @return code for type or -1 if unknown
     */
    public static int toType(String type) {
        Integer code = (type != null) ? (Integer)stringToType.get(type) : null;
        return (code == null) ? TYPE_UNKNOWN : code.intValue();
    }

    private static final Map types;
    static {
        try {
            XMLHepRepReader.readDefaults();
        } catch (IOException ioe) {
            System.err.println("Problem reading HepRep Defaults "+ioe);
        }
        
        // add all defaults to types.
        Set attValues = HepRepDefaults.getAttValues();
        types = new HashMap(attValues.size());
        for (Iterator i=attValues.iterator(); i.hasNext(); ) {
            HepRepAttValue attValue = (HepRepAttValue)i.next();
            types.put(attValue.getLowerCaseName(), attValue.getTypeName());
        }
    }

    /**
     * Add a new type for guessing
     * @param name name of the type
     * @param type suggested type
     */
    public static void addGuessedType(String name, String type) {
        types.put(name.toLowerCase(), type);
    }

    /**
     * Returns type, unless type is null, in which case it guesses the type from the name,
     * and if not found from the value. If found from the value a message is printed
     * and the new association is added to the name table. If not found, the type defaults
     * to String and is also added to the table, but no message is printed.
     * @param name name from which to guess the type
     * @param value value from which to guess the type
     * @param type suggested type, or null
     * @return guessed type
     */
    public static String guessType(String name, String value, String type) {
        if (type != null) return type;
        
        type = guessTypeFromName(name);

        if (type == null) {            
            type = guessTypeFromValue(value);

            if (!type.equals("String")) {
                System.out.println("Guessed type for '"+name+"' to be '"+type+"'.");
            }
            types.put(name.toLowerCase(), type);
        }
        return type;
    }
    
    /**
     * Returns type from a guess by name, or null if it cannot be guessed.
     * @param name name to guess from
     * @return guessed type
     */
    public static String guessTypeFromName(String name) {         
        return (String)types.get(name.toLowerCase());

    }

    /**
     * Returns type, guessed from the value. Defaults to String.
     * @param value value to guess from
     * @return guessed type
     */
    public static String guessTypeFromValue(String value) {
        if (value == null) return "String";
        try {
            // guess long and ints to be double to allow for flexibility...
            Double.valueOf(value);
            return "double";
        } catch (NumberFormatException e) {
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true")) {
            return "boolean";
        }
        try {
            ColorConverter cc = new ColorConverter();
            cc.stringToColor(value);
            return "Color";
        } catch (ColorConverter.ColorConversionException e) {
        }
        return "String";
    }

    public int showLabel() {
        return showLabel;
    }

    public String getString() throws HepRepTypeException {
        try {
            return (String)objectValue;
        } catch (ClassCastException cce) {
            throw new HepRepTypeException("Attribute Value for '"+getName()+
                                          "' with value '"+getAsString()+
                                          "' of type '"+getTypeName()+
                                          "' cannot be converted to type 'String'");
        }
    }

    public String getLowerCaseString() throws HepRepTypeException {
        try {
            return lowerCaseString;
        } catch (ClassCastException cce) {
            throw new HepRepTypeException("Attribute Value for '"+getName()+
                                          "' with value '"+getAsString()+
                                          "' of type '"+getTypeName()+
                                          "' cannot be converted to type 'String'");
        }
    }

    public Color getColor() throws HepRepTypeException {
        try {
            return (Color)objectValue;
        } catch (ClassCastException cce) {
            throw new HepRepTypeException("Attribute Value for '"+getName()+
                                          "' with value '"+getAsString()+
                                          "' of type '"+getTypeName()+
                                          "' cannot be converted to type 'Color'");
        }
    }

    public long getLong() throws HepRepTypeException {
        if ((type != TYPE_LONG) && (type != TYPE_INT)) {
            throw new HepRepTypeException("Attribute Value for '"+getName()+
                                          "' with value '"+getAsString()+
                                          "' of type '"+getTypeName()+
                                          "' cannot be converted to type 'long'");
        }
        return longValue;
    }

    public int getInteger() throws HepRepTypeException {
        if (type != TYPE_INT) {
            throw new HepRepTypeException("Attribute Value for '"+getName()+
                                          "' with value '"+getAsString()+
                                          "' of type '"+getTypeName()+
                                          "' cannot be converted to type 'int'");
        }
        return (int)longValue;
    }

    public double getDouble() throws HepRepTypeException {
        if ((type != TYPE_DOUBLE) && (type != TYPE_LONG) && (type != TYPE_INT)) {
            throw new HepRepTypeException("Attribute Value for '"+getName()+
                                          "' with value '"+getAsString()+
                                          "' of type '"+getTypeName()+
                                          "' cannot be converted to type 'double'");
        }
        return (type == TYPE_DOUBLE) ? doubleValue : longValue;
    }

    public boolean getBoolean() throws HepRepTypeException {
        if ((type != TYPE_BOOLEAN) && (type != TYPE_LONG) && (type != TYPE_INT))  {
            throw new HepRepTypeException("Attribute Value for '"+getName()+"'and value+'"+objectValue+"' of type'"+getTypeName()+"' cannot be converted to type 'boolean'");
        }
        return (type == TYPE_BOOLEAN) ? booleanValue : longValue != 0;
    }

    public String getAsString() {
        return getAsString(this);
    }

    /**
     * Return the Attribute Value as string
     * @param attValue attribute value
     * @return attribute string
     */
    public static String getAsString(HepRepAttValue attValue) {
        switch(attValue.getType()) {
            case TYPE_STRING:
                return attValue.getString();
            case TYPE_COLOR:
                return getAsString(attValue.getColor());
            case TYPE_LONG:
                return getAsString(attValue.getLong());
            case TYPE_INT:
                return getAsString(attValue.getInteger());
            case TYPE_DOUBLE:
                return getAsString(attValue.getDouble());
            case TYPE_BOOLEAN:
                return getAsString(attValue.getBoolean());
            default:
                return "Unknown typecode: "+attValue.getType();
        }
    }
    
    /**
     * Return Color Attribute Value as String
     * @param value value
     * @return attribute string
     */
    public static String getAsString(Color value) {
        return HepRepColor.get(value);
    }

    /**
     * Return long Attribute Value as String
     * @param value value
     * @return attribute string
     */
    public static String getAsString(long value) {
        return Long.toString(value);
    }

    /**
     * Return int Attribute Value as String
     * @param value value
     * @return attribute string
     */
    public static String getAsString(int value) {
        return Integer.toString(value);
    }

    /**
     * Return double Attribute Value as String
     * @param value value
     * @return attribute string
     */
    public static String getAsString(double value) {
        return scientific.format(value);
    }

    /**
     * Return boolean Attribute Value as String
     * @param value value
     * @return attribute string
     */
    public static String getAsString(boolean value) {
        return value ? "true" : "false";
    }

    public String toString() {
        return getClass()+"["+
               "name(lcase)="+getLowerCaseName()+", "+
               "value="+getAsString()+", "+
               "showLabel="+toShowLabel(showLabel())+"]";
    }

/* Disabled for FREEHEP-386
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof HepRepAttValue) {
            HepRepAttValue attValue = (HepRepAttValue)o;
            boolean r = (attValue.getLowerCaseName().equals(getLowerCaseName()) &&
                        (attValue.getType() == getType()) &&
                        (attValue.showLabel() == showLabel()));
            
            if (r) {
                switch(attValue.getType()) {
                    case TYPE_STRING:
                        r = r && (attValue.getString().equals(getString()));
                        break;
                    case TYPE_COLOR:
                        r = r && (attValue.getColor().equals(getColor()));
                        break;
                    case TYPE_LONG:
                        r = r && (attValue.getLong() == getLong());
                        break;
                    case TYPE_INT:
                        r = r && (attValue.getInteger() == getInteger());
                        break;
                    case TYPE_DOUBLE:
// FREEHEP-386
//                        r = r && (attValue.getDouble() == getDouble());
                        break;
                    case TYPE_BOOLEAN:
                        r = r && (attValue.getBoolean() == getBoolean());
                        break;
                    default:
                        r = false;
                        break;
                }
            }
            if (HepRepUtil.debug() && !r) {
                System.out.println(this+" != "+attValue);
            }
            return r;
        }
        return false;
    }

    public int hashCode() {
        long hash = getLowerCaseName().hashCode();
        hash |= getType();
        hash |= showLabel();
        switch(getType()) {
            default:
            case TYPE_STRING:
                hash |= getString().hashCode();
                break;
            case TYPE_COLOR:
                hash |= getColor().hashCode();
                break;
            case TYPE_LONG:
                hash |= getLong();
                break;
            case TYPE_INT:
                hash |= getInteger();
                break;
            case TYPE_DOUBLE:
                hash |= Double.doubleToLongBits(getDouble());
                break;
            case TYPE_BOOLEAN:
                hash |= getBoolean() ? 1 : 2;
                break;
        }
        return (int)hash;
    }
*/
}

