// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import hep.graphics.heprep1.HepRepAttValue;
import hep.graphics.heprep1.HepRepColor;
import hep.graphics.heprep1.HepRepFont;

import java.awt.Color;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TCKind;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAttValueAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepAttValueAdapter implements HepRepAttValue {

    private hep.graphics.heprep1.corba.idl.HepRepAttValue hepRepAttValue;

    /**
     * Add a CORBA Wrapper
     * @param hepRepAttValue att value
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.corba.idl.HepRepAttValue hepRepAttValue) {
        this.hepRepAttValue = hepRepAttValue;
    }

    public String getName() {
        return hepRepAttValue.name;
    }
    
    // FIXME: when we change the IDL, we can change this to point to CORBA
    public int showLabel() {
        return hepRepAttValue.label;
    }
    
    // warning, this object cannot be used to determine the type!
    public Object getValue() {
        try {
            return hepRepAttValue.value.extract_Object();
        } catch (BAD_OPERATION bo) {
            return "BAD_OPERATION";
        }
    }

    public String getString() {
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_string)) 
            return hepRepAttValue.value.extract_string();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_wstring)) 
            return hepRepAttValue.value.extract_wstring();
        // Have to do this the complicated way since JDK1.3.1_02 is missing the
        // method Boolean.toString().
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_boolean))
            if (hepRepAttValue.value.extract_boolean() == true)
                return "TRUE";
            else
                return "FALSE";
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_double)) 
            return Double.toString(hepRepAttValue.value.extract_double());
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_float)) 
            return Float.toString(hepRepAttValue.value.extract_float());
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_long)) 
            return Long.toString(hepRepAttValue.value.extract_long());
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_short)) 
            return Short.toString(hepRepAttValue.value.extract_short());
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ulong)) 
            return Long.toString(hepRepAttValue.value.extract_ulong());
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ushort)) 
            return Short.toString(hepRepAttValue.value.extract_ushort());
        return "BAD_OPERATION";
    }

    public long getLong() {
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_short)) 
            return hepRepAttValue.value.extract_short();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_long)) 
            return hepRepAttValue.value.extract_long();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ushort)) 
            return hepRepAttValue.value.extract_ushort();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ulong)) 
            return hepRepAttValue.value.extract_ulong();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_longlong)) 
            return hepRepAttValue.value.extract_longlong();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ulonglong)) 
            return hepRepAttValue.value.extract_ulonglong();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_double)) 
            return (int)hepRepAttValue.value.extract_double();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_float))  
            return (int)hepRepAttValue.value.extract_float();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_longdouble)) 
// Not supported (yet) OB 3.3.1
//            return hepRepAttValue.value.extract_longdouble();
            return (int)hepRepAttValue.value.extract_double();
        throw new BAD_OPERATION();
    }

    public int getInteger() {
        return (int)getLong();
    }
    
    public double getDouble() {
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_short)) 
            return hepRepAttValue.value.extract_short();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_long)) 
            return hepRepAttValue.value.extract_long();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ushort)) 
            return hepRepAttValue.value.extract_ushort();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ulong)) 
            return hepRepAttValue.value.extract_ulong();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_longlong)) 
            return hepRepAttValue.value.extract_longlong();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_ulonglong)) 
            return hepRepAttValue.value.extract_ulonglong();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_double)) 
            return hepRepAttValue.value.extract_double();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_float))  
            return hepRepAttValue.value.extract_float();
        if (hepRepAttValue.value.type().kind().equals(TCKind.tk_longdouble)) 
// Not supported (yet) OB 3.3.1
//            return hepRepAttValue.value.extract_longdouble();
            return hepRepAttValue.value.extract_double();
        throw new BAD_OPERATION();
    }
    
    public boolean getBoolean() {
        return hepRepAttValue.value.extract_boolean();
    }        
    
    public Color getColor() {
        return HepRepColor.get(getString());
    }

    public int getFontStyle() {
        return HepRepFont.getStyle(getString());
    }
}

