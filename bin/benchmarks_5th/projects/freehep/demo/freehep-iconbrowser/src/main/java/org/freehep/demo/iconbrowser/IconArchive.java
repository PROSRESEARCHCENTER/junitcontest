/*
 * IconArchive.java
 *
 * Created on March 15, 2001, 12:23 PM
 */


package org.freehep.demo.iconbrowser;

/**
 * An interface to be implemented by an IconArchive
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: IconArchive.java 10506 2007-01-30 22:48:57Z duns $
 */
interface IconArchive extends IconDirectory
{
    void close();
}