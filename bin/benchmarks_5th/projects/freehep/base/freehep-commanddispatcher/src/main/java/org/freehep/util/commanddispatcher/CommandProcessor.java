package org.freehep.util.commanddispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;


/**
 * A default implementation of CommandGroup.
 *
 * Typically applications provide their own CommandProcessor(s) which extend this base class
 * and which can handle a set of commands. By default CommandProcessor's acceptCommand method
 * uses reflection to search for methods in the subClass which correspond to specific commands,
 * although subclasses could also override the acceptCommand method to implement a different
 * scheme. The default scheme looks for methods of type:
 * <pre>
 *            public void onXXX()
 * </pre>
 * to handle the command XXX. Also
 * <pre>
 *            public void enableXXX(CommandState state)
 * </pre>
 * to determine if the command is active or not.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: CommandProcessor.java 8584 2006-08-10 23:06:37Z duns $
 */

public class CommandProcessor extends Observable implements CommandGroup
{
   private static final Class[] noArg = {  };
   private static final Object[] noArgs = {  };
   private static final Class[] boolArg = { java.lang.Boolean.TYPE };
   private static final Class[] simpleEnableArg = { CommandState.class };
   private static final Class[] booleanEnableArg = { BooleanCommandState.class };
   private CommandTargetManager manager;

   public void setChanged()
   {
      if (!hasChanged())
      {
         super.setChanged();
         javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  notifyObservers();
                  clearChanged();
               }
            });
      }
   }

   public void setManager(CommandTargetManager m)
   {
      this.manager = m;
   }

   /**
    * The CommandTargetManager calls acceptCommand to find out if this CommandProcessor
    * can respond to the specified command. If it can it returns a CommandTarget for the command,
    * otherwise it returns null.
    * @param command The command to test for
    */
   public CommandTarget acceptCommand(String command)
   {
      return acceptCommand(getClass(), command);
   }

   /**
    * Uses reflection to check if the specified class has an "on" or "enable" method
    * for this comamnd.
    * @param klass The class to check
    * @param command The command to test for
    */
   protected CommandTarget acceptCommand(Class klass, String command)
   {
      String t = translate(command);
      String c = "on" + t;
      String e = "enable" + t;
      Method mc;
      Method me;
      try
      {
         mc = klass.getMethod(c, noArg);
         try
         {
            me = klass.getMethod(e, simpleEnableArg);
         }
         catch (NoSuchMethodException x)
         {
            me = null;
         }
         try // Support for RadioMenuItem
         {
            if (me == null)
               me = klass.getMethod(e, booleanEnableArg);
         }
         catch (NoSuchMethodException x)
         {
            me = null;
         }
         return new SimpleTarget(mc, me);
      }
      catch (NoSuchMethodException x) {}
      try
      {
         mc = klass.getMethod(c, boolArg);
         try
         {
            me = klass.getMethod(e, booleanEnableArg);
         }
         catch (NoSuchMethodException x)
         {
            me = null;
         }
         return new BooleanTarget(mc, me);
      }
      catch (NoSuchMethodException x) {}
      return null;
   }

   protected void invoke(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException
   {
      method.invoke(this, args);
   }

   /**
    * Override this method to provide application specific handling
    * for errors generated during command dispatch
    */
   protected void invokeCommand(SimpleTarget t)
   {
      try
      {
         t.doCommand();
      }
      catch (CommandInvocationException x)
      {
         manager.handleCommandError(x.getTargetException());
      }
   }

   /**
    * Override this method to provide application specific handling
    * for errors generated during command dispatch
    */
   protected void invokeCommand(BooleanTarget t, boolean arg)
   {
      try
      {
         t.doCommand(arg);
      }
      catch (CommandInvocationException x)
      {
         manager.handleCommandError(x.getTargetException());
      }
   }

   protected void invokeCommand(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException
   {
      invoke(method, args);
   }

   protected void invokeEnable(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException
   {
      invoke(method, args);
   }

   /**
    * Translates a command. The default implementation removes trailing ..., and
    * any spaces and then uppercases the first character .
    */
   protected String translate(String command)
   {
      if (command.endsWith("..."))
         command = command.substring(0, command.length() - 3);

      for (int i = command.indexOf(" "); i >= 0; i = command.indexOf(" "))
         command = command.substring(0, i) + command.substring(i + 1);

      if ((command.length() < 2) || Character.isUpperCase(command.charAt(0)))
         return command;
      else
         return command.substring(0, 1).toUpperCase() + command.substring(1);
   }

   /**
    * Boolean target is an implementation of CommandTarget
    * for command targets which can be swithced on/off. (Typically
    * corresponding to JCheckBoxMenuItem or JRadioButtonMenuItem).
    */
   protected class BooleanTarget implements BooleanCommandTarget
   {
      private Method m_command;
      private Method m_enable;

      BooleanTarget(Method command, Method enable)
      {
         m_command = command;
         m_enable = enable;
         try
         {
            command.setAccessible(true);
            if (enable != null) enable.setAccessible(true);
         }
         catch (SecurityException x) {}
      }

      public CommandGroup getGroup()
      {
         return CommandProcessor.this;
      }

      public void doCommand(boolean arg) throws CommandInvocationException
      {
         try
         {
            Object[] args = { new Boolean(arg) };
            invokeCommand(m_command, args);
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

      public void enable(CommandState state)
      {
         if (m_enable == null)
            state.setEnabled(true);
         else
         {
            try
            {
               Object[] args = { (BooleanCommandState) state };
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

      public void invoke(boolean arg)
      {
         CommandProcessor.this.invokeCommand(this, arg);
      }
   }

   /**
    * A SimpleTarget is an implementation of CommandTarget
    *
    */
   protected class SimpleTarget implements SimpleCommandTarget
   {
      private Method m_command;
      private Method m_enable;

      SimpleTarget(Method command, Method enable)
      {
         m_command = command;
         m_enable = enable;
         try
         {
            command.setAccessible(true);
            if (enable != null) enable.setAccessible(true);
         }
         catch (SecurityException x) {}
      }

      public CommandGroup getGroup()
      {
         return CommandProcessor.this;
      }

      public void doCommand() throws CommandInvocationException
      {
         try
         {
            invokeCommand(m_command, noArgs);
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

      public void enable(CommandState state)
      {
         if (m_enable == null)
            state.setEnabled(true);
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

      public void invoke()
      {
         CommandProcessor.this.invokeCommand(this);
      }
   }
}
