package hep.io.root.util;

import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.RootObject;
import hep.io.root.daemon.RootAuthenticator;
import hep.io.root.daemon.xrootd.XrootdURLStreamFactory;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TNamed;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * A simple application for browsing the contents of Root Files
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootObjectBrowser.java 13849 2011-07-01 23:49:23Z tonyj $
 */
public class RootObjectBrowser extends JPanel implements TreeSelectionListener
{
   private final static String aboutMessage = "<HTML>RootObjectBrowser $Id: RootObjectBrowser.java 13849 2011-07-01 23:49:23Z tonyj $<br>Author: Tony Johnson (tonyj@slac.stanford.edu)";
   private final static TreeModel emptyTree = null;
   private JTree objTree;
   private JTree tree;
   private RootFileReader reader;
   private RootMenuBar menuBar;

   public RootObjectBrowser()
   {
      super(new BorderLayout());
      tree = new JTree(emptyTree);
      tree.setCellRenderer(new RootDirectoryTreeCellRenderer());
      tree.addTreeSelectionListener(this);

      objTree = new JTree(emptyTree);
      objTree.setCellRenderer(new RootObjectTreeCellRenderer());
      ToolTipManager.sharedInstance().registerComponent(objTree);

      JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(objTree));
      add(split, BorderLayout.CENTER);
      add(menuBar = new RootMenuBar(), BorderLayout.NORTH);
      menuBar.setFileOpen(false);

      setPreferredSize(new Dimension(500, 300));
      split.setDividerLocation(245);

      ToolTipManager.sharedInstance().setEnabled(true);
   }

   public void setRootFile(File file) throws IOException
   {
      Cursor old = getCursor();
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try
      {
         reader = new RootFileReader(file);

         TreeModel model = new RootDirectoryTreeModel(reader)
         {
            public void handleException(IOException x)
            {
               error(x);
               x.printStackTrace();
               super.handleException(x);
            }
         };
         tree.setModel(model);
         tree.setRowHeight(20);
         tree.setLargeModel(true);
         menuBar.setFileOpen(true);
      }
      finally
      {
         setCursor(old);
      }
   }
   public void setRootFile(URL url) throws IOException
   {
      Cursor old = getCursor();
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try
      {
         reader = new RootFileReader(url);

         TreeModel model = new RootDirectoryTreeModel(reader)
         {
            public void handleException(IOException x)
            {
               error(x);
               x.printStackTrace();
               super.handleException(x);
            }
         };
         tree.setModel(model);
         tree.setRowHeight(20);
         tree.setLargeModel(true);
         menuBar.setFileOpen(true);
      }
      finally
      {
         setCursor(old);
      }
   }
   public static void main(String[] argv) throws IOException
   {
      if (argv.length > 1)
         usage();
      if ((argv.length == 1) && argv[0].startsWith("-"))
         usage();

      URL.setURLStreamHandlerFactory(new XrootdURLStreamFactory());
      
      JFrame frame = new JFrame("Root Object Browser");
      RootObjectBrowser browser = new RootObjectBrowser();
      Authenticator.setDefault(new RootAuthenticator(browser));
      URLConnection.setDefaultAllowUserInteraction(true);

      if (argv.length == 1)
      {
         if (argv[0].startsWith("root:")) browser.setRootFile(new URL(argv[0]));
         else browser.setRootFile(new File(argv[0]));
      }
      frame.setContentPane(browser);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
   }

   public void valueChanged(TreeSelectionEvent event)
   {
      if (!event.isAddedPath())
         return;

      Cursor old = getCursor();
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try
      {
         TreePath path = tree.getSelectionPath();
         if (path == null)
            objTree.setModel(emptyTree);
         else
         {
            List save = saveToggledBranches(objTree);
            Object node = path.getLastPathComponent();
            if (node instanceof BranchEntry)
               node = ((BranchEntry) node).getValue();
            if (node instanceof TKey)
            {
               TKey key = (TKey) node;
               objTree.setModel(new RootObjectTreeModel(key.getObject(), key.getName()));
            }
            else if (node instanceof TNamed)
               objTree.setModel(new RootObjectTreeModel(node, ((TNamed) node).getName()));
            else if (node instanceof RootObject)
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else if (node.getClass().isArray())
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else if (node instanceof List)
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else if (node instanceof Map)
               objTree.setModel(new RootObjectTreeModel(node, ""));
            else
               objTree.setModel(emptyTree);
            restoreToggledBranches(objTree, save);
         }
      }
      catch (RootClassNotFound x)
      {
         objTree.setModel(emptyTree);
         x.printStackTrace();
         error(x);
      }
      catch (IOException x)
      {
         objTree.setModel(emptyTree);
         x.printStackTrace();
         error(x);
      }
      finally
      {
         setCursor(old);
      }
   }

   private static void usage()
   {
      System.out.println("java RootObjectBrowser [<file>]");
      System.exit(0);
   }

   private void error(Throwable x)
   {
      x.printStackTrace();
      JOptionPane.showMessageDialog(this, x.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
   }

   private void restoreToggledBranches(JTree tree, List save)
   {
      TreeModel model = tree.getModel();
      if (model == null)
         return;

      Object root = model.getRoot();
      Iterator iter = save.iterator();
outer: while (iter.hasNext())
      {
         Object parent = root;
         TreePath realPath = new TreePath(root);
         TreePath path = (TreePath) iter.next();
         Object[] nodes = path.getPath();
         for (int i = 1; i < nodes.length; i++)
         {
            String node = (String) nodes[i];
            int pos = node.indexOf(' ');
            int index = Integer.parseInt(node.substring(0, pos));
            if (index >= model.getChildCount(parent))
               continue outer;

            Object n = model.getChild(parent, index);
            parent = n;
            if (n.toString().equals(node.substring(pos + 1)))
               realPath = realPath.pathByAddingChild(n);
            else
               continue outer;
         }
         tree.expandPath(realPath);
      }
   }

   private List saveToggledBranches(JTree tree)
   {
      List save = new ArrayList();
      TreeModel model = tree.getModel();
      if (model == null)
         return save;

      Object root = model.getRoot();
      TreePath rootPath = new TreePath(new Object[] { root });
      Enumeration e = tree.getExpandedDescendants(rootPath);
      if (e == null)
         return save;
      while (e.hasMoreElements())
      {
         Object parent = root;
         TreePath path = (TreePath) e.nextElement();

         // canonicalize the elements
         Object[] nodes = path.getPath();
         if (nodes.length < 2)
            continue;
         for (int i = 0; i < nodes.length; i++)
         {
            Object node = nodes[i];
            int index = model.getIndexOfChild(parent, node);
            parent = node;
            nodes[i] = String.valueOf(index) + " " + node.toString();
         }

         TreePath newPath = new TreePath(nodes);
         save.add(newPath);
      }
      return save;
   }

   private void showStreamerInfo()
   {
      try
      {
         Comparator sortByName = new Comparator()
         {
            public int compare(Object a, Object b)
            {
               return ((TNamed) a).getName().compareTo(((TNamed) b).getName());
            }

            public boolean equals(Object o)
            {
               return o.getClass() == this.getClass();
            }
         };

         List streamerInfo = new ArrayList();
         for (Iterator i = reader.streamerInfo().iterator();i.hasNext();)
         {
             Object o = i.next();
             if (o instanceof TNamed) streamerInfo.add(o);
         }
         Collections.sort(streamerInfo, sortByName);

         StreamerInfoBrowser browser = new StreamerInfoBrowser(streamerInfo);
         JFrame f = new JFrame("Root Streamer Info Browser");
         f.setContentPane(browser);
         f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         f.pack();
         f.setVisible(true);
      }
      catch (IOException x)
      {
         error(x);
      }
   }

   private static class RootFileFilter extends FileFilter
   {
      public String getDescription()
      {
         return "Root Files (*.root)";
      }

      public boolean accept(File file)
      {
         return file.isDirectory() || file.getName().endsWith(".root");
      }
   }

   private class RootFileMenu extends JMenu
   {
      RootFileMenu()
      {
         super("File");
         add(new JMenuItem("Open File...")
            {
               public void fireActionPerformed(ActionEvent e)
               {
                  JFileChooser dlg = new JFileChooser("Open Root File...");
                  dlg.setFileFilter(new RootFileFilter());

                  int rc = dlg.showOpenDialog(RootObjectBrowser.this);
                  if (rc == JFileChooser.APPROVE_OPTION)
                  {
                     try
                     {
                        setRootFile(dlg.getSelectedFile());
                     }
                     catch (IOException x)
                     {
                        error(x);
                     }
                  }
               }
            });
            add(new JMenuItem("Open URL...")
            {
               public void fireActionPerformed(ActionEvent e)
               {
                  String url = JOptionPane.showInputDialog(RootObjectBrowser.this,"URL","root://");
                  if (url != null)
                  {
                     try
                     {
                        setRootFile(new URL(url));
                     }
                     catch (IOException x)
                     {
                        error(x);
                     }
                  }
               }
            });
         add(new JMenuItem("Exit")
            {
               public void fireActionPerformed(ActionEvent e)
               {
                  System.exit(0);
               }
            });
      }
   }

   private class RootHelpMenu extends JMenu
   {
      RootHelpMenu()
      {
         super("Help");
         add(new JMenuItem("About...")
            {
               public void fireActionPerformed(ActionEvent e)
               {
                  JOptionPane.showMessageDialog(RootObjectBrowser.this, aboutMessage);
               }
            });
      }
   }

   private class RootMenuBar extends JMenuBar
   {
      private RootToolsMenu tools;

      RootMenuBar()
      {
         add(new RootFileMenu());
         add(tools = new RootToolsMenu());
         add(new RootHelpMenu());
      }

      void setFileOpen(boolean state)
      {
         tools.setFileOpen(state);
      }
   }

   private class RootToolsMenu extends JMenu
   {
      private JMenuItem view;

      RootToolsMenu()
      {
         super("Tools");
         add(view = new JMenuItem("View Streamer Info...")
               {
                  public void fireActionPerformed(ActionEvent e)
                  {
                     showStreamerInfo();
                  }
               });
      }

      void setFileOpen(boolean state)
      {
         view.setEnabled(state);
      }
   }
}