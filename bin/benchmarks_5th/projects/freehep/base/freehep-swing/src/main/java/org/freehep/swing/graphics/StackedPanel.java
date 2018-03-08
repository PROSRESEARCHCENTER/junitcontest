// Copyright 2000-2005, FreeHEP
package org.freehep.swing.graphics;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.border.Border;

import org.freehep.swing.layout.StackedLayout;

/**
 * StackedPanel defines an extension to JLayeredPane which allows a
 * set of equally-sized, overlayed panels to form a single 2D surface
 * on which to draw.
 *
 * @author Charles Loomis
 * @version $Id: StackedPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class StackedPanel
    extends JLayeredPane
    implements java.io.Serializable,
               PropertyChangeListener {

    /**
     * This marks the current component which is being printed. */
    private Component componentToPrint = null;

    /**
     * The graphics context to use for printing. */
    Graphics printGraphics = null;

    /**
     * This flag indicates that the JInternalFrame which contains this
     * component wants painting inhibited.  This is generally done
     * during a resizing operation. */
    private boolean inhibitRepaint = false;

    /**
     * An error message indicating that the class passed into
     * addGraphicalSelectionPanelOfClass method was not a type of
     * GraphicalSelectionPanel. */
    final public static String NEED_GRAPHICAL_SELECTION_PANEL =
        "Class must be a type of GraphicalSelectionPanel";

    /**
     * The AbstractPanelArtist which provides the graphics content for
     * this panel. */
    private AbstractPanelArtist panelArtist = null;

    /**
     * Create one update task for reuse. */
    private UpdateTask updateTask;

    /**
     * The Timer used to schedule periodic updates of the display. */
    private javax.swing.Timer timer;

    /**
     * Layer intended for interaction with the user.  (e.g. zooming,
     * picking, etc.)  Positioned just behind the palette layer. */
    public static final Integer INTERACTION_LAYER =
        new Integer(PALETTE_LAYER.intValue()-1);

    /**
     * The lowest allowed value for components. */
    public static final Integer MINIMUM_LAYER = DEFAULT_LAYER;

    /**
     * One larger than the maximum allowed layer for components. */
    public static final Integer MAXIMUM_LAYER = INTERACTION_LAYER;

    /**
     * A LinkedList containing the layers defined by the user. */
    private LinkedList panelList;

    /**
     * A hash table which maps the layer name to the component(s). */
    private HashMap panelHash;

    /**
     * A flag to indicate whether redrawing is needed at the next
     * repaint. */
    private boolean redrawNeeded;

    /**
     * This constructor makes a new stacked panel which has no
     * PanelArtist associated with it. */
    public StackedPanel() {
        this(null);
    }

    /**
     * The constructor allocates all of the panels for drawing.  */
    public StackedPanel(AbstractPanelArtist panelArtist) {

        // Allocate space for the panel list.
        panelList = new LinkedList();

        // Allocate space for the component HashMap.  This looks up a
        // particular pair of components based on the layer name.
        panelHash = new HashMap(15);

        // Make an UpdateTask for reuse; set initial period to 300ms.
        updateTask = new UpdateTask();

        // Make a Timer.
        timer = new javax.swing.Timer(300, updateTask);

        // This panel must be redrawn at the next repaint.
        setRedrawNeeded(true);

        // Set the layout manager for this panel to a StackedLayout.
        setLayout(new StackedLayout());

        // Set the panel artist.  Make sure that this is done after
        // all of the rest of the initialization.
        setPanelArtist(panelArtist);
    }

    /**
     * A routine which adds a new layer to this StackedPanel.  By
     * default, the layer is added above all others.  Each layer will
     * contain the given number of sublayers.  If the number of
     * sublayers is larger than one and the layer should be opaque,
     * only the lowest sublayer will be opaque, all others will be
     * transparent.  When retrieving the graphics context for the
     * sublayers, the topmost layer is listed first.  If the number of
     * sublayers is non-positive, then an IllegalArgumentException
     * will be thrown. */
    public void addLayer(String layerName,
                         int sublayers,
                         boolean opaque) {

        // Check that the number of sublayers is reasonable.
        if (sublayers<=0) throw new IllegalArgumentException();

        // Create the backed panels.
        JComponent[] panels = new JComponent[sublayers];
        for (int i=0; i<sublayers; i++) {
            panels[i] = new BackedPanel(opaque && (i==sublayers-1));
        }

        addLayer(layerName, panels);
    }

    /**
     * adds a Panel as layer to this StackedPanel
     */
    public void addLayer(String layerName, JComponent panel) {
        addLayer(layerName, new JComponent[] {panel});
    }

    /**
     * adds a set of Panels as layer and sublayers to this StackedPanel
     */
    public void addLayer(String layerName, JComponent[] panels) {
        // Create the panel array.
        PanelArray panelArray = new PanelArray(panels);

        // Check to see if this layer already exists.  If so,
        // implicitly remove it from the component.
        if (panelList.contains(layerName)) removeLayer(layerName);

        // Add these panels into this component.
        for (int i=0; i<panels.length; i++) {
            add(panels[i]);
        }

        // Now add it to the list.
        panelList.add(layerName);
        panelHash.put(layerName, panelArray);

        // Reorder all of the layers.
        reorderLayers();
    }

    /**
     * Remove a layer from the stacked panel. */
    public void removeLayer(String layerName) {

        PanelArray panelArray =
            (PanelArray) panelHash.get(layerName);

        if (panelArray!=null) {

            Component[] comps = panelArray.getComponents();
            for (int i=0; i<comps.length; i++) {
                remove(comps[i]);
            }

            // Remove the references in the panel list and panel
            // hash.
            panelArray.clear();
            panelList.remove(layerName);
            panelHash.remove(layerName);

            // Finally, reorder the layers.
            reorderLayers();
        }
    }

    /**
     * Reorder the layers. */
    public void reorderLayers() {

        // Start at the minimum layer number.
        int layer = MINIMUM_LAYER.intValue();

        // Loop over all of the layers.
        Iterator i = panelList.iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            PanelArray panelArray =
                (PanelArray) panelHash.get(key);

            if (panelArray!=null) {
                Component[] comps = panelArray.getComponents();
                for (int j=comps.length-1; j>=0; j--) {
                    setLayer(comps[j],layer++);
                }
            }
        }
    }

    /**
     * Add an interaction component.  Initially the component is
     * inactive (invisible). */
    public void addGraphicalSelectionPanel(GraphicalSelectionPanel panel) {
        panel.setVisible(false);
        add(panel, INTERACTION_LAYER);
    }

    /**
     * Remove an interaction component. */
    public void removeGraphicalSelectionPanel(GraphicalSelectionPanel
                                              panel) {
        remove(panel);
    }

    /**
     * Make the GraphicalSelectionPanel of the given class active. */
    public void activateGraphicalSelectionPanelOfClass(Class c)
        throws InstantiationException,
               IllegalAccessException {

        // Make sure that the class given is a type of
        // GraphicalSelectionPanel.
        if (!(GraphicalSelectionPanel.class).isAssignableFrom(c)) {
            throw new
                IllegalArgumentException(NEED_GRAPHICAL_SELECTION_PANEL);
        }

        // Get an array of interaction components.
        Component[] components =
            getComponentsInLayer(INTERACTION_LAYER.intValue());

        // Loop over components and see if one if of the correct
        // class.
        boolean create = true;
        for (int i=0; i<components.length; i++) {
            boolean test = (c==components[i].getClass());
            components[i].setVisible(test);
            if (test) create = false;
        }

        // If a component of the correct class was not found, then
        // make an instance of this class and make it visible.
        if (create) {
            GraphicalSelectionPanel newSelector =
                (GraphicalSelectionPanel) c.newInstance();
            addGraphicalSelectionPanel(newSelector);
            newSelector.setVisible(true);

            // Make the panel artist a listener for this.
            if (panelArtist!=null)
                newSelector.addGraphicalSelectionListener(panelArtist);
        }
    }

    /**
     * Make a given GraphicalSelectionPanel active. */
    // FIXME: this method does not do all stuff that the above method seem to do, such as handling listener subscription
    public void
        activateGraphicalSelectionPanel(GraphicalSelectionPanel
                                        panel) {

        // Get an array of interaction components.
        Component[] components =
            getComponentsInLayer(INTERACTION_LAYER.intValue());

        // Loop over components and make the selected one visible.
        // Others should be make invisible.
        for (int i=0; i<components.length; i++) {
            components[i].setVisible(components[i]==panel);
        }

    }

    /**
     * Make all GraphicalSelectionPanels inactive. */
    public void deactivateGraphicalSelectionPanels() {

        // Get an array of interaction components.
        Component[] components =
            getComponentsInLayer(INTERACTION_LAYER.intValue());

        // Loop over components and make them all invisible.
        for (int i=0; i<components.length; i++) {
            components[i].setVisible(false);
        }
    }

    /**
     * Tell the component whether or not a given layer is visible.
     *
     * @param layerName String describing a particular layer
     * @param visible true if layer should be visible, false otherwise
     * */
    public void setVisible(String layerName,
                           boolean[] visible) {

        PanelArray panelArray =
            (PanelArray) panelHash.get(layerName);

        if (panelArray!=null) {
            Component[] comps = panelArray.getComponents();

            // Check for mismatch in the size of the arrays.
            if (visible.length<comps.length)
                throw new IllegalArgumentException();

            for (int i=0; i<comps.length; i++) {
                comps[i].setVisible(visible[i]);
            }
        }
    }

    /**
     * Set which PanelArtist will control the graphics content of this
     * panel.  If the PanelArtist is not yet available, then null can
     * be passed in here.
     *
     * @param panelArtist PanelArtist which provides the graphics
     * content */
    public void setPanelArtist(AbstractPanelArtist panelArtist) {

        // Get a list of the interaction components.
        Component[] components =
            getComponentsInLayer(INTERACTION_LAYER.intValue());

        // Add the panel artist as the receiver of any graphical
        // selections.  Remove the current panelArtist and add the new
        // one as Graphical Selection Listeners.
        for (int i=0; i<components.length; i++) {
            GraphicalSelectionPanel selector =
                (GraphicalSelectionPanel) components[i];
            if (this.panelArtist!=null)
                selector.removeGraphicalSelectionListener(this.panelArtist);
            if (panelArtist!=null)
                selector.addGraphicalSelectionListener(panelArtist);
        }

        // First check to see if a PanelArtist is already being used.
        // If so, remove it.  Note that the PanelArtist is responsible
        // for stopping any drawing which is taking place on this
        // component.
        if (this.panelArtist!=null)
            this.panelArtist.setStackedPanel(null);

        // Save which PanelArtist to use.  Tell the panel artist that
        // it is controlling this stacked panel.
        this.panelArtist = panelArtist;
        if (panelArtist!=null) {
            panelArtist.setStackedPanel(this);

            if (panelArtist instanceof MouseListener) {
                addMouseListener((MouseListener) panelArtist);
            }
        }
    }

    /**
     * Return the PanelArtist which controls the graphics content of
     * this panel.  Note this can return null if no PanelArtist has
     * been set.
     *
     * @return the PanelArtist which control the graphics content */
    public AbstractPanelArtist getPanelArtist() {
        return panelArtist;
    }

    /**
     * Return a boolean indicating whether or not this panel will be
     * completely redrawn at the next repaint. */
    public boolean isRedrawNeeded() {
        return redrawNeeded;
    }

    /**
     * Set the redraw flag.  If set to true, the entire panel will be
     * redrawn at the next repaint.  (The paintComponent method will
     * call drawPanel() rather than flushing the saved image to the
     * screen. */
    public void setRedrawNeeded(boolean redrawNeeded) {
        this.redrawNeeded = redrawNeeded;
    }

    /**
     * This provides an additional getGraphics method which returns
     * the graphics object associated with the image of the given
     * layer.  This will return the graphics object associated with a
     * given layer even if that layer is hidden. */
    public void getGraphics(String layerName, Graphics[] g) {

        // Get the associated components.
        PanelArray panelArray =
            (PanelArray) panelHash.get(layerName);

        if (panelArray!=null) {

            Component[] comps = panelArray.getComponents();

            if (g.length<comps.length)
                throw new IllegalArgumentException();

            for (int i=0; i<comps.length; i++) {
                if (componentToPrint!=null && comps[i]==componentToPrint) {
                    g[i] = (Graphics) printGraphics;
                } else if (componentToPrint!=null) {
                    g[i] = null;
                } else if (comps[i]!=null) {
                    g[i] = comps[i].getGraphics();
                }
            }
        }
    }

    /**
     * This provides a method for PanelArtists to clear all layers. */
    public void clearAllLayers() {
        Iterator iterator = panelHash.keySet().iterator();
        while (iterator.hasNext()) {
            clearLayer((String)iterator.next());
        }
    }

    /**
     * This provides a method for PanelArtists to clear a layer.  This
     * conditionally clears a particular layer depending on whether or
     * not the images are being printed. */
    public void clearLayer(String layerName) {
        if (componentToPrint==null) {
            Graphics2D[] g = new Graphics2D[2];
            getGraphics(layerName,g);
            Insets insets = getInsets();
            int w = getWidth()-(insets.left+insets.right);
            int h = getHeight()-(insets.top+insets.bottom);
            for (int i=0; i<2; i++) {
                Graphics2D g2d = g[i];
                if (g2d!=null) {

                    g2d.setComposite(AlphaComposite.Clear);

//        long t0 = System.currentTimeMillis();
                    g2d.fillRect(0,0,w,h);

                    g2d.setComposite(AlphaComposite.Src);
//        long t1 = System.currentTimeMillis();
//        System.out.println("SubClear "+layerName+" took: "+(t1-t0)+" ms.");
                }
            }
        }
    }

    /**
     * This sets the anti-aliasing on or off for the given layer. */
    public void setAntialias(String layerName, boolean antialias) {
        if (componentToPrint==null) {
            Graphics[] g = new Graphics[2];
            getGraphics(layerName,g);
            for (int i=0; i<2; i++) {
                Graphics2D g2d = (Graphics2D) g[i];
                if (g2d!=null) {

                    if (antialias) {
                        g2d.setRenderingHint(
                          RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(
                          RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    } else {
                        g2d.setRenderingHint(
                          RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_OFF);
                        g2d.setRenderingHint(
                          RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                    }
                }

            }
        }
    }


    public void paint(Graphics g) {
        super.paint(g);
//        try { Thread.sleep(5000); } catch (InterruptedException ie) {}
        System.out.println("Done");
    }

    public void paintChildren(Graphics g) {
        System.out.println("Children...");
        long t0 = System.currentTimeMillis();
        super.paintChildren(g);
        long t1 = System.currentTimeMillis();
        System.out.println("Children took: "+(t1-t0)+" ms.");
    }

    /**
     * Override of the paintComponent() method ensures that the
     * drawPanel() method will be called if a complete redraw is
     * needed. */
    public void paintComponent(Graphics g) {
        // Always call the parent's method.
        super.paintComponent(g);

        // Check if a redraw needs to be done from scratch.
        if (redrawNeeded && !inhibitRepaint) {
            setRedrawNeeded(false);
            stopPeriodicUpdate();

            // If drawPanel() returns false, then the drawing will
            // occur in another thread and is not yet complete.
            // Periodically update the panel in this case.
            if (panelArtist!=null &&
                !panelArtist.drawPanel()) startPeriodicUpdate();
        }
    }

    /**
     * This prints this StackedPanel.  */
    public void printComponent(Graphics g) {

        if (panelArtist!=null) {

            // Make a sub-context which is correctly shifted to
            // account for the borders of this component.  This is
            // done automatically for the various component layers,
            // but we must do it by hand for the printing.
            Insets insets = getInsets();
            int w = getWidth();
            int h = getHeight();
            printGraphics = g.create(insets.left,insets.top,
                                     w-(insets.left+insets.right),
                                     h-(insets.top+insets.bottom));

            // Get the list of components and sort according to the
            // ordering. We must do this because the order is not
            // guaranteed to be correct from getComponents().
            Component[] clist = getComponents();
            ComponentIndex[] indexed = new ComponentIndex[clist.length];
            for (int i=0; i<clist.length; i++) {
                Component c = clist[i];
                indexed[i] =
                    new ComponentIndex(c,getLayer(c),getPosition(c));
            }
            Arrays.sort(indexed);

            // Do the printing.
            for (int i=0; i<indexed.length; i++) {
                componentToPrint = indexed[i].getComponent();
                if (componentToPrint.isVisible()) {
                    if (componentToPrint instanceof BackedPanel) {
                        panelArtist.drawPanel();
                    } else if (componentToPrint instanceof JComponent) {
                        componentToPrint.print(g);
                    } else {
                        componentToPrint.paintAll(g);
                    }
                }
            }

            // Clean-up after the printing.
            componentToPrint = null;
            printGraphics.dispose();
            printGraphics = null;
        }
    }

    /**
     * This callback routine is intended to be used by the controlling
     * PanelArtist after it has returned false from the drawPanel()
     * method, indicating that the redraw of the panel has not yet
     * completed.  This signals that the redraw is now complete. */
    public void drawComplete() {
        stopPeriodicUpdate();
        updateTask.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_LAST, "timer.stop"));
    }

    /**
     * Set the delay for the periodic update of the display. */
    public void setUpdatePeriod(int period) {
        timer.setDelay(Math.max(0,period));
    }

    /**
     * Get the delay for the periodic update of the display. */
    public long getUpdatePeriod() {
        return timer.getDelay();
    }

    /**
     * Start a periodic update of this panel.  This is called
     * internally when the controlling PanelArtist is drawing into the
     * images from another, background thread. */
    protected void startPeriodicUpdate() {
        timer.restart();
    }

    /**
     * Stop the periodic update of this panel. */
    protected void stopPeriodicUpdate() {
        timer.stop();
    }

    /**
     * Set the border of this component.  This is intercepted so that
     * if the size of the border changes, the redraw flag can be
     * set. */
    public void setBorder(Border border) {

        // Get the old and the new insets.
        Insets insets = getInsets();
        Insets borderInsets = null;
        if (border!=null) {
            borderInsets = border.getBorderInsets(this);
        } else {
            borderInsets = new Insets(0,0,0,0);
        }

        // Check to see if they have changed.
        if (insets.left!=borderInsets.left ||
            insets.top!=borderInsets.top ||
            insets.right!=borderInsets.right ||
            insets.bottom!=borderInsets.bottom) setRedrawNeeded(true);

        // Now let my parent do it's processing.
        super.setBorder(border);
    }

    /**
     * Intercept a resize event so that the redraw flag can be
     * set. (Swing internally calls reshape() which is then forwarded
     * to setBounds().) */
    public void setBounds(int x, int y, int w, int h) {

        // Do not forward messages about the resize if it is currently
        // in progress.  Wait for the inhibit to be withdrawn.
        if (!inhibitRepaint) {
            super.setBounds(x,y,w,h);

            // Notify the panel artist that the size has changed.
            if (panelArtist!=null) panelArtist.panelResized();
        }
    }

    /**
     * This inner class is the task which is executed to periodically
     * update the display. */
    class UpdateTask
        implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            repaint();
        }
    }

    /**
     * Look for changes in property values.  If a property is set on a
     * component which has a key "InhibitRepaint" and a value of true,
     * repainting of the component will be inhibited. */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("InhibitRepaint")) {
            if (evt.getNewValue()==null) {

                // Turn off the inhibit.
                inhibitRepaint = false;

                // Force this component to be resized if necessary.
                invalidate();
                validate();

                // Finally, repaint if necessary.
                repaint();
            } else {
                inhibitRepaint = true;
            }
        }
    }

    /**
     * A protected class which simply wraps the set of Panels
     * which form the sublayers of a particular layer. */
    protected class PanelArray {

        private JComponent[] panels;

        public PanelArray(JComponent[] panels) {
            this.panels = panels;
        }

        public Component[] getComponents() {
            return (Component[]) panels;
        }

        public JComponent getComponent(int index) {
            return panels[index];
        }

        public void clear() {
            for (int i=0; i<panels.length; i++) {
                panels[i] = null;
            }
            panels = null;
        }

    }

    /**
     * This private class takes a component, its layer, and its
     * position.  It is used to sort the components into the display
     * order (lowest layer, highest position). */
    private class ComponentIndex
        implements Comparable {

        private Component component;
        private int layer;
        private int position;

        public ComponentIndex(Component component, int layer, int position) {
            this.component = component;
            this.layer = layer;
            this.position = position;
        }

        public Component getComponent() {
            return component;
        }

        public int hashcode() {
            return (layer ^ position);
        }

        public boolean equals(Object o) {

            // Cast this to the correct type.  This will throw a class
            // cast exception if this isn't possible.
            ComponentIndex other = (ComponentIndex) o;

            return (layer==other.layer && position==other.position);
        }

        public int compareTo(Object o) {

            // Cast this to the correct type.  This will throw a class
            // cast exception if this isn't possible.
            ComponentIndex other = (ComponentIndex) o;

            // Now do the ordering.
            if (layer<other.layer) {
                return -1;
            } else if (layer>other.layer) {
                return 1;
            } else {
                if (position>other.position) {
                    return -1;
                } else if (position<other.position) {
                    return 1;
                }
            }
            return 0;
        }

    }

}

