package org.freehep.application.mdi;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.InternalFrameEvent;

import org.freehep.application.Application;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.commanddispatcher.CommandTargetManager;

/**
 * {@code PageManager} that manages its pages as InternalFrames on a Desktop.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: InternalFramePageManager.java 16331 2015-09-10 23:37:57Z onoprien $
 */
public class InternalFramePageManager extends PageManager {

    public InternalFramePageManager() {
        setPageManagerType("Window");
    }

    @Override
    protected void show(PageContext page) {
        try {
            JInternalFrame frame = frameForPage(page);
            frame.moveToFront();
            if (frame.isIcon()) {
                frame.setIcon(false);
            }
            frame.setSelected(true);
        } catch (PropertyVetoException x) {
        }
    }

    @Override
    protected Component getEmbodiment() {
        return top;
    }

    @Override
    protected boolean close(PageContext page) {
        boolean ok = super.close(page);
        if (ok) {
            ((JInternalFrame) map.remove(page)).dispose();
        }
        return ok;
    }

    @Override
    protected void titleChanged(PageContext page) {
        frameForPage(page).setTitle(page.getTitle());
    }

    @Override
    protected void iconChanged(PageContext page) {
        frameForPage(page).setFrameIcon(page.getIcon());
    }

    private JInternalFrame frameForPage(PageContext page) {
        return (JInternalFrame) map.get(page);
    }

    @Override
    public PageContext openPage(Component c, String title, Icon icon, String type, boolean selectOnOpen) {
        PageContext context = super.openPage(c, title, icon, type, selectOnOpen);
        JInternalFrame frame = new FrameWithContext(context);
        map.put(context, frame);
        top.add(frame);
        if (init) {
            cascadeFrame(frame);
        }
        frame.setVisible(true);
        super.firePageOpened(context);
        return context;
    }

    @Override
    protected void init(List pages, PageContext selected) {
        Iterator i = pages.iterator();
        while (i.hasNext()) {
            PageContext context = (PageContext) i.next();
            JInternalFrame frame = new FrameWithContext(context);
            map.put(context, frame);
            top.add(frame);
            frame.setVisible(true);
        }
        if (selected != null) {
            try {
                frameForPage(selected).setSelected(true);
            } catch (java.beans.PropertyVetoException x) {
            }
        }
        super.init(pages, selected);
    }

    private void cascadeFrame(JInternalFrame frame) {
        Dimension paneSize = top.getSize();
        Dimension frameSize = new Dimension();
        frameSize.height = (int) (paneSize.height * m_yCascadeFraction);
        frameSize.width = (int) (paneSize.width * m_xCascadeFraction);
        Point position = new Point(); // at (0, 0)
        position.translate(m_nCascaded * m_xCascadeOffset, m_nCascaded * m_yCascadeOffset);
        frame.setLocation(position);
        frame.setPreferredSize(frameSize);
        frame.setSize(frameSize);
        m_nCascaded++;
    }

    void arrange(int index) {
        m_nCascaded = 0;
        JInternalFrame[] frames = top.getAllFrames();
        // these are in reverse order from when they were last used
        JInternalFrame frame; // the current frame
        if (index == CASCADE) {
            for (int i = frames.length - 1; i >= 0; i--) {
                frame = frames[i];
                if (!frame.isIcon()) {
                    try {
                        frames[i].setMaximum(false);
                        cascadeFrame(frame);
                    } catch (java.beans.PropertyVetoException x) {
                    }
                }
            }
        } else {
            final int numberOfWindows = getPageCount();
            int nLeft = numberOfWindows;
            // Initially, the number left to put on the screen is the total number of frames.
            for (int i = 0; i < numberOfWindows; i++) {
                if (frames[i].isIcon()) {
                    nLeft--; // We don't count iconified frames.
                } else {
                    try {
                        frames[i].setMaximum(false);
                    } catch (java.beans.PropertyVetoException x) {
                        nLeft--;
                    }
                }
            }
            final int n = (int) Math.sqrt(nLeft);
            /*
             * In the case of horizontal tile, n is number of
             * columns; n is the number of rows in the case of
             * a vertical tile.
             */
            if (n < 1) {
                return;
            }
            final int paneWidth = top.getSize().width;
            final int paneHeight = top.getSize().height;
            final boolean isHorizontal = (index == TILE_HORIZONTALLY);
            final int xTranslation_1 = (isHorizontal) ? paneWidth / n : 0;
            final int yTranslation_1 = (isHorizontal) ? 0 : paneHeight / n;
            /*
             * The above translations are used to go between rows
             * in a vertical tile and between rows in a horizontal
             * tile.
             */
            int nThis; // number this row (vertical tile) or column (horizontal tile)
            int k = 0; // index for frames array, incremented as frames are accessed
            Dimension size = new Dimension(); // size of the current frame
            Point point = new Point(); // location of the current frame
            int xTranslation_2 = 0, yTranslation_2 = 0;
            /*
             * The above translations are used to go along rows
             * in a vertical tile and along rows in a horizontal
             * tile.
             */

            if (isHorizontal) {
                size.width = paneWidth / n; // n is the number of columns
            } else {
                size.height = paneHeight / n; // n is the number of rows
            }
            for (int i = 0; i < n; i++) {
                point.setLocation(0, 0);
                point.translate(i * xTranslation_1, i * yTranslation_1);
                // translated to the starting point for this row (vert case) or column (horiz case)
                nThis = nLeft / (n - i);
                // n - i is the number or rows (vert case) or columns (horiz case) remaining
                nLeft -= nThis;
                if (isHorizontal) {
                    yTranslation_2 = size.height = paneHeight / nThis;
                } // nThis is the number of rows in this column
                // yTranslation_2 is the amount of y translation between frames in this column
                // Leave xTranslation_2 as zero because there is no x transition while going along a column
                else {
                    xTranslation_2 = size.width = paneWidth / nThis;
                }
                // nThis is the number of columns in this row
                // xTranslation_2 is the amount of x translation between frames in this row
                // Leave yTranslation_2 as zero because there is no y transition while traversing a row
                int j = 0;
                while (true) {
                    do {
                        frame = frames[k++];
                    } while (frame.isIcon() || frame.isMaximum()); // Don't include iconified frames.
                    frame.setSize(new Dimension(size));
                    frame.setLocation(new Point(point));
                    if (++j < nThis) {
                        point.translate(xTranslation_2, yTranslation_2);
                    } // set up the point for the next frame in this column (horiz case) or row (vert case)
                    else {
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected CommandProcessor createCommandProcessor() {
        return new InternalFrameCommandProcessor();
    }

    @Override
    protected void setActive(boolean active) {
        if (active) {
            map = new HashMap();
            top = new MDIDesktopPane();
            init = false;
        } else {
            map = null;
            top = null;
        }
    }
    private int m_nCascaded = 0;
    private boolean init = false;
    private final int m_xCascadeOffset = 20, m_yCascadeOffset = 20;
    private final float m_xCascadeFraction = 0.75F, m_yCascadeFraction = 0.75F;
    public static final int CASCADE = -3;
    public static final int TILE_HORIZONTALLY = -4;
    public static final int TILE_VERTICALLY = -5;
    private Map map;
    private JDesktopPane top;

    protected class MDIDesktopPane extends JDesktopPane implements HasPopupItems {

        @Override
        public void doLayout() {
            super.doLayout();
            if (!init) {
                arrange(CASCADE);
                init = true;
            }
        }

        @Override
        public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
            if (source == this) {
                CommandTargetManager cm = Application.getApplication().getCommandTargetManager();
                JMenuItem cascade = new JMenuItem("Cascade");
                cascade.setMnemonic('C');
                cascade.setActionCommand("cascade");
                cm.add(new CommandSourceAdapter(cascade));
                menu.add(cascade);

                JMenuItem tileV = new JMenuItem("Tile Vertically");
                tileV.setMnemonic('V');
                tileV.setActionCommand("tileVertically");
                cm.add(new CommandSourceAdapter(tileV));
                menu.add(tileV);

                JMenuItem tileH = new JMenuItem("Tile Horizontally");
                tileH.setMnemonic('H');
                tileH.setActionCommand("tileHorizontally");
                cm.add(new CommandSourceAdapter(tileH));
                menu.add(tileH);

                JMenuItem close = new JMenuItem("Close All");
                close.setMnemonic('A');
                close.setActionCommand("closeAllPages");
                cm.add(new CommandSourceAdapter(close));
                menu.add(close);
            } else {
                InternalFramePageManager.this.modifyPopupMenu(menu, source, p);
            }
            return menu;
        }
    }

    private class FrameWithContext extends JInternalFrame {

        FrameWithContext(PageContext context) {
            super(context.getTitle(), true, true, true, true);
            this.context = context;
            setFrameIcon(context.getIcon());
            setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
            Component c = context.getPage();
            if (c instanceof Container) {
                setContentPane((Container) c);
            } else {
                getContentPane().add(c);
            }
        }

        @Override
        protected void fireInternalFrameEvent(int id) {
            switch (id) {
                case InternalFrameEvent.INTERNAL_FRAME_ICONIFIED:
                    ManagedPage mp = getManagedPage(context.getPage());
                    if (mp != null) {
                        mp.pageIconized();
                    }
                    firePageEvent(context, PageEvent.PAGEICONIZED);
                    getCommandProcessor().setChanged();
                    break;

                case InternalFrameEvent.INTERNAL_FRAME_DEICONIFIED:
                    mp = getManagedPage(context.getPage());
                    if (mp != null) {
                        mp.pageDeiconized();
                    }
                    firePageEvent(context, PageEvent.PAGEDEICONIZED);
                    getCommandProcessor().setChanged();
                    break;

                case InternalFrameEvent.INTERNAL_FRAME_CLOSING:
                    close(context);
                    break;

                case InternalFrameEvent.INTERNAL_FRAME_ACTIVATED:
                    fireSelectionChanged(context);
            }
            super.fireInternalFrameEvent(id);
        }
        private PageContext context;
    }

    class InternalFrameCommandProcessor extends PageManagerCommandProcessor {

        public void onCascade() {
            arrange(CASCADE);
        }

        public void onTileHorizontally() {
            arrange(TILE_HORIZONTALLY);
        }

        public void onTileVertically() {
            arrange(TILE_VERTICALLY);
        }

        public void onIconizeAll() {
            JInternalFrame[] frames = top.getAllFrames();
            for (int i = 0; i < frames.length; i++) {
                JInternalFrame frame = frames[i];
                try {
                    if (!frame.isIcon()) {
                        frame.setIcon(true);
                    }
                } catch (PropertyVetoException x) {
                }
            }
        }

        private boolean hasOpenPages() {
            JInternalFrame[] frames = top.getAllFrames();
            for (int i = 0; i < frames.length; i++) {
                if (!frames[i].isIcon()) {
                    return true;
                }
            }
            return false;
        }

        public void enableCascade(CommandState state) {
            state.setEnabled(hasOpenPages());
        }

        public void enableTileVertically(CommandState state) {
            state.setEnabled(hasOpenPages());
        }

        public void enableTileHorizontally(CommandState state) {
            state.setEnabled(hasOpenPages());
        }

        public void enableIconizeAll(CommandState state) {
            state.setEnabled(hasOpenPages());
        }
    }
}
