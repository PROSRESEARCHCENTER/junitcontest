package jas.util;

import java.awt.Color;

class ColorFieldBinding extends FieldBinding implements ColorListener
{
   ColorFieldBinding(ColorChooser field)
   {
      m_field = field;
      field.addColorListener(this);
   }
   void set(Object value) throws UnsupportedType
   {
      if (!(value instanceof Color)) throw new UnsupportedType(m_field,value.getClass());
      Color c = (Color) value;
      if (!m_oldVal.equals(c))
      {
         m_field.setColor(c);
         m_oldVal = c;
      }
   }
   Object get(Class type) throws UnsupportedType
   {
      Color c = m_field.getColor();
      if (!type.isInstance(c)) throw new UnsupportedType(m_field,type);
      return c;
   }
   public void colorChanged(ColorEvent e)
   {
      Color c = m_field.getColor();
      if (c != m_oldVal) setChanged();
      notifyObservers();
   }
   protected void reset()
   {
      super.reset();
      m_oldVal = m_field.getColor();
   }
   private Color m_oldVal = Color.black;
   private ColorChooser m_field;
}
