// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import javax.swing.JComponent;

/**
 * This interface allows the implementing component to interact with a
 * StackedPanel.  The StackedPanel handles all of the details of
 * layering, repainting, etc.; however, it cannot provide the actual
 * content to display.  This is the responsibility of the
 * PanelArtist. 
 * 
 * If the content changes then the PanelArtist is expected to signal
 * to the StackedPanel that a redraw is needed (redrawNeeded method of
 * StackedPanel).  The StackedPanel will then callback the PanelArtist
 * via the drawPanel method.  The PanelArtist is then responsible for
 * deciding what needs to be drawn.  The actual drawing can be done in
 * a background thread if desired. 
 *
 * This interface allows only one StackedPanel to be controlled by a
 * given PanelArtist.  Consequently, when the setPanelArtist method is
 * called the component should save the value. 
 *
 * @author Charles Loomis
 * @version $Id: PanelArtist.java 8584 2006-08-10 23:06:37Z duns $ */
public interface PanelArtist {

    /**
     * This returns a descriptive string for this PanelArtist.
     *
     * @return a String describing this PanelArtist */
    public String getDescription();

    /**
     * This method draws the graphics onto the StackedPanel.  Usually
     * this is called only when the content has changed, simple
     * repaints are handled by the StackedPanel's buffering.
     * 
     * The class which implements this interface has the option of
     * doing the drawing in a background thread.  In such a case, the
     * method should return immediately after starting the thread and
     * return false indicating that the drawing has not completed.
     * After the drawing has completed, this method should call the
     * drawComplete() method of the StackedPanel.
     *
     * @return boolean indicating whether the drawing has completed */ 
    public boolean drawPanel();

    /**
     * This method sets which data sample should be used.
     *
     * @param data data sample to use */
    public void setEventData(Object data);

    /**
     * This method returns the data sample which is currently being
     * used. 
     *
     * @return the event data currently being used. */
    public Object getEventData();

    /**
     * Set the StackedPanel that this PanelArtist will control. 
     *
     * @param panel StackedPanel to control */
    public void setStackedPanel(StackedPanel panel);

    /**
     * Return the StackedPanel which is being controlled. 
     *
     * @return the StackedPanel which is being controlled */
    public StackedPanel getStackedPanel();

    /**
     * This method aborts any drawing which is being done in another
     * thread.  This method should NOT return until the drawing has
     * been stopped. 
     * 
     */
    public void abortDraw();

    /**
     * This method is called when the size of the StackedPanel has
     * changed.  This gives the implementing class the opportunity to
     * change transformation matricies, flag that a redraw is needed,
     * etc. 
     *
     */
    public void panelResized();

    /**
     * This method returns a JComponent which contains controls for
     * the given PanelArtist.  The implementing class may return null
     * if no controls are relevant. 
     *
     * @return JComponent containing controls for the Panel Artist */ 
    public JComponent getControlPanel();

}
