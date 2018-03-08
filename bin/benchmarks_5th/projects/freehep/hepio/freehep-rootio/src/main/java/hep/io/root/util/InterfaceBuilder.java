package hep.io.root.util;

import hep.io.root.RootClass;
import hep.io.root.RootFileReader;
import hep.io.root.RootMember;
import hep.io.root.interfaces.TStreamerInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Builds a Java interface corresponding to a RootClass.
 * @author tonyj
 * @version $Id: InterfaceBuilder.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class InterfaceBuilder
{
   private File base;
   private hep.io.root.core.NameMangler nameMangler = hep.io.root.core.NameMangler.instance();

   /** Creates new InterfaceBuilder */
   public InterfaceBuilder(File dir)
   {
      base = dir;
   }

   public static void main(String[] argv) throws Exception
   {
      if (argv.length < 1)
         usage();

      RootFileReader rfr = new RootFileReader(argv[0]);
      hep.io.root.core.RootClassFactory rcf = rfr.getFactory();
      InterfaceBuilder ib = new InterfaceBuilder(new File("."));
      if (argv.length > 1)
      {
         RootClass rc = rcf.create(argv[1]);
         File f = ib.write(rc);
         System.out.println("Created " + f.getPath());
      }
      else
      {
         List list = rfr.streamerInfo();
         for (Iterator i = list.iterator(); i.hasNext();)
         {
            TStreamerInfo info = (TStreamerInfo) i.next();
            String name = info.getName();

            // See if this class already exists
            try
            {
               Class k = Class.forName("hep.io.root.interfaces." + name);
               System.out.println("Skipping " + name);
            }
            catch (ClassNotFoundException x)
            {
               RootClass rc = rcf.create(name);
               File f = ib.write(rc);
               System.out.println("Created " + f.getPath());
            }
         }
      }
   }

   public File write(RootClass klass) throws IOException
   {
      String klassPath = nameMangler.mangleInterfaceName(klass.getClassName());
      StringBuffer packidge = new StringBuffer();
      File file = base;
      StringTokenizer t = new StringTokenizer(klassPath, ".");
      int count = t.countTokens() - 1;
      for (int i = 0; i < count; i++)
      {
         String token = t.nextToken();
         file = new File(file, token);
         if (i > 0)
            packidge.append('.');
         packidge.append(token);
      }

      String klassName = t.nextToken();
      file.mkdirs();
      file = new File(file, klassName + ".java");

      PrintWriter out = new PrintWriter(new FileOutputStream(file));
      out.println("/*");
      out.println(" * Interface created by InterfaceBuilder. Do not modify.");
      out.println(" *");
      out.println(" * Created on " + new Date());
      out.println(" */");
      out.println();
      out.println("package " + packidge + ";");
      if (!packidge.toString().equals("hep.io.root.interfaces"))
         out.println("import hep.io.root.interfaces.*;");
      out.println();
      out.print("public interface " + klassName + " extends hep.io.root.RootObject");

      RootClass[] superClasses = klass.getSuperClasses();
      for (int i = 0; i < superClasses.length; i++)
         out.print(", " + nameMangler.mangleInterfaceName(superClasses[i].getClassName()));
      out.println();
      out.println("{");

      RootMember[] members = klass.getMembers();
      for (int i = 0; i < members.length; i++)
      {
         String comment = members[i].getComment();
         if ((comment != null) && (comment.length() > 0))
            out.println("\t/** " + comment + " */");

         RootClass type = members[i].getType();
         if (type != null)
         {
             StringBuffer jType = new StringBuffer(type.getJavaClass().getName());
             for (int j = 0; j < members[i].getArrayDim(); j++)
                jType.append("[]");

             String name = nameMangler.mangleMember(members[i].getName());
             out.println("\t" + jType + " " + name + "();");
         }
      }

      int version = klass.getVersion();
      int checkSum = klass.getCheckSum();
      if ((version > 0) || (checkSum > 0))
         out.println();
      if (version > 0)
         out.println("\tpublic final static int rootIOVersion=" + version + ";");
      if (checkSum > 0)
         out.println("\tpublic final static int rootCheckSum=" + checkSum + ";");
      out.println("}");
      out.close();
      return file;
   }

   private static void usage()
   {
      System.out.println("java RootObjectBrowser <file> [<class>]");
      System.exit(0);
   }
}
