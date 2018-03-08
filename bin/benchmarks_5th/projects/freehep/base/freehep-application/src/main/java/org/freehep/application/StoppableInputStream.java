package org.freehep.application;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * An input stream which also implements Stoppable. Useful when downloading
 * files in order to provide the user with feedback on progress.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StoppableInputStream.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class StoppableInputStream extends FilterInputStream implements Stoppable, BoundedRangeModel {

    private boolean stop = false;
    private int pos = 0;
    private int max;
    private int markPos;
    private EventListenerList listenerList = new EventListenerList();
    private ChangeEvent event = new ChangeEvent(this);

    public StoppableInputStream(URL url) throws IOException {
        this(url.openConnection());
    }

    private StoppableInputStream(URLConnection connection) throws IOException {
        super(connection.getInputStream());
        max = connection.getContentLength();
    }

    public StoppableInputStream(InputStream in, int length) {
        super(in);
        max = length;
    }

    @Override
    public BoundedRangeModel getModel() {
        return this;
    }

    @Override
    public void stop() {
        stop = true;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (stop) {
            throw new InterruptedIOException("IO aborted by user");
        }
        int l = super.read(b);
        pos += l;
        firePositionChanged();
        return l;
    }

    @Override
    public int read() throws IOException {
        if (stop) {
            throw new InterruptedIOException("IO aborted by user");
        }
        int value = super.read();
        pos++;
        firePositionChanged();
        return value;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (stop) {
            throw new InterruptedIOException("IO aborted by user");
        }
        int l = super.read(b, off, len);
        pos += l;
        firePositionChanged();
        return l;
    }

    @Override
    public long skip(long n) throws IOException {
        if (stop) {
            throw new InterruptedIOException("IO aborted by user");
        }
        long l = super.skip(n);
        pos += (int) l;
        firePositionChanged();
        return l;
    }

    @Override
    public void reset() throws IOException {
        if (stop) {
            throw new InterruptedIOException("IO aborted by user");
        }
        super.reset();
        pos = markPos;
        firePositionChanged();
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        markPos = pos;
    }

    @Override
    public void addChangeListener(ChangeListener x) {
        listenerList.add(ChangeListener.class, x);
    }

    @Override
    public void removeChangeListener(ChangeListener x) {
        listenerList.remove(ChangeListener.class, x);
    }

    private void firePositionChanged() {
        ChangeListener[] list = (ChangeListener[]) listenerList.getListeners(ChangeListener.class);
        for (int i = 0; i < list.length; i++) {
            list[i].stateChanged(event);
        }
    }

    @Override
    public int getExtent() {
        return 0;
    }

    @Override
    public int getMaximum() {
        return max;
    }

    @Override
    public int getMinimum() {
        return 0;
    }

    @Override
    public int getValue() {
        return pos;
    }

    @Override
    public boolean getValueIsAdjusting() {
        return false;
    }

    @Override
    public void setExtent(int newExtent) {
    }

    @Override
    public void setMaximum(int newMaximum) {
    }

    @Override
    public void setMinimum(int newMinimum) {
    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
    }

    @Override
    public void setValue(int newValue) {
    }

    @Override
    public void setValueIsAdjusting(boolean b) {
    }
}