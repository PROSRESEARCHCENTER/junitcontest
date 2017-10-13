package jas.plot;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

final class FontMenuItem extends JMenuItem
{
	FontMenuItem(JComponent c, final String type)
	{
		super(type.concat(" Font..."));
		component = c;
	}
	FontMenuItem(JComponent c)
	{
		super("Font...");
		component = c;
	}
	protected void fireActionPerformed(final ActionEvent e)
	{
		final Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, component);
		final Font font = new FontDialog(frame, component.getGraphics().getFont()).showDialog();
		if (font != null)
		{
			component.setFont(font);
			component.revalidate();
		}
	}
	private JComponent component;
}
