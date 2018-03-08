package jas.util;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

class JComboBoxFieldBinding extends FieldBinding implements ItemListener
{
	JComboBoxFieldBinding(JComboBox field)
	{
		m_field = field;
		field.addItemListener(this);
	}
	void set(Object value) throws UnsupportedType
	{
		if (!(value instanceof Integer)) throw new UnsupportedType(m_field,value.getClass());
		int x = ((Integer) value).intValue();
		if (x != m_oldVal)
		{
			m_field.setSelectedIndex(x);
			m_oldVal = x;
			m_field.repaint(); // should not really be necessary?? 
		}
	}
	Object get(Class type) throws UnsupportedType
	{
		if (type != Integer.TYPE) throw new UnsupportedType(m_field,type);
		int x = m_field.getSelectedIndex();
		return new Integer(x);
	}
	public void itemStateChanged(ItemEvent e)
	{
		int x = m_field.getSelectedIndex();
		if (x != m_oldVal) setChanged();
		notifyObservers();
	}
	protected void reset()
	{
		super.reset();
		m_oldVal = m_field.getSelectedIndex();
	}
	private int m_oldVal = 0;
	private JComboBox m_field;
}
