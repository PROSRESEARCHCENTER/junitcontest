package org.freehep.util.commanddispatcher;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tony Johnson
 */
class Dumper
{
   private boolean started;
   private Set sources;
   Dumper(boolean started, Set sources, Set targets)
   {
      this.started = started;
      this.sources = sources;
   }
   void dump(PrintStream out)
   {
      out.println("Dump of CommandTargetManager state...");
      if (!started) out.println("   CommandTargetManager not Started.");
      else
      {
         DebugCommandState state = new DebugCommandState();
         out.println("   Sources:");
         List l = new LinkedList(sources);
         Collections.sort(l,new SourceComparator());
         for (Iterator i = l.iterator(); i.hasNext(); )
         {
            CommandSource source = (CommandSource) i.next();
            out.print("      "+source.getCommand());
            CommandTarget target = source.getTarget();
            if (target != null) 
            {
               out.print("->"+target);
               target.enable(state);
               if (state.isEnabled()) out.print(" (enabled) ");
               else                   out.print(" (disabled) ");
            }
            out.println();
         }
      }
   }
   private static class SourceComparator implements Comparator
   {
      public int compare(Object o1, Object o2)
      {
         CommandSource s1 = (CommandSource) o1;
         CommandSource s2 = (CommandSource) o2;
         return s1.getCommand().compareTo(s2.getCommand());
      }
   }
   private static class DebugCommandState implements BooleanCommandState
   {
      private boolean enabled;
      private boolean isEnabled()
      {
         return enabled;
      }
      public void setEnabled(boolean state)
      {
         enabled = state;
      }
      
      public void setSelected(boolean check)
      {
      }
      
      public void setText(String text)
      {
      }
      
      public void setToolTipText(String text)
      {
      }
      
   }
}
