package org.freehep.util.commanddispatcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * An adapter used in the case where one source maps to multiple targets
 * @author tonyj
 * @version $Id: MultiTarget.java 8584 2006-08-10 23:06:37Z duns $
 */
class MultiTarget implements SimpleCommandTarget
{
   private List l = new ArrayList();
   private MultiGroup group = new MultiGroup();
   private MultiCommandState myState = new MultiCommandState();

   MultiTarget(String command)
   {
   }
   void add(CommandTarget t)
   {
      l.add(t);
      group.notifyObservers();
      t.getGroup().addObserver(group);
   }
   void removeGroup(CommandGroup g)
   {
      boolean wasChanged = false;
      for (Iterator i = l.iterator(); i.hasNext(); )
      {
         CommandTarget t = (CommandTarget) i.next();
         if (t.getGroup() == g)
         {
            i.remove();
            g.deleteObserver(group);
            wasChanged = true;
         }
      }
      if (wasChanged) group.notifyObservers();
   }
   public void enable(CommandState state)
   {
      myState.setEnabled(false);
      // We are enabled as long as one target is enabled
      for (int i=0; i<l.size(); i++)
      {
         ((CommandTarget) l.get(i)).enable(myState);
         if (myState.isEnabled()) 
         {
            state.setEnabled(true);
            return;
         }
      }
      state.setEnabled(false);
   }
   public CommandGroup getGroup()
   {
      return group;
   }
   public void invoke()
   {
      for (int i=0; i<l.size(); i++)
      {
         SimpleCommandTarget target = (SimpleCommandTarget) l.get(i);
         myState.setEnabled(false);
         target.enable(myState);
         if (myState.isEnabled()) 
         {
            target.invoke();
         }
      }
   } 
   private class MultiCommandState implements CommandState
   {
      boolean enabled = false;
      boolean isEnabled()
      {
         return enabled;
      }
      public void setEnabled(boolean state)
      {
         enabled = state;
      }  
      public void setText(String text)
      {
         // ignored
      }
      public void setToolTipText(String text)
      {
         // ignored
      }
   }
   private class MultiGroup extends Observable implements CommandGroup, Observer
   {
      public CommandTarget acceptCommand(String command)
      {
         return MultiTarget.this;
      }
            
      public void setManager(CommandTargetManager manager)
      {
         // who cares?
      }
      
      public void update(Observable o, Object arg)
      {
         notifyObservers(arg);
      }
      public void notifyObservers(Object arg)
      {
         setChanged();
         super.notifyObservers(arg);
      }
   }
}