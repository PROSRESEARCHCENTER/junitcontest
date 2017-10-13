// Copyright 2003, SLAC, Stanford, U.S.A.
package org.freehep.swing.undo;

import javax.swing.undo.UndoableEdit;

/**
 * Allows the Edit to be used to execute
 * the action the first time using the redo() method.
 *
 * @author Mark Donszelmann
 * @version $Id: DoableEdit.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface DoableEdit extends UndoableEdit {
}
