// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing.test;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.freehep.swing.JTriStateBox;
import org.freehep.swing.JTriStateMenuItem;

/**
 * @author Mark Donszelmann
 * @version $Id: JTriStateTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class JTriStateTest extends TestFrame {
    
    Icon icon;
    
    protected void addToMenuBar(JMenuBar menuBar) {
		icon = new ImageIcon(getClass().getResource("Stop.gif"));

		JMenu m1 = new JMenu("CheckBoxes");
		menuBar.add(m1);
		m1.add(new JCheckBoxMenuItem("CheckBox1", true));
		m1.add(new JCheckBoxMenuItem("CheckBox2", false));
		m1.add(new JCheckBoxMenuItem("CheckBox3"));
		m1.add(new JCheckBoxMenuItem("CheckBox4", icon, false));
		
		JMenu m2 = new JMenu("TriStateBoxes");
		menuBar.add(m2);
		m2.add(new JTriStateMenuItem("TriStateBox1", 1));
		m2.add(new JTriStateMenuItem("TriStateBox2", 0));
		m2.add(new JTriStateMenuItem("TriStateBox3", -1));
		m2.add(new JTriStateMenuItem("TriStateBox4", icon));
	}        
    
	protected JComponent createComponent() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(4,2));
		p.add(new JCheckBox("CheckBox1", true));
		p.add(new JTriStateBox("TriStateBox1", 1));
		p.add(new JCheckBox("CheckBox2", false));
		p.add(new JTriStateBox("TriStateBox2", 0));
		p.add(new JCheckBox("CheckBox3"));
		p.add(new JTriStateBox("TriStateBox3", -1));
		p.add(new JCheckBox("CheckBox4", icon, false));
		p.add(new JTriStateBox("TriStateBox4", icon));
		
		return p;
    }	
    
        
	public static void main(String[] argv) {
        new JTriStateTest();
	}
}
