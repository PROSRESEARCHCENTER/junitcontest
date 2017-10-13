package jas.util;
import java.util.Enumeration;
import java.util.Vector;

public class NestedCommandProcessor extends CommandProcessor
{
	public synchronized void addSubProcessor(CommandProcessor p)
	{
		subProcessors.addElement(p);
		if (manager != null) manager.add(p);
	}
	public synchronized void removeSubProcessor(CommandProcessor p)
	{	
		subProcessors.removeElement(p);
		if (manager != null) manager.remove(p);
	}
	protected void setManager(CommandTargetManager m)
	{
		Enumeration e = subProcessors.elements();
		while (e.hasMoreElements())
		{
			CommandProcessor p = (CommandProcessor) e.nextElement();
			if (m == null) manager.remove(p);
			else m.add(p);
		}
		manager = m;
	}
	private CommandTargetManager manager;
	private Vector subProcessors = new Vector();
}
