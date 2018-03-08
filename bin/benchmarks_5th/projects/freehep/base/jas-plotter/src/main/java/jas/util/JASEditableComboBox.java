package jas.util;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is ideal if you want to have a text field for user input but
 * also have a list of recently selected items available to choose from.
 * Some notes on using this class:
 * <ul>
 *  <li>The default button will be clicked on the root pane of this
 *  component if the value of the constructor parameter <code>clickDefault</code>
 *  is <code>true</code>.  This behaviour may be desirable because
 *  the default button is normally "clicked" when the user clicks on
 *  the "Enter" key, but this behaviour doesn't normally happen unless
 *  you add an ActionListener to the text field.</li>
 *  <li>Calling <code>addActionListener(ActionListener)</code> on this
 *  object causes the ActionListener to be added to the text field.</li>
 *  <li>A KeyEvent is generated for every time a key is pressed in the
 *  field, and all listeners are notified.  KeyEvents normally generated
 *  by the JComboBox will not be sent by this class; they are suppressed.</li>
 *  <li>A ChangeEvent is generated any time the text in the field changes.
 *  Therefore, if you want to update a button state (for example) each time the
 *  text changes, simply implement a ChangeListener and add it to the
 *  listener list for this object.  Otherwise, you would have to add a
 *  KeyListener and an ItemListener, and it's better just to have one.</li>
 * </ul>
 * It is important to point this out because the KeyEvents, ActionEvents,
 * and ChangeEvents described above are not how a normal editable combo
 * box will generate them.
 * <p>
 * Basically, a ChangeEvent is a dual purpose event.  One is sent to listeners
 * both when a KeyEvent is generated from the text field, and one is sent when
 * an ItemEvent is generated (which happens when an item is selected from the
 * drop-down list).  This means that somebody who wants to know when the actual
 * text showing has changed only needs to implement ChangeListener instead of
 * both ItemListener and KeyListener.
 *  @author Jonas Gifford
 */

final public class JASEditableComboBox extends JComboBox implements KeyListener
{
	/**
	 * Creates a JASEditableComboBox with the given list of drop-down items.  You are
	 * responsible for keeping and providing an updated list.
	 *  @param dropDownItems the items to show in the drop-down list (can safely be <code>null</code>)
	 *  @param clickDefault whether the default button should be clicked when Enter is pressed
	 *  @see javax.swing.JRootPane#defaultButton
	 */

	public JASEditableComboBox(String[] dropDownItems, boolean clickDefault)
	{
		m_clickDefault = clickDefault;
		init(dropDownItems);
	}

	/**
	 * Creates a JASEditableComboBox with a dropDown list that will be stored in
	 * the UserProperties object for the Application.  Invoke the <code>saveState()</code>
	 * method to have the list be updated to include the selected item.
	 *  @param key the key that the drop-down items will be stored by
	 *  @param nItems the maximum number of items that will be stored on the drop-down list
	 *  @param clickDefault whether the default button should be clicked when Enter is pressed
	 *  @see UserProperties
	 *  @see javax.swing.JRootPane#defaultButton
	 */

	public JASEditableComboBox(String key, int nItems, boolean clickDefault)
	{
		m_clickDefault = clickDefault;
		m_prop = UserProperties.getUserProperties();
		m_nItems = nItems;
		m_key = key;
		init(m_prop.getStringArray(m_key, null));
	}

	/**
	 * Creates a JASEditableComboBox with a dropDown list that will be stored in
	 * the UserProperties object for the Application.  Invoke the <code>saveState()</code>
	 * method to have the list be updated to include the selected item.
	 *  @param key the key that the drop-down items will be stored by
	 *  @param lengthKey the key that maps to the maximum number of items that will be stored on the drop-down list
	 *  @param clickDefault whether the default button should be clicked when Enter is pressed
	 *  @see UserProperties
	 *  @see javax.swing.JRootPane#defaultButton
	 */

	public JASEditableComboBox(String key, String lengthKey, boolean clickDefault)
	{
		this(key, Application.getApplication().getUserProperties().getInteger(lengthKey, 4), clickDefault);
	}

	private void init(String[] dropDownItems)
	{
//		addItem(""); // the first line is blank
// JComboBox has trouble with an empty first line (it causes a tiny drop-down list)
		m_dropDownItems = dropDownItems;
		if (m_dropDownItems != null)
			for (int i = 0; i < m_dropDownItems.length; i++)
				addItem(m_dropDownItems[i]);
		setEditable(true);
		m_textField = (JTextField) getEditor().getEditorComponent();
		m_textField.addKeyListener(this);
//		super.addKeyListener(this);
	}

	/**
	 * If a key was supplied to the constructor, the drop-down list will be updated to
	 * include the currently selected item.
	 */
	public void saveState()
	{
		if (m_key != null)
			m_prop.setStringArray(m_key,
				UserProperties.updateStringArray(m_dropDownItems, getText(), m_nItems));
	}

	/** Returns the text currently showing in the text field. */
	public String getText()
	{
		return m_textField.getText();
	}

	/** Sets the text showing in the text field. */
	public void setText(String s)
	{
//		removeItem(""); // delete the first item (if it is blank)
		if (s.equals(getItemAt(0))) return; // it's already there; nothing to do
		removeItem(s); // if the item is already there, just move it to the top
		insertItemAt(s, 0); // put the new text at the top
		setSelectedIndex(0);
	}

	/** Adds a key listener to the text field, <strong>not</strong> to the JComboBox. */
	public void addKeyListener(KeyListener kl)
	{
		if (m_textField != null)
			m_textField.addKeyListener(kl);
	//	super.addKeyListener(kl); (suppress default key events)
	}

	/** This method is public as an implementation side effect; do not call. */
	final public void keyReleased(KeyEvent e) {}

	/** This method is public as an implementation side effect; do not call. */
	final public void keyPressed(KeyEvent e)
	{
		if (e.getSource() != m_textField) return; // avoid cases where this listens to another by accident
		fireStateChanged(); // for any key released, notify listeners with ChangeEvent
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (m_clickDefault)
			{
				JButton b = getRootPane().getDefaultButton();
				if (b != null) b.doClick();
			}
		}
	}

    private void fireStateChanged()
	{
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				if (m_changeEvent == null)
					m_changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i+1]).stateChanged(m_changeEvent);
			}	       
		}
    }

	/**
	 * Change listeners will be notified any time the text visible in the text field changes.
	 * This is equivalent to having a KeyListener and an ItemListener.
	 *  @param l the ChangeListener to add
	 */

    public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
    }

	/**
	 * Adds an the given ActionListener to the text field.
	 */

    public void addActionListener(ActionListener l) {
		m_textField.addActionListener(l);
    }

	/** This method is protected as an implementation side effect. */

	final protected void fireItemStateChanged(ItemEvent event)
	{
//		if (m_isHandlingItemEvent) return;
//		m_isHandlingItemEvent = true;
/*		Object item = event.getItem();
		removeItem(item);
		insertItemAt(item, 0);

  Causes mysterious problems on the line with removeItem(item)
  This method was meant to move the selected item to the top of
  the list, but an IllegalArgumentException is caused for some reason.
*/

		super.fireItemStateChanged(event); // allow normal ItemEvent firing
		fireStateChanged(); // when an ItemEvent is generated, also notify listeners of ChangeEvent
//		m_isHandlingItemEvent = false;
	}
	/** Requests focus for the text field of the box. */
	final public void requestFocus()
	{
		m_textField.requestFocus();
	}

	/** This method is public as an implementation side effect; do not call. */
	final public void keyTyped(KeyEvent e)
	{
   }

	/**
	 * Set the maximum width of the box.  The default is used if this method is not called.
	 *  @param maxWidth give a number in pixels, or <code>-1</code> to use default
	 */

	final public void setMaxWidth(int maxWidth)
	{
		m_maxWidth = maxWidth;
	}

	/**
	 * Set the minimum width of the box.  The default is used if this method is not called.
	 *  @param minWidth give a number in pixels, or <code>-1</code> to use default
	 */

	final public void setMinWidth(int minWidth)
	{
		m_minWidth = minWidth;
	}
	/**
	 * Encorporates the maximum width if it has been set.
	 *  @see #setMaxWidth(int)
	 */

	final public Dimension getMaximumSize()
	{
		final Dimension d = super.getMaximumSize();
		if (m_maxWidth > 0 && m_maxWidth < d.width) d.width = m_maxWidth;
		return d;
	}

	/**
	 * Encorporates the minimum width if it has been set.
	 *  @see #setMinWidth(int)
	 */

	final public Dimension getMinimumSize()
	{
		final Dimension d = super.getMinimumSize();
		if (m_minWidth > 0 && m_minWidth > d.width) d.width = m_minWidth;
		return d;
	}

	/**
	 * Encorporates the minimum and maximum widths if they have been set.
	 *  @see #setMinWidth(int)
	 *  @see #setMaxWidth(int)
	 */

	final public Dimension getPreferredSize()
	{
		final Dimension d = super.getPreferredSize();
		if (m_minWidth > 0 && m_minWidth > d.width) d.width = m_minWidth;
		if (m_maxWidth > 0 && m_maxWidth < d.width) d.width = m_maxWidth;
		return d;
	}

	private int m_minWidth = -1, m_maxWidth = -1; // -1 indicates use default
	private int m_nItems;
//	private boolean m_isHandlingItemEvent = false;
	private String[] m_dropDownItems;
	private String m_key = null;
	private JTextField m_textField;
	private ChangeEvent m_changeEvent = null;
//	private final javax.swing.plaf.ComboBoxUI m_ui = getUI();

	private UserProperties m_prop = null;
	private boolean m_clickDefault;
}