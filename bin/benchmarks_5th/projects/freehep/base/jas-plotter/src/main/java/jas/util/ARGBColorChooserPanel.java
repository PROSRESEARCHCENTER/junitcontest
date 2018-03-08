package jas.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author tonyj
 */

public class ARGBColorChooserPanel extends AbstractColorChooserPanel implements ChangeListener
{
   protected JSlider redSlider;
   protected JSlider greenSlider;
   protected JSlider blueSlider;
   protected JSlider alphaSlider;
   protected JSpinner redSpinner;
   protected JSpinner blueSpinner;
   protected JSpinner greenSpinner;
   protected JSpinner alphaSpinner;
   
   private final int minValue = 0;
   private final int maxValue = 255;
   
   private boolean isAdjusting = false;
   
   public ARGBColorChooserPanel()
   {
      super();
   }
   
   private void setColor(Color color)
   {
      int red = color.getRed();
      int blue = color.getBlue();
      int green = color.getGreen();
      int alpha = color.getAlpha();
      
      if (redSlider.getValue() != red)
      {
         redSlider.setValue(red);
      }
      if (greenSlider.getValue() != green)
      {
         greenSlider.setValue(green);
      }
      if (blueSlider.getValue() != blue)
      {
         blueSlider.setValue(blue);
      }
      if (alphaSlider.getValue() != alpha)
      {
         alphaSlider.setValue(alpha);
      }
      
      
      if (((Integer)redSpinner.getValue()).intValue() != red)
      {
         redSpinner.setValue(new Integer(red));
      }
      if (((Integer)greenSpinner.getValue()).intValue() != green)
      {
         greenSpinner.setValue(new Integer(green));
      }
      if (((Integer)blueSpinner.getValue()).intValue() != blue)
      {
         blueSpinner.setValue(new Integer(blue));
      }
      if (((Integer)alphaSpinner.getValue()).intValue() != alpha)
      {
         alphaSpinner.setValue(new Integer(alpha));
      }
   }
   
   public String getDisplayName()
   {
      return "ARGB";
   }
   
   public Icon getSmallDisplayIcon()
   {
      return null;
   }
   
   public Icon getLargeDisplayIcon()
   {
      return null;
   }
   
   protected void buildChooser()
   {
      
      setLayout(new BorderLayout());
      Color color = getColorFromModel();
      
      JPanel panel = new JPanel();
      GridBagLayout layout = new GridBagLayout();
      GridBagConstraints con = new GridBagConstraints();
      panel.setLayout(layout);
      
      add(panel, BorderLayout.CENTER);
      
      JLabel l = new JLabel("Red");
      con.gridwidth = 1;
      layout.setConstraints(l, con);
      panel.add(l);
      redSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, color.getRed());
      redSlider.setMajorTickSpacing(85);
      redSlider.setMinorTickSpacing(17);
      redSlider.setPaintTicks(true);
      redSlider.setPaintLabels(true);
      layout.setConstraints(redSlider, con);
      l.setLabelFor(redSlider);
      panel.add(redSlider);
      redSpinner = new JSpinner(
              new SpinnerNumberModel(color.getRed(), minValue, maxValue, 1));
      JPanel redSpinnerHolder = new JPanel(new FlowLayout());
      redSpinner.addChangeListener(this);
      redSpinnerHolder.add(redSpinner);
      con.gridwidth = GridBagConstraints.REMAINDER;
      layout.setConstraints(redSpinnerHolder, con);
      panel.add(redSpinnerHolder);
      
      l = new JLabel("Green");
      con.gridwidth = 1;
      layout.setConstraints(l, con);
      panel.add(l);
      greenSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, color.getGreen());
      greenSlider.setMajorTickSpacing(85);
      greenSlider.setMinorTickSpacing(17);
      greenSlider.setPaintTicks(true);
      greenSlider.setPaintLabels(true);
      layout.setConstraints(greenSlider, con);
      panel.add(greenSlider);
      greenSpinner = new JSpinner(
              new SpinnerNumberModel(color.getGreen(), minValue, maxValue, 1));
      l.setLabelFor(greenSlider);
      JPanel greenSpinnerHolder = new JPanel(new FlowLayout());
      greenSpinnerHolder.add(greenSpinner);
      greenSpinner.addChangeListener(this);
      con.gridwidth = GridBagConstraints.REMAINDER;
      layout.setConstraints(greenSpinnerHolder, con);
      panel.add(greenSpinnerHolder);
      
      l = new JLabel("Blue");
      con.gridwidth = 1;
      layout.setConstraints(l, con);
      panel.add(l);
      blueSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, color.getBlue());
      blueSlider.setMajorTickSpacing(85);
      blueSlider.setMinorTickSpacing(17);
      blueSlider.setPaintTicks(true);
      blueSlider.setPaintLabels(true);
      layout.setConstraints(blueSlider, con);
      panel.add(blueSlider);
      blueSpinner = new JSpinner(
              new SpinnerNumberModel(color.getBlue(), minValue, maxValue, 1));
      l.setLabelFor(blueSlider);
      JPanel blueSpinnerHolder = new JPanel(new FlowLayout());
      blueSpinnerHolder.add(blueSpinner);
      blueSpinner.addChangeListener(this);
      con.gridwidth = GridBagConstraints.REMAINDER;
      layout.setConstraints(blueSpinnerHolder, con);
      panel.add(blueSpinnerHolder);
      
      l = new JLabel("Alpha");
      con.gridwidth = 1;
      layout.setConstraints(l, con);
      panel.add(l);
      alphaSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, color.getAlpha());
      alphaSlider.setMajorTickSpacing(85);
      alphaSlider.setMinorTickSpacing(17);
      alphaSlider.setPaintTicks(true);
      alphaSlider.setPaintLabels(true);
      layout.setConstraints(alphaSlider, con);
      panel.add(alphaSlider);
      alphaSpinner = new JSpinner(
              new SpinnerNumberModel(color.getAlpha(), minValue, maxValue, 1));
      l.setLabelFor(alphaSlider);
      JPanel alphaSpinnerHolder = new JPanel(new FlowLayout());
      alphaSpinnerHolder.add(alphaSpinner);
      alphaSpinner.addChangeListener(this);
      con.gridwidth = GridBagConstraints.REMAINDER;
      layout.setConstraints(alphaSpinnerHolder, con);
      panel.add(alphaSpinnerHolder);
      
      redSlider.addChangeListener(this);
      greenSlider.addChangeListener(this);
      blueSlider.addChangeListener(this);
      alphaSlider.addChangeListener(this);
   }
   
   public void updateChooser()
   {
      if (!isAdjusting)
      {
         isAdjusting = true;
         
         setColor(getColorFromModel());
         
         isAdjusting = false;
      }
   }
   
   public void stateChanged(ChangeEvent e)
   {
      if (e.getSource() instanceof JSlider && !isAdjusting)
      {
         
         int red = redSlider.getValue();
         int green = greenSlider.getValue();
         int blue = blueSlider.getValue() ;
         int alpha = alphaSlider.getValue() ;
         Color color = new Color(red, green, blue, alpha);
         
         getColorSelectionModel().setSelectedColor(color);
      }
      else if (e.getSource() instanceof JSpinner && !isAdjusting)
      {
         
         int red = ((Integer)redSpinner.getValue()).intValue();
         int green = ((Integer)greenSpinner.getValue()).intValue();
         int blue = ((Integer)blueSpinner.getValue()).intValue();
         int alpha = ((Integer)alphaSpinner.getValue()).intValue();
         Color color = new Color(red, green, blue, alpha);
         
         getColorSelectionModel().setSelectedColor(color);
      }
   }  
}