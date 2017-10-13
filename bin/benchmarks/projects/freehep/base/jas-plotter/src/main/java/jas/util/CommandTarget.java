package jas.util;

/**
 * A CommandTarget represents a single action that will be performed as a result of a command
 * being issued. CommandTargets can be enabled is disabled. A set of CommandTargets are
 * typically grouped together into a CommandProcessor.
 */

public interface CommandTarget
{
	/**
	 * Called to determine if CommandTarget is enabled or disabled.
	 */
	void enable(JASState state);
	/**
	 * Gets the CommandProcessor associated with this CommandTarget.
	 */
	CommandProcessor getProcessor();
}
