// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.BasicStroke;
import java.awt.KeyboardFocusManager;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

/**
 * The primary superclass of all graphical selection panels.  These
 * panels are expected to handle all of the interaction with the user,
 * and generate a GraphicalSelectionEvent when a selection has been
 * made.
 *
 * Note that GraphicalSelectionPanels use the information about the
 * size of the component to send back meaningful zoom transformation
 * and the like.  To keep these calculations simple, Borders are not
 * allowed on these components.  If a Border is desired, then embed
 * the selection panel within a container and put the Border on the
 * container.
 *
 * @author Charles Loomis
 * @version $Id: GraphicalSelectionPanel.java 8584 2006-08-10 23:06:37Z duns $ */
public class GraphicalSelectionPanel
    extends JPanel
    implements MouseListener,
               KeyListener,
               MouseMotionListener,
               java.io.Serializable {

    /**
     * A private ActionEvent which is used when a selection is made.  Since
     * the sender never changes, this event is simply reused. */
    private ActionEvent actionEvent;

    /**
     * The list of selection actions. */
    protected LinkedList selectionActions = new LinkedList();

    /**
     * The hash map which maps keys to actions. */
    protected ActionMap actionMap = new ActionMap();

    /**
     * The "Leave" action in the popup menu.  Created here as an
     * Action so that subclasses can more easily enable and disable
     * this item. */
    protected SelectionAction defaultModeAction =
        new SelectionAction("Default Mode",
                            GraphicalSelectionEvent.DEFAULT_MODE,
                            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0));

    /**
     * The "Next" action in the popup menu.  Created here as an
     * Action so that subclasses can more easily enable and disable
     * this item. */
    protected SelectionAction nextAction =
        new SelectionAction("Next Mode",
                            GraphicalSelectionEvent.NEXT_MODE,
                            KeyStroke.getKeyStroke(KeyEvent.VK_N,0));

    /**
     * The "Previous" action in the popup menu.  Created here as an
     * Action so that subclasses can more easily enable and disable
     * this item. */
    protected SelectionAction previousAction =
        new SelectionAction("Previous Mode",
                            GraphicalSelectionEvent.PREVIOUS_MODE,
                            KeyStroke.getKeyStroke(KeyEvent.VK_P,0));

    /**
     * Thin stroke for the white part of the selection box.  Set the
     * miter limit so that the drawing doesn't extend outside of the
     * bounding box. */
    final protected static Stroke thinStroke =
        new BasicStroke(1.f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        3.f);

    /**
     * Thick stroke for the black part of the selection box.  Set the
     * miter limit so that the drawing doesn't extend outside of the
     * bounding box. */
    final protected static Stroke thickStroke =
        new BasicStroke(3.f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        5.f);

    /**
     * An empty popup menu is available for subclasses of this
     * object. */
    private JPopupMenu popup;

    /**
     * A flag to indicate whether a popup menu is currently being
     * processed or not. */
    private boolean processingPopup;

    /**
     * Keeps track of all of the event listeners. */
    private EventListenerList listenerList;

    /**
     * Error string when user attempts to set a non-null border. */
    private final static String NON_NULL_BORDER_ERROR =
        "GraphicalSelectionPanel does not support borders.";

    /**
     * Creates a selection panel which is transparent. */
    public GraphicalSelectionPanel() {
        // make sure that tab and shift-tab can be used to cycle between components.
        Set forwardKeys = new TreeSet();
        forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        Set backwardKeys = new TreeSet();
        backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
	    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        setOpaque(false);
        listenerList = new EventListenerList();

        actionEvent = new ActionEvent(this,0,"KeyAction");

        addMouseListener(this);
        addMouseMotionListener(this);

        addKeyListener(this);
        setRequestFocusEnabled(true);

        // Make the necessary selection actions.
        makeSelectionActions();

        // Make the popup menu.
        popup = new JPopupMenu();
        processingPopup = false;

        // Get the popup menu.
        JPopupMenu popup = getPopupMenu();

        // Add items to this popup.
        JMenuItem item;

        // Add all of the selection actions.
        Iterator i = selectionActions.iterator();
        while (i.hasNext()) {
            Action action = (Action) i.next();
            item = new JMenuItem(action);
            KeyStroke accelerator =
                (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
            addActionEntry(accelerator,action);
            item.setAccelerator(accelerator);
            popup.add(item);
        }
        popup.addSeparator();

        item = new JMenuItem(nextAction);
        KeyStroke accelerator =
            (KeyStroke) nextAction.getValue(Action.ACCELERATOR_KEY);
        addActionEntry(accelerator,nextAction);
        item.setAccelerator(accelerator);
        popup.add(item);

        item = new JMenuItem(previousAction);
        accelerator =
            (KeyStroke) previousAction.getValue(Action.ACCELERATOR_KEY);
        addActionEntry(accelerator,previousAction);
        item.setAccelerator(accelerator);
        popup.add(item);

        item = new JMenuItem(defaultModeAction);
        accelerator =
            (KeyStroke) defaultModeAction.getValue(Action.ACCELERATOR_KEY);
        addActionEntry(accelerator,defaultModeAction);
        item.setAccelerator(accelerator);
        popup.add(item);
    }

    /**
     * This makes all of the selection actions and binds them to specific
     * keys. */
    private void makeSelectionActions() {

        // Make the zoom actions.
        Action action =
            new SelectionAction("Zoom",GraphicalSelectionEvent.ZOOM,
                                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
        selectionActions.add(action);
        addActionEntry(KeyEvent.VK_ENTER,action);

        action =
            new SelectionAction("Zoom (new view)",
                                GraphicalSelectionEvent.ZOOM_NEW_VIEW,
                                KeyStroke.getKeyStroke(KeyEvent.VK_V,0));
        selectionActions.add(action);
        addActionEntry(KeyEvent.VK_N,action);

        // Make all of the picking actions.
        action =
            new SelectionAction("Pick",
                                GraphicalSelectionEvent.PICK,
                                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0));
        selectionActions.add(action);
        addActionEntry(KeyEvent.VK_SPACE,action);

        action =
            new SelectionAction("Pick (add)",
                                GraphicalSelectionEvent.PICK_ADD,
                                KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,0));
        selectionActions.add(action);
        addActionEntry(KeyEvent.VK_EQUALS,action);
        addActionEntry(KeyEvent.VK_PLUS,action);

        action =
            new SelectionAction("Un-Pick",
                                GraphicalSelectionEvent.UNPICK,
                                KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,0));
        selectionActions.add(action);
        addActionEntry(KeyEvent.VK_UNDERSCORE,action);
        addActionEntry(KeyEvent.VK_MINUS,action);

        action =
            new ClearAction(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        selectionActions.add(action);
        addActionEntry(KeyEvent.VK_ESCAPE,action);
        addActionEntry(KeyEvent.VK_BACK_SPACE,action);
        addActionEntry(KeyEvent.VK_DELETE,action);
    }

    /**
     * Activate or inactivate all of the selection actions. */
    protected void setSelectionActionsEnabled(boolean enable) {

        Iterator i = selectionActions.iterator();
        while (i.hasNext()) {
            Action action = (Action) i.next();
            action.setEnabled(enable);
        }
    }

    /**
     * The default implementation of this method does nothing.
     * Subclasses should provide the needed functionality to ensure
     * that the selection is no longer visible after this method is
     * called. */
    public void resetSelection() {
    }

    /**
     * This method returns the popup menu for this component.  This
     * may be modified by subclasses of this event to provide needed
     * menu items.
     *
     * @return the component's popup menu */
    public JPopupMenu getPopupMenu() {
        return popup;
    }

    /**
     * This resets a flag which indicates when a popup menu is being
     * processed.  This should be called by subclasses when the
     * ActionEvent from a popup menu is received. */
    protected void cancelPopupProcessing() {
        processingPopup = false;
    }

    /**
     * This method determines whether or not a popup menu is being
     * processed.  If it is then this will return true and the mouse
     * event should be ignored.
     *
     * @param e MouseEvent passed into mouse handling routine
     *
     * @return a boolean indicating whether or not to ignore the mouse
     * event */
    public boolean isProcessingPopup(MouseEvent e) {

        int id = e.getID();
        boolean flag = processingPopup;

        if (id==MouseEvent.MOUSE_PRESSED) {
            if (!processingPopup && testPopupTrigger(e)) {
                getPopupMenu().show(e.getComponent(),
                                    e.getX(),e.getY());
                processingPopup = true;
                flag = true;
            }
        } else if (id==MouseEvent.MOUSE_RELEASED) {
            if (!processingPopup && testPopupTrigger(e)) {
                getPopupMenu().show(e.getComponent(),
                                    e.getX(),e.getY());
                processingPopup = true;
                flag = true;
            } else if (processingPopup &&
                       !getPopupMenu().isVisible()) {
                processingPopup = false;
                flag = true;
            }
        }
        return flag;
    }

    /**
     * This component does not support borders.  If this method is
     * called with any non-null argument, then an
     * IllegalArgumentException is thrown.  If a border is desired,
     * then this component should be embedded within container which
     * has one.
     *
     * @param border must be null */
    public final void setBorder(Border border) {
        if (border!=null)
            throw new IllegalArgumentException(NON_NULL_BORDER_ERROR);
    }

    /**
     * This component does not support borders.  Null is always
     * returned by this method.
     *
     * @return null */
    public final Border getBorder() {
        return null;
    }

    /**
     * Moves and resizes this component.  This is overridden so that
     * the selection can be reset if the size changes.
     *
     * @param x x-coordinate of component
     * @param y y-coordinate of component
     * @param width width of the component
     * @param height height of the component */
    public void setBounds(int x, int y, int width, int height) {
        resetSelection();
        super.setBounds(x,y,width,height);
    }

    /**
     * Add a GraphicalSelectionListener.
     *
     * @param listener the GraphicalSelectionListener to add  */
    public void
        addGraphicalSelectionListener(GraphicalSelectionListener
                                      listener) {

        listenerList.add(GraphicalSelectionListener.class, listener);
    }

    /**
     * Remove a GraphicalSelectionListener.
     *
     * @param listener the GraphicalSelectionListener to remove */
    public void
        removeGraphicalSelectionListener(GraphicalSelectionListener
                                         listener) {
        listenerList.remove(GraphicalSelectionListener.class,
                            listener);
    }

    /**
     * Send the GraphicalSelectionMade event to all currently
     * registered GraphicalSelectionListeners.
     *
     * @param gsEvent the GraphicalSelectionEvent which is sent to all
     * currently registered GraphicalSelectionListeners */
    protected void fireGraphicalSelectionMade(GraphicalSelectionEvent gsEvent){

        Object[] listeners = listenerList.getListenerList();

        for (int i=listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==GraphicalSelectionListener.class) {
                ((GraphicalSelectionListener)listeners[i+1]).
                    graphicalSelectionMade(gsEvent);
            }
        }
    }

    /**
     * Invoked when the mouse has been clicked on a component.  This
     * is an empty method which subclasses should override if
     * necessary.
     *
     * @param e MouseEvent describing action */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when the mouse enters a component.   This method just
     * requests the keyboard focus.  Subclasses which override this
     * method should also request the keyboard focus with a call to
     * requestFocus().
     *
     * @param e MouseEvent describing action */
    public void mouseEntered(MouseEvent e) {
        requestFocus();
    }

    /**
     * Invoked when the mouse exits a component. This is an empty
     * method which subclasses should override if necessary.
     *
     * @param e MouseEvent describing action */
    public void mouseExited(MouseEvent e) {}

    /**
     * Invoked when the mouse button has been pressed on a component.
     * This is an empty method which subclasses should override if
     * necessary.
     *
     * @param e MouseEvent describing action */
    public void mousePressed(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been released on a component.
     * This is an empty method which subclasses should override if
     * necessary.
     *
     * @param e MouseEvent describing action */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  This is an empty method which subclasses should
     * override if necessary.
     *
     * @param e MouseEvent describing action */
    public void mouseDragged(MouseEvent e) {}

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons down).  This is an empty method which
     * subclasses should override if necessary.
     *
     * @param e MouseEvent describing action */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Invoked when a key has been pressed.  This is an empty method
     * which subclasses should override if necessary.
     *
     * @param e KeyEvent describing key which has been pressed. */
    public void keyPressed(KeyEvent e) {}

    /**
     * Process key-released events.  This defines and uses the following key
     * bindings:
     *
     * Subclasses may override this method to provide additional
     * key-bindings.  However if the subclass doesn't handle a particular
     * key event, this method should be called.
     *
     * @param e KeyEvent describing the key which has been released */
    public void keyReleased(KeyEvent e) {

        // Get the keystroke.  Ignore the modifiers for all keys except the
        // arrow keys.
        int keyCode = e.getKeyCode();
        int modifiers = 0;
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode,modifiers);

        InputMap inputMap = getInputMap();
        Object actionKey = inputMap.get(keyStroke);

        if (actionKey!=null) {
            Action action = (Action) actionMap.get(actionKey);
            if (action!=null) {
                action.actionPerformed(actionEvent);
            }
        }
    }

    /**
     * Invoked when a key has been typed.  This is an empty method
     * which subclasses should override if necessary.
     *
     * @param e KeyEvent describing key which has been typed. 
     */
    public void keyTyped(KeyEvent e) {}

    /**
     * A utility function which creates an appropriate selection event
     * when the user accepts the current selection and sends it to all
     * listeners.  */
    protected void makeSelectionEvent(int type) {}

    /**
     * A utility method which tests to see if the given mouse event
     * should trigger the popup menu.  Normally, Java itself has an
     * isPopupTrigger() method, but this doesn't work reliably under
     * Windows.  This method will return true if the mouse click is on
     * the right button. */
    protected boolean testPopupTrigger(MouseEvent e) {
        int modifiers = e.getModifiers();
        return ((modifiers & InputEvent.BUTTON3_MASK)!=0);
    }

    /**
     * This class defines the Select action.  This causes a
     * GraphicalSelectionEvent to be generated and sent to all
     * listeners. */
    class SelectionAction
        extends AbstractAction {

        private int actionCode;

        public SelectionAction(String name, int actionCode) {
            super(name);
            this.actionCode = actionCode;
        }

        public SelectionAction(String name,
                               int actionCode,
                               KeyStroke keyStroke) {
            this(name,actionCode);
            putValue(ACCELERATOR_KEY,keyStroke);
        }

        public void actionPerformed(ActionEvent e) {
            cancelPopupProcessing();
            if (isEnabled()) {
                makeSelectionEvent(actionCode);
            }
        }
    }

    /**
     * This class defines the Clear action.  This causes the current
     * selection to be cleared. */
    class ClearAction
        extends AbstractAction {
        public ClearAction(KeyStroke keyStroke) {
            super("Clear");
            putValue(ACCELERATOR_KEY,keyStroke);
        }
        public void actionPerformed(ActionEvent e) {
              cancelPopupProcessing();
              resetSelection();
        }
    }

    /**
     * This utility method binds an action to a particular key.  The
     * associated action will be done when the given key is typed. */
    protected void addActionEntry(int keyCode, Action action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode,0);
        addActionEntry(keyStroke,action);
    }

    /**
     * This utility method binds an action to a KeyStroke.  The associated
     * action will be done when the given KeyStroke is encountered. */
    protected void addActionEntry(KeyStroke keyStroke, Action action) {
        Object actionMapKey = action.getValue(Action.NAME);
        actionMap.put(actionMapKey,action);

        InputMap inputMap = getInputMap();
        inputMap.put(keyStroke,actionMapKey);
    }

}
