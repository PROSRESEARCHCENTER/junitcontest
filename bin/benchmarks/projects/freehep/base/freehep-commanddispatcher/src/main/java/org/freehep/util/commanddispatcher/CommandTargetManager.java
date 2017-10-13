package org.freehep.util.commanddispatcher;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observer;
import java.util.Set;

/**
 * A CommandTargetManager manages a set of CommandSources and a set of CommandGroups,
 * and figure out the wiring from the CommandSources to the CommandTargets within the CommandGroups.
 * The CommandGroups can be dynamically added and removed from the CommandTargetManager, 
 * as sets of commands become available or not.
 * 
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public class CommandTargetManager
{
   private Set groups = new HashSet();
   private Set sources = new HashSet();
   private boolean started = false;

   public synchronized void add(CommandGroup group)
   {
      if (groups.add(group) && started)
      {
         for (Iterator i = sources.iterator(); i.hasNext();)
         {
            CommandSource s = (CommandSource) i.next();
            String command = s.getCommand();
            CommandTarget target = group.acceptCommand(command);
            if (target != null)
            {
               CommandTarget existingTarget = s.getTarget();
               if (existingTarget == null)
               {
                  if (s.setTarget(target) && s instanceof Observer)
                     group.addObserver((Observer) s);
               }
               else if (existingTarget instanceof MultiTarget)
               {
                  ((MultiTarget) existingTarget).add(target);
               }
               else
               {
                  if (s instanceof Observer)
                     existingTarget.getGroup().deleteObserver((Observer) s);

                  MultiTarget multi = new MultiTarget(command);
                  multi.add(existingTarget);
                  multi.add(target);
                  if (s.setTarget(multi) && s instanceof Observer)
                     multi.getGroup().addObserver((Observer) s);
               }
            }
         }
      }
      group.setManager(this);
   }

   public synchronized void add(CommandSource source)
   {
      if (sources.add(source) && started)
      {
         linkCommandSource(source);
      }
   }

   /**
    * Override this method to provide custom handling of errors generated during command
    * processing.
    */
   public void handleCommandError(Throwable x)
   {
      System.err.println("Error during command invocation");
      x.printStackTrace();
   }

   public synchronized void remove(CommandGroup group)
   {
      if (groups.remove(group) && started)
      {
         for (Iterator i = sources.iterator(); i.hasNext();)
         {
            CommandSource s = (CommandSource) i.next();
            CommandTarget target = s.getTarget();
            if (target != null)
            {
               if (target.getGroup() == group)
               {
                  s.clearTarget();
                  if (s instanceof Observer)
                     group.deleteObserver((Observer) s);
               }
               else if (target instanceof MultiTarget)
               {
                  ((MultiTarget) target).removeGroup(group);
               }
            }
         }
      }
      group.setManager(null);
   }

   public synchronized void remove(CommandSource source)
   {
      if (sources.remove(source) && started)
      {
         CommandTarget t = source.getTarget();
         if (t != null)
         {
            source.clearTarget();
            if (source instanceof Observer) t.getGroup().deleteObserver((Observer) source);
         }
      }
   }

   public synchronized void start()
   {
      started = true;
      for (Iterator i = sources.iterator(); i.hasNext();)
      {
         linkCommandSource((CommandSource) i.next());
      }
   }
   /**
    * Dump the state of the manager for debug purposes
    */
   public void dump(PrintStream out)
   {
      Dumper dumper = new Dumper(started, sources, groups);
      dumper.dump(out);
   }
   
   /**
    * This method links the CommandSource to any CommandGroup it hits
    * that contains a CommandTarget that accepts the CommandSource.
    */
   private void linkCommandSource(CommandSource s)
   {
      String command = s.getCommand();
      CommandTarget theTarget = null;

      for (Iterator i = groups.iterator(); i.hasNext();)
      {
         CommandGroup g = (CommandGroup) i.next();
         CommandTarget t = g.acceptCommand(command);

         if (t != null)
         {
            if (theTarget == null)
               theTarget = t;
            else if (theTarget instanceof MultiTarget)
               ((MultiTarget) theTarget).add(t);
            else
            {
               MultiTarget multi = new MultiTarget(command);
               multi.add(theTarget);
               multi.add(t);
               theTarget = multi;
            }
         }
      }
      if (theTarget != null)
      {
         if (s.setTarget(theTarget) && s instanceof Observer)
            theTarget.getGroup().addObserver((Observer) s);
      }
   }
}
