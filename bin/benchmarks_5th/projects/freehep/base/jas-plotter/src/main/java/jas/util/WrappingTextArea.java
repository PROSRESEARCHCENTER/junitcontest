package jas.util;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;
/** Use this class to implement a component that contains line-wrapping text. */
public class WrappingTextArea extends JEditorPane
{
	/**
	 * @param text the text to display
	 * @param editable whether the text is editable
	 * @param background use <code>getBackground()</code> from the parent component
	 * @param size the preferred size of this component
	 */
	public WrappingTextArea(final String text, final boolean editable, final Color background,
		final Dimension size)
	{
		final WrappingEditorKit kit = new WrappingEditorKit();
		setEditorKitForContentType("text/wrapping", kit);
		setContentType("text/wrapping");
		setBackground(background);
		setCaretColor(new Color(0, 0, 0));
		if (editable)
		{
			getCaret().setBlinkRate(500);
			getCaret().setVisible(true);
		}
		setEditable(editable);
		setFont(new java.awt.Font("Dialog", 0, 12));
		setText(text);
		m_size = size;
	}
	public Dimension getPreferredSize()
	{
		return m_size;
	}
	private Dimension m_size;
}
class WrappingEditorKit extends DefaultEditorKit
{
	public ViewFactory getViewFactory()
	{
		if (m_viewFactory == null)
			m_viewFactory = new WrappingViewFactory();
		return m_viewFactory;
	}
	private ViewFactory m_viewFactory;
}
class WrappingViewFactory implements ViewFactory
{
    public View create(Element elem) {
		return new WrappedPlainView(elem, true);
    }
}
