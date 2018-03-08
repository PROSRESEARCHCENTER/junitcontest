package jas.util;

public class CommandInvocationException extends Exception
{
	CommandInvocationException(Throwable t)
	{
		super("Error during command invocation");
		m_target = t;
	}
	public Throwable getTargetException()
	{
		return m_target;	
	}
	private Throwable m_target;
}
