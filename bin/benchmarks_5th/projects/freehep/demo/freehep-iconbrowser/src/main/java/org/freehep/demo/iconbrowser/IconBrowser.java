/*
 * IconBrowser.java
 *
 * Created on March 6, 2001, 4:48 PM
 */

package org.freehep.demo.iconbrowser;

import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.freehep.application.*;
import org.freehep.application.services.FileAccess;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.swing.layout.FlowScrollLayout;
import org.freehep.swing.popup.HasPopupItems;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.*;
import org.freehep.util.commanddispatcher.BooleanCommandState;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;


/**
 * A simple GUI based browser for Icon Collections.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: IconBrowser.java 10506 2007-01-30 22:48:57Z duns $
 */
public class IconBrowser extends Application implements TreeSelectionListener
{
   private JTree tree = new JTree();
   private IconPanel iconPanel;
   private JScrollPane scroll = new JScrollPane();
   private IconMagnifier magnifier = new IconMagnifier();
   private JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,new JScrollPane(tree),new JScrollPane(magnifier));
   private JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,split2,scroll);
   private boolean showNames;
   private DefaultMutableTreeNode currentFile;
   private IconLabel currentIcon;
   private FileTree fileTree = new FileTree();
   private String[] builtIn;
   private ProgressMeter meter = new ProgressMeter(false);

   /** Creates new IconBrowser */
   public IconBrowser() throws Exception
   {
      super("IconBrowser");
      add(split);

      Properties user = getUserProperties();
      int pos = PropertyUtilities.getInteger(user,"splitPosition",0);
      if (pos > 0) split.setDividerLocation(pos);
      pos = PropertyUtilities.getInteger(user,"splitPosition2",0);
      if (pos > 0) split2.setDividerLocation(pos);
      showNames = PropertyUtilities.getBoolean(user,"showNames",false);
      magnifier.setShowGrid(PropertyUtilities.getBoolean(user,"showGrid",true));
      magnifier.setShowChecks(PropertyUtilities.getBoolean(user,"showChecks",true));
      magnifier.setMagnification(PropertyUtilities.getInteger(user,"magnification",5));

      tree.setModel(fileTree);
      tree.setRootVisible(false);
      tree.setCellRenderer(new IconTreeRenderer());

      tree.addTreeSelectionListener(this);
      scroll.getViewport().setBackground(Color.white);
      getStatusBar().add(meter);
   }
   protected void init()
   {
      // Look for built-in archives

      InputStream in = getClass().getResourceAsStream("/IconBrowser.list");
      if (in != null)
      {
         try
         {
            Vector v = new Vector();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
            for (;;)
            {
               String line = reader.readLine();
               if (line == null) break;
               v.add(line);
            }
            reader.close();
            builtIn = new String[v.size()];
            v.copyInto(builtIn);
         }
         catch (IOException x)
         {
         }
      }

      boolean atLeastOneFileOpenFailed = false;
      String[] files = PropertyUtilities.getStringArray(getUserProperties(),"openFiles",null);
      if (files != null)
      {
         for (int i=0; i<files.length; i++)
         {
            String file = files[i];
            setStatusMessage("Scanning "+fileName(file));
            try
            {
               if (file.startsWith("builtin:/"))
               {
                  DefaultMutableTreeNode node = new ZipListTree(file.substring(9));
                  fileTree.addArchive(node);
               }
               else
               {
                  DefaultMutableTreeNode node = new ZipTree(new ZipFile(files[i]));
                  fileTree.addArchive(node);
               }
            }
            catch (SecurityException x)
            {
               // In case we are in JNLP
               atLeastOneFileOpenFailed = true;
            }
            catch (IOException x)
            {
               // Probably the file doesnt exist anymore
               atLeastOneFileOpenFailed = true;
            }
         }
      }
      if (atLeastOneFileOpenFailed) return;
      // restore open tree nodes
      try
      {
         String openPaths = PropertyUtilities.getString(getUserProperties(),"openPaths",null);
         if (openPaths != null)
         {
            StringTokenizer tk = new StringTokenizer(openPaths,",");
            int[] paths = new int[tk.countTokens()];
            for (int i=0; i<paths.length; i++) paths[i] = Integer.parseInt(tk.nextToken());

            Arrays.sort(paths);
            for (int i=0; i<paths.length; i++)
            {
               int row = paths[i];
               if (row < 0) continue;
               tree.expandRow(row);
            }
         }
         int selRow = PropertyUtilities.getInteger(getUserProperties(),"selectedRow",-1);
         if (selRow >= 0) tree.setSelectionRow(selRow);
      }
      catch (NumberFormatException x)
      {
         // o well, we tried
      }
   }
   public static void main(String[] argv) throws Exception
   {
      new IconBrowser().createFrame(argv).setVisible(true);
   }
   public void onSaveIcon()
   {
      Runnable run = new Runnable()
      {
         public void run()
         {
            try
            {
               SaveAs saveAs = (SaveAs) Class.forName("org.freehep.demo.iconbrowser.SaveAsDialog").newInstance();
               String name = fileName(currentIcon.getToolTipText());
               saveAs.showExportDialog(IconBrowser.this,"Save Icon...",currentIcon,name);
            }
            catch (Throwable t)
            {
               error("Error creating export dialog",t);
            }
         }
      };
      whenAvailable("graphicsio",run);
   }
   public void enableSaveIcon(CommandState state)
   {
      state.setEnabled(currentIcon != null);
   }
   public void onShowNames(boolean state)
   {
      showNames = state;
      if (iconPanel != null)
      {
         iconPanel.showNames(state);
         iconPanel.revalidate();
      }
      getCommandProcessor().setChanged();
   }
   public void enableShowNames(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(showNames);
   }
   public void onShowGrid(boolean state)
   {
      magnifier.setShowGrid(state);
   }
   public void enableShowGrid(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(magnifier.getShowGrid());
   }
   public void onShowChecks(boolean state)
   {
      magnifier.setShowChecks(state);
   }
   public void enableShowChecks(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(magnifier.getShowChecks());
   }
   public void on2x(boolean state)
   {
      if (state) magnifier.setMagnification(2);
      getCommandProcessor().setChanged();
   }
   public void enable2x(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(magnifier.getMagnification() == 2);
   }
   public void on3x(boolean state)
   {
      if (state) magnifier.setMagnification(3);
      getCommandProcessor().setChanged();
   }
   public void enable3x(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(magnifier.getMagnification() == 3);
   }
   public void on5x(boolean state)
   {
      if (state) magnifier.setMagnification(5);
      getCommandProcessor().setChanged();
   }
   public void enable5x(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(magnifier.getMagnification() == 5);
   }
   public void on10x(boolean state)
   {
      if (state) magnifier.setMagnification(10);
       getCommandProcessor().setChanged();
   }
   public void enable10x(BooleanCommandState state)
   {
      state.setEnabled(true);
      state.setSelected(magnifier.getMagnification() == 10);
   }
   public void onCopyIcon()
   {
      IconSelection t = new IconSelection(currentIcon);
      getServiceManager().setClipboardContents(t);
   }
   public void enableCopyIcon(CommandState state)
   {
      state.setEnabled(currentIcon != null);
   }
   public void onLicense()
   {
      showHelpTopic("License");
   }
   public void onSearch()
   {
      String search = RecentItemTextField.showInputDialog(this,"Search for: ","Icon Search");
      if (search != null)
      {
         search = search.toLowerCase();
         IconPanel iconPanel = new IconPanel();
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) fileTree.getRoot();
         Enumeration e = root.depthFirstEnumeration();
         while (e.hasMoreElements())
         {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() instanceof IconDirectory)
            {
               IconDirectory id = (IconDirectory) node.getUserObject();
               for (int i=0; i<id.getNEntries(); i++)
               {
                  String name = id.getEntryName(i).toLowerCase();

                  if (name.indexOf(search) >= 0)
                  {
                     DefaultMutableTreeNode zipNode = (DefaultMutableTreeNode) node.getPath()[1];
                     Icon icon = id.getEntryIcon(i);
                     iconPanel.add(new IconLabel(id.getEntryName(i),icon,showNames));
                  }
               }
            }
         }
         if (iconPanel.getComponentCount() == 0)
         {
            error("No matches for "+search);
         }
         else
         {
            tree.clearSelection();
            setIconPanel(iconPanel);
         }
      }
   }
   public void onOpen()
   {
      FileFilter[] filters = { new ExtensionFileFilter(new String[]{"jar","zip"},"Image Icon Libraries") };
      FileAccess file = getServiceManager().openFileDialog(filters,null,"openFile");
      try
      {
         if (file != null)
         {
            setStatusMessage("Scanning "+file.getName());
            try
            {
               DefaultMutableTreeNode node = new ZipTree(new ZipFile(file.getFile()));
               fileTree.addArchive(node);
            }
            catch (SecurityException x)
            {
               DefaultMutableTreeNode node = new ZipInputStreamTree(file.getName(),new ZipInputStream(file.getInputStream()));
               fileTree.addArchive(node);
            }
         }
      }
      catch (IOException x)
      {
         error("Error opening file",x);
      }
   }
   public void onOpenFromClassPath() throws Exception
   {
      String sel = (String) JOptionPane.showInputDialog(this,
      "Select Icon Collection","Open Icon Collection",JOptionPane.QUESTION_MESSAGE,
      null,builtIn,builtIn[0]);
      if (sel != null)
      {
         setStatusMessage("Scanning "+sel);
         fileTree.addArchive(new ZipListTree(sel));
         getCommandProcessor().setChanged();
      }
   }
   public void enableOpenFromClassPath(CommandState state)
   {
      state.setEnabled(builtIn != null && builtIn.length > 0);
   }
   public void onClose()
   {
      DefaultMutableTreeNode old = currentFile;
      currentFile = null;
      fileTree.removeNodeFromParent(old);
      getCommandProcessor().setChanged();
      IconArchive archive = (IconArchive) old.getUserObject();
      archive.close();
   }
   public void enableClose(CommandState state)
   {
      state.setEnabled(currentFile!=null);
   }
   public void onPrintPreview()
   {
      Pageable pageable = iconPanel.getPageable(getServiceManager().getDefaultPage());
      PrintPreview pp =  createPrintPreview();
      pp.setPageable(pageable);
      showDialog(pp.createDialog(this),"PrintPreview");
   }
   public void enablePrintPreview(CommandState state)
   {
      state.setEnabled(iconPanel != null && iconPanel.getComponentCount()>0);
   }
   public void onPrint()
   {
      Pageable pageable = iconPanel.getPageable(getServiceManager().getDefaultPage());
      getServiceManager().print(pageable);
   }
   public void enablePrint(CommandState state)
   {
      state.setEnabled(iconPanel != null && iconPanel.getComponentCount()>0);
   }
   void setCurrentIcon(IconLabel label)
   {
      magnifier.setIcon(label.getIcon());
      currentIcon = label;
      getCommandProcessor().setChanged();
   }
   void setIconPanel(IconPanel p)
   {
      iconPanel = p;
      scroll.setViewportView(p);
      p.validate();
      getCommandProcessor().setChanged();
   }
   public void valueChanged(TreeSelectionEvent event)
   {
      if (!event.isAddedPath())
      {
         currentFile = null;
         setIconPanel(new IconPanel());
         return;
      }
      TreePath path = event.getPath();
      currentFile = (DefaultMutableTreeNode) path.getPathComponent(1);
      getCommandProcessor().setChanged();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      if (node.getUserObject() instanceof IconDirectory)
      {
         final IconDirectory dir = (IconDirectory) node.getUserObject();
         final String name = dir.getName();

         final BoundedRangeModel bdr = new DefaultBoundedRangeModel(0,0,0,dir.getNEntries());
         meter.setModel(bdr);

         Thread t = new Thread()
         {
            public void run()
            {
               final IconPanel iconPanel = new IconPanel();
               for (int n=0, i=0; i < dir.getNEntries(); i++)
               {
                  Icon icon = dir.getEntryIcon(i);
                  iconPanel.add(new IconLabel(dir.getEntryName(i),icon,showNames));
                  bdr.setValue(++n);
               }
               SwingUtilities.invokeLater(new Runnable()
               {
                  public void run()
                  {
                     setIconPanel(iconPanel);
                  }
               });
            }
         };
         t.start();
      }
   }

   protected void saveUserProperties()
   {
      Properties user = getUserProperties();
      PropertyUtilities.setBoolean(user,"showNames",showNames);
      PropertyUtilities.setInteger(user,"splitPosition",split.getDividerLocation());
      PropertyUtilities.setInteger(user,"splitPosition2",split2.getDividerLocation());
      PropertyUtilities.setBoolean(user,"showGrid",magnifier.getShowGrid());
      PropertyUtilities.setBoolean(user,"showChecks",magnifier.getShowChecks());
      PropertyUtilities.setInteger(user,"magnification",magnifier.getMagnification());
      // Save any open files
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) fileTree.getRoot();
      String[] files = new String[root.getChildCount()];
      for (int i=0; i<files.length;i++)
      {
         DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
         files[i] = ((IconDirectory) child.getUserObject()).getName();
      }
      PropertyUtilities.setStringArray(user,"openFiles",files);

      // Remember which nodes of the tree were open
      StringBuffer openPaths = new StringBuffer();
      Enumeration e = root.depthFirstEnumeration();
      while (e.hasMoreElements())
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
         TreePath path = new TreePath(node.getPath());
         if (tree.isExpanded(path))
         {
            if (openPaths.length()>0) openPaths.append(',');
            openPaths.append(tree.getRowForPath(path));
         }
      }
      user.setProperty("openPaths",openPaths.toString());
      // Finally save the open folder, if any
      int[] selRows = tree.getSelectionRows();
      int selRow = selRows != null ? selRows[0] : -1;
      PropertyUtilities.setInteger(user,"selectedRow",selRow);
      super.saveUserProperties();
   }
   private class FileTree extends DefaultTreeModel
   {
      FileTree()
      {
         super(new DefaultMutableTreeNode("root"));
      }
      void addArchive(DefaultMutableTreeNode archive)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
         fileTree.insertNodeInto(archive,root,root.getChildCount());
         tree.expandPath(new TreePath(new Object[]
         {root,archive}));
      }
   }
   /**
    * returns the directory part of fullName, including the trailing slash
    */
   static String dirName(String fullName)
   {
      int l = fullName.length();
      if (fullName.endsWith("/")) l--;
      int pos = fullName.lastIndexOf('/',l-1);
      if (pos < 0) return null;
      else return fullName.substring(0,pos+1);
   }
   /**
    * returns the file name, minus the directory
    */
   static String fileName(String fullName,String separator)
   {
      int l = fullName.length();
      if (fullName.endsWith(separator)) l--;
      int pos = fullName.lastIndexOf(separator,l-1);
      if (pos < 0) return fullName.substring(0,l);
      return fullName.substring(pos+1,l);
   }
   static String fileName(String fullName)
   {
      return fileName(fullName,"/");
   }
   public static class IconSelection implements Transferable
   {
      private DataFlavor imageFlavor;
      private DataFlavor stringFlavor;
      private Vector supportedFlavors = new Vector();
      private IconLabel icon;

      public IconSelection(IconLabel icon)
      {
         imageFlavor = getFlavor("imageFlavor");
         stringFlavor = getFlavor("stringFlavor");
         if (imageFlavor != null) supportedFlavors.add(imageFlavor);
         if (stringFlavor != null) supportedFlavors.add(stringFlavor);
         this.icon = icon;
      }
      private DataFlavor getFlavor(String flavor)
      {
         try
         {
            // For JDK 1.3 compatibility
            Class k = DataFlavor.class;
            Field field = k.getField(flavor);
            return (DataFlavor) field.get(null);
         }
         catch (Throwable t)
         {
            return null;
         }
      }
      public DataFlavor [] getTransferDataFlavors()
      {
         DataFlavor[] result = new DataFlavor[supportedFlavors.size()];
         supportedFlavors.toArray(result);
         return result;
      }
      public boolean isDataFlavorSupported(DataFlavor parFlavor)
      {
         return supportedFlavors.contains(parFlavor);
      }
      public Object getTransferData(DataFlavor parFlavor) throws UnsupportedFlavorException
      {
         if      (parFlavor.equals(imageFlavor)) return ((ImageIcon) icon.getIcon()).getImage();
         else if (parFlavor.equals(stringFlavor)) return icon.getToolTipText();
         else throw new UnsupportedFlavorException(imageFlavor);
      }
   }
   private class IconPanel extends JPanel implements Scrollable, HasPopupItems
   {
      IconPanel()
      {
         FlowScrollLayout l = new FlowScrollLayout(scroll);
         l.setHgap(0);
         l.setVgap(0);
         setLayout(l);
         setBackground(Color.white);
      }
      public Dimension getPreferredScrollableViewportSize()
      {
         return getPreferredSize();
      }
      public int getScrollableUnitIncrement(Rectangle visibleRect,
      int orientation,
      int direction)
      {
         return 1;
      }
      public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation,
      int direction)
      {
         return 10;
      }
      public boolean getScrollableTracksViewportWidth()
      {
         return true;
      }
      public boolean getScrollableTracksViewportHeight()
      {
         return false;
      }
      void showNames(boolean state)
      {
         Component[] comps = getComponents();
         for (int i=0; i<comps.length; i++)
         {
            IconLabel label = (IconLabel) comps[i];
            label.setShowText(state);
         }
      }
      public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point point)
      {
         if (source != this) return menu;
         return getXMLMenuBuilder().getPopupMenu("panelMenu");
      }

      Pageable getPageable(final PageFormat pf)
      {
         // Figure out which is the first icon on each line, and how many pages
         // we need.

         int xsize = (int) pf.getImageableWidth();
         int ysize = (int) pf.getImageableHeight();
         final Vector pages = new Vector();
         final Vector lines = new Vector();
         pages.addElement(new Integer(0));
         lines.addElement(new Integer(0));
         int maxHeight = 0;
         int totWidth = 0;
         int totHeight = 0;

         int n = getComponentCount();
         for (int i=0; i<n; i++)
         {
            IconLabel l = (IconLabel) getComponent(i);
            int height = l.getHeight();
            int width = l.getWidth();
            if (totWidth + width > xsize) // new line?
            {
               lines.addElement(new Integer(i));
               totHeight += maxHeight;
               maxHeight = 0;
               totWidth = width;

               if (totHeight > ysize)
               {
                  pages.addElement(new Integer(lines.size()));
                  totHeight = maxHeight;
               }
            }
            else
            {
               totWidth += width;
               if (height > maxHeight) maxHeight = height;
            }
         }
         final Printable printable = new Printable()
         {
            public int print(Graphics graphics,PageFormat pageFormat,int pageIndex) throws PrinterException
            {
               Graphics2D g2 = (Graphics2D) graphics;
               g2.translate(pageFormat.getImageableX(),pageFormat.getImageableY());
               if (pageIndex >= pages.size()) return NO_SUCH_PAGE;
               int firstLine = ((Integer) pages.elementAt(pageIndex)).intValue();
               int lastLine = pageIndex+1 == pages.size() ? lines.size() : ((Integer) pages.elementAt(pageIndex+1)).intValue();
               for (int l = firstLine; l < lastLine; l++)
               {
                  int firstLabel = ((Integer) lines.elementAt(l)).intValue();
                  int lastLabel = l+1 == lines.size() ? getComponentCount() : ((Integer) lines.elementAt(l+1)).intValue();
                  double maxHeight = 0;
                  double offset = 0;
                  for (int c = firstLabel; c<lastLabel; c++)
                  {
                     IconLabel label = (IconLabel) getComponent(c);
                     label.print(g2);
                     double w = label.getWidth();
                     double h = label.getHeight();
                     offset += w;
                     if (h > maxHeight) maxHeight = h;
                     g2.translate(w,0);
                  }
                  g2.translate(-offset,maxHeight);
               }
               return PAGE_EXISTS;
            }
         };
         return new Pageable()
         {
            public Printable getPrintable(int page)
            {
               return printable;
            }
            public PageFormat getPageFormat(int page)
            {
               return pf;
            }
            public int getNumberOfPages()
            {
               return pages.size();
            }
         };

      }     
   }
   private class IconLabel extends JLabel implements HasPopupItems
   {
      IconLabel(String name, Icon icon, boolean showText)
      {
         super(icon);
         setToolTipText(name);
         setHorizontalTextPosition(CENTER);
         setVerticalTextPosition(BOTTOM);
         setBorder(new EmptyBorder(3,3,3,3));
         enableEvents(AWTEvent.MOUSE_EVENT_MASK);
         setShowText(showText);
      }
      void setShowText(boolean show)
      {
         if (show) setText(fileName(getToolTipText()));
         else setText(null);
      }
      protected void processMouseEvent(MouseEvent e)
      {
         int id = e.getID();
         if (id == e.MOUSE_ENTERED)
         {
            Icon icon  = getIcon();
            setStatusMessage(getToolTipText()+" ("+icon.getIconWidth()+"x"+icon.getIconHeight()+")");
            paintBorder = true;
            repaint();
         }
         else if (id == e.MOUSE_EXITED)
         {
            paintBorder = false;
            repaint();
         }
         else if (id == e.MOUSE_PRESSED)
         {
            if (currentIcon != this)
            {
               JLabel oldIcon = currentIcon;
               setCurrentIcon(this);
               if (oldIcon != null) oldIcon.repaint();
               repaint();
            }
         }
         super.processMouseEvent(e);
      }
      protected void printBorder(Graphics g)
      {
         // no border when printing
      }
      protected void paintBorder(Graphics g)
      {
         if (paintBorder)
         {
            g.setColor(Color.red);
            g.drawRect(2,2,getWidth()-5,getHeight()-5);
         }
         if (currentIcon == this)
         {
            g.setColor(Color.blue);
            g.drawRect(1,1,getWidth()-3,getHeight()-3);
            g.drawRect(0,0,getWidth()-1,getHeight()-1);
         }
      }
      public JPopupMenu modifyPopupMenu(JPopupMenu menu,Component source, Point p)
      {
         return getXMLMenuBuilder().getPopupMenu("labelMenu");
      }
      
      private boolean paintBorder;
   }
   private class IconTreeRenderer extends DefaultTreeCellRenderer
   {
      public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus)
      {
         super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
         if (value instanceof DefaultMutableTreeNode)
         {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object user = node.getUserObject();
            if (user instanceof IconDirectory)
            {
               IconDirectory id = (IconDirectory) user;
               if (id instanceof IconArchive) setIcon(zipIcon);
               else if (expanded) setIcon(openFolderIcon);
               else if (sel && id.getNEntries() > 0) setIcon(openFolderIcon);
               else setIcon(closedFolderIcon);
            }
         }
         return this;
      }
   }
   private class IconMagnifier extends JPanel implements HasPopupItems
   {
      private ImageIcon icon;
      private boolean showGrid = true;
      private boolean showChecks = true;
      private int mag = 5;
      IconMagnifier()
      {
         setBackground(Color.white);
      }
      void setIcon(Icon icon)
      {
         this.icon = (ImageIcon) icon;
         if (icon != null)
         {
            setPreferredSize(new Dimension(mag*icon.getIconWidth(),mag*icon.getIconHeight()));
            revalidate();
         }
         repaint();
      }
      void setShowGrid(boolean value)
      {
         showGrid = value;
         repaint();
      }
      boolean getShowGrid()
      {
         return showGrid;
      }
      void setShowChecks(boolean value)
      {
         showChecks = value;
         repaint();
      }
      boolean getShowChecks()
      {
         return showChecks;
      }
      int getMagnification()
      {
         return mag;
      }
      void setMagnification(int value)
      {
         mag = value;
         if (icon != null)
         {
            setPreferredSize(new Dimension(mag*icon.getIconWidth(),mag*icon.getIconHeight()));
            revalidate();
         }
         repaint();
      }
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         if (icon != null)
         {
            if (showChecks)
            {
               for (int i=0; i<icon.getIconWidth(); i++)
               {
                  for (int j=0; j<icon.getIconHeight(); j++)
                  {
                     g.setColor((i+j)%2 == 0 ? Color.lightGray : Color.darkGray);
                     int x = i*mag;
                     int y = j*mag;
                     g.fillRect(x,y,mag,mag);
                  }
               }
            }

            g.drawImage(icon.getImage(),0,0,mag*icon.getIconWidth(),mag*icon.getIconHeight(),this);

            if (showGrid)
            {
               g.setColor(Color.gray);
               for (int i=0; i<=icon.getIconWidth(); i++)
               {
                  int x = i*mag;
                  g.drawLine(x,0,x,mag*icon.getIconHeight());
               }
               for (int i=0; i<=icon.getIconHeight(); i++)
               {
                  int y = i*mag;
                  g.drawLine(0,y,mag*icon.getIconWidth(),y);
               }
            }
         }
      }

      public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p)
      {
         return getXMLMenuBuilder().getPopupMenu("magnifierMenu");
      }
      
      
   }
   private final static Icon zipIcon = ImageHandler.getIcon(IconBrowser.class.getResource("/org/javalobby/icons/20x20/Package.gif"));
   private final static Icon openFolderIcon = ImageHandler.getIcon(IconBrowser.class.getResource("/org/javalobby/icons/20x20/OpenProject.gif"));
   private final static Icon closedFolderIcon = ImageHandler.getIcon(IconBrowser.class.getResource("/org/javalobby/icons/20x20/Project.gif"));

 }
interface SaveAs
{
   public void showExportDialog(Component parent, String title, Component target, String name);
}
