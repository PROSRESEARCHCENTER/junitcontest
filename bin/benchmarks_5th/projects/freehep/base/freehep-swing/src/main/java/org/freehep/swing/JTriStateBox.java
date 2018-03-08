// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;


/**
 *
 * @author Mark Donszelmann
 * @version $Id: JTriStateBox.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JTriStateBox extends JCheckBox implements TriState {
    
    static {
        UIManager.getDefaults().put("TriStateBoxUI", "org.freehep.swing.plaf.metal.MetalTriStateBoxUI");
    }   
    
    boolean otherState;

    public JTriStateBox () {
        this(null, null, 0);
    }

    public JTriStateBox(Icon icon) {
        this(null, icon, 0);
    }
    
    public JTriStateBox(Icon icon, int selected) {
        this(null, icon, selected);
    }
    
    public JTriStateBox (String text) {
        this(text, null, 0);
    }

    public JTriStateBox (String text, int selected) {
        this(text, null, selected);
    }

    public JTriStateBox(String text, Icon icon) {
        this(text, icon, 0);
    }

    public JTriStateBox (String text, Icon icon, int selected) {
        super(text, icon, false);
        setModel(new TriStateModel());
        setTriState(selected);
    }
    
    public int getTriState() {
        return ((TriStateModel)getModel()).getTriState();
    }
    
    public void setTriState(int state) {
        ((TriStateModel)getModel()).setTriState(state);
    }

    public void setTriState(boolean state) {
        setTriState((state) ? 1 : 0);
    }

    public String getUIClassID() {
        return "TriStateBoxUI";
    }
}
  
