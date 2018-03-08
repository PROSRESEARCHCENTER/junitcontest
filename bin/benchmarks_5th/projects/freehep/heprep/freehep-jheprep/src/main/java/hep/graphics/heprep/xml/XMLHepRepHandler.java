// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.xml;

import java.io.*;
import java.net.*;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.freehep.util.VersionComparator;

import hep.graphics.heprep.*;
import hep.graphics.heprep.ref.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: XMLHepRepHandler.java 8584 2006-08-10 23:06:37Z duns $
 */

public class XMLHepRepHandler extends DefaultHandler {

    /**
     * Minimum expected HepRep version number.
     */
    public static final String expectedVersion = "2.0";
    
    private boolean isHepRep2 = false;

    private HepRepFactory factory;
    private HepRep heprep;
    private int level = 0;

    private HepRepTypes types;

    private HepRepTypeTree currentTypeTree = null;
    private HepRepTreeID currentTypeTreeRef = null;
    private HepRepInstanceTree instanceTree = null;

    private HepRepPoint currentPoint = null;

    private Stack/*<HepRepInstance>*/ instanceStack = new Stack();
    private HepRepInstance currentInstance = null;

    private Stack/*<HepRepType>*/ typeStack = new Stack();
    private HepRepType currentType = null;

    XMLHepRepHandler(HepRep heprep) {
        this.heprep = heprep;
        factory = new XMLHepRepFactory();
    }

    public void startDocument() throws SAXException {
        types = new HepRepTypes();
        currentTypeTree = null;
        currentTypeTreeRef = null;
        instanceTree = null;
        currentPoint = null;
        instanceStack = new Stack();
        currentInstance = null;
        typeStack = new Stack();
        currentType = null;
        level = 0;
    }

    public void startElement(String namespace, String tag, String qName, Attributes atts) throws SAXException {
        level++;
        tag = tag.intern();
//               System.out.println(namespace+", "+tag+", "+qName);
        if (tag == "point") {
            double x = HepRepUtil.decodeNumber(atts.getValue("x"));
            double y = HepRepUtil.decodeNumber(atts.getValue("y"));
            double z = HepRepUtil.decodeNumber(atts.getValue("z"));
            currentPoint = factory.createHepRepPoint(currentInstance, x, y, z);

        } else if (tag == "instancetree") {
            String name = atts.getValue("name");
            String version = atts.getValue("version");
            currentTypeTreeRef = factory.createHepRepTreeID(atts.getValue("typetreename"), atts.getValue("typetreeversion"));
            instanceTree = factory.createHepRepInstanceTree(name, version, currentTypeTreeRef);
            heprep.addInstanceTree(instanceTree);
//                System.out.println("Created Instance Tree: "+name+", "+version);

        } else if (tag == "treeid") {
            String name = atts.getValue("name");
            String version = atts.getValue("version");
            String qualifier = atts.getValue("qualifier");
            instanceTree.addInstanceTree(factory.createHepRepTreeID(name, version, qualifier));

        } else if (tag == "action") {
            String name = atts.getValue("name");
            String expression = atts.getValue("expression");
            // FIXME: JHEPREP-3
            factory.createHepRepAction(name, expression);

        } else if (tag == "layer") {
            String order = atts.getValue("order");
            StringTokenizer st = new StringTokenizer(order,",");
            while (st.hasMoreTokens()) {
                heprep.addLayer(st.nextToken().trim());
            }

        } else if (tag == "instance") {
            String typeName = atts.getValue("type");
            if (typeName == null) {
                throw new SAXException("[XMLHepRepReader] Instance cannot exist without referring to a type.");
            }
            HepRepType type = types.getType(currentTypeTreeRef, typeName, currentInstance);
            if (type == null) {
                throw new SAXException("[XMLHepRepReader] Cannot find type: '"+typeName+"' "+
                                       "in tree: '"+types.getID(currentTypeTreeRef)+"'");
            }
            instanceStack.push(currentInstance);
            if (currentInstance != null) {
                currentInstance = factory.createHepRepInstance(currentInstance, type);
            } else {
                currentInstance = factory.createHepRepInstance(instanceTree, type);
            }

        } else if (tag == "typetree") {
            isHepRep2 = true;
            String name = atts.getValue("name");
            String version = atts.getValue("version");
            HepRepTreeID id = factory.createHepRepTreeID(name, version);
            currentTypeTree = factory.createHepRepTypeTree(id);
            heprep.addTypeTree(currentTypeTree);
            
            types.put(id, currentTypeTree);            
//                System.out.println("Created Type Tree: "+name+", "+version);

        } else if (tag == "type") {
            // if we did not get a typetree, no heprep2.
            if (!isHepRep2) {
                throw new SAXException(
                    new HepRepVersionException(getClass()+": Could not deduce heprep version, expected version '"+expectedVersion+"'.")
                ); 
            }

            String name = atts.getValue("name");
            typeStack.push(currentType);
            if (currentType != null) {
                currentType = factory.createHepRepType(currentType, name);
            } else {
                currentType = factory.createHepRepType(currentTypeTree, name);
            }
            
            types.put(currentTypeTree, name, currentType);            
        } else if (tag == "heprep") {
            // heprep already created, check version
            String version = atts.getValue("version");
            if (version != null) {
                isHepRep2 = true;
                VersionComparator comparator = new VersionComparator();
                if (comparator.versionNumberCompare(version,expectedVersion) < 0) {
                    throw new SAXException(
                        new HepRepVersionException(getClass()+": Found version '"+version+"' while expected version '"+expectedVersion+"'.")
                    );
                }
            } else {
                // check for hepreps with no versions attribute
                String xmlns = atts.getValue("xmlns");
                if ((xmlns != null) && xmlns.startsWith("http://java.freehep.org/schemas/heprep/2")) {
                    isHepRep2 = true;
                }
            }
        } else if (tag == "attvalue") {
            String name = atts.getValue("name");
            String value = atts.getValue("value");
            String type = DefaultHepRepAttValue.guessType(name, value, atts.getValue("type"));
            // Here for backward compatibility with G4.5.0 we keep the cased showLabel around
            String showLabelString = atts.getValue("showlabel");
            if (showLabelString == null) showLabelString = atts.getValue("showLabel");
            int showLabel = DefaultHepRepAttValue.toShowLabel(showLabelString);

            HepRepAttValue attValue = new DefaultHepRepAttValue(name, value, type, showLabel);

            if (currentPoint != null) {
                currentPoint.addAttValue(attValue);
            } else if (currentInstance != null) {
                currentInstance.addAttValue(attValue);
            } else {
                if (currentType == null) {
                    throw new SAXException("[XMLHepRepReader] Cannot use 'attvalue' outside 'type' tag.");
                }
                currentType.addAttValue(attValue);
            }

        } else if (tag == "attdef") {
            if (!isHepRep2) {
                throw new SAXException(
                    new HepRepVersionException(getClass()+": Found a probable HepRep1 version while expected version '"+expectedVersion+"'.")
                );
            }
        
            if (currentType == null) {
                throw new SAXException("[XMLHepRepReader] Cannot use 'attdef' outside 'type' tag.");
            }

            String name = atts.getValue("name");
            String desc = atts.getValue("desc");
            String category = atts.getValue("category");
            String extra = atts.getValue("extra");
            currentType.addAttDef(name, desc, category, extra);

        } else {
            throw new SAXException("[XMLHepRepReader] Unknown tag: <"+
                                   ((namespace != null) ? namespace+":" : "")+
                                   tag+"> qualifiedName: '"+qName+"'");
        }

        if (Thread.interrupted()) throw new SAXException(new InterruptedException());
    }

    public void endElement(String namespace, String tag, String qName) throws SAXException {
        try {
//                System.out.println("/"+namespace+", "+tag+", "+qName);
            if (tag.lastIndexOf(':') >= 0) tag = tag.substring(tag.lastIndexOf(':')+1);
            tag = tag.intern();
            if (tag == "point") {
                currentPoint = null;
            } else if (tag == "instancetree") {
                instanceTree = null;
                currentTypeTreeRef = null;
            } else if (tag == "treeid") {
            } else if (tag == "layer") {
            } else if (tag == "instance") {
                if (currentInstance instanceof DefaultHepRepInstance) ((DefaultHepRepInstance)currentInstance).optimize();
                currentInstance = (HepRepInstance)instanceStack.pop();
            } else if (tag == "typetree") {
                currentTypeTree = null;
            } else if (tag == "type") {
                currentType = (HepRepType)typeStack.pop();
            } else if (tag == "heprep") {
                // ignored, toplevel already stored
            } else if (tag == "attvalue") {
            } else if (tag == "attdef") {
            } else {
                throw new SAXException("[XMLHepRepReader] Unknown tag: "+tag);
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }

        if (Thread.interrupted()) throw new SAXException(new InterruptedException());

        // make sure this is at the end, and that heprep is set!
        level--;
//        if (level == 0) throw new DocumentFinishedException();
    }

	public InputSource resolveEntity(String publicId, String systemId) {
	    if (publicId != null) {
	        return null;
	    }

        // try to open systemId directly
        InputStream is = null;
        URL url = null;

        try {
            url = new URL(systemId);
            is = url.openStream();
        } catch (MalformedURLException mfue) {
            return null;
        } catch (IOException ioe) {
            // try to resolve systemId relative to object or class
            String file = url.getFile().substring(url.getFile().lastIndexOf('/')+1);
            is = getClass().getResourceAsStream(file);
        }

        return new InputSource(is);
	}    
}
