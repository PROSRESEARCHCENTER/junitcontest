package jas.hist;

import jas.util.ColorChooser;
import jas.util.PropertyBinding;
import jas.util.PropertyPage;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

final class JASHistPropFunctionStyle extends PropertyPage
{
   JASHistPropFunctionStyle()
   {
      setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
      
      add(new JLabel("Color"));
      ColorChooser color = new ColorChooser();
      add(color);

      final JComboBox lineStyle = new JComboBox();
      lineStyle.addItem("Solid");
      lineStyle.addItem("Dotted");
      lineStyle.addItem("Dashed");
      lineStyle.addItem("DotDashed");
      add(lineStyle);
      final JSpinner lineWidth = new JSpinner(new SpinnerNumberModel(1,0.0,5.5,0.5));
      add(lineWidth);
      
      addBinding(new PropertyBinding(color,"LineColor"));
      addBinding(new PropertyBinding(lineStyle,"LineStyle"));
      addBinding(new PropertyBinding(lineWidth,"LineWidth"));
   }
}

