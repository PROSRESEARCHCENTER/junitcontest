/*
 * ZipList.java
 *
 * Created on March 15, 2001, 12:39 PM
 */
package org.freehep.demo.iconbrowser;

import java.util.zip.*;
import java.util.*;
import javax.swing.tree.*;
import java.io.*;

/**
 * Creates a jar file describing image archives.
 * When reading images from a jar file on the classpath it is not possible
 * to find out what images are available. This utility generates a separate
 * jar file that includes listings of the available images.
 * @author  Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ZipAnnotate.java 10506 2007-01-30 22:48:57Z duns $ 
 */
public class ZipAnnotate
{
    /**
    * @param args the command line arguments (a list of jar files to annotate)
    */
    public static void main (String args[]) throws Exception
    {
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream("ZipAnnotate.jar"));
        zout.putNextEntry(new ZipEntry("IconBrowser.list"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(zout));
        for (int i=0; i<args.length; i++) out.println(IconBrowser.fileName(args[i],File.separator));
        out.flush();
        for (int i=0; i<args.length; i++)
        {
            String file = args[i];
            ZipFile zip = new ZipFile(file);
            ZipTree tree = new ZipTree(zip);
            zout.putNextEntry(new ZipEntry(IconBrowser.fileName(file,File.separator)+".list"));
            out = new PrintWriter(new OutputStreamWriter(zout));
            dumpNode(out,tree,true);
            out.flush();
            zout.closeEntry();
        }
        zout.close();
        System.exit(0);
    }
    private static void dumpNode(PrintWriter out, DefaultMutableTreeNode node, boolean root)
    {
        IconDirectory dir = (IconDirectory) node.getUserObject();
        if (!root) out.println(dir.getName());
        for (int i=0; i<dir.getNEntries(); i++)
        {
            out.println(dir.getEntryName(i));
        }
        Enumeration e = node.children();
        while (e.hasMoreElements())
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
            dumpNode(out,child,false);
        }
    }
}
