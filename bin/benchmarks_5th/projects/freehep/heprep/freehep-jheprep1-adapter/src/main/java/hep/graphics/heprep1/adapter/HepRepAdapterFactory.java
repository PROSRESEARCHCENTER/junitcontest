// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.ref.DefaultHepRepAttValue;
import hep.graphics.heprep.util.HepRepColor;
import hep.graphics.heprep.xml.XMLHepRepFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepAdapterFactory.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepAdapterFactory extends XMLHepRepFactory {

    private static HepRepAdapterFactory factory;
    private static Map/*<String, Map<String, String> >*/ valueTranslator;
    static {
        // FIXME, more translations
        // NOTE: lowercase keys
        Map poly = new HashMap();
        poly.put("polypoint",   "Point");
        poly.put("polyline",    "Line");
        
        Map fonts = new HashMap();
        fonts.put("arial",      "SansSerif");
        fonts.put("helvetica",  "SansSerif");
        fonts.put("times",      "Serif");
        fonts.put("courrier",   "MonoSpaced");
        
        // NOTE: lowercase keys
        valueTranslator = new HashMap();
        valueTranslator.put("drawas",               poly);
        valueTranslator.put("drawasoptions",        poly);
        valueTranslator.put("fontname",             fonts);
    }

    private HepRepAdapterFactory() {
    }

    /**
     * @return adapter factory singleton
     */
    public static HepRepAdapterFactory getFactory() {
        if (factory == null) {
            factory = new HepRepAdapterFactory();
        }
        return factory;
    }

    /**
     * Create a HepRep instance from a HepRep1
     * @param heprep1 heprep1
     * @return heprep2
     */
    public HepRep createHepRep(hep.graphics.heprep1.HepRep heprep1) {
        return new HepRepAdapter(heprep1);
    }

    /**
     * Create a HepRep AttValue
     * @param value1 heprep1 attvalue
     * @return heprep2 attvalue
     */
    public HepRepAttValue createHepRepAttValue(hep.graphics.heprep1.HepRepAttValue value1) {
        if (value1 == null) return null;
         
        String name = AttributeNameTranslator.getName2(value1.getName());

        // FIXME get type in a different way if possible
        String type = DefaultHepRepAttValue.guessType(name, value1.getString(), null);
        int typeCode = DefaultHepRepAttValue.toType(type);
        
        switch (typeCode) {
            case HepRepAttValue.TYPE_COLOR:
                return new HepRepAttValueAdapter(value1, name, HepRepColor.get(value1.getString()), value1.showLabel());
            case HepRepAttValue.TYPE_LONG:
                return new HepRepAttValueAdapter(value1, name, value1.getLong(), value1.showLabel());
            case HepRepAttValue.TYPE_INT:
                return new HepRepAttValueAdapter(value1, name, value1.getInteger(), value1.showLabel());
            case HepRepAttValue.TYPE_DOUBLE:
                return new HepRepAttValueAdapter(value1, name, value1.getDouble(), value1.showLabel());
            case HepRepAttValue.TYPE_BOOLEAN:
                return new HepRepAttValueAdapter(value1, name, value1.getBoolean(), value1.showLabel());
            case HepRepAttValue.TYPE_STRING:
                String s = value1.getString();
                Map translations = (Map)valueTranslator.get(name.toLowerCase());
                if (translations != null) {
                    for (Iterator i=translations.keySet().iterator(); i.hasNext(); ) {
                        String key = (String)i.next();
                        String value = (String)translations.get(key);
    	                Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
    	                s = pattern.matcher(s).replaceAll(value);
                    }
                }
                return new HepRepAttValueAdapter(value1, name, s, value1.showLabel());
            default:
                System.err.println("Unknown type in DefaultHepRepAttValue: '"+type+"'");
                return new HepRepAttValueAdapter(value1, name, value1.getString(), value1.showLabel());
         }
    }

    /**
     * Create a HepRep2 AttDef
     * @param def1 heprep1 attdef
     * @return heprep2 attdef
     */
    public HepRepAttDef createHepRepAttDef(hep.graphics.heprep1.HepRepAttDef def1) {
        if (def1 == null) return null;
        return new HepRepAttDefAdapter(def1, 
                                       AttributeNameTranslator.getName2(def1.getName()),
                                       def1.getDescription(),
                                       def1.getType(),
                                       def1.getExtra());
    }

    /**
     * Create a HepRep2 Type
     * @param heprep1 heprep1
     * @param parent heprep2 parent type
     * @return heprep2 type
     */
    public HepRepType createHepRepType(hep.graphics.heprep1.HepRep heprep1, HepRepType parent) {
        return new HepRepTypeFromHepRepAdapter(heprep1, parent);
    }

    /**
     * Create a HepRep2 Type
     * @param type1 heprep1 type
     * @param parent heprep2 parent type
     * @param instance heprep2 instance
     * @return heprep2 type
     */
    public HepRepType createHepRepType(hep.graphics.heprep1.HepRepType type1, HepRepType parent, HepRepInstance instance) {
        return new HepRepTypeFromTypeAdapter(type1, parent, instance);
    }

    /**
     * Create a HepRep2 Type
     * @param type1 heprep1 type
     * @param instance1 heprep1 instance
     * @param parent heprep2 parent type
     * @param instance heprep2 instance
     * @param suffix ???
     * @return heprep2 type
     */
    public HepRepType createHepRepType(hep.graphics.heprep1.HepRepType type1, hep.graphics.heprep1.HepRepInstance instance1, HepRepType parent, HepRepInstance instance, String suffix) {
        return new HepRepTypeFromInstanceAdapter(type1, instance1, parent, instance, suffix);
    }

    /**
     * Create a HepRep2 Instance
     * @param heprep1 heprep1
     * @param parent heprep2 parent instance
     * @param type heprep2 type
     * @return heprep2 instance
     */
    public HepRepInstance createHepRepInstance(hep.graphics.heprep1.HepRep heprep1, HepRepInstance parent, HepRepType type) {
        return new HepRepInstanceFromHepRepAdapter(heprep1, parent, type);
    }

    /**
     * Create a HepRep2 Instance
     * @param instance1 heprep1 instance
     * @param parent heprep2 parent instance
     * @param type heprep2 type
     * @return heprep2 instance
     */
    public HepRepInstance createHepRepInstance(hep.graphics.heprep1.HepRepInstance instance1, HepRepInstance parent, HepRepType type) {
        return new HepRepInstanceFromInstanceAdapter(instance1, parent, type);
    }

    /**
     * Create a HepRep2 Instance
     * @param instance1 heprep1 instance
     * @param primitive1 heprep1 primitive
     * @param parent heprep2 parent instance
     * @param type heprep2 type
     * @return heprep2 instance
     */
    public HepRepInstance createHepRepInstance(hep.graphics.heprep1.HepRepInstance instance1, hep.graphics.heprep1.HepRepPrimitive primitive1, HepRepInstance parent, HepRepType type) {
        return new HepRepInstanceFromPrimitiveAdapter(instance1, primitive1, parent, type);
    }

    /**
     * Create a HepRep2 Point
     * @param point1 heprep1 point
     * @param parent heprep2 parent instance
     * @return heprep2 point
     */
    public HepRepPoint createHepRepPoint(hep.graphics.heprep1.HepRepPoint point1, HepRepInstance parent) {
        return new HepRepPointAdapter(point1, parent);
    }

//    public HepRepInstance createHepRepInstance(hep.graphics.heprep1.HepRepType type1, HepRepInstance parent, HepRepType type) {
//        return new HepRepInstanceFromTypeAdapter(type1, parent, type);
//    }

//    public HepRepInstance createHepRepInstanceFromPoints(hep.graphics.heprep1.HepRepType type1, HepRepInstance parent, HepRepType type) {
//        return new HepRepInstanceFromPointAdapter(type1, parent, type);
//    }

//    public HepRepInstance createHepRepInstanceFromPoints(hep.graphics.heprep1.HepRepInstance instance1, HepRepInstance parent, HepRepType type) {
//        return new HepRepInstanceFromPointAdapter(instance1, parent, type);
//    }
}