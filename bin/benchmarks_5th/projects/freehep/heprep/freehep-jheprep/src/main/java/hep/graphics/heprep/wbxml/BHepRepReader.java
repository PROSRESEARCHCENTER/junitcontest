// Copyright 2005, FreeHEP.
package hep.graphics.heprep.wbxml;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.HepRepVersionException;
import hep.graphics.heprep.ref.DefaultHepRepAttValue;
import hep.graphics.heprep.ref.DefaultHepRepInstance;
import hep.graphics.heprep.util.HepRepTypes;
import hep.graphics.heprep.xml.XMLHepRepFactory;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.StringTokenizer;

import org.freehep.util.VersionComparator;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Reader to read Binary HepRep streams/files and create a HepRep.
 * 
 * @author Mark Donszelmann
 * @version $Id: BHepRepReader.java 8584 2006-08-10 23:06:37Z duns $
 */
public class BHepRepReader extends BHepRepParser {
 
    /**
     * Minimally expected Binary HepRep version number
     */
    public static final String expectedVersion = "2.0";

    private HepRepFactory factory;
    private HepRep heprep;
    private int level = 0;

    private HepRepTypes types;

    private HepRepTypeTree currentTypeTree = null;
    private HepRepTreeID currentTypeTreeRef = null;
    private HepRepInstanceTree instanceTree = null;

    private HepRepPoint currentPoint = null;
    private boolean inPoint = false;

    private Stack/*<HepRepInstance>*/ instanceStack = new Stack();
    private HepRepInstance currentInstance = null;

    private Stack/*<HepRepType>*/ typeStack = new Stack();
    private HepRepType currentType = null;

    
	/**
     * Create a Binary HepRep Reader and add content to heprep
	 * @param heprep heprep to add content to
	 */
	public BHepRepReader(HepRep heprep) {
	    super();
	    this.heprep = heprep;
	    
        factory = new XMLHepRepFactory();
	}
	
    /**
     * Changes the names "valueString, valueBoolean" to "value" and sets an artifical attribute "*type" to
     * the integer value of the type. This only happens for the ATTVALUE tag.
     */
    protected void addAttribute(int attId, String prefix, String namespace, String name, Object value) {
        if ((getTag() == ATTVALUE) && 
            ((attId == VALUE_STRING) ||
             (attId == VALUE_COLOR) ||
             (attId == VALUE_LONG) ||
             (attId == VALUE_INT) ||
             (attId == VALUE_BOOLEAN) ||
             (attId == VALUE_DOUBLE))) {
            
            super.addAttribute(attId, prefix, namespace, "*type", new Integer(attId));
            super.addAttribute(attId, prefix, namespace, "value", value);
            return;
        } 
        
        super.addAttribute(attId, prefix, namespace, name, value);
    }

    /**
     * Adds actual point.
     */
    protected Object parseOpaque(int len, int tagId, int attId) throws IOException, XmlPullParserException {
        Object opaque = super.parseOpaque(len, tagId, attId);
        
        if ((tagId == 0) && (inPoint)) {
            // CONTENT of point (NOTE tagId not set at top-level)
            // NOTE: single precision only...
            // NOTE: length of points array is not accurate for number of points.
            double[] points = (double[])opaque;
            if ((len % 12) != 0) throw new XmlPullParserException(getClass()+": Point should have n*3*4 coordinates rather than "+len+"."); 
            int nPoints = len / 4;
            if (nPoints == 3) {
                currentPoint = factory.createHepRepPoint(currentInstance, points[0], points[1], points[2]);
            } else {
                currentPoint = null;
                for (int i=0; i<nPoints; i+=3) {
                    factory.createHepRepPoint(currentInstance, points[i], points[i+1], points[i+2]);
                }
            }
        }
        return opaque;    
    }    


    /**
     * Parse stream as Binary HepRep
     * @param in stream to parse
     * @throws XmlPullParserException in case format is incorrect
     * @throws IOException in case stream cannot be read
     */
    public void parse(InputStream in) throws XmlPullParserException, IOException {
        
        setInput(in, null);
        
        int eventType = getEventType();
        while (eventType != END_DOCUMENT) {
            switch (eventType) {
                case START_DOCUMENT:
                    startDocument();
                    break;
                case END_DOCUMENT:
                    endDocument();
                    break;
                case START_TAG:
                    startTag();
                    break;
                case END_TAG:
                    endTag();
                    break;
                case TEXT:
                    text();
                    break;
                default:
                    break;
            }
            eventType = next();
        }         
    }
    
    protected void startDocument() {
        types = new HepRepTypes();
        currentTypeTree = null;
        currentTypeTreeRef = null;
        instanceTree = null;
        currentPoint = null;
        inPoint = false;
        instanceStack = new Stack();
        currentInstance = null;
        typeStack = new Stack();
        currentType = null;
        level = 0;
    }
    
    protected void endDocument() {
    }
    
    protected void startTag() throws XmlPullParserException {
        level++;
        
        switch (getTag()) {
            case HEPREP: {
                String version = getAttributeValue("version");
                if (version != null) {
                    VersionComparator comparator = new VersionComparator();
                    if (comparator.versionNumberCompare(version, expectedVersion) < 0) {
                        throw new XmlPullParserException("Wrong version", this,
                            new HepRepVersionException(getClass()+": Found version '"+version+"' while expected version '"+expectedVersion+"'.")
                        );
                    }
                } else {
                    throw new XmlPullParserException("Wrong version", this,
                        new HepRepVersionException(getClass()+": Missing 'version' attribute on 'heprep' tag.")
                    );
                }
                break;
            }
            case ATTDEF: {
                if (currentType == null) {
                    throw new XmlPullParserException(getClass()+": Cannot use 'attdef' outside 'type' tag.");
                }
    
                String name = getAttributeValue("name");
                String desc = getAttributeValue("desc");
                String category = getAttributeValue("category");
                String extra = getAttributeValue("extra");
                currentType.addAttDef(name, desc, category, extra);
                break;
            }
            case ATTVALUE: {
                String name = getAttributeValue("name");
                Integer type = (Integer)getAttributeObject("*type");
                Integer showLabelInt = (Integer)getAttributeObject("showlabel");
                int showLabel = (showLabelInt == null) ? HepRepAttValue.SHOW_NONE : showLabelInt.intValue();
                
                HepRepAttValue attValue;
                switch(type.intValue()) {
                    case VALUE_STRING:
                        attValue = new DefaultHepRepAttValue(name, getAttributeValue("value"), showLabel);
                        break;
                    case VALUE_COLOR:
                        attValue = new DefaultHepRepAttValue(name, (Color)getAttributeObject("value"), showLabel);                    
                        break;
                    case VALUE_LONG:
                        attValue = new DefaultHepRepAttValue(name, ((Long)getAttributeObject("value")).longValue(), showLabel);                    
                        break;
                    case VALUE_INT:
                        attValue = new DefaultHepRepAttValue(name, ((Integer)getAttributeObject("value")).intValue(), showLabel);                    
                        break;
                    case VALUE_BOOLEAN:
                        // NOTE: this is written as an ATTRVALUE and thus is resolved as string.
                        attValue = new DefaultHepRepAttValue(name, ((String)getAttributeObject("value")).equalsIgnoreCase("true"), showLabel);                    
                        break;
                    case VALUE_DOUBLE:
                        attValue = new DefaultHepRepAttValue(name, ((Double)getAttributeObject("value")).doubleValue(), showLabel);                    
                        break;
                    default:
                        throw new XmlPullParserException(getClass()+": Unknown '*type' in 'attValue' tag: "+type.intValue()+".");
                } 
     
                if (currentPoint != null) {
                    currentPoint.addAttValue(attValue);
                } else if (currentInstance != null) {
                    currentInstance.addAttValue(attValue);
                } else if (currentType != null) {
                    currentType.addAttValue(attValue);
                } else if (inPoint) {
                    throw new XmlPullParserException(getClass()+": Coordinates of point need to come before the 'attvalue' tag.");
                } else {
                    throw new XmlPullParserException(getClass()+": Cannot use 'attvalue' outside 'type', 'instance' or 'point' tag.");
                }
                break;
            }
            case INSTANCE: {
                String typeName = getAttributeValue("type");
                if (typeName == null) {
                    throw new XmlPullParserException(getClass()+": Instance cannot exist without referring to a type.");
                }
                HepRepType type = types.getType(currentTypeTreeRef, typeName, currentInstance);
                if (type == null) {
                    throw new XmlPullParserException(getClass()+": Cannot find type: '"+typeName+"' "+
                                           "in tree: '"+types.getID(currentTypeTreeRef)+"'");
                }
                instanceStack.push(currentInstance);
                if (currentInstance != null) {
                    currentInstance = factory.createHepRepInstance(currentInstance, type);
                } else {
                    currentInstance = factory.createHepRepInstance(instanceTree, type);
                }
                break;
            }
            case TREEID: {
                String name = getAttributeValue("name");
                String version = getAttributeValue("version");
                String qualifier = getAttributeValue("qualifier");
                instanceTree.addInstanceTree(factory.createHepRepTreeID(name, version, qualifier));
                break;
            }
            case ACTION: {
                String name = getAttributeValue("name");
                String expression = getAttributeValue("expression");
                // FIXME: JHEPREP-3
                factory.createHepRepAction(name, expression);
                break;
            }
            case INSTANCETREE: {
                String name = getAttributeValue("name");
                String version = getAttributeValue("version");
                currentTypeTreeRef = factory.createHepRepTreeID(getAttributeValue("typetreename"), getAttributeValue("typetreeversion"));
                instanceTree = factory.createHepRepInstanceTree(name, version, currentTypeTreeRef);
                heprep.addInstanceTree(instanceTree);
                break;
            }
            case TYPE: {   
                String name = getAttributeValue("name");
                typeStack.push(currentType);
                if (currentType != null) {
                    currentType = factory.createHepRepType(currentType, name);
                } else {
                    currentType = factory.createHepRepType(currentTypeTree, name);
                }
                
                types.put(currentTypeTree, name, currentType);            
                break;
            }
            case TYPETREE: {
                String name = getAttributeValue("name");
                String version = getAttributeValue("version");
                HepRepTreeID id = factory.createHepRepTreeID(name, version);
                currentTypeTree = factory.createHepRepTypeTree(id);
                heprep.addTypeTree(currentTypeTree);
                
                types.put(id, currentTypeTree);            
                break;
            }
            case LAYER: {
                String order = getAttributeValue("order");
                StringTokenizer st = new StringTokenizer(order,",");
                while (st.hasMoreTokens()) {
                    heprep.addLayer(st.nextToken().trim());
                }
                break;
            }
            case POINT: {
                currentPoint = null;
                inPoint = true;
                break;
            }
            default:
                throw new XmlPullParserException(getClass()+": Unknown start tag: "+getTag());
        }

        if (Thread.interrupted()) throw new XmlPullParserException(getClass()+": Interrupted", this, new InterruptedException());

    }
    
    protected void endTag() throws XmlPullParserException {
        switch (getTag()) {
            case HEPREP:
                // ignored, toplevel already stored
                break;
            case ATTDEF:
                break;
            case ATTVALUE:
                break;
            case INSTANCE:
                if (currentInstance instanceof DefaultHepRepInstance) ((DefaultHepRepInstance)currentInstance).optimize();
                currentInstance = (HepRepInstance)instanceStack.pop();
                break;
            case TREEID:
                break;
            case ACTION:
                break;
            case INSTANCETREE:
                instanceTree = null;
                currentTypeTreeRef = null;
                break;
            case TYPE:
                currentType = (HepRepType)typeStack.pop();
                break;
            case TYPETREE:
                currentTypeTree = null;
                break;
            case LAYER:
                break;
            case POINT:
                currentPoint = null;
                inPoint = false;
                break;
            default:
                throw new XmlPullParserException(getClass()+": Unknown end tag: "+getTag());
        }

        if (Thread.interrupted()) throw new XmlPullParserException(getClass()+": Interrupted", this, new InterruptedException());

        // make sure this is at the end, and that heprep is set!
        level--;
    }
    
    protected void text() {
    }
    
    /**
     * Small test
     * @param args binary heprep file
     * @throws XmlPullParserException for format problems
     * @throws IOException for read problems
     */
    public static void main(String[] args) throws XmlPullParserException, IOException {
        System.out.println("Start");
		BHepRepReader r = new BHepRepReader(new XMLHepRepFactory().createHepRep());
		r.parse(new FileInputStream(args[0]));
    }
}