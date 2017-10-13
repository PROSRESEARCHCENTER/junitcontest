// Copyright 2000-2004, FreeHEP.

package hep.graphics.heprep1.ref;

import hep.graphics.heprep1.HepRepAttValue;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepDefaults.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepDefaults {

    private static Map defaults;
    static {
        defaults = new HashMap(75);
        // FIXME, capitalize the rest...
        defaults.put("drawas",              new DefaultHepRepAttValue("DrawAs","PolyPoint",0x0000));
        defaults.put("drawasoptions",       new DefaultHepRepAttValue("DrawAsOptions","PolyPoint",0x0000));
        defaults.put("label",               new DefaultHepRepAttValue("Label","",0x0000));
        defaults.put("visibility",          new DefaultHepRepAttValue("Visibility","True",0x0000));
        defaults.put("fontname",            new DefaultHepRepAttValue("FontName","SansSerif",0x0000));
        defaults.put("fontstyle",           new DefaultHepRepAttValue("FontStyle","Plain",0x0000));
        defaults.put("fontsize",            new DefaultHepRepAttValue("fontsize","12",0x0000));
        defaults.put("fontcolor",           new DefaultHepRepAttValue("fontcolor","White",0x0000));
        defaults.put("fonthasframe",        new DefaultHepRepAttValue("fonthasframe","True",0x0000));
        defaults.put("fontframecolor",      new DefaultHepRepAttValue("fontframecolor","White",0x0000));
        defaults.put("fontframewidth",      new DefaultHepRepAttValue("fontframewidth","2",0x0000));
        defaults.put("fonthasbanner",       new DefaultHepRepAttValue("fonthasbanner","False",0x0000));
        defaults.put("fontbannercolor",     new DefaultHepRepAttValue("fontbannercolor","Black",0x0000));
        defaults.put("layer",               new DefaultHepRepAttValue("layer","100",0x0000));
        defaults.put("markname",            new DefaultHepRepAttValue("markname","Dot",0x0000));
        defaults.put("marksize",            new DefaultHepRepAttValue("marksize","6",0x0000));
        defaults.put("markcolor",           new DefaultHepRepAttValue("markcolor","White",0x0000));
        defaults.put("markhasframe",        new DefaultHepRepAttValue("markhasframe","True",0x0000));
        defaults.put("markframecolor",      new DefaultHepRepAttValue("markframecolor","Black",0x0000));
        defaults.put("markframewidth",      new DefaultHepRepAttValue("markframewidth","2",0x0000));
        defaults.put("linecolor",           new DefaultHepRepAttValue("linecolor","White",0x0000));
        defaults.put("linestyle",           new DefaultHepRepAttValue("linestyle","Solid",0x0000));
        defaults.put("linewidth",           new DefaultHepRepAttValue("linewidth","2",0x0000));
        defaults.put("linehasframe",        new DefaultHepRepAttValue("linehasframe","True",0x0000));
        defaults.put("lineframecolor",      new DefaultHepRepAttValue("lineframecolor","Black",0x0000));
        defaults.put("lineframewidth",      new DefaultHepRepAttValue("lineframewidth","2",0x0000));
        defaults.put("fillcolor",           new DefaultHepRepAttValue("fillcolor","White",0x0000));
        defaults.put("fill",                new DefaultHepRepAttValue("fill","False",0x0000));
        defaults.put("radius",              new DefaultHepRepAttValue("radius","6",0x0000));
        defaults.put("phi",                 new DefaultHepRepAttValue("phi","0",0x0000));
        defaults.put("theta",               new DefaultHepRepAttValue("theta","0",0x0000));
        defaults.put("omega",               new DefaultHepRepAttValue("omega","0",0x0000));
        defaults.put("radius1",             new DefaultHepRepAttValue("radius1","5",0x0000));
        defaults.put("phi1",                new DefaultHepRepAttValue("phi1","0",0x0000));
        defaults.put("theta1",              new DefaultHepRepAttValue("theta1","0",0x0000));
        defaults.put("radius2",             new DefaultHepRepAttValue("radius2","5",0x0000));
        defaults.put("phi2",                new DefaultHepRepAttValue("phi2","0",0x0000));
        defaults.put("theta2",              new DefaultHepRepAttValue("theta2","0",0x0000));
        defaults.put("sum",                 new DefaultHepRepAttValue("sum","5",0x0000));
        defaults.put("sum1",                new DefaultHepRepAttValue("sum1","5",0x0000));
        defaults.put("sum2",                new DefaultHepRepAttValue("sum2","5",0x0000));
        defaults.put("radiusx",             new DefaultHepRepAttValue("radiusx","5",0x0000));
        defaults.put("radiusy",             new DefaultHepRepAttValue("radiusy","5",0x0000));
        defaults.put("radiusz",             new DefaultHepRepAttValue("radiusz","5",0x0000));
        defaults.put("curvature",           new DefaultHepRepAttValue("curvature","0.02",0x0000));
        defaults.put("flylength",           new DefaultHepRepAttValue("flylength","10",0x0000));
        defaults.put("text",                new DefaultHepRepAttValue("text","",0x0000));
        defaults.put("hpos",                new DefaultHepRepAttValue("hpos","1.0",0x0000));
        defaults.put("vpos",                new DefaultHepRepAttValue("vpos","0.5",0x0000));
        defaults.put("halign",              new DefaultHepRepAttValue("halign","Center",0x0000));
        defaults.put("valign",              new DefaultHepRepAttValue("valign","Bottom",0x0000));
    }

    private HepRepDefaults() {
    }
    
    /**
     * @param name attribute name
     * @return attribute value for name, or null
     */
    public static HepRepAttValue getAttValue(String name) {
        return (HepRepAttValue)defaults.get(name.toLowerCase());
    }
}

