// Copyright 2003, SLAC, Stanford, U.S.A.
package org.freehep.swing.undo;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 * @author Mark Donszelmann
 * @version $Id: AnimatedEditSupport.java 8584 2006-08-10 23:06:37Z duns $
 */

public class AnimatedEditSupport extends UndoableEditSupport {

    public AnimatedEditSupport() {
        super();
    }

    public AnimatedEditSupport(Object source) {
        super(source);
    }

    protected CompoundEdit createCompoundEdit() {
        // create a "done" AnimatedCompountEdit, since the edits themselves will
        // all be "redone" individually before adding.
	    return new AnimatedCompoundEdit(true);
    }
}
