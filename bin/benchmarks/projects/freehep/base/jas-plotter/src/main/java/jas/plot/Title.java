package jas.plot;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JMenuItem;
import javax.swing.border.Border;

public final class Title extends MovableObject
{
	public Title(final String title)
	{
		super("Title");
		setLayout(new BorderLayout());
		label = new EditableLabel(title,"Title");
		setFont(new Font("SansSerif",Font.BOLD,14));
		add(label,BorderLayout.CENTER);
	}
	public Title()
	{
		this("");
	}
	public EditableLabel getLabel()
	{
		return label;
	}
	public String getText()
	{
		return label.getText();
	}
	public void setText(final String text)
	{
		label.setText(text);
	}
	public void edit()
	{
		label.edit();
	}
	public void setBorder(final Border b)
	{
		super.setBorder(b);
		revalidate();
	}
	private JMenuItem hideMenuItem = null;
	private EditableLabel label;
}
