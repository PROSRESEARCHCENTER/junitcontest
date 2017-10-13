package jas.util;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


class JSpinnerFieldBinding extends FieldBinding implements ChangeListener
{
   JSpinnerFieldBinding(JSpinner field)
   {
      m_field = field;
      field.addChangeListener(this);
   }
   void set(Object value) throws UnsupportedType
   {
      if (!m_oldVal.equals(value))
      {
         m_field.setValue(value);
         m_oldVal = value;
      }
   }
   Object get(Class type) throws UnsupportedType
   {
      Object result = m_field.getValue();
      if (type == Float.TYPE && result instanceof Double) return new Float(((Double) result).floatValue());
      return result;
   }
   public void stateChanged(ChangeEvent e)
   {
      Object x = m_field.getValue();
      if (!x.equals(m_oldVal)) setChanged();
      notifyObservers();
   }
   protected void reset()
   {
      super.reset();
      m_oldVal = m_field.getValue();
   }
   private Object m_oldVal = new Integer(0);
   private JSpinner m_field;
}
