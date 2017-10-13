// Copyright 2004, Freehep.
package hep.graphics.heprep1;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * HepRepReader interface, for campatibility with HepRep2 (and almost the same as HepRep2's).
 *
 * @author Mark Donszelmann
 */
public interface HepRepReader {

    /**
     * Closes the reader and its underlying stream.
     *
     * @throws IOException in case of a stream problem.
     */
    public void close() throws IOException;

    /**
     * Allows random access.
     *
     * @return true if this reader provides random access
     * @throws IOException in case of a stream problem.
     */
    public boolean hasRandomAccess() throws IOException;

    /**
     * Reads a HepRep by name (random access only).
     *
     * @param name for the heprep to be read.
     * @return heprep.
     * @throws IOException in case of a stream problem.
     * @throws UnsupportedOperationException if the reader does not support random access.
     * @throws NoSuchElementException if the heprep is not available.
     */
    public HepRep read(String name) throws IOException, UnsupportedOperationException, NoSuchElementException;

    /**
     * Returns the current entry name (random acces only).
     *
     * @return name of the current entry or null if not supported.
     */
    public String entryName();

    /**
     * Returns a list of names of available entries (random rccess only). 
     * Zip files may contain instructions to skip a number of files. These files 
     * will not be included in the entries.
     *
     * @return list of entrynames or null if not supported.
     */
    public List/*<String>*/ entryNames();

    /**
     * Allows for sequential access.
     *
     * @return true if sequential access is possible.
     * @throws IOException in case of a stream problem.
     */
    public boolean hasSequentialAccess() throws IOException;

    /**
     * Resets a sequential HepRep reader.
     *
     * @throws IOException in case of a stream problem.
     * @throws UnsupportedOperationException if the reader does not support sequential access.
     */
    public void reset() throws IOException, UnsupportedOperationException;

    /**
     * Returns the (estimated) number of HepReps in the reader.
     * Zip files may contain instructions to skip a number of files. These files 
     * will not be included in the estimate.
     *
     * @return number of HepReps, or -1 if cannot be calculated.
     */
    public int size();

    /**
     * Skips a number of HepReps in the reader.
     * Zip files may contain instructions to skip a number of files. These files 
     * will not be included in the count to be skipped.
     *
     * @param n number of HepReps to be skipped.
     * @return number of HepReps skipped.
     * @throws UnsupportedOperationException if the reader does not support sequential access.
     */
    public int skip(int n) throws UnsupportedOperationException;

    /**
     * Is there a next heprep.
     *
     * @return true if the next heprep is available.
     * @throws UnsupportedOperationException if the reader does not support sequential access.
     * @throws IOException in case of a stream problem.
     */
    public boolean hasNext() throws IOException, UnsupportedOperationException;

    /**
     * Reads the next HepRep from the Reader.
     *
     * @return heprep.
     * @throws IOException in case of a stream problem.
     * @throws UnsupportedOperationException if the reader does not support sequential access.
     * @throws NoSuchElementException if the heprep is not available, or EOF.
     */
    public HepRep next() throws IOException, UnsupportedOperationException, NoSuchElementException;
} // class or interface

