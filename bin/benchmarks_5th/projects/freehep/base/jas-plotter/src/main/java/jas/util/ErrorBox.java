package jas.util;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * This class is used to show the user that an error has occurred.
 * If the error is in the form of a Throwable (normally an
 * Exception, but possibly an Error) then that object can be passed
 * to the error box so that the user can be notified of the exact
 * error.  Invoke <code>doModal()</code> on the object to show the error.
 *  @see java.lang.Throwable
 *  @see java.lang.Exception
 *  @see java.lang.Error
 *  @author Tony Johnson
 *  @author Jonas Gifford
 */

public class ErrorBox extends JASDialog
{
	/**
	 * Opens an ErrorBox with no help button and no displayed Throwable.
	 *  @param parent the parent JFrame
	 *  @param message a String to display as a message
	 */
	public ErrorBox(JFrame parent, String message)
	{
		this(parent, message, null, null);
	}
	/**
	 * Opens an ErrorBox that has a Help button that opens the specified topic.  Help books
	 * are accessible throught static constants in the Application class.
	 *  @see Application
	 *  @param parent the parent JFrame
	 *  @param message a String to display as a message
	 *  @param helpTopic the help topic (as specified in the <code>.oht</code> topics file for the specified book)
	 */
	public ErrorBox(JFrame parent, String message, String helpTopic)
	{
		this(parent, message, null, helpTopic);
	}
	/**
	 * Opens an ErrorBox that displays a Throwable, but has no help button.  Book objects
	 * are accessible through static constants in the Application class.
	 *  @see Application
	 *  @param parent the parent JFrame
	 *  @param message a String to display as a message
	 *  @param throwable the Error or Exception that was generated
	 */
	public ErrorBox(JFrame parent, String message, Throwable throwable)
	{
		this(parent,message,throwable,null);
	}
	/**
	 * Opens an ErrorBox that displays a Throwable and has a Help button
	 * that opens the specified topic.  Book objects are available through
	 * the Application class.
	 *  @see Application
	 *  @param parent the parent JFrame
	 *  @param message a String to display as a message
	 *  @param throwable the Error or Exception that was generated
	 *  @param helpTopic the help topic (XML target)
	 */
	public ErrorBox(JFrame parent, String message, Throwable throwable, String helpTopic)
	{
		super(parent, "Error...", true, OK_BUTTON | 
			  (throwable != null ? APPLY_BUTTON | CANCEL_BUTTON : 0) | 
			  (helpTopic != null ? HELP_BUTTON : 0));

		if (helpTopic != null) setHelpTopic(helpTopic);
		init(message, throwable,parent);
	}
	private void init(String m, Throwable t,JFrame parent)
	{
		setApplyLabel("Details");
      setApplyMnemonic('D');
		setCancelLabel("Traceback");
      setCancelMnemonic('T');
		JPanel p = new JPanel();
		p.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon")));
		p.add(new JLabel(m));
		getContentPane().add(p,BorderLayout.NORTH);

		m_frame = parent;
		m_throw = t;
		m_details = (t != null);
		pack();
		getToolkit().beep();
	}
	/** Inherited from JASDialog; do not call. */
	final protected void enableApply(JASState state)
	{
		state.setEnabled(m_details);
	}
	/** Inherited from JASDialog; do not call. */
	final protected void onCancel()
	{
      // We used to use a JASDialog here, but it needs a Frame as its parent.
      // This cannot be fixed while maintaining JDK 1.1 compatibility, so instead
      // we swicthed to using a JOptionPane.
		JTextArea ta = new JTextArea();
		PrintWriter pw = new PrintWriter(new DocumentOutputStream(ta.getDocument()));
		m_throw.printStackTrace(pw);
		pw.close();
      ta.setEditable(false);
      ta.addMouseListener(new PopupListener(new PopupMenu(ta)));
		JScrollPane scroll = new JScrollPane(ta);
      scroll.setPreferredSize(new Dimension(500,300));
      JOptionPane.showMessageDialog(this,scroll,"Traceback...",JOptionPane.PLAIN_MESSAGE);
	}
	/** Inherited from JASDialog; do not call. */
	final protected void onApply()
	{
		JTextArea text = new JTextArea(m_throw.toString());
      text.setEditable(false);
      text.addMouseListener(new PopupListener(new PopupMenu(text)));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(text);
		getContentPane().add(scroll,BorderLayout.CENTER);
		pack();
		m_details = false;
		callEnable();
	}
   private static class PopupMenu extends OnScreenPopupMenu implements ActionListener
   {
      private JTextArea parent;
      JMenuItem copy = new JMenuItem("Copy",'C');
      JMenuItem select = new JMenuItem("Select All",'S');
      
      PopupMenu(JTextArea parent)
      {
         this.parent = parent;
         copy.addActionListener(this);
         select.addActionListener(this);
         add(copy);
         add(select);
      }
      public void actionPerformed(ActionEvent e)
      {
         Object source = e.getSource();
         if      (source == select) parent.selectAll();
         else if (source == copy) parent.copy();
      }
   }
   private static class PopupListener extends MouseAdapter
   {
      private JPopupMenu menu;
      PopupListener(JPopupMenu menu)
      {
         this.menu = menu;
      }
      public void mouseReleased(MouseEvent me)
      {
         maybePopup(me);
      }
      public void mousePressed(MouseEvent me)
      {
         maybePopup(me);
      }
      private void maybePopup(MouseEvent me)
      {
         if (menu.isPopupTrigger(me)) menu.show(me.getComponent(),me.getX(),me.getY());
      }
   }
	private Throwable m_throw;
	private boolean m_details;
	private JFrame m_frame;
}