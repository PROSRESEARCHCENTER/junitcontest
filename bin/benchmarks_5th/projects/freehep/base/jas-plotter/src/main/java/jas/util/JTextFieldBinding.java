package jas.util;
import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JTextFieldBinding extends FieldBinding implements DocumentListener
{
	protected JTextFieldBinding(JTextField field, byte flags)
	{
		this(field);
		m_flags = flags;
	}
	protected JTextFieldBinding(JTextField field)
	{
		m_field = field;
		field.getDocument().addDocumentListener(this);
	}
	void set(Object value)
	{
		String x = setValue(value);
		if (!m_oldVal.equals(x))
		{
			m_field.setText(x);
			m_oldVal = x;
		}
	}
	protected String setValue(Object value)
	{
		String x;
		if (value == null) x = "";
		else if (value instanceof String) x = (String) value;
		else x = value.toString();
		return x;
	}
	protected Object getValue(String value, Class type) throws UnsupportedType
	{
		if (type.isPrimitive())
		{
			if (type == Double.TYPE ) return new Double(value);
			if (type == Integer.TYPE) return new Integer(value); 
			throw new UnsupportedType(m_field,type);
		}
		else
		{
			Class[] strarg = { value.getClass() };
			try
			{
				Constructor c = type.getConstructor(strarg);
				Object[] args = { value };
				return c.newInstance(args);
			}
			catch (Exception xx)
			{
				throw new UnsupportedType(m_field,type);
			}
		}
	}
	Object get(Class type) throws UnsupportedType
	{
		return getValue(m_field.getText(),type);
	}
	public void changedUpdate(DocumentEvent e) { update(); } 
	public void insertUpdate(DocumentEvent e) { update(); } 
	public void removeUpdate(DocumentEvent e) { update(); } 
	private void update()
	{
		String x = m_field.getText();
		if (!x.equals(m_oldVal)) setChanged();
		notifyObservers();
	}
	protected void reset()
	{
		super.reset();
		m_oldVal = m_field.getText();
	}
	boolean hasValidInput()
	{
		final String value = m_field.getText();
		if ((m_flags & MUST_BE_NUMBER) != 0)
		{
			try
			{
				double d = (m_flags & MUST_BE_INTEGER_FLAG) != 0 ? (double) Integer.parseInt(value) : Double.valueOf(value).doubleValue();
				if ((m_flags & MUST_BE_POSITIVE) != 0 && d <= 0.0)
				{
					JOptionPane.showMessageDialog(m_field,
						value.concat(" is invalid input; value must be positive."),
						"Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(m_field,
					value +" is invalid input; value must be a"+
					((m_flags & MUST_BE_INTEGER) != 0 ? "n integer." : " number."),
					"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}
	private String m_oldVal = "";
	private JTextField m_field;
	private byte m_flags = 0;
	public static final byte MUST_BE_NUMBER = 1;
	private static final byte MUST_BE_INTEGER_FLAG = 2;
	public static final byte MUST_BE_INTEGER = MUST_BE_INTEGER_FLAG | MUST_BE_NUMBER;
	public static final byte MUST_BE_POSITIVE = 4;
}
