// Copyright 2003-2005, SLAC, Stanford, U.S.A.
package org.freehep.swing.undo;

import java.util.logging.Logger;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * @author Mark Donszelmann
 * @version $Id: AnimatedCompoundEdit.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AnimatedCompoundEdit extends CompoundEdit implements DoableEdit, LinkableEdit {

    private static Logger logger = Logger.getLogger(AnimatedCompoundEdit.class.getPackage().getName());
    /**
     * True if this edit has never received <code>end</code>.
     */
    boolean inProgress;
    boolean hasBeenDone;
    boolean alive;
    String name;

    /**
     * The start of the LinkableEdit sub chain
     */
    LinkableEdit first, last;

    LinkableEdit parent;
    LinkableEdit nextEdit, previousEdit;

    /**
     * Create an AnimatedCompoundEdit that still needs to be "(re)done".
     */
    public AnimatedCompoundEdit() {
        this(null);
    }

    public AnimatedCompoundEdit(String name) {
        this(name, false);
    }

    public AnimatedCompoundEdit(boolean done) {
        this(null, done);
    }

    /**
     * Create an AnimatedCompoundEdit that can be in either "done" or "undone" state.
     */
    public AnimatedCompoundEdit(String name, boolean done) {
    	super();
    	inProgress = true;
    	hasBeenDone = done;
    	alive = true;
    	this.name = name;
        logger.fine("begin "+((name != null) ? name : "anonymous"));
    }

    public LinkableEdit getParent() {
        return parent;
    }

    public void setParent(LinkableEdit parent) {
        assert(this.parent == null) : "parent can only be set once";
        this.parent = parent;
    }

    public LinkableEdit getFirstEdit() {
        return first;
    }
    
    public LinkableEdit getLastEdit() {
        return last;
    }

    public LinkableEdit getNextEdit() {
        return nextEdit;
    }

    public void setNextEdit(LinkableEdit edit) {
        assert (nextEdit == null) || (edit == null) : "trying to set next while already set";
        nextEdit = edit;
    }

    public LinkableEdit getPreviousEdit() {
        return previousEdit;
    }

    public void setPreviousEdit(LinkableEdit edit) {
        assert(previousEdit == null) || (edit == null) : "trying to set previous while already set";
        previousEdit = edit;
    }

    /**
     * Sends <code>undo</code> to all contained
     * <code>LinkableEdits</code> in the reverse of
     * the order in which they were added.
     */
    public void undo() throws CannotUndoException {
    	logger.finer("Started on last sub-edit");
    	if (!canUndo()) throw new CannotUndoException();
        last.undo();
        hasBeenDone = false;
    }

    /**
     * Sends <code>redo</code> to all contained
     * <code>LinkableEdit</code>s in the order in
     * which they were added.
     */
    public void redo() throws CannotRedoException {
    	logger.finer("Started on first sub-edit");
    	if (!canRedo()) throw new CannotRedoException();
        first.redo();
        hasBeenDone = true;
    }

    /**
     * Returns the last <code>LinkableEdit</code>, or <code>null</code>.
     */
    protected UndoableEdit lastEdit() {
    	return last;
    }

    /**
     * Sends <code>die</code> to each subedit,
     * in the reverse of the order that they were added.
     */
    public void die() {
    	LinkableEdit cursor = last;
    	while (cursor != null) {
    	    cursor.die();
    	    cursor = cursor.getPreviousEdit();
    	}
    	alive = false;
    }

    /**
     * Returns false since only LinkableEdits can be added.
     */
    public boolean addEdit(UndoableEdit edit) {
        if (edit instanceof LinkableEdit) return addEdit((LinkableEdit)edit);
        return false;
    }

    /**
     * If this edit is <code>inProgress</code>,
     * accepts <code>anEdit</code> and returns true.
     *
     * <p>The last edit added to this <code>CompoundEdit</code>
     * is given a chance to <code>addEdit(anEdit)</code>.
     * If it refuses (returns false), <code>anEdit</code> is
     * given a chance to <code>replaceEdit</code> the last edit.
     * If <code>anEdit</code> returns false here,
     * it is added to <code>edits</code>.
     *
     * @param edit the edit to be added
     * @return true if the edit is <code>inProgress</code>;
     *	otherwise returns false
     */
    public boolean addEdit(LinkableEdit edit) {
        logger.fine(edit.toString());
    	if (!inProgress) return false;

	    // If this is the first subedit received, just add it.
	    if (last == null) {
		    first = edit;
		    last = edit;
		    edit.setParent(this);
		    edit.setNextEdit(null);
		    edit.setPreviousEdit(null);
		    return true;
	    }

	    // Otherwise, give the last one a chance to absorb the new
	    // one.  If it won't, give the new one a chance to absorb
	    // the last one.
	    if (!last.addEdit(edit)) {
    		if (edit.replaceEdit(last)) {
    		    // remove last edit
                last = last.getPreviousEdit();
                last.setNextEdit(null);
		    }
		    // add new edit as last one
		    last.setNextEdit(edit);
		    edit.setParent(this);
		    edit.setNextEdit(null);
		    edit.setPreviousEdit(last);
		    last = edit;
	    }

	    return true;
    }

    /**
     * Sets <code>inProgress</code> to false.
     *
     * @see #canUndo
     * @see #canRedo
     */
    public void end() {
        logger.fine("end");
	    inProgress = false;
    }

    /**
     * Returns false if <code>isInProgress</code>.
     *
     * @see	#isInProgress
     */
    public boolean canUndo() {
	    return !isInProgress() && alive && hasBeenDone;
    }

    /**
     * Returns false if <code>isInProgress</code>.
     *
     * @see	#isInProgress
     */
    public boolean canRedo() {
	    return !isInProgress() && alive && !hasBeenDone;
    }

    /**
     * Returns true if this edit is in progress--that is, it has not
     * received end. This generally means that edits are still being
     * added to it.
     *
     * @see	#end
     */
    public boolean isInProgress() {
	    return inProgress;
    }

    /**
     * Returns true if any of the <code>LinkableEdit</code>s
     * in <code>edits</code> do.
     * Returns false if they all return false.
     */
    public boolean isSignificant() {
	    LinkableEdit cursor = first;
	    while (cursor != null) {
	        if (cursor.isSignificant()) {
		        return true;
	        }
	        cursor = cursor.getNextEdit();
	    }
	    return false;
    }

    /**
     * Returns name (if set).
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns <code>getPresentationName</code> from the
     * last <code>LinkableEdit</code> added. If this edit is empty,
     * calls super.
     */
    public String getPresentationName() {
        if (name != null) return name;
    	if (last != null) return last.getPresentationName();
    	return super.getPresentationName();
    }

    /**
     * Returns <code>getUndoPresentationName</code>
     * from the last <code>LinkableEdit</code>
     * added.
     * If edit is empty, calls super.
     */
    public String getUndoPresentationName() {
        if (name != null) return "Undo "+name;
    	if (last != null) return last.getUndoPresentationName();
    	return super.getUndoPresentationName();
    }

    /**
     * Returns <code>getRedoPresentationName</code>
     * from the last <code>LinkableEdit</code>
     * added.
     * If edit is empty, calls super.
     */
    public String getRedoPresentationName() {
        if (name != null) return "Redo "+name;
    	if (last != null) return last.getRedoPresentationName();
    	return super.getRedoPresentationName();
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        if (name != null) return name;
     	if (last != null) return last.toString();
    	return super.toString();
    }
}
