/*
 * ZipInputStreamTree.java
 *
 * Created on March 15, 2001, 4:33 PM
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
 * @version $Id: ZipInputStreamTree.java 10506 2007-01-30 22:48:57Z duns $
 */
class ZipInputStreamTree extends DefaultMutableTreeNode
{
    private Hashtable hash = new Hashtable();
    private String name;
    
    ZipInputStreamTree(String name, ZipInputStream zip) throws IOException
    {
        super(new ZipArchive(name));
        this.name = name;
        for (;;)
        {
            ZipEntry entry = zip.getNextEntry();
            if (entry == null) break;
            String fullName = entry.getName();
            DefaultMutableTreeNode parent = findParentNode(fullName);
            if (entry.isDirectory())
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ZipDirectory(fullName));
                hash.put(fullName,node);
                parent.add(node);
            }
            else
            {
                if (fullName.endsWith(".gif") || fullName.endsWith(".png") || fullName.endsWith(".jpg"))
                {
                    Object dir = parent.getUserObject();
                    if (dir instanceof ZipDirectory) ((ZipDirectory) dir).addEntry(entry,zip);
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
            parent = new DefaultMutableTreeNode(new ZipDirectory(dirName));
            hash.put(dirName,parent);
            pp.add(parent);
        }
        return parent;
    }
    public String toString()
    {
        return IconBrowser.fileName(name,File.separator);
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
        void addEntry(ZipEntry entry, InputStream in)
        {
            entries.add(createIconFromZipEntry(entry,in));
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
            ImageIcon e = (ImageIcon) entries.get(index);
            return e.getDescription();
        }
        public Icon getEntryIcon(int index)
        {
            return (Icon) entries.get(index);
        }
        private Icon createIconFromZipEntry(ZipEntry entry, InputStream in)
        {
            try
            {
                int len = (int) entry.getSize();
                if (len <= 0) len = 1000;
                byte[] data = new byte[len];
                for (int off = 0;; )
                {
                    int l = in.read(data,off,len-off);
                    if (l<0) break;
                    if (l>0) off += l;
                    else
                    {
                        len += 1000;
                        byte[] newData = new byte[len];
                        System.arraycopy(data,0,newData,0,off);
                        data = newData;
                    }
                }
                return new ImageIcon(data,entry.getName());
            }
            catch (IOException x)
            {
                return ImageHandler.brokenIcon;
            }
        }
        private String name;
        private java.util.List entries = new ArrayList();
    }
}

