package org.freehep.swing.test;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.freehep.swing.JDirectoryChooser;

public class JDirectoryChooserTest extends TestFrame
{
	protected JComponent createComponent()
	{
		JPanel p = new JPanel();
		final JDirectoryChooser chooser = new JDirectoryChooser();
		JButton browse = new JButton("Browse...")
		{
			public void fireActionPerformed(ActionEvent e)
			{
				chooser.showDialog(this);
			}
		};
		p.add(browse);
		JCheckBox dirOnly = new JCheckBox("Directories Only")
		{
			public void fireActionPerformed(ActionEvent e)
			{
				chooser.setFileSelectionMode(isSelected() ? JDirectoryChooser.DIRECTORIES_ONLY : JDirectoryChooser.FILES_AND_DIRECTORIES);
			}
		};
		dirOnly.setSelected(chooser.getFileSelectionMode() == JDirectoryChooser.DIRECTORIES_ONLY);
		p.add(dirOnly);
		JCheckBox multi = new JCheckBox("Multiple Selection")
		{
			public void fireActionPerformed(ActionEvent e)
			{
				chooser.setMultiSelectionEnabled(isSelected());
			}
		};
		multi.setSelected(chooser.isMultiSelectionEnabled());
		p.add(multi);
		return p;
	}
	public static void main(String[] arg) throws Exception
	{
		new JDirectoryChooserTest();
	}
}
