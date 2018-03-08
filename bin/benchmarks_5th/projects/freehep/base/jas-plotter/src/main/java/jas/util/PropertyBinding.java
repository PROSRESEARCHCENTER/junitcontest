package jas.util;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.TextField;
import java.beans.PropertyDescriptor;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;

public class PropertyBinding
{
	public PropertyBinding(FieldBinding field, String property)
	{
		m_field = field;
		m_property = property;
	}
	public PropertyBinding(TextField field, String property, byte flags)
	{
		m_field = new TextFieldBinding(field, flags);
		m_property = property;
	}
	public PropertyBinding(TextField field, String property)
	{
		m_field = new TextFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(JTextField field, String property, byte flags)
	{
		m_field = new JTextFieldBinding(field, flags);
		m_property = property;
	}
	public PropertyBinding(JTextField field, String property)
	{
		m_field = new JTextFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(JCheckBox field, String property)
	{
		m_field = new JCheckBoxFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(Checkbox field, String property)
	{
		m_field = new CheckboxFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(Choice field, String property)
	{
		m_field = new ChoiceFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(JComboBox field, String property)
	{
		m_field = new JComboBoxFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(ColorChooser field, String property)
	{
		m_field = new ColorFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(SpinBox field, String property)
	{
		m_field = new SpinFieldBinding(field);
		m_property = property;
	}
	public PropertyBinding(JSpinner field, String property)
	{
		m_field = new JSpinnerFieldBinding(field);
		m_property = property;
	}
   public void setBeanClass(Class c)
	{
		m_beanClass = c;
	}
	void addObserver(Observer o)
	{
		m_field.addObserver(o);
	}
	void doDataExchange(boolean set,Object bean)
	{
		if (m_beanClass != null && !m_beanClass.isAssignableFrom(bean.getClass())) return;
		try
		{
			PropertyDescriptor desc = new PropertyDescriptor(m_property,bean.getClass());
			Class type = desc.getPropertyType();
			if (!set)
			{
				Object value = desc.getReadMethod().invoke(bean,noargs);
				m_field.set(value);
			}
			else
			{
				if (!m_field.hasValueChanged()) return;
				Object o = m_field.get(type);
				Object[] args = { o };
				desc.getWriteMethod().invoke(bean,args);
			}
			m_field.reset();
		}
		catch (java.lang.reflect.InvocationTargetException x)
		{
			System.err.println("Problem dealing with property "+m_property);
			x.getTargetException().printStackTrace();
		}
		catch (Exception x)
		{
			System.err.println("Problem dealing with property "+m_property);
			x.printStackTrace();
		}
	}
	boolean hasValidInput()
	{
		return m_field.hasValidInput();
	}
	private Class m_beanClass;
	private FieldBinding m_field;
	private String m_property;
	private final static Object[] noargs = new Object[0];
}
