package jas.util;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
public final class FontChooserPanel extends JPanel
{
	public FontChooserPanel(final String currentFont, final int currentStyle, final int currentSize)
	{
		super(new GridBagLayout());

		m_font = new JComboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		// deprecated fonts: see http://java.sun.com/products/jdk/1.1/docs/api/java.awt.Toolkit.html#getFontList()
		// removing these fonts seems to cause problems on OSF machines.
		//m_font.removeItem("TimesRoman"); m_font.removeItem("Courier"); m_font.removeItem("Helvetica");
		m_font.setSelectedItem(currentFont);
		m_bold = new JCheckBox("Bold", (currentStyle & Font.BOLD) != 0);
		m_italic = new JCheckBox("Italic", (currentStyle & Font.ITALIC) != 0);
		m_bold.setMnemonic('B');
		m_italic.setMnemonic('I');
		m_fontSize = new SpinBox(currentSize, 0, 100);

		JPanel temp;
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;

		temp = new JPanel(new FlowLayout(FlowLayout.CENTER)); // row 1
		temp.add(new JLabel("Font: "));
		temp.add(m_font);
		add(temp, c);

		temp = new JPanel(new FlowLayout(FlowLayout.CENTER)); // row 2
		temp.add(new JLabel("Font size (points): "));
		temp.add(m_fontSize);
		add(temp, c);

		temp = new JPanel(new FlowLayout(FlowLayout.CENTER)); // row 3
		temp.add(m_bold);
		temp.add(m_italic);
		add(temp, c);
	}

	/** Returns whether a font object can be created with the current input.   Shows a dialog if an error occurs. */
	public boolean inputIsValid()
	{
		try
		{
			return getSpecifiedFont() != null;
		}
		catch (final Throwable e)
		{
			if (e instanceof NumberFormatException)
				JOptionPane.showMessageDialog(this, "An invalid number was supplied: ".concat(e.getMessage()),
					"Invalid number", JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, "Exception occurred while creating font: ".concat(e.toString()), "Invalid font",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/** Returns the font described by the settings on the screen. */
	public Font getSpecifiedFont()
	{
		int style = Font.PLAIN;
		if (m_bold.isSelected())   style |= Font.BOLD;
		if (m_italic.isSelected()) style |= Font.ITALIC;
		return new Font((String) m_font.getSelectedItem(), style, m_fontSize.getValue());
	}

	/** Adds the given KeyListener to the font size text field. */
	public void addKeyListener(final KeyListener l)
	{
		m_fontSize.addKeyListener(l);
	}

	/** Adds the given ChangeListener to the bold and italic check boxes. */
	public void addChangeListener(final ChangeListener l)
	{
		m_bold.addChangeListener(l);
		m_italic.addChangeListener(l);
	}

	/** Adds the given ItemListener to the font combo box. */
	public void addItemListener(final ItemListener l)
	{
		m_font.addItemListener(l);
	}
	private SpinBox m_fontSize;
	private JComboBox m_font;
	private JCheckBox m_bold, m_italic;
}
