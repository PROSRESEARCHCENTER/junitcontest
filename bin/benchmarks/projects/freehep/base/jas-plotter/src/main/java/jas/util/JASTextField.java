package jas.util;
/**
 * Works just like a JTextField, except that the default button
 * for this component's root pane will is clicked when the user clicks on Enter
 * in this field.  In a normal JTextField, clicking on Enter
 * produces an ActionEvent, but it does not do a click on
 * the default button as most users would expect.
 *  @author Jonas Gifford
 *  @see javax.swing.JRootPane
 *  @see javax.swing.JTextField
 */
public class JASTextField extends javax.swing.JTextField
{
	/**
	 * Just like calling <code>JTextField()</code>
	 * except that the default button for this component's root pane will
	 * be clicked when Enter is clicked in the field.
	 */
	public JASTextField()
	{
		super();
	}

	/**
	 * Just like calling <code>JTextField(String)</code>
	 * except that the default button for this component's root pane will
	 * be clicked when Enter is clicked in the field.
	 */
	public JASTextField(String text)
	{
		super(text);
	}

	/**
	 * Just like calling <code>JTextField(int)</code>
	 * except that the default button for this component's root pane will
	 * be clicked when Enter is clicked in the field.
	 */
	public JASTextField(int columns)
	{
		super(columns);
	}

	/**
	 * Just like calling <code>JTextField(String, int)</code>
	 * except that the default button for this component's root pane will
	 * be clicked when Enter is clicked in the field.
	 */
	public JASTextField(String text, int columns)
	{
		super(text, columns);
	}

	/**
	 * Just like calling <code>JTextField(Document, String, int)</code>
	 * except that the default button for this component's root pane will
	 * be clicked when Enter is clicked in the field.
	 */
	public JASTextField(javax.swing.text.Document doc, String text, int columns)
	{
		super(doc, text, columns);
	}
	protected void fireActionPerformed()
	{
		super.fireActionPerformed();
		javax.swing.JButton b = getRootPane().getDefaultButton();
		if (b != null) b.doClick();
	}
}
