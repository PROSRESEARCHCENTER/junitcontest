package jas.plot;
import jas.util.FontChooserPanel;
import jas.util.JASDialog;

import java.awt.Font;
import java.awt.Frame;
final class FontDialog extends JASDialog
{
	FontDialog(final Frame parent, final Font currentFont)
	{
		super(parent, "Set font", true, OK_BUTTON | CANCEL_BUTTON);
		m_chooser = new FontChooserPanel(currentFont.getName(), currentFont.getStyle(), currentFont.getSize());
		setContentPane(m_chooser);
	}
	Font showDialog()
	{
		pack();
		if (doModal() && m_chooser.inputIsValid())
			return m_chooser.getSpecifiedFont();
		else
			return null;
	}
	private final FontChooserPanel m_chooser;
}
