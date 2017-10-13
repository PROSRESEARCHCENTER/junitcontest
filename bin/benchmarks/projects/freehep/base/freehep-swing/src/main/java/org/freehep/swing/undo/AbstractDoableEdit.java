// Copyright 2003, SLAC, Stanford, U.S.A.
package org.freehep.swing.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

/**
 * Extension of AbstractUndoableEdit which allows the Edit to be used to execute
 * the action the first time using the redo() method.
 *
 * Subclasses of this class should override redo() and undo() and as the
 * first call in their methods call super.redo() or super.undo().
 *
 * @author Mark Donszelmann
 * @version $Id: AbstractDoableEdit.java 8584 2006-08-10 23:06:37Z duns $
 */

public class AbstractDoableEdit extends AbstractUndoableEdit implements DoableEdit {

    protected boolean firstTimeDone = false;

    public void redo() throws CannotRedoException {
        super.redo();
        firstTimeDone = true;
    }

    public boolean canRedo() {
        return (firstTimeDone) ? super.canRedo() : true;
    }

    public boolean canUndo() {
        return (firstTimeDone) ? super.canUndo() : false;
    }
}
