package org.freehep.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
/**
 * This is an implementation of a simple spin box suitable for use
 * with positive integers. It displays as a text area with two small
 * up and down buttons beside it. Pressing the up or down button increments
 * or decrements the counter. Pressing and holding either button causes the
 * value to change continuously (unless delay is set to 0).
 *
 * @deprecated Probably better to use java.swing.JSpinBox now.
 */
public class JSpinBox extends JComponent
{
   /**
    * Creates a spin box
    * @param init The initial value for the spin box
    * @param min The minimum value for the spin box
    * @param max The maximum value for the spin box
    */
   public JSpinBox(int init, int min, int max)
   {
      if (min<0 || max<min || init<min || init>max)
         throw new IllegalArgumentException("Invalid initial parameters for spin box");
      
      this.value = init;
      this.min = min;
      this.max = max;
      
      plus = new JButton(new ImageIcon(getClass().getResource("images/plus.gif")));
      minus = new JButton(new ImageIcon(getClass().getResource("images/minus.gif")));
      
      valueChanging = true;
      field = new JTextField(new JSpinDocument(),String.valueOf(init),
              (int) (1+Math.log(Math.abs(max))/Math.log(10)));
      valueChanging = false;
      valueChanged();
      plus.setMargin(new Insets(0,0,0,0));
      plus.setModel(new MachineGunButtonModel());
      minus.setMargin(new Insets(0,0,0,0));
      minus.setModel(new MachineGunButtonModel());
      
      setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
      JPanel p = new JPanel(new BorderLayout(0,0));
      
      p.add(BorderLayout.NORTH,plus);
      p.add(BorderLayout.SOUTH,minus);
      add(field);
      add(p);
      
      plus.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            value++;
            valueChanged();
         }
      });
      minus.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            value--;
            valueChanged();
         }
      });
   }
   public void addActionListener(ActionListener l)
   {
      listener.addElement(l);
   }
   public void removeActionListener(ActionListener l)
   {
      listener.removeElement(l);
   }
   private void fireActionListener()
   {
      ActionEvent event = new ActionEvent(this,ActionEvent.ACTION_FIRST,"hi");
      Enumeration e = listener.elements();
      while (e.hasMoreElements())
      {
         ((ActionListener) e.nextElement()).actionPerformed(event);
      }
   }
   private void valueChanged()
   {
      if (value > max) value = max;
      if (value < min) value = min;
      
      plus.setEnabled(value < max);
      minus.setEnabled(value > min);
      if (!valueChanging)
      {
         valueChanging = true;
         field.setText(String.valueOf(value));
         valueChanging = false;
      }
      fireActionListener();
   }
   
   public void setMin(int value)
   {
      min = value;
   }
   public void setMax(int value)
   {
      max = value;
   }
   public int getMin()
   {
      return min;
   }
   public int getMax()
   {
      return max;
   }
   public int getValue()
   {
      return value;
   }
   public void setValue(int value)
   {
      this.value = value;
      valueChanged();
   }
   public int getRepeatDelay()
   {
      MachineGunButtonModel model = (MachineGunButtonModel) plus.getModel();
      return model.getRepeatDelay();
   }
   public int getInitialDelay()
   {
      MachineGunButtonModel model = (MachineGunButtonModel) plus.getModel();
      return model.getInitialDelay();
   }
   /**
    * Sets the delay for the repeat function of the arrow keys.
    * @param delay The delay between increments in milliseconds, or 0 to disable repeat
    */
   public void setRepeatDelay(int delay)
   {
      MachineGunButtonModel model = (MachineGunButtonModel) plus.getModel();
      model.setRepeatDelay(delay);
      model = (MachineGunButtonModel) minus.getModel();
      model.setRepeatDelay(delay);
   }
   /**
    * Sets the initial delay for the repeat function of the arrow keys.
    * @param delay The delay between increments in milliseconds, or 0 to disable repeat
    */
   public void setInitialDelay(int delay)
   {
      MachineGunButtonModel model = (MachineGunButtonModel) plus.getModel();
      model.setInitialDelay(delay);
      model = (MachineGunButtonModel) minus.getModel();
      model.setInitialDelay(delay);
   }
   public void setEnabled(boolean enabled)
   {
      plus.setEnabled(enabled);
      minus.setEnabled(enabled);
      field.setEnabled(enabled);
      super.setEnabled(enabled);
   }
   private int max = 100;
   private int min = 0;
   private int value = 0;
   
   private boolean valueChanging = false;
   private final JTextField field;
   private final JButton plus;
   private final JButton minus;
   private final Vector listener = new Vector();
   private class JSpinDocument extends PlainDocument implements Runnable
   {
      public void insertString(int pos, String string, AttributeSet p3) throws BadLocationException
      {
         if (!valueChanging)
         {
            for (int i=0; i<string.length(); i++)
            {
               if (!Character.isDigit(string.charAt(i)))
               {
                  getToolkit().beep();
                  return;
               }
            }
         }
         
         super.insertString(pos, string, p3);
         if (!valueChanging)
         {
            int nValue = Integer.parseInt(field.getText());
            if (nValue < min || nValue > max)
            {
               super.remove(pos,string.length());
               Thread.dumpStack();
               getToolkit().beep();
               return;
            }
            value = nValue;
            valueChanging = true;
            valueChanged();
            valueChanging = false;
         }
      }
      public void remove(int pos, int len) throws BadLocationException
      {
         // Things are complicated here by the fact that the remove may be just
         // the first part of a replace, so we cant simply test for a illegal value
         // after the remove, in case an insert is to follow immediately. Instead we
         // queue a later check on the validity of the field.
         super.remove(pos,len);
         if (!valueChanging) SwingUtilities.invokeLater(this);
      }
      public void run()
      {
         if (field == null) return;
         String text = field.getText();
         int nValue;
         if (text.length() == 0)
         {
            nValue = Integer.MIN_VALUE;
         }
         else
         {
            nValue = Integer.parseInt(field.getText());
         }
         if (nValue < min)
         {
            nValue = min;
            getToolkit().beep();
            field.setText(String.valueOf(nValue));
         }
         value = nValue;
         valueChanging = true;
         valueChanged();
         valueChanging = false;
      }
   }
}
