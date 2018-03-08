// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import java.util.*;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepSelectFilter;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: AbstractHepRepInstanceAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class AbstractHepRepInstanceAdapter extends HepRepAttributeAdapter implements HepRepInstance {

    private HepRepInstance parent;
    private HepRepType type;
    private List/*<HepRepInstance>*/ instances;

    private transient String layer;
    private transient boolean hasFrame;

    /**
     * Instance Wrapper
     * @param attribute heprep1 instance
     * @param parent parent instance
     * @param type type
     */
    public AbstractHepRepInstanceAdapter(hep.graphics.heprep1.HepRepAttribute attribute,
                                         HepRepInstance parent,
                                         HepRepType type) {
        this(null, attribute, parent, type);                                   
    }
    
    /**
     * Instance Wrapper
     * @param parentAttribute heprep1 parent
     * @param attribute heprep1 instance
     * @param parent parent instance
     * @param type type
     */
    public AbstractHepRepInstanceAdapter(hep.graphics.heprep1.HepRepAttribute parentAttribute,
                                         hep.graphics.heprep1.HepRepAttribute attribute,
                                         HepRepInstance parent,
                                         HepRepType type) {
        super(parentAttribute, attribute);
        this.parent = parent;
        this.type = type;
        this.instances = new ArrayList();
        layer = null;
        hasFrame = false;
    }

    public void overlay(HepRepInstance instance) {
        throw new UnsupportedOperationException();
    }

    public void addInstance(HepRepInstance instance) {
        instances.add(instance);
    }
            
    public void removeInstance(HepRepInstance instance) {
        throw new UnsupportedOperationException();
    }
  
    public List/*<HepRepInstance>*/ getInstances() {
        return instances;
    }
        
    public void addPoint(HepRepPoint point) {
        throw new UnsupportedOperationException();
    }

    public HepRepType getType() {
        return type;
    }
            
    public int getPoints(double[][] xyz) {
        return -1;
    }
        
    public HepRepInstance getSuperInstance() {
        return parent;
    }
        
    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstance parent) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstance parent, HepRepSelectFilter filter) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstanceTree parent) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstanceTree parent, HepRepSelectFilter filter) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
        
    public void setUserObject(Object object) {
    }   

    public Object getUserObject() {
        return null;
    }

    /**
     * search for attribute on node, then search on type
     */
    public HepRepAttValue getAttValue(String name) {
        String lowerCaseName = name.toLowerCase();
        HepRepAttValue value = getAttValueFromNode(lowerCaseName);
        return (value != null) ? value : type.getAttValue(lowerCaseName);
    }

    public String getLayer() {
        validate();
        return layer;
    }
    
    public boolean hasFrame() {
        validate();
        return hasFrame;
    }
    
    private void validate() {
        if (layer != null) return;
        layer = getAttValue("layer").getString().intern();
        hasFrame = getAttValue("hasframe").getBoolean();
    }     
}
