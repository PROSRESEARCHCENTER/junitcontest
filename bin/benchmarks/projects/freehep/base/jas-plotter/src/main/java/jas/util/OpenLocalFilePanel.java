package jas.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * This class provides a convenient way to get a file name from the user.  It is merely
 * a panel with a text field for the file name with some built in convenience items.
 * This panel does not actually open a file for you; it simply provides an easy way to get
 * a file name from the user.  Use <code>getText()</code> to get the file name selected.
 * <ul>
 *  <li>The text field is in fact a JASEditableComboBox, so recently selected files are
 *  available on a drop-down list.  The drop-down list is stored in the Application's
 *  UserProperties object according to a key that you specify for the constructor.</li>
 *  <li>A "Browse..." button opens a file dialog so that the user can select a file.
 *  The object will remember the last directory used according to the given key and
 *  open the dialog to that directory the next time it is opened.</li>
 *  <li>Optionally, you can have a "Preview..." button that opens a dialog showing the
 *  beginning of the file.</li>
 *  <li>Optionally, you can have GZip option, where the user can select whether the file
 *  in in GZip format.</li>
 *  <li>If you add an <a href="http://www.javasoft.com/products/jdk/1.2/docs/api/java.awt.event.ActionListener.html">ActionListener</a>
 *  to this class, you will receive <a href="http://www.javasoft.com/products/jdk/1.2/docs/api/java.awt.event.ActionEvent.html">ActionEvents</a>
 *  from the JASEditableComboBox.  An <a href="http://www.javasoft.com/products/jdk/1.2/docs/api/java.awt.event.ActionEvent.html">ActionEvent</a>
 *  will be sent only when the 'Enter' button is clicked in the text field.  Similarly,
 *  if you add a <a href="http://java.sun.com/products/jfc/swingdoc-api/javax.swing.event.ChangeListener.html#_top_">ChangeListener</a>
 *  to this class, you will receive <a href="http://java.sun.com/products/jfc/swingdoc-api/javax.swing.event.ChangeEvent.html#_top_">ChangeEvents</a>
 *  from the JASEditableComboBox.  A <a href="http://java.sun.com/products/jfc/swingdoc-api/javax.swing.event.ChangeEvent.html#_top_">ChangeEvent</a>
 *  will be sent every time the visible text changes.</li>
 * </ul>
 * Be sure to invoke the <code>saveState()</code> method when you are done.  This will set
 * include the selected file in the drop-down list for the next time the panel is used.
 *  @author Jonas Gifford
 *  @author Tony Johnson
 *  @see JASEditableComboBox
 *  @see UserProperties
 *  @see #getText()
 *  @see #saveState()
 */

public class OpenLocalFilePanel extends JPanel implements ActionListener, ChangeListener
{
	/**
	 * Creates an OpenLocalFilePanel component that you can add to a container.
	 *  @param includePreviewButton whether a "Preview" button should be shown
	 *  @param includeGZIP whether the GZip option should be shown
	 *  @param key the key used to store the drop-down items and the last directory for the browse dialog
	 *  @param filter sets a FileFilter for the browse dialog
	 */
	public OpenLocalFilePanel(boolean includePreviewButton, boolean includeGZIP, String key, FileFilter filter)
	{
		this(includePreviewButton, includeGZIP, key);
		m_filter = filter;
	}

	/**
	 * Creates an OpenLocalFilePanel component that you can add to a container.
	 * @param includePreviewButton whether a "Preview" button should be shown
	 *  @param includeGZIP whether the GZip option should be shown
	 *  @param key the key used to store the drop-down items and the last directory for the browse dialog
	 */
	public OpenLocalFilePanel(boolean includePreviewButton, boolean includeGZIP, String key)
	{
		m_lastLocalDirectory_Key = key +"-directory";
		m_fileName = new JASEditableComboBox(key +"-files", 4, true);
		m_fileName.addChangeListener(this);
		m_fileName.setMinWidth(250);
		m_fileName.setMaxWidth(250);
		add(m_fileName);

		m_browse = new JButton("Browse...");
		m_browse.addActionListener(this);
		m_browse.setMnemonic('B');
		add(m_browse);
		
		if (includePreviewButton)
		{
			m_view = new JButton("Preview");
			m_view.addActionListener(this);
			m_view.setMnemonic('V');
			add(m_view);
			m_view.setEnabled(false);
		}

		if (includeGZIP)
		{
			m_gzip = new JCheckBox("GZIPed");
			m_gzip.addActionListener(this);
			m_gzip.setMnemonic('Z');
			add(m_gzip);
			m_gzip.setEnabled(false);
		}

		setBorder(BorderFactory.createTitledBorder("Enter the data file name"));
		setViewEnabled();
	}

	/**
	 * The given ChangeListener will be notified when the visible
	 * text changes in the file name field.
	 */
	public void addChangeListener(ChangeListener cl)
	{
		m_fileName.addChangeListener(cl);
	}

	/**
	 * The given ActionListener will be notified when the "Enter" button
	 * is clicked in the file name text field.
	 */
	public void addActionListener(ActionListener al)
	{
		m_fileName.addActionListener(al);
	}

	/** This method is public as an implelentation side effect; do not call. */

	public final void stateChanged(ChangeEvent e)
	{
		setViewEnabled();
	}
	private void setViewEnabled()
	{
		String text = m_fileName.getText();
		boolean enable = text.length() > 0;
		if (m_view != null) m_view.setEnabled(enable);
		if (m_gzip != null) 
		{
			m_gzip.setEnabled(enable);
			m_gzip.setSelected(text.endsWith(".gz"));
		}
	}

	/** This method is public as an implelentation side effect; do not call. */

	public final void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == m_browse)
		{
			JFileChooser dlg = new JFileChooser(m_prop.getString(m_lastLocalDirectory_Key, System.getProperty("user.home")));
			dlg.setDialogTitle("Select a file");
			if (m_filter != null)
				dlg.setFileFilter(m_filter);
			if (dlg.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION)
			{
				final File file = dlg.getSelectedFile();
				m_fileName.setText(file.getAbsolutePath());
				m_prop.setString(m_lastLocalDirectory_Key, file.getParent());
			}
		}
		else if (source == m_view)
		{
			try
			{
				java.awt.Component c = this;
				while (! (c instanceof java.awt.Frame)) c = c.getParent();
				new FilePreview((java.awt.Frame) c, new File(m_fileName.getText()),
					m_gzip == null ? false : m_gzip.isSelected());
			}
			catch (Exception x)
			{
				JOptionPane.showMessageDialog(this,
					x instanceof FileNotFoundException ? "File not found" : "Could not read file",
					"Error...", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Returns the file name showing in the text field.
	 *  @return the selected file name
	 */
	public String getText()
	{
		String fileName = m_fileName.getText();
		return fileName;
	}

	/**
	 * Includes the currently selected file name in the drop-down list for the next time
	 * this class is instantiated.  It merely invokes the <code>saveState()</code> method in the
	 * JASEditableComboBox that is shown on the panel.
	 *  @see JASEditableComboBox
	 *  @see JASEditableComboBox#saveState()
	 */

	public void saveState()
	{
		m_fileName.saveState();
	}

	/**
	 * Returns whether the user has selected the GZip option.  If the GZip option was not
	 * available (i.e., in the constructor the parameter <code>includeGZIP</code> was <code>false</code>) then it will
	 * return <code>false</code>.
	 *  @return whether the GZip option was selected
	 */

	public boolean getGZIPed()
	{
		return m_gzip == null ? false : m_gzip.isSelected();
	}
	private String m_lastLocalDirectory_Key;
	private JASEditableComboBox m_fileName;
	private JButton m_browse, m_view = null;
	private JCheckBox m_gzip = null;
	final private UserProperties m_prop = UserProperties.getUserProperties();
	private FileFilter m_filter = null;
}

class FilePreview extends JDialog
{
	FilePreview(java.awt.Frame frame, File f, boolean gzip) throws IOException, FileNotFoundException
	{
		super(frame, "File preview");
		JTextArea text = new JTextArea();
		if (!gzip) text.read(new PreviewReader(new FileReader(f)), f);
		else text.read(new PreviewReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f)))), f);
		text.setEditable(false);
		JScrollPane pane = new JScrollPane(text);
		setContentPane(pane);
		pack();
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension dialogSize = getPreferredSize();
		int maxHeight = (int) (screenSize.height * 0.6);
		if (dialogSize.height > maxHeight)
			dialogSize.height = maxHeight;
		int maxWidth = (int) (screenSize.width * 0.9);
		if (dialogSize.width > maxWidth)
			dialogSize.width = maxWidth;
		else dialogSize.width += 50;
		Point position = getLocation();
		position.translate((screenSize.width-dialogSize.width)/2,
			(screenSize.height-dialogSize.height)/2);
		setLocation(position);	
		setSize(dialogSize);
		show();
	}
}
class PreviewReader extends LineNumberReader
{
	PreviewReader(Reader r)
	{
		super(r);
	}
	public int read() throws IOException 
	{
		return getLineNumber()>maxLines ? -1 : super.read();
	}
	public int read(char[] cbuf, int off, int len) throws IOException 
	{
		return getLineNumber()>maxLines ? -1 : super.read(cbuf,off,len);
	}
	public String readLine() throws IOException 
	{
		return getLineNumber()>maxLines ? null : super.readLine();
	}
	private final int maxLines = 100;
}