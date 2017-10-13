package jas.hist;

import jas.util.PropertyBinding;
import jas.util.PropertyPage;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

final class JASHistPropDataStyle extends PropertyPage
{
   JASHistPropDataStyle()
   {
      setLayout(new FlowLayout(FlowLayout.LEFT));
      
      JCheckBox show = new JCheckBox("Is Showing");
      show.setMnemonic('I');
      JComboBox axis = new JComboBox();
      axis.addItem("Y0");
      axis.addItem("Y1");
      
      add(show);
      add(new JLabel("Y Axis:"));
      add(axis);
      
      addBinding(new PropertyBinding(show,"Showing"));
      addBinding(new PropertyBinding(axis,"YAxis"));
   }
   public Dimension getMaximumSize()
   {
      Dimension d1 = super.getMaximumSize();
      Dimension d2 = super.getPreferredSize();
      d1.height = d2.height;
      return d1;
   }
}
