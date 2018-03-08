/*
 * ZipTree.java
 *
 * Created on March 15, 2001, 12:27 PM
 */

package org.freehep.demo.iconbrowser;

import javax.swing.tree.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.*;
import java.util.zip.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import org.freehep.util.images.ImageHandler;
/**
 * Build an IconArchive by reading a ZipFile
 * @author  Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ZipTree.java 10506 2007-01-30 22:48:57Z duns $
 */
class ZipTree extends DefaultMutableTreeNode
{
    private Hashtable hash = new Hashtable();
    private ZipFile zip;
    
    ZipTree(ZipFile zip)
    {
        super(new ZipArchive(zip));
        this.zip = zip;
        Enumeration e = zip.entries();
        while (e.hasMoreElements())
        {
            ZipEntry entry = (ZipEntry) e.nextElement();
            String fullName = entry.getName();
            DefaultMutableTreeNode parent = findParentNode(fullName);
            if (entry.isDirectory())
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ZipDirectory(zip,fullName));
                hash.put(fullName,node);
                parent.add(node);
            }
            else
            {
                if (fullName.endsWith(".gif") || fullName.endsWith(".png") || fullName.endsWith(".jpg"))
                {
                    Object dir = parent.getUserObject();
                    if (dir instanceof ZipDirectory) ((ZipDirectory) dir).addEntry(entry);
                }
            }
        }
        // Trim empty nodes
        boolean changesWereMade = true;
        while (changesWereMade)
        {
            changesWereMade = false;
            for (DefaultMutableTreeNode node = getFirstLeaf(); node != null; )
            {
                DefaultMutableTreeNode next = node.getNextLeaf();
                if (node != this)
                {
                    ZipDirectory dir = (ZipDirectory) node.getUserObject();
                    if (dir.getNEntries() == 0)
                    {
                        node.removeFromParent();
                        changesWereMade = true;
                    }
                }
                node = next;
            }
        }
        hash = null; // not needed anymore
    }
    private DefaultMutableTreeNode findParentNode(String fullName)
    {
        String dirName = IconBrowser.dirName(fullName);
        if (dirName == null) return this;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) hash.get(dirName);
        if (parent == null)
        {
            DefaultMutableTreeNode pp = findParentNode(dirName);
            parent = new DefaultMutableTreeNode(new ZipDirectory(zip,dirName));
            hash.put(dirName,parent);
            pp.add(parent);
        }
        return parent;
    }
    public String toString()
    {
        return IconBrowser.fileName(zip.getName(),File.separator);
    }
    
    private static class ZipArchive extends ZipDirectory implements IconArchive
    {
        ZipArchive(ZipFile file)
        {
            super(file,file.getName());
        }
        public void close()
        {
            try
            {
                getZip().close();
            }
            catch (IOException x) {}
        }
    }
    private static class ZipDirectory implements IconDirectory
    {
        ZipDirectory(ZipFile file, String name)
        {
            this.file = file;
            this.name = name;
        }
        public String getName()
        {
            return name;
        }
        void addEntry(ZipEntry entry)
        {
            entries.add(entry);
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
            ZipEntry e = (ZipEntry) entries.get(index);
            return e.getName();
        }
        public Icon getEntryIcon(int index)
        {
            ZipEntry e = (ZipEntry) entries.get(index);
            return createIconFromZipEntry(file,e);
        }
        private Icon createIconFromZipEntry(ZipFile zip, ZipEntry entry)
        {
            try
            {
                InputStream in = zip.getInputStream(entry);
                try
                {
                    int len = (int) entry.getSize();
                    byte[] data = new byte[len];
                    for (int off = 0; len > 0; )
                    {
                        int l = in.read(data,off,len);
                        off += l;
                        len -= l;
                    }
                    return new ImageIcon(data,entry.getName());
                }
                finally
                {
                    in.close();
                }
            }
            catch (IOException x)
            {
                return ImageHandler.brokenIcon;
            }
        }
        ZipFile getZip()
        {
            return file;
        }
        private String name;
        private java.util.List entries = new ArrayList();
        private ZipFile file;
    }
}
