package jas.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * A class which allows a user to select directories or files, similar to JFileChooser,
 * except that it display files as a tree, and is better suited to selecting directories
 * than the current file chooser (see java bug id 4239219).
 * 
 * TODO: Understand issue with moving mouse between double clicks 
 * (seems like MOUSE_PRESSED and MOUSE_RELEASED events are generated, but not MOUSE_CLICKED)
 * See Bug ID 4218549
 * 
 * @author Tony Johnson (tony_johnson@slac.stanford.edu)
 */
public class JDirectoryChooser extends JComponent
{
	/**
	 * Create a JDirectoryChooser with the default FileSystemView
	 */
	public JDirectoryChooser()
	{
		this(FileSystemView.getFileSystemView());
	}
	/**
	 * Create a JDirectoryChooser with the default FileSystemView 
	 * @param currentDirectory The directory to which the tree is initially set
	 */
	public JDirectoryChooser(File currentDirectory)
	{
		this();
		setCurrentDirectory(currentDirectory);
	}
	/**
	 * Create a JDirectoryChooser with the default FileSystemView 
	 * @param currentDirectory The directory to which the tree is initially set
	 */
	public JDirectoryChooser(String currentDirectory)
	{
		this();
		if (currentDirectory != null) setCurrentDirectory(new File(currentDirectory));
	}
	/**
	 * Create a JDirectoryChooser
	 * @param currentDirectory The directory to which the tree is initially set
	 * @param view The FileSystemView to use
	 */
	public JDirectoryChooser(File currentDirectory, FileSystemView view)
	{
		this(view);
		setCurrentDirectory(currentDirectory);	
	}
	/**
	 * Create a JDirectoryChooser
	 * @param currentDirectory The directory to which the tree is initially set
	 * @param view The FileSystemView to use
	 */
	public JDirectoryChooser(String currentDirectory, FileSystemView view)
	{
		this(view);
		setCurrentDirectory(new File(currentDirectory));	
	}
	/**
	 * Create a JDirectoryChooser
	 * @param view The FileSystemView to use
	 */
	public JDirectoryChooser(FileSystemView view)
	{
		setup(view);
		ButtonListener al = new ButtonListener();
		
		tree = new JTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new FileRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(al);
		
		approve.addActionListener(al);
		cancel.addActionListener(al);
		approve.setEnabled(false);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tree));
		
		JPanel p = new JPanel(new FlowLayout());
		p.add(approve);
		p.add(cancel);
		add(p,BorderLayout.SOUTH);
	}
	public void addNotify()
	{
		tree.setModel(model);
      makeCurrentDirectoryVisible();
      super.addNotify();
   }
   private void makeCurrentDirectoryVisible()
   {
		if (currentDirectory != null)
		{
			File dir = currentDirectory;
			FileSystemView view = foo.getFileSystemView();
			Vector v = new Vector();
			while (true)
			{
				if (dir == null) return; // Something wrong, ignore dir
				v.addElement(dir);
				if (view.isRoot(dir)) break;
            dir = view.getParentDirectory(dir);
			}
			Object[] files = new File[v.size()+1];
			Object node = model.getRoot();
			files[0] = node;
			for (int i=1; i<files.length; i++) 
			{
				File here = (File) v.elementAt(files.length - i -1);
				int index = model.getIndexOfChild(node,here);
            if (index < 0) return;
				files[i] = node = model.getChild(node,index);
			}
			TreePath path = new TreePath(files);
			tree.setSelectionPath(path);
			tree.scrollPathToVisible(path);
		}
	}
	/**
	 * Set the directory to which the tree is to open
	 */
	public void setCurrentDirectory(File dir)
	{
		currentDirectory = dir;
	}
	/**
	 * Set a filter to control which files are displayed in the tree
	 */
	public void setFileFilter(FileFilter fileFilter)
	{
		foo.setFileFilter(fileFilter);
		model.changed();
	}
	/**
	 * Get the current file filter
	 * @return The current FileFilter or null if none set
	 */
	public FileFilter getFileFilter()
	{
		return foo.getFileFilter();
	}
	/**
	 * Select whether to show "hidden" files in the tree
	 * @param hide True if hidden files should not be shown
	 */
	public void setFileHidingEnabled(boolean hide)
	{
		foo.setFileHidingEnabled(hide);
		model.changed();
	}
	/**
	 * Test if file hiding is enabled
	 * @return true if file hiding is enabled
	 * @see #setFileHidingEnabled
	 */
	public boolean isFileHidingEnabled()
	{
		return foo.isFileHidingEnabled();
	}
	/**
	 * Set the file selection mode. Valid modes are
	 * @param mode either JFileChooser.DIRECTORIES_ONLY or JFileChooser.FILES_AND_DIRECTORIES (the default)
	 * @see javax.swing.JFileChooser#setFileSelectionMode
	 */
	public void setFileSelectionMode(int mode)
	{
		foo.setFileSelectionMode(mode);
		model.changed();
	}
	/**
	 * Test the file selection mode
	 * @return The current file selection mode
	 * @see #setFileSelectionMode
	 */
	public int getFileSelectionMode()
	{
		return foo.getFileSelectionMode();
	}
	/**
	 * Sets the filechooser to allow multiple file selections. 
	 */
	public void setMultiSelectionEnabled(boolean enable)
	{
		foo.setMultiSelectionEnabled(enable);
		tree.getSelectionModel().setSelectionMode(enable ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION :
														   TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	/**
	 * Returns true if multi-file selection is enabled.
	 */
	public boolean isMultiSelectionEnabled()
	{
		return foo.isMultiSelectionEnabled();
	}
	/**
	 * Returns the currently selected file (or the first selected file if multiple files are selected)
	 */
	public File getSelectedFile()
	{
		return (File) tree.getLastSelectedPathComponent();
	}
	/**
	 * Returns a list of selected files if the filechooser is set to allow multi-selection.
	 */
	public File[] getSelectedFiles()
	{
		TreePath[] paths =  tree.getSelectionPaths();
		File[] result = new File[paths.length];
		for (int i=0; i<paths.length; i++) result[i] = (File) paths[i].getLastPathComponent();
		return result;
	}
	protected void setup(FileSystemView view)
	{
		foo.setFileSystemView(view);		
	}
	public void updateUI()
	{
		super.updateUI();
		foo.updateUI();
	}
	/**
	 * Popup up a modal dialog containing the JDirectoryChooser
	 * @param parent The parent of the dialog box
	 * @return either JFileChooser.CANCEL_OPTION or JFileChooser.APPROVE_OPTION
	 * @see javax.swing.JFileChooser#showDialog
	 */
	public int showDialog(Component parent) 
	{
		Frame frame = parent instanceof Frame ? (Frame) parent
					: (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);

		String title = null;

		title = foo.getDialogTitle();
		if (title == null) foo.getUI().getDialogTitle(foo);

		returnValue = foo.CANCEL_OPTION;
		
		dialog = new JDialog(frame, title, true);
		dialog.getContentPane().add(this,BorderLayout.CENTER);
		
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.show();
		
		return returnValue;
	}
	/**
	 * Sets the string that goes in the FileChooser window's title bar
	 */
	public void setDialogTitle(String dialogTitle)
	{
		foo.setDialogTitle(dialogTitle);
	}
	/**
	 * Gets the string that goes in the FileChooser's titlebar
	 */
	public String getDialogTitle()
	{
		return foo.getDialogTitle();
	}
	/**
	 * Sets the file view to used to retrieve UI information, such as the icon that 
	 * represents a file or the type description of a file.
	 */
	public void setFileView(FileView fileView)
	{
		foo.setFileView(fileView);
	}
	/**
	 * Returns the current file view.
	 */
	public FileView getFileView()
	{
		return foo.getFileView();
	}
	private FileTreeModel model = new FileTreeModel();
	private JFileChooser foo = new JFileChooser();
	private JButton approve = new JButton("Select");
	private JButton cancel = new JButton("Cancel");
	private File currentDirectory = null;
	private JTree tree;
	private JDialog dialog;
	private int returnValue;
	
	public static int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	public static int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;	
		
	private class ButtonListener implements ActionListener, TreeSelectionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == approve) returnValue = foo.APPROVE_OPTION;
			dialog.hide();
			dialog = null;
		}
		public void valueChanged(TreeSelectionEvent e)
		{
			approve.setEnabled(tree.getSelectionCount() > 0); 
		}
	}
	
	private class FileRenderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			Component comp = super.getTreeCellRendererComponent(tree,node,selected,expanded,leaf,row,hasFocus);
			if (comp instanceof JLabel && node instanceof File)
			{
				JLabel label = (JLabel) comp;
				File f = (File) node;
				label.setText(foo.getName(f));
				label.setIcon(foo.getIcon(f));
			}
			return comp;
		}
	}	
	private class FileTreeModel implements TreeModel
	{
		public Object getChild(Object node, int index)
		{
			return children(node)[index];
		}
		public int getChildCount(Object node)
		{
			return children(node).length;
		}
		public int getIndexOfChild(Object node, Object child)
		{
			File[] children = children(node);

			for (int i=0; i<children.length; i++) 
				if (children[i].equals(child)) return i;
         
         return -1;
		}
		public Object getRoot()
		{
			return root;
		}
		public boolean isLeaf(Object node)
		{
			//return children(node).length == 0;
			return !(node == root || ((File) node).isDirectory());
		}
		public void valueForPathChanged(TreePath p1, Object p2)
		{
			// We dont allow model changes
		}
		/**
		 * Adds a listener for the TreeModelEvent posted after the tree changes.
		 *
		 * @see     #removeTreeModelListener
		 * @param   l       the listener to add
		 */
		public void addTreeModelListener(TreeModelListener l) 
		{
		    listenerList.add(TreeModelListener.class, l);
		}

		/**
		 * Removes a listener previously added with <B>addTreeModelListener()</B>.
		 *
		 * @see     #addTreeModelListener
		 * @param   l       the listener to remove
		 */  
		public void removeTreeModelListener(TreeModelListener l) 
		{
		    listenerList.remove(TreeModelListener.class, l);
		}
		private File[] children(Object node)
		{
			for (int i=0; i<CACHE_SIZE; i++)
			{
				if (node == cachedNode[i]) return cachedChildren[i];
			}
			
			Window w = null;
			Cursor oldCursor = null;
			if (tree != null && tree.isVisible())
			{
				w = (Window) SwingUtilities.getAncestorOfClass(Window.class,tree);
				if (w != null) 
				{
					oldCursor = w.getCursor();
					w.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			}
			try
			{
				FileSystemView view = foo.getFileSystemView();
				File dir = (File) node;
				boolean isRoot = (dir == root);
				File[] children = isRoot ? view.getRoots() : view.getFiles(dir,foo.isFileHidingEnabled());
				if (!isRoot)
				{
					boolean dirOnly = foo.getFileSelectionMode() == foo.DIRECTORIES_ONLY;
					if (foo.getFileFilter() != null || dirOnly)
					{
						Vector v = new Vector();
						for (int i=0; i<children.length; i++)
						{
							File f = children[i];
							if (dirOnly && !f.isDirectory()) continue;
							if (foo.accept(f)) v.addElement(children[i]);
						}
						if (v.size() != children.length) 
						{
							children = new File[v.size()];
							v.copyInto(children);
						}
					}
				}
				cachedNode[nextCache] = dir;
				cachedChildren[nextCache] = children;
				nextCache = (nextCache + 1) % CACHE_SIZE;
				return children;
			}
			finally
			{
				if (w != null) w.setCursor(oldCursor);
			}
		}
		void changed()
		{
			cachedNode = new File[CACHE_SIZE];
			cachedChildren = new File[CACHE_SIZE][];
			fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(root)));
		}
 		protected void fireTreeStructureChanged(TreeModelEvent e)
		{
		    Object[] listeners = listenerList.getListenerList();
		    // Process the listeners last to first, notifying
		    // those that are interested in this event
		    for (int i = listeners.length-2; i>=0; i-=2) 
			{
		        if (listeners[i]==TreeModelListener.class) 
				{
		            ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
		        }          
		    }
		}
		private EventListenerList listenerList = new EventListenerList();
		private final int CACHE_SIZE = 10;
		private File[] cachedNode = new File[CACHE_SIZE]; 
		private File[][] cachedChildren = new File[CACHE_SIZE][];
		private int nextCache = 0;
	}
	private static File root = new java.io.File("root");
	// Test code
	public static void main(String argv[])
	{
      final JTextField tf = new JTextField(40);
      JButton b = new JButton("Show Browser")
		{
			public void fireActionPerformed(ActionEvent e)
			{
				JDirectoryChooser dlg = new JDirectoryChooser(tf.getText());
				dlg.showDialog(this);
            if (dlg.getSelectedFile() != null) tf.setText(dlg.getSelectedFile().toString());
			}
		};
		JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.getContentPane().add(b,BorderLayout.CENTER);
      frame.getContentPane().add(tf,BorderLayout.SOUTH);
		frame.pack();
		frame.show();
	}
}
