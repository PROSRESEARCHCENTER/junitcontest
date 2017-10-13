package org.freehep.swing.wizard;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

/**
 * Extend this class to create a page for the WizardDialog.  Implement the
 * interface HasNextPages if you want there to be pages following this
 * page, and implement the Finishable interface if you want this to be
 * the last page.  You may implement one or both, but implementing
 * neither would be silly because then neither the "Next" nor "Finish"
 * button will enable and your page will be a "dead end".  Implement
 * HasHelpPage if you want your page to link to a help topic.  Override
 * the methods <code>getNextEnabled()</code> and <code>getFinishEnabled()</code>
 * to control how the "Next" and "Finish" buttons enable.
 * <p>
 * This class is a KeyListener and has implemented the interface
 * with non-methods.  If you do not override these
 * methods, <code>doEnable()</code> will be called when a key
 * is released on components registered as KeyListeners.
 * This implementation is designed for when the buttons
 * enable when text is added to them.  For example, if you page had<br>
 * <code>
 * m_text = new JASTextField();<br>
 * text.addKeyListener(this);<br>
 * </code><br>
 * then the "Next" and "Finish" buttons would enable each time a key is released
 * in the field <code>text</code>.  You might define <code>getNextEnabled()</code>
 * as follows:<br>
 * <pre>
 * protected boolean getNextEnabled()
 * {
 *                String s = m_text.getText();
 *                return s != null && s.length() > 0;
 * }
 * </pre>
 *  @author Jonas Gifford
 *  @see WizardDialog
 *  @see HasNextPages
 *  @see Finishable
 *  @see HasHelpPage
 */
public abstract class WizardPage extends JPanel implements KeyListener
{
   static int pageNumber = 0;
   private WizardDialog m_wizard;
   private WizardPage m_prev = null;
   private WizardPage[] m_nextWizardPages = null;
   private boolean m_hasNextPages;
   private boolean m_isFinishable;
   private String m_number;
   private boolean m_nextEnabled = true;
   private boolean m_finishEnabled = true;

   /**
    * Supply a layout manager for the panel to use.  See Sun's notes on
    * <a href="http://www.javasoft.com/docs/books/tutorial/ui/layout/using.html">using
    * layout managers</a>.  In the constructor of your subclass, add components
    * to the page as you would add components to a regular container, using the
    * <code>add(..)</code> methods.  It is not a good idea to use AWT components
    * here because those heavy-weight components do not mix well with swing's
    * light-weight components.  You should use only
    * <a href="http://java.sun.com/products/jfc/swingdoc-api/packages.html">swing components</a>.
    * The jas.util package has a number of
    * <a href="http://www-sldnt.slac.stanford.edu/jas/Documentation/JASUtilClasses.htm">utility
    * components</a> that may be useful for these pages.
    * @param lm the layout manager to use for this page.
    * @see java.awt.LayoutManager
    */
   public WizardPage(LayoutManager lm)
   {
      super(lm);

      m_isFinishable = (this instanceof Finishable);
      m_hasNextPages = (this instanceof HasNextPages);
   }
   public WizardPage()
   {
      m_isFinishable = (this instanceof Finishable);
      m_hasNextPages = (this instanceof HasNextPages);
   }

   /**
    * This method is called just before the page is brought to the screen.  By default, it
    * does nothing but you can override it to do some last-minute setup on your page.
    */
   protected void beforeShowing() {}

   public void doBusy(Runnable run)
   {
      m_wizard.setToWaitCursor();
      try
      {
         run.run();
      }
      finally
      {
         m_wizard.setToDefaultCursor();
      }
   }

   /** Override this to provide your own behaviour for key events; does nothing by default. */
   public void keyPressed(KeyEvent e) {}

   /**
    * If you add this class as a KeyListener and don't override this method then
    * the buttons will be enabled on every key event in the definition for this method.
    */
   public void keyReleased(KeyEvent e)
   {
      if (e.getKeyCode() != KeyEvent.VK_ENTER)
         doEnable();
   }

   /** Override this to provide your own behaviour for key events; does nothing by default. */
   public void keyTyped(KeyEvent e) {}

   /**
    * This method is called if the user uses the previous button to traverse backwards 
    * from this page. By default it does nothing, but you can override it if necessary
    * to do some cleanup.
    */
   protected void onPrevious() {}

   /**
    * This method is called if the user has canceled the wizard.  By default it does nothing,
    * but you can override it to perform cleanup if necessary.  The wizard follows a preorder
    * tree traversal, so the <code>onCancel()</code> methods are called starting from the bottom
    * and working up each branch separately to the root.
    */
   protected void onCancel() {}
   
   /**
    * Called when a page following this page had its onFinish() method invoked.
    * By default does nothing, but can be used to perform cleanup operations
    */
   
   protected void onWasFinished() {}

   /** This method is public as an implementation side effect; do not call. */
   public String toString()
   {
      return m_number;
   }

   /**
    * Override to provide customized behaviour for enabling the "Finish" button.  By
    * default, this method returns whether the page implements Finishable.  If
    * your page does not implement that interface, the "Finish" button will never
    * enable.  Call <code>doEnable()</code> to call this method, or add your
    * page as a key listener to a component, and then <code>doEnable()</code> will
    * be called each time a key is released in that component.
    *  @see #doEnable()
    *  @see Finishable
    *  @return whether to enable the "Finish" button
    */
   protected boolean getFinishEnabled()
   {
      return m_isFinishable && m_finishEnabled;
   }

   /**
    * Override to provide customized behaviour for enabling the "Next" button.  By
    * default, this method returns whether the page implements HasNextPages.  If
    * your page does not implement that interface, the "Next" button will never
    * enable.  Call <code>doEnable()</code> to call this method, or add your
    * page as a key listener to a component, and then <code>doEnable()</code> will
    * be called each time a key is released in that component.
    *  @see #doEnable()
    *  @see HasNextPages
    *  @return whether to enable the "Next" button
    */
   protected boolean getNextEnabled()
   {
      return m_hasNextPages && m_nextEnabled;
   }
   protected void setNextEnabled(boolean enabled)
   {
      if (enabled != m_nextEnabled)
      {
         m_nextEnabled = enabled;
         doEnable();
      }
   }
   protected void setFinishEnabled(boolean enabled)
   {
      if (enabled != m_finishEnabled)
      {
         m_finishEnabled = enabled;
         doEnable();
      }
   }
   /** Call to dispose the wizard (i.e., to have it close). */
   protected void dispose()
   {
      m_wizard.dispose();
   }

   /**
    * Forces the wizard to call <code>getNextEnabled()</code> and <code>getFinishEnabled()</code> to enable the
    * buttons on the wizard.  This method is called by the <code>keyReleased(KeyEvent)</code> method
    * if you add this class as a KeyListener to a text field.
    *  @see #getNextEnabled()
    *  @see #getFinishEnabled()
    *  @see #keyReleased(KeyEvent)
    *  @see java.awt.event.KeyEvent
    *  @see java.awt.event.KeyListener
    */
   protected void doEnable()
   {
      // m_wizard will be null if this page has not yet been attached to a WizardDialog
      if (m_wizard == null) return;
      m_wizard.setNextEnabled(getNextEnabled());
      m_wizard.setFinishEnabled(getFinishEnabled());
      m_wizard.doPrevEnabled();
      if (m_isFinishable && m_hasNextPages)
         m_wizard.setDefaultButton();
   }

   protected void handleError(String message, Throwable t)
   {
      m_wizard.handleError(message, t);
   }

   boolean isFinishable()
   {
      return m_isFinishable;
   }

   WizardPage getPrev()
   {
      return m_prev;
   }

   void addTo(Container c, WizardDialog wizard, WizardPage prev)
   {
      c.add(m_number = String.valueOf(pageNumber++), this);
      if (m_hasNextPages)
      {
         m_nextWizardPages = ((HasNextPages) this).getNextWizardPages();
         if (m_nextWizardPages != null)
            for (int i = 0; i < m_nextWizardPages.length; i++)
               if (m_nextWizardPages[i] != null)
                  m_nextWizardPages[i].addTo(c, wizard, this);
      }
      m_wizard = wizard;
      m_prev = prev;
   }

   void clear()
   {
      if (m_hasNextPages)
      {
         for (int i = 0; i < m_nextWizardPages.length; i++)
            if (m_nextWizardPages[i] != null)
               m_nextWizardPages[i].clear();
         m_nextWizardPages = null;
      }
      m_prev = null;
      m_wizard = null;
   }

   void doCancel() // preorder tree traversal
   {
      if (m_nextWizardPages != null)
         for (int i = 0; i < m_nextWizardPages.length; i++)
            if (m_nextWizardPages[i] != null)
               m_nextWizardPages[i].doCancel();
      onCancel();
   }

   boolean hasNextPages()
   {
      return m_hasNextPages;
   }
}
