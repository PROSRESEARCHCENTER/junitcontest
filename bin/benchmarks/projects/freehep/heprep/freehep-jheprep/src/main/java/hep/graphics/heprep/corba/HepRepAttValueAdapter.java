// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corba;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepTypeException;
import hep.graphics.heprep.ref.DefaultHepRepAttValue;
import hep.graphics.heprep.util.HepRepColor;
import hep.graphics.heprep.util.HepRepUtil;

import java.awt.Color;

import org.omg.CORBA.TCKind;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepAttValueAdapter.java 13861 2011-07-21 00:30:38Z onoprien $
 */

public class HepRepAttValueAdapter implements HepRepAttValue {

    private hep.graphics.heprep.corba.idl.HepRepAttValue hepRepAttValue;
    private String lowerCaseName;
    private int type;       // cached type
    private Color color;    // cached color

    /**
     * Create a CORBA wrapper for an Attribute Value
     * @param hepRepAttValue corba att value
     */
    public HepRepAttValueAdapter(hep.graphics.heprep.corba.idl.HepRepAttValue hepRepAttValue) {
        this.hepRepAttValue = hepRepAttValue;
        lowerCaseName = hepRepAttValue.name.toLowerCase().intern();
        type = 0;
        color = null;
    }

    public HepRepAttValue copy() throws CloneNotSupportedException {
        switch (getType()) {
            case TYPE_STRING:
                return new DefaultHepRepAttValue(getName(), getString(), showLabel());
            case TYPE_COLOR:
                return new DefaultHepRepAttValue(getName(), getColor(), showLabel());
            case TYPE_INT:
                return new DefaultHepRepAttValue(getName(), getInteger(), showLabel());
            case TYPE_LONG:
                return new DefaultHepRepAttValue(getName(), getLong(), showLabel());
            case TYPE_DOUBLE:
                return new DefaultHepRepAttValue(getName(), getDouble(), showLabel());
            case TYPE_BOOLEAN:
                return new DefaultHepRepAttValue(getName(), getBoolean(), showLabel());
            default:
                throw new CloneNotSupportedException("Type not supported: "+getType());
        }

    }

    public String getName() {
        return hepRepAttValue.name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public int getType() {
        if (type == 0) {
            switch(hepRepAttValue.value.type().kind().value()) {
                case TCKind._tk_string:
                case TCKind._tk_wstring:
                    // just in case it is a non-corba type (Color, ...)
                    String typeName = DefaultHepRepAttValue.guessType(getName(), null, null);
                    type = DefaultHepRepAttValue.toType(typeName);
                    break;
                case TCKind._tk_short:
                case TCKind._tk_long:
                case TCKind._tk_ulong:
                    type = TYPE_INT;
                    break;
                case TCKind._tk_longlong:
                case TCKind._tk_ulonglong:
                    type = TYPE_LONG;
                    break;
                case TCKind._tk_double:
                case TCKind._tk_float:
                case TCKind._tk_longdouble:
                    type = TYPE_DOUBLE;
                    break;
                case TCKind._tk_boolean:
                    type = TYPE_BOOLEAN;
                    break;
                default:
                    type = TYPE_UNKNOWN;
                    break;
            }
        }
        return type;
    }

    public String getTypeName() {
        return DefaultHepRepAttValue.toString(getType());
    }

    public int showLabel() {
        return hepRepAttValue.showLabel;
    }

    public String getString() throws HepRepTypeException {
        switch (hepRepAttValue.value.type().kind().value()) {
            case TCKind._tk_string:
                return hepRepAttValue.value.extract_string().intern();
            case TCKind._tk_wstring:
                return hepRepAttValue.value.extract_wstring().intern();
            default:
                throw new HepRepTypeException("Attribute Value of type '"+getTypeName()+"' cannot be converted to type 'String'");
        }
    }

    public String getLowerCaseString() throws HepRepTypeException {
        return getString().toLowerCase().intern();
    }

    public Color getColor() throws HepRepTypeException {
        if (color == null) {
            color = HepRepColor.get(getString());
        }
        return color;
    }

    public long getLong() throws HepRepTypeException {
        switch (hepRepAttValue.value.type().kind().value()) {
            case TCKind._tk_longlong:
                return hepRepAttValue.value.extract_longlong();
            case TCKind._tk_ulonglong:
                return hepRepAttValue.value.extract_ulonglong();
            default:
                throw new HepRepTypeException("Attribute Value of type '"+getTypeName()+"' cannot be converted to type 'long'");
        }
    }

    public int getInteger() throws HepRepTypeException {
        switch(hepRepAttValue.value.type().kind().value()) {
            case TCKind._tk_short:
                return hepRepAttValue.value.extract_short();
            case TCKind._tk_long:
                return hepRepAttValue.value.extract_long();
            case TCKind._tk_ushort:
                return hepRepAttValue.value.extract_ushort();
            case TCKind._tk_ulong:
                return hepRepAttValue.value.extract_ulong();
            default:
                throw new HepRepTypeException("Attribute Value of type '"+getTypeName()+"' cannot be converted to type 'int'");
        }
    }

    public double getDouble() throws HepRepTypeException {
        switch(hepRepAttValue.value.type().kind().value()) {
            case TCKind._tk_double:
            case TCKind._tk_longdouble:
                return hepRepAttValue.value.extract_double();
            case TCKind._tk_float:
                return hepRepAttValue.value.extract_float();
            default:
                throw new HepRepTypeException("Attribute Value of type '"+getTypeName()+"' cannot be converted to type 'double'");
        }
    }

    public boolean getBoolean() throws HepRepTypeException {
        switch(hepRepAttValue.value.type().kind().value()) {
            case TCKind._tk_boolean:
                return hepRepAttValue.value.extract_boolean();
            case TCKind._tk_short:
                return hepRepAttValue.value.extract_short() != 0;
            case TCKind._tk_long:
                return hepRepAttValue.value.extract_long() != 0;
            case TCKind._tk_ushort:
                return hepRepAttValue.value.extract_ushort() != 0;
            case TCKind._tk_ulong:
                return hepRepAttValue.value.extract_ulong() != 0;
            default:
                throw new HepRepTypeException("Attribute Value of type '"+getTypeName()+"' cannot be converted to type 'boolean'");
        }
    }

    public String getAsString() {
        return DefaultHepRepAttValue.getAsString(this);
    }

    public String toString() {
        return getClass()+"["+
               "name(lcase)="+getLowerCaseName()+", "+
               "value="+getAsString()+", "+
               "showLabel="+DefaultHepRepAttValue.toShowLabel(showLabel())+"]";
    }

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
                        r = false;
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
}

