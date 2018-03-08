package jas.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

public class CommandDispatcher implements ActionListener
{
	public CommandDispatcher(Object p)
	{
		parent = p;
	}
	public void actionPerformed(ActionEvent evt)
	{
		String command = evt.getActionCommand();

		if (command.endsWith("...")) command = command.substring(0,command.length()-3);

		for (int i=command.indexOf(" "); i >= 0; i=command.indexOf(" ")) 
			command = command.substring(0,i)+command.substring(i+1); 

		try
		{
			Class target = parent.getClass();
			Class[] noarg = new Class[0];
			Method dispatcher = target.getMethod("on"+command,noarg);
			dispatcher.invoke(parent,null);
		}
		catch (Exception e)
		{
			System.out.println("Exception "+e+" dispatching command: "+command);
		}
	}
	protected void invoke(Object target,Method method,Object[] args)
	{

	}
	private Object parent;
}
