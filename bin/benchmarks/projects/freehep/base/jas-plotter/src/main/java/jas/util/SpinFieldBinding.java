package jas.util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class SpinFieldBinding extends FieldBinding implements ActionListener
{
	SpinFieldBinding(SpinBox field)
	{
		m_field = field;
		field.addActionListener(this);
	}
	void set(Object value) throws UnsupportedType
	{
		if (!(value instanceof Integer)) throw new UnsupportedType(m_field,value.getClass());
		int x = ((Integer) value).intValue();
		if (m_oldVal != x)
		{
			m_field.setValue(x);
			m_oldVal = x;
		}
	}
	Object get(Class type) throws UnsupportedType
	{
		if (type != Integer.TYPE) throw new UnsupportedType(m_field,type);
		int x = m_field.getValue();
		return new Integer(x);
	}
	public void actionPerformed(ActionEvent e)
	{
		int x = m_field.getValue();
		if (x != m_oldVal) setChanged();
		notifyObservers();
	}
	protected void reset()
	{
		super.reset();
		m_oldVal = m_field.getValue();
	}
	private int m_oldVal = 0;
	private SpinBox m_field;
}
