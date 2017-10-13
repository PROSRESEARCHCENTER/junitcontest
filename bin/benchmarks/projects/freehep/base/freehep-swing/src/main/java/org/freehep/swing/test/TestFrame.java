package org.freehep.swing.test;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Abstract frame to be extended by test classes
 */
public abstract class TestFrame extends JFrame
{
    private JMenuBar menuBar;
    private JPanel topPanel;
    
	protected TestFrame()
	{
		setTitle(createTitle());
		// create panel, add menubar and component
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
	    menuBar = createMenuBar();
	    if (menuBar != null) {
            topPanel.add(menuBar, BorderLayout.NORTH);
            addToMenuBar(menuBar);
        }
        topPanel.add(createComponent(), BorderLayout.CENTER);
		getContentPane().add(topPanel);
		
		// add Look and Feel menu
		menuBar.add(createLookAndFeelMenu());
		
		// Make this exit when the close button is clicked.
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{
				quit();
			}
		});
		pack();
		setVisible(true);
	}

    protected JMenu createLookAndFeelMenu() {
		JMenu look = new JMenu("Look and Feel");
		ButtonGroup group = new ButtonGroup();
        final javax.swing.UIManager.LookAndFeelInfo info[] = UIManager.getInstalledLookAndFeels();
        for (int i=0; i< info.length; i++) {
            JRadioButtonMenuItem radio = new JRadioButtonMenuItem(info[i].getName());
            radio.setActionCommand(info[i].getClassName());
			radio.setSelected(info[i].getName().equals(UIManager.getLookAndFeel().getName()));
			look.add(radio);
            group.add(radio);
            radio.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
                    try {
                	    UIManager.setLookAndFeel(e.getActionCommand());
                	    SwingUtilities.updateComponentTreeUI(topPanel);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }                
            });
        }
        return look;
    }
	
	
	protected JMenuBar createMenuBar() {
	    return new JMenuBar();
    }

    protected void addToMenuBar(JMenuBar menuBar) {
    }
    
	protected abstract JComponent createComponent();
	protected String createTitle()
	{
		return "Test Frame";
	}
    /**
     * This method brings up a dialog box to ask if the user really
     * wants to quit.  If the answer is yes, the application is
     * stopped.  */
    public void quit() 
	{
		// Create a dialog box to ask if the user really wants to quit.
		int n = JOptionPane.showConfirmDialog
			(this, "Do you really want to quit?","Confirm Quit",
			 JOptionPane.YES_NO_OPTION);

		if (n==JOptionPane.YES_OPTION) System.exit(0);
	}
}
