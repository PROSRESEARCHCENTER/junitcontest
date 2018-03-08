package hep.physics.particle;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;


public class ParticleUtilities
{
   private ParticleUtilities()
   {
      // never instantiated
   }
   /**
    * Dump the particle heirarchy within the event to System.out
    * @param p the particle to start the dump from
    */
   public static void dumpParticleHierarchy(Particle p)
   {
      dumpParticleHierarchy(p, System.out);
   }
   /**
    * Dump the particle heirarchy within the event
    * @param p the particle to start the dump from
    * @param out the stream to write to
    */
   public static void dumpParticleHierarchy(Particle p, PrintStream out)
   {
      PrintWriter pw = new PrintWriter(out);
      dumpParticleHierarchy(p,pw);
      pw.flush();
   }
   public static void dumpParticleHierarchy(Particle p, PrintWriter out)
   {
      dumpParticleHierarchy(p,out,"","");
   }
   private static void dumpParticleHierarchy(Particle p, PrintWriter out, String indent, String prefix)
   {
      out.println(indent + prefix + p.getType().toString());
      
      Iterator e = p.getDaughters().iterator();
      for (boolean more = e.hasNext() ; more ; )
      {
         Particle next = (Particle) e.next();
         more = e.hasNext();
         dumpParticleHierarchy(next, out, indent + (prefix.equals("+--") ? "|  " : "   ") , (more ? "+--" : "\\--"));
      }
   }
}
