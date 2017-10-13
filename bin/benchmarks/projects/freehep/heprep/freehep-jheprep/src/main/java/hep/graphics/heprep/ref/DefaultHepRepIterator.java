// Copyright 2000-2006, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepAttributeListener;
import hep.graphics.heprep.HepRepFrameListener;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Fast iterator, which allows for iteration of all HepRepInstances
 * in a HepRepInstanceTree or for iteration of a specific layer.
 * It also features a callback to a HepRepListener to signal
 * changes in attributes.
 *
 * @author M.Donszelmann
 * @version $Id: DefaultHepRepIterator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DefaultHepRepIterator implements HepRepIterator {

    // state
    private List/*<HepRepInstanceTree>*/ instanceTrees;
    private Iterator treeIterator;
    private HepRepInstanceTree currentTree;
    private Iterator layerIterator;
    private String currentLayer;
    private boolean iterateFrames;
    private boolean inFrameLayer;
    private boolean inFrameLayerChanged;
    
    private Set types = null;

    // for the iteration
    private HepRepInstance nextInstance = null;            // next Instance
    private HepRepInstance currentInstance = null;         // current Instance
    
    // Fast Instance Stack
    private HepRepInstance[] instanceStack = new HepRepInstance[1000]; // HepRepInstances to be processed
    private int instanceStackTop = -1;

    // for the listeners
    private Map/*<LowerCaseName, List<HepRepAttributeListener>>*/ attListeners;
    private List/*<HepRepFrameListener>*/ frameListeners; 

    // for the attributes
    private Map/*<LowerCaseName, HepRepAttValue>*/ attributes;         // current attributes subscribed to

    /**
     * Creates a HepRepIterator for the given list of InstanceTrees.
     * Layer changes are reported but ignored.
     *
     * @param instanceTrees to be iterated over.
     */
    public DefaultHepRepIterator(List/*<HepRepInstanceTree>*/ instanceTrees) {
        this(instanceTrees, null);
    }

    /**
     * Creates a HepRepIterator for the given list of InstanceTrees and set of layers.
     *
     * @param instanceTrees to be iterated over.
     * @param layers to be used in iteration.
     */
    public DefaultHepRepIterator(List/*<HepRepInstanceTree>*/ instanceTrees, List/*<String>*/ layers) {
        this(instanceTrees, layers, false);
    }

    /**
     * Creates a HepRepIterator for the given list of InstanceTrees and set of layers.
     *
     * @param instanceTrees to be iterated over.
     * @param layers to be used in iteration.
     * @param iterateFrames iterate separately over a frame layer for each layer.
     */
    public DefaultHepRepIterator(List/*<HepRepInstanceTree>*/ instanceTrees, List/*<String>*/ layers, boolean iterateFrames) {
        this(instanceTrees, layers, null, iterateFrames);
    }
        
    /**
     * Creates a HepRepIterator for the given list of InstanceTrees and set of layers.
     *
     * @param instanceTrees to be iterated over.
     * @param layers to be used in iteration.
     * @param types to be used in iteration.
     * @param iterateFrames iterate separately over a frame layer for each layer.
     */
    public DefaultHepRepIterator(List/*<HepRepInstanceTree>*/ instanceTrees, List/*<String>*/ layers, Set/*<Object>*/ types, boolean iterateFrames) {
        if ((layers == null) || (layers.size() == 0)) {
            layers = new ArrayList();
            layers.add(null);
        }
        layerIterator = layers.iterator();
        currentLayer = (String)layerIterator.next();
        if (currentLayer != null) currentLayer = currentLayer.intern();
       
        this.types = types;
       
        this.instanceTrees = instanceTrees;
        treeIterator = instanceTrees.iterator();
        currentTree = null;
        
        this.iterateFrames = iterateFrames;

        inFrameLayer = false;
        inFrameLayerChanged = true;

        //setup attribute tables
        attListeners = new HashMap();
        frameListeners = new ArrayList();
        attributes = new HashMap();
    }

    private void fillInstanceStack(Collection/*<HepRepInstance>*/ instances) {
        // put instances of this instancetree on stack for processing
        Iterator iterator = instances.iterator();
        while (iterator.hasNext()) {
            instanceStackTop++;
            if (instanceStackTop >= instanceStack.length) {
                // increment stack size
	            Object oldInstanceStack[] = instanceStack;
	            int newCapacity = (oldInstanceStack.length * 3)/2 + 1;
	            instanceStack = new HepRepInstance[newCapacity];
	            System.arraycopy(oldInstanceStack, 0, instanceStack, 0, oldInstanceStack.length); 
            }
            instanceStack[instanceStackTop] = (HepRepInstance)iterator.next();
        }
    }

    /**
     * Add a listener to be informed about a certain attribute's changes
     * while the iteration is ongoing.
     *
     * @param name attribute name, null listens to all attributes
     * @param l listener to be added.
     */
    public void addHepRepAttributeListener(String name, HepRepAttributeListener listener) {
        String lowerCaseName = name != null ? name.toLowerCase().intern() : name;
        
        List list = (List)attListeners.get(lowerCaseName);
        if (list == null) {            
            list = new ArrayList();
            attListeners.put(lowerCaseName, list);
        }
        list.add(listener);
    }

    /**
     * Remove a listener for a certain attribute.
     *
     * @param name attribute name, null removes all attribute listener
     * @param l listener to be removed.
     */
    public void removeHepRepAttributeListener(String name, HepRepAttributeListener listener) {
        String lowerCaseName = (name != null) ? name.toLowerCase() : null;
        
        List list = (List)attListeners.get(lowerCaseName);
        if (list != null) {
            list.remove(listener);
            if (list.isEmpty()) {
                attListeners.remove(lowerCaseName);
            }
        }
    }

    public void addHepRepFrameListener(HepRepFrameListener l) {
        frameListeners.add(l);
    }

    public void removeHepRepFrameListener(HepRepFrameListener l) {
        frameListeners.remove(l);
    }

    private void informFrameListeners() {
        if (inFrameLayerChanged) {
            inFrameLayerChanged = false;
            for (Iterator i = frameListeners.iterator(); i.hasNext();  ) {
                HepRepFrameListener l = (HepRepFrameListener)i.next();
                l.setFrameLayer(inFrameLayer);
            }
        }
    }

    /**
     * Informs listeners of any attribute changes.
     */
    private void informAttributeListeners() {
        for (Iterator i = attListeners.keySet().iterator(); i.hasNext();  ) {
            String lowerCaseName = (String)i.next();
            List list = (List)attListeners.get(lowerCaseName);
            if (list == null) continue;
            
            for (Iterator j = list.iterator(); j.hasNext(); ) {
                HepRepAttributeListener listener = (HepRepAttributeListener)j.next();
                if (listener == null) continue;
            
                if (lowerCaseName == null) {
                    informAttributeListener(listener);
                } else {
                    informAttributeListener(lowerCaseName, listener);
                }
            }
        }        
    }

    /**
     * Inform the "all attribute" listener of all changed attributes.
     * @param listener listener for "all atribute"
     */
    private void informAttributeListener(HepRepAttributeListener listener) {
        Set/*<String>*/ attNames = new HashSet();
        if (currentInstance != null) {
            for (Iterator i = currentInstance.getAttValuesFromNode().iterator(); i.hasNext(); ) {
                attNames.add(((HepRepAttValue)i.next()).getLowerCaseName());
            }
        }
        
        for (Iterator i = nextInstance.getAttValuesFromNode().iterator(); i.hasNext(); ) {
            attNames.add(((HepRepAttValue)i.next()).getLowerCaseName());
        }

//        System.err.println("***** atts to report on:");
//        for (Iterator i=attNames.iterator(); i.hasNext(); ) {
//            System.err.println("     "+i.next());
//        }
//        System.err.println("-----");
        
        for (Iterator i=attNames.iterator(); i.hasNext(); ) {
            informAttributeListener((String)i.next(), listener);
        }   
    }
    
    /**
     * Informs specific attribute listener
     * @param lowerCaseName name of the attribute listened to.
     * @param listener listener for attribuye
     */
    private void informAttributeListener(String lowerCaseName, HepRepAttributeListener listener) {
        HepRepAttValue nextAttValue = null;
        
        if (currentInstance != null) {
            nextAttValue = nextInstance.getAttValueFromNode(lowerCaseName);
            if (nextAttValue == null) {
                if (currentInstance.getAttValueFromNode(lowerCaseName) == null) {
                    if (currentInstance.getType() == nextInstance.getType()) {
                        // notdef -> notdef, types equal: special DO NOTHING case.
                        return;
                    }
                }
                
                // * -> notdef: lookup
                nextAttValue = nextInstance.getAttValue(lowerCaseName);
            }       
        } else {
            // first time: lookup
            nextAttValue = nextInstance.getAttValue(lowerCaseName);
        }    

        informAttributeListener(lowerCaseName, listener, nextAttValue);        
    }
    
    /**
     * Informs listener in case of a change in the given AttributeValue and cache the value.
     *
     * @param listener listener
     * @param lowerCaseName name of the attribute
     * @param attValue the changed attributeValue.
     */
    private void informAttributeListener(String lowerCaseName, HepRepAttributeListener listener, HepRepAttValue attValue) {
        
        // look if changed
        Object oldAttValue = attributes.get(lowerCaseName);
        if (oldAttValue == attValue) return;
        if ((attValue != null) && (attValue.equals(oldAttValue))) return;

        // cache
        attributes.put(lowerCaseName, attValue);
        
        // new value removed ?
        if (attValue == null) {
            listener.removeAttribute(nextInstance, lowerCaseName);            
            return;
        }
        
//        System.err.println("--> "+attValue.getName()+" "+attValue.getAsString());

        // inform
        // NOTE, should take type specified on definition (if there was). Now a change in int will not be notified
        // to the double. See JHEPREP-57.
        // we forward the call from int to double in HepRepAttributeAdapter
        switch (attValue.getType()) {
            case HepRepAttValue.TYPE_STRING:
                listener.setAttribute(nextInstance, lowerCaseName, attValue.getString(), attValue.getLowerCaseString(), attValue.showLabel());
                break;
            case HepRepAttValue.TYPE_COLOR:
                listener.setAttribute(nextInstance, lowerCaseName, attValue.getColor(), attValue.showLabel());
                break;
            case HepRepAttValue.TYPE_LONG:
                listener.setAttribute(nextInstance, lowerCaseName, attValue.getLong(), attValue.showLabel());
                break;
            case HepRepAttValue.TYPE_INT:
                listener.setAttribute(nextInstance, lowerCaseName, attValue.getInteger(), attValue.showLabel());
                break;
            case HepRepAttValue.TYPE_DOUBLE:
                listener.setAttribute(nextInstance, lowerCaseName, attValue.getDouble(), attValue.showLabel());
                break;
            case HepRepAttValue.TYPE_BOOLEAN:
                listener.setAttribute(nextInstance, lowerCaseName, attValue.getBoolean(), attValue.showLabel());
                break;
            default:
                System.err.println("Unknown type in DefaultHepRepIterator: '"+attValue.getType()+"'");
                listener.setAttribute(nextInstance, lowerCaseName, attValue.toString(), attValue.toString().toLowerCase(), attValue.showLabel());
                break;
        }
    }

    /**
     * @return the next HepRepInstance
     */
    public HepRepInstance nextInstance() {
        return (HepRepInstance)next();
    }

    /**
     * @return the next HepRepInstance
     */
    public Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // inform about in/out frame changes
        informFrameListeners();

        // report all attribute changes
        informAttributeListeners();

        // move on
        currentInstance = nextInstance;
        nextInstance = null;

        return currentInstance;
    }

    /**
     * Prepares the next instance and returns true if exists. Successive
     * calls to hasNext() without calling next() or nextInstance() are ignored.
     *
     * The instances are walked through the tree in in-order traversal.
     *
     * @return true if there exists a next HepRepInstance
     */
    public boolean hasNext() {
        // no need if nextInstance is prepared
        if (nextInstance != null) return true;

        do {
            if (instanceStackTop < 0) {
                // stack is empty, what next...
                if (treeIterator.hasNext()) {
                    // get next tree
                    currentTree = (HepRepInstanceTree)treeIterator.next();
                } else if (inFrameLayer) {
                    // change to normal layer and re-fill for same layer
                    inFrameLayer = false;
                    inFrameLayerChanged = true;

                    // run over all trees again...
                    treeIterator = instanceTrees.iterator();
                    currentTree = (HepRepInstanceTree)treeIterator.next();
                    
                } else {
                    // select next layer if exists...
                    if (!layerIterator.hasNext()) {
                        nextInstance = null;
                        return false;
                    }   
                    currentLayer = (String)layerIterator.next();
                    if (currentLayer != null) {
                        currentLayer = currentLayer.intern();
                    }
                    // run over all trees again...
                    treeIterator = instanceTrees.iterator();
                    currentTree = (HepRepInstanceTree)treeIterator.next();
                    
                    if (iterateFrames) {
                        // change to frame layer
                        inFrameLayer = true;
                        inFrameLayerChanged = true;
                    }
                }    
                // fill stack from current tree
                fillInstanceStack(currentTree.getInstances());
            }

            // get stacked instance
            nextInstance = instanceStack[instanceStackTop];
            instanceStackTop--;

            // add children of this instance to the instanceStack
            fillInstanceStack(nextInstance.getInstances());

        // skip if not in layer, unless layer is null;
        // skip if not in types, unless types is null;
        // skip if iterating frames and inFrameLayer and instance has no frame
        } while (((currentLayer != null) && (nextInstance.getLayer() != currentLayer)) ||
                 ((types != null) && !types.contains(nextInstance.getType())) ||
                 (iterateFrames && inFrameLayer && !nextInstance.hasFrame()));

        // nextInstance is valid.
        return true;
    }

    /**
     * Returns true if the current instance, just delivered by nextInstance(), is to be drawn as a frame.
     */
    public boolean drawAsFrame() {
        return inFrameLayer;
    }

    /**
     * Removes the current instance, however this is not permitted.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
