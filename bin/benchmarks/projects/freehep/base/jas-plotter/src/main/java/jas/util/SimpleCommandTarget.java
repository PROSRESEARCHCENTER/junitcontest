package jas.util;

/** 
 * A CommandTarget which does not have a selected/deselected state associated with it
 * @see BooleanCommandTarget
 */

public interface SimpleCommandTarget extends CommandTarget
{
	/**
	 * The invoke method is called to actually perform the command.
	 */
	void invoke();
}
