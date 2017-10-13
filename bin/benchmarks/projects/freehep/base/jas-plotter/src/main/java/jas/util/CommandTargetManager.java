package jas.util;
import java.util.Enumeration;
import java.util.Observer;
import java.util.Vector;

/**
 * A CommandTargetManager manages a set of CommandSources and a set of CommandProcessors,
 * and figure out the wiring from the CommandSources to the CommandTargets within the CommandProcessors.
 * The CommandProcessors can be dynamically added and removed from the CommandTargetManager, 
 * as sets of commands become available or not.
 */
final public class CommandTargetManager
{	
	public synchronized void add(CommandProcessor processor)
	{
		processors.addElement(processor);
		if (started)
		{
			Enumeration es = sources.elements();
			while (es.hasMoreElements())
			{
				CommandSource s = (CommandSource) es.nextElement();
				if (s.getTarget() == null)
				{
					String command = s.getCommand();
					CommandTarget target = processor.acceptCommand(command);
					if (target != null) 
					{
						if (s.setTarget(target) && s instanceof Observer) processor.addObserver((Observer) s);
					}
				}
			}
		}
		processor.setManager(this);
	}
	public synchronized void remove(CommandProcessor processor)
	{
		processors.removeElement(processor);
		if (started)
		{
			Enumeration es = sources.elements();
			while (es.hasMoreElements())
			{
				CommandSource s = (CommandSource) es.nextElement();
				CommandTarget target = s.getTarget();
				if (target != null)
				{
					if (target.getProcessor() == processor) 
					{
						s.clearTarget();
						if (s instanceof Observer) processor.deleteObserver((Observer) s);
					}
				}
			}
		}
		processor.setManager(null);
	}
	public synchronized void add(CommandSource source)
	{
		sources.addElement(source);
		if (started)
		{
			linkCommandSource(source);
		}
	}
	public synchronized void remove(CommandSource source)
	{
		sources.removeElement(source);
		final String command = source.getCommand();
			
		final Enumeration ep = processors.elements();
		while (ep.hasMoreElements())
		{
			final CommandProcessor p = (CommandProcessor) ep.nextElement();
			final CommandTarget t = p.acceptCommand(command);
			if (t != null) 
			{
				source.clearTarget();
				if (source instanceof Observer) p.deleteObserver((Observer) source);
			}
		}
	}
	public synchronized void start()
	{
		started = true;
		Enumeration es = sources.elements();
		while (es.hasMoreElements())
		{
			linkCommandSource((CommandSource) es.nextElement());
		}
	}
	/**
	 * This method just links the CommandSource to the first CommandProcessor it hits
	 * that contains a CommandTarget that accepts the CommandSource--which is a problem
	 * if we want to have two simultaneously active CommandProcessors which both contain
	 * (different) CommandTargets of the same name (e.g. the case of the ProgramPage and
	 * the Console window both having a Copy command).
	 */
	private void linkCommandSource(final CommandSource s)
	{
		final String command = s.getCommand();
			
		final Enumeration ep = processors.elements();
		while (ep.hasMoreElements())
		{
			final CommandProcessor p = (CommandProcessor) ep.nextElement();
			final CommandTarget t = p.acceptCommand(command);
			if (t != null) 
			{
				if (s.setTarget(t) && s instanceof Observer) p.addObserver((Observer) s);
			}
		}
	}

	private boolean started = false;
	private final Vector processors = new Vector();
	private final Vector sources = new Vector();
}
