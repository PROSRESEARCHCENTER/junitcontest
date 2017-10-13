/*
 * IconDirectory.java
 *
 * Created on March 15, 2001, 12:23 PM
 */

package org.freehep.demo.iconbrowser;

import javax.swing.Icon;

/**
 * Represents a directory in an IconArchive
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: IconDirectory.java 10506 2007-01-30 22:48:57Z duns $
 */

interface IconDirectory
{
    String getName();
    int getNEntries();
    Icon getEntryIcon(int index);
    String getEntryName(int index);
}
