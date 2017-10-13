package jas.util;
import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class CheckboxFieldBinding extends FieldBinding implements ItemListener
{
	CheckboxFieldBinding(Checkbox field)
	{
		m_field = field;
		field.addItemListener(this);
	}
	void set(Object value) throws UnsupportedType
	{
		if (!(value instanceof Boolean)) throw new UnsupportedType(m_field,value.getClass());
		boolean x = ((Boolean) value).booleanValue();
		if (m_oldVal != x)
		{
			m_field.setState(x);
			m_oldVal = x;
		}
	}
	Object get(Class type) throws UnsupportedType
	{
		if (type != Boolean.TYPE) throw new UnsupportedType(m_field,type);
		boolean x = m_field.getState();
		return new Boolean(x);
	}
	public void itemStateChanged(ItemEvent e)
	{
		boolean x = m_field.getState();
		if (x != m_oldVal) setChanged();
		notifyObservers();
	}
	protected void reset()
	{
		super.reset();
		m_oldVal = m_field.getState();
	}
	private boolean m_oldVal = false;
	private Checkbox m_field;
}
