package jas.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

public class SplashScreen extends JWindow
{
	private JProgressBar progress;

	// SplashScreen's constructor
	public SplashScreen(ImageIcon CoolPicture, String message, String title)
	{
		// Create a JPanel so we can use a BevelBorder
		JPanel PanelForBorder=new JPanel(new BorderLayout());
		PanelForBorder.setLayout(new BorderLayout());
		PanelForBorder.add(new JLabel(CoolPicture),BorderLayout.CENTER);

		PanelForBorder.add(new JLabel(title,JLabel.CENTER),BorderLayout.NORTH);
		
		progress = new JProgressBar(0,80);
		progress.setStringPainted(true);
		progress.setString(message);
		
		PanelForBorder.add(progress,BorderLayout.SOUTH);
		PanelForBorder.setBorder(new BevelBorder(BevelBorder.RAISED));

		getContentPane().add(PanelForBorder);    
	}
	public void setVisible(boolean show)
	{
		if (show)
		{
			pack();

			// Plonk it on center of screen
			Dimension WindowSize=getSize(),
				ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((ScreenSize.width-WindowSize.width)/2,
					  (ScreenSize.height-WindowSize.height)/2,WindowSize.width,
					  WindowSize.height);
		}
		super.setVisible(show);
	}

	public void showStatus(String CurrentStatus, int percent)
	{
		if (isVisible())
		{
			SwingUtilities.invokeLater(new UpdateStatus(CurrentStatus,percent));
		}
	}

	public void close() 
	{
		if (isVisible())
		{
			SwingUtilities.invokeLater(new CloseSplashScreen());
		}
	}

	private class UpdateStatus implements Runnable
	{
		public UpdateStatus(String status, int pc)
		{
			message = status; 
			value = pc; 
		}
		public void run()
		{
			progress.setValue(value);
			progress.setString(message);
		}
		private String message;
		private int value;
	}

	private class CloseSplashScreen implements Runnable
	{
		public void run()
		{
			setVisible(false);
			dispose();
		}
	}
}
