package jas.util;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;

class TextFieldBinding extends FieldBinding implements TextListener
{
	TextFieldBinding(TextField field, byte flags)
	{
		this(field);
		m_flags = flags;
	}
	TextFieldBinding(TextField field)
	{
		m_field = field;
		field.addTextListener(this);
	}
	void set(Object value)
	{
		String x;
		if (value == null) x = "";
		else if (value instanceof String) x = (String) value;
		else x = value.toString();
		if (!m_oldVal.equals(x))
		{
			m_field.setText(x);
			m_oldVal = x;
		}
	}
	Object get(Class type) throws UnsupportedType
	{
		Object result = null;
		String x = m_field.getText();
		if (type.isPrimitive())
		{
			if (type == Double.TYPE ) return new Double(x);
			if (type == Integer.TYPE) return new Integer(x); 
			throw new UnsupportedType(m_field,type);
		}
		else
		{
			Class[] strarg = { x.getClass() };
			try
			{
				Constructor c = type.getConstructor(strarg);
				Object[] args = { x };
				result = c.newInstance(args);
			}
			catch (Exception xx)
			{
				throw new UnsupportedType(m_field,type);
			}
		}
		return result;
	}
	public void textValueChanged(TextEvent e)
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
		String value = m_field.getText();
		if ((m_flags & MUST_BE_NUMBER) != 0)
		{
			try
			{
				double d = (m_flags & MUST_BE_INTEGER) != 0 ? (double) Integer.parseInt(value) : Double.valueOf(value).doubleValue();
				if ((m_flags & MUST_BE_POSITIVE) != 0 && d <= 0.0)
				{
					JOptionPane.showMessageDialog(Application.getApplication().getFrame(),
						value.concat(" is invalid input; value must be positive."),
						"Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(Application.getApplication().getFrame(),
					value +" is invalid input; value must be a"+
					((m_flags & MUST_BE_INTEGER) != 0 ? "n integer." : " number."),
					"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}
	private String m_oldVal = "";
	private TextField m_field;
	private byte m_flags = 0;
	public static final byte MUST_BE_NUMBER = 1;
	public static final byte MUST_BE_INTEGER = 2;
	public static final byte MUST_BE_POSITIVE = 4;
}
