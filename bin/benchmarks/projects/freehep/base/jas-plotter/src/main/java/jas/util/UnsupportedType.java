package jas.util;
import java.awt.Component;

class UnsupportedType extends Exception
{
	UnsupportedType(Component c,Class t)
	{
		super("Cannot bind type "+t+" to "+c.getClass());
	}
}
