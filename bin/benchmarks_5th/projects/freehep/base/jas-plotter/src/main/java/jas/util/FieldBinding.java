package jas.util;
import java.util.Observable;

abstract class FieldBinding extends Observable
{
	// override to implement a test for valid input
	boolean hasValidInput()
	{
		return true;
	}
	abstract void set(Object value) throws UnsupportedType;
	abstract Object get(Class type) throws UnsupportedType;
	protected void reset() { valueChanged = false; }
	protected void setChanged()
	{
		super.setChanged();
		valueChanged = true;
	}
	protected boolean hasValueChanged()
	{
		return valueChanged;
	}
	private boolean valueChanged = false;
}
