/*
 * ZipListTree.java
 *
 * Created on March 15, 2001, 1:02 PM
 */

package org.freehep.demo.iconbrowser;

import javax.swing.tree.*;
import javax.swing.Icon;
import java.util.*;
import java.io.*;
import org.freehep.util.images.ImageHandler;
/**
 * Builds an ImageArchive by reading a list of files. The actual icons
 * are assumed to be on the classpath
 * @author  Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ZipListTree.java 10506 2007-01-30 22:48:57Z duns $
 */
public class ZipListTree extends DefaultMutableTreeNode
{
    /** Creates new ZipListTree */
    public ZipListTree(String name) throws IOException
    {
        super(new ZipArchive("builtin:/"+name));
        Hashtable hash = new Hashtable();
        String resource = "/"+name+".list";
        InputStream in = getClass().getResourceAsStream(resource);
        if (in == null) throw new IOException("Cannot open "+resource);
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
        for (;;)
        {
            String line = reader.readLine();
            if (line == null || line.length() == 0) break;
            String dirName = IconBrowser.dirName(line);
            DefaultMutableTreeNode parent = dirName == null ? this : (DefaultMutableTreeNode) hash.get(dirName);
            if (line.endsWith("/"))
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ZipDirectory(line));
                hash.put(line,node);
                parent.add(node);
            }
            else
            {
                Object dir = parent.getUserObject();
                if (dir instanceof ZipDirectory) ((ZipDirectory) dir).addEntry(line);
            }           
        }
    }
    private static class ZipArchive extends ZipDirectory implements IconArchive
    {
        ZipArchive(String name)
        {
            super(name);
        }
        public void close()
        {
        }
    }
    private static class ZipDirectory implements IconDirectory
    {
        ZipDirectory(String name)
        {
            this.name = name;
        }
        public String getName()
        {
            return name;
        }
        void addEntry(String name)
        {
            entries.add(name);
        }
        public String toString()
        {
            StringBuffer b = new StringBuffer(IconBrowser.fileName(name));
            if (!entries.isEmpty())
            {
                int n = entries.size();
                b.append(" (");
                b.append(n);
                if (n == 1) b.append(" entry)");
                else b.append(" entries)"); 
            }
            return b.toString();
        }
        public int getNEntries()
        {
            return entries.size();
        }
        public String getEntryName(int index)
        {
            return entries.get(index).toString();
        }
        public Icon getEntryIcon(int index)
        {
            String name = entries.get(index).toString();
            return ImageHandler.getIcon(getClass().getResource("/"+name));
        }
        private String name;
        private java.util.List entries = new ArrayList();
    }
}
