package jas.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;

/**
 * A CommandProcessor represents a collection of CommandTargets. The CommandProcessor is Observable,
 * and is normally Observed by the CommandTargetManager. When the CommandProcessor calls
 * its notifyObservers method, the CommandTargetManager prompts each CommandSource currently
 * attached to CommandTargets within the CommandProcessor to update their enabled/disabled status.
 * 
 * Typically applications provide their own CommandProcessor(s)  which extend this base class
 * and which can handle a set of commands. By default CommandProcessor's acceptCommand method
 * uses reflection to search for methods in the subClass which correspond to specific commands,
 * although subclasses could also override the acceptCommand method to implement a different
 * scheme. The default scheme looks for methods of type:
 * <pre>
 *            public void onXXX()
 * </pre>
 * to handle the command XXX. Also
 * <pre>
 *            public void enableXXX(JASState state)
 * </pre>
 * to determine if the command is active or not.
 * @dependency jas.util.CommandProcessor$SimpleTarget creates
 * @dependency jas.util.CommandProcessor$BooleanTarget creates
 */
public class CommandProcessor extends Observable
{
	protected String translate(String command)
	{
		if (command.endsWith("...")) 
			command = command.substring(0,command.length()-3);

		for (int i=command.indexOf(" "); i >= 0; i=command.indexOf(" ")) 
			command = command.substring(0,i)+command.substring(i+1); 

		return command;
	}
	protected void setChanged()
	{
		super.setChanged();
		notifyObservers();
	}
	protected void setManager(CommandTargetManager m)
	{
	}
	/**
	 * The CommandTargetManager called acceptCommand to find out if this CommandProcessor
	 * can respond to the specified command. If it can it returns a CommandTarget for the command,
	 * otherwise it returns null.
	 * 
	 */
	protected CommandTarget acceptCommand(String command)
	{
		String t = translate(command);
		String c = "on"+t;
		String e = "enable"+t;
		Method mc,me;
		try
		{
			mc = this.getClass().getMethod(c,noArg);
			try 
			{
				me = this.getClass().getMethod(e,simpleEnableArg);	
			}
			catch (NoSuchMethodException x)
			{
				me = null;
			}
			try // Support for RadioMenuItem
			{
				if (me == null) me = this.getClass().getMethod(e,booleanEnableArg);	
			}
			catch (NoSuchMethodException x)
			{
				me = null;
			}
			return new SimpleTarget(mc,me);
		}
		catch (NoSuchMethodException x)
		{
		}
		try
		{
			mc = this.getClass().getMethod(c,boolArg);
			try 
			{
				me = this.getClass().getMethod(e,booleanEnableArg);	
			}
			catch (NoSuchMethodException x)
			{
				me = null;
			}
			return new BooleanTarget(mc,me);
		}
		catch (NoSuchMethodException x)
		{
		}
		return null;
	}	
	
	protected void invoke(Method method,Object[] args) 
		throws IllegalAccessException,InvocationTargetException
	{
		method.invoke(this,args);
	}
	protected void invokeEnable(Method method,Object[] args) 
		throws IllegalAccessException,InvocationTargetException
	{
		invoke(method,args);
	}
	protected void invokeCommand(Method method,Object[] args) 
		throws IllegalAccessException,InvocationTargetException
	{
		invoke(method,args);
	}
	protected void invokeCommand(SimpleTarget t)
	{
		try
		{
			t.doCommand();
		}
		catch (CommandInvocationException x)
		{
			System.err.println("Error during command invocation: "+x.getTargetException());
		}
	}
	protected void invokeCommand(BooleanTarget t, boolean arg)
	{
		try
		{
			t.doCommand(arg);
		}
		catch (CommandInvocationException x)
		{
			System.err.println("Error during command invocation: "+x.getTargetException());
		}
	}
	private static final Class noArg[] = {}; 
	private static final Object noArgs[] = {}; 
	private static final Class boolArg[] = {java.lang.Boolean.TYPE}; 
	private static final Class simpleEnableArg[] = {JASState.class}; 
	private static final Class booleanEnableArg[] = {JASCheckboxState.class}; 

	/**
	 * A SimpleTarget is an implementation of CommandTarget which implements its
	 * 
	 */
	protected class SimpleTarget implements SimpleCommandTarget
	{
		SimpleTarget(Method command, Method enable)
		{
			m_command = command;
			m_enable = enable;
		}
		public void invoke()
		{
			CommandProcessor.this.invokeCommand(this);
		}
		public void doCommand() throws CommandInvocationException
		{
			try
			{
				invokeCommand(m_command,noArgs);
			}
			catch (IllegalAccessException x)
			{
				throw new RuntimeException("IllegalAccessException during command invocation");
			}
			catch (InvocationTargetException x)
			{
				throw new CommandInvocationException(x.getTargetException());
			}
		}
		public void enable(JASState state)
		{
			if (m_enable == null) state.setEnabled(true);
			else
			{
				try
				{	
					Object[] args = { state };
					invokeEnable(m_enable, args);
				}
				catch (IllegalAccessException x)
				{
					state.setEnabled(false);
				}
				catch (InvocationTargetException x)
				{
					state.setEnabled(false);
				}
			}
		}
		public CommandProcessor getProcessor()
		{
			return CommandProcessor.this;
		}
		private Method m_command;
		private Method m_enable;
	}
	protected class BooleanTarget implements BooleanCommandTarget
	{
		BooleanTarget(Method command, Method enable)
		{
			m_command = command;
			m_enable = enable;
		}
		public void invoke(boolean arg)
		{
			CommandProcessor.this.invokeCommand(this,arg);
		}
		public void enable(JASState state)
		{
			if (m_enable == null) state.setEnabled(true);
			else
			{
				try
				{	
					Object[] args = { (JASCheckboxState) state };
					invokeEnable(m_enable, args);
				}
				catch (IllegalAccessException x)
				{
					state.setEnabled(false);
				}
				catch (InvocationTargetException x)
				{
					state.setEnabled(false);
				}
			}
		}
		public void doCommand(boolean arg) throws CommandInvocationException
		{
			try
			{
				Object[] args = { new Boolean(arg) };
				invokeCommand(m_command,args);
			}
			catch (IllegalAccessException x)
			{
				throw new RuntimeException("IllegalAccessException during command invocation");
			}
			catch (InvocationTargetException x)
			{
				throw new CommandInvocationException(x.getTargetException());
			}
		}
		public CommandProcessor getProcessor()
		{
			return CommandProcessor.this;
		}
		private Method m_command;
		private Method m_enable;
	}
}
