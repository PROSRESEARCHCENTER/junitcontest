// Copyright 2003-2004, SLAC, Stanford, U.S.A.
package org.freehep.swing.undo;

import java.awt.Component;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.Timer;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Extension of DoableEdit which allows the Edit to animate a shape before doing
 * the redoEdit or undoEdit.
 *
 * Subclasses of this class should override redoEdit() and undoEdit().
 *
 * @author Mark Donszelmann
 * @version $Id: AnimatedEdit.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class AnimatedEdit extends AbstractDoableEdit implements LinkableEdit {

    private static Logger logger = Logger.getLogger(AnimatedEdit.class.getPackage().getName());
    private int frames;
    private LinkableEdit parent;
    private LinkableEdit nextEdit, previousEdit;

    /**
     * Creates an animated edit with given number of frames.
     */
    public AnimatedEdit(int frames) {
        this.frames = frames;
    }

    public LinkableEdit getParent() {
        return parent;
    }

    public void setParent(LinkableEdit parent) {
        assert(this.parent == null) : "parent can only be set once";
        this.parent = parent;
    }

    public LinkableEdit getNextEdit() {
        return nextEdit;
    }

    public LinkableEdit getPreviousEdit() {
        return previousEdit;
    }

    public void setNextEdit(LinkableEdit edit) {
        assert (nextEdit == null) || (edit == null) : "trying to set next while already set";
        nextEdit = edit;
    }

    public void setPreviousEdit(LinkableEdit edit) {
        assert(previousEdit == null) || (edit == null) : "trying to set previous while already set";
        previousEdit = edit;
    }

    /**
     * Called when the edit is (re)done. Do not override, use redoEdit instead.
     */
    public void redo() throws CannotRedoException {
        super.redo();
        logger.fine(this.toString());
        if (frames > 0) {
            new TimedEdit(true);
        } else {
            redoAndNext();
        }
    }

    private void redoAndNext() throws CannotRedoException {
        redoEdit();
        LinkableEdit current = this;
        do {
            LinkableEdit next = current.getNextEdit();
            if (next != null) {
                logger.finer("Found next in list: "+next);
                next.redo();
                return;
            }
            current = current.getParent();
        } while (current != null);
        logger.finer("Reached end of list");
    }

    /**
     * Called when the edit is undone. Do not override, use undoEdit instead.
     */
    public void undo() throws CannotUndoException {
        super.undo();
        logger.fine(this.toString());
        if (frames > 0) {
            new TimedEdit(false);
        } else {
            undoAndPrevious();
        }
    }

    private void undoAndPrevious() throws CannotUndoException {
        undoEdit();
        LinkableEdit current = this;
        do {
            LinkableEdit previous = current.getPreviousEdit();
            if (previous != null) {
                logger.finer("Found previous in list: "+previous);
                previous.undo();
                return;
            }
            current = current.getParent();
        } while (current != null);
        logger.finer("Reached start of list");
    }

    /**
     * Returns number of frames.
     */
    public int getFrames() {
        return frames;
    }

    public String toString() {
        return "AnimatedEdit: ("+frames+")";
    }

    /**
     * (Re)does the edit.
     */
    protected abstract void redoEdit();
    
    /**
     * Undoes the edit.
     */
    protected abstract void undoEdit();
    
    /**
     * Starts the animation for redo or undo.
     */
    protected abstract void startAnimation(boolean redo);
    
    /**
     * Show frameNo of animation.
     */
    protected abstract void showAnimation(int frameNo);
    
    /**
     * Ends the animation.
     */
    protected abstract void endAnimation();

    /**
     * Returns a transformed shape, from the given shape and component.
     */
    public Shape createTransformedShape(Component component, Shape shape) {
        return shape;
    }


    class TimedEdit implements ActionListener {
        private boolean redo;
        private Timer timer;
        private int steps;
        private long t0;

        public TimedEdit(boolean redo) {
            this.redo = redo;
            t0 = System.currentTimeMillis();
            logger.finer("Animation Starts for "+AnimatedEdit.this.getClass());
            timer = new Timer(3, this);
            timer.setInitialDelay(0);
            timer.start();
            AnimatedEdit.this.startAnimation(redo);
            steps = frames;
        }

        public void actionPerformed(ActionEvent event) {
            if (steps > 0) {
                steps--;
                int frameNo = (redo) ? frames-1-steps: steps;
                AnimatedEdit.this.showAnimation(frameNo);
            } else {
                AnimatedEdit.this.endAnimation();
                timer.stop();
                logger.finer("Animation Ends for "+AnimatedEdit.this.getClass()+
                            " in "+(System.currentTimeMillis()-t0)+" ms.");
                if (redo) {
                    redoAndNext();
                } else {
                    undoAndPrevious();
                }
            }
        }
    }
}
