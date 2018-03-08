package org.freehep.util.commanddispatcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;


/**
 * A class which implements CommandSource and can be
 * used to associate any AbstractButton with a command
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public class CommandSourceAdapter implements CommandSource, ActionListener, BooleanCommandState, Observer
{
   private AbstractButton m_button;
   private CommandTarget m_target;
   private boolean m_bool;

   public CommandSourceAdapter(AbstractButton button)
   {
      m_button = button;
      m_button.addActionListener(this);
      m_button.setEnabled(false);

      m_bool = m_button instanceof JCheckBoxMenuItem || m_button instanceof JRadioButtonMenuItem || m_button instanceof JToggleButton;
   }

   public String getCommand()
   {
      return m_button.getActionCommand();
   }

   public void setEnabled(boolean state)
   {
      m_button.setEnabled(state);
   }

   public void setSelected(boolean selected)
   {
      m_button.setSelected(selected);
   }

   public boolean setTarget(CommandTarget target)
   {
      if (m_bool && target instanceof SimpleCommandTarget)
      {
         return false;
      }
      if (!m_bool && target instanceof BooleanCommandTarget)
      {
         return false;
      }
      m_target = target;
      m_target.enable(this);
      return true;
   }

   public CommandTarget getTarget()
   {
      return m_target;
   }

   public void setText(String text)
   {
      // NOTE: we only set the text if it was set before, just to make sure
      // we do not set text on buttons that appear in a JToolBar
      if (!m_button.getText().equals(""))
         m_button.setText(text);
   }

   public void setToolTipText(String text)
   {
      m_button.setToolTipText(text);
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (m_bool)
         ((BooleanCommandTarget) m_target).invoke(m_button.isSelected());
      else
         ((SimpleCommandTarget) m_target).invoke();
   }

   public void clearTarget()
   {
      m_target = null;
      setEnabled(false);
   }

   public void update(Observable o, Object arg)
   {
      m_target.enable(this);
   }
}
