package jas.util;

/**
 * A boolean command target is a CommandTarget which corresponds to a command which
 * may have an on/off state associated with it.
 * @see SimpleCommandTarget
 * @see CommandTarget
 */
public interface BooleanCommandTarget extends CommandTarget
{
	/**
	 * Called when the on/off state changes (i.e. when the comamnd is invoked)
	 */
	void invoke(boolean onOff);
}
