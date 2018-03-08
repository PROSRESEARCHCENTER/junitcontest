package org.freehep.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.freehep.swing.layout.FlowScrollLayout;
import org.freehep.swing.popup.PopupListener;
import org.freehep.util.images.ImageHandler;

/**
 * This class can be used to add print preview capabilities to an application.
 * Originally taken from the book <a
 * href="http://manning.spindoczine.com/sbe/">Swing</a> by Matthew Robinson and
 * Pavel Vorobiev, Ph.D.
 *
 * @author Tony Johnson
 * @version $Id: PrintPreview.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class PrintPreview extends JPanel {

    private Pageable m_pageable;
    private JPanel m_preview;
    private Properties m_props;
    private int m_scale = 10;

    /**
     * Create an empty PrintPreview
     */
    public PrintPreview() {
        Application app = Application.getApplication();
        if (app != null) {
            m_props = app.getUserProperties();
            m_scale = PropertyUtilities.getInteger(m_props, "printPreviewScale", 10);
        }

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));
        add(createToolBar(), BorderLayout.NORTH);

        JScrollPane ps = new JScrollPane();
        FlowScrollLayout layout = new FlowScrollLayout(ps);
        layout.setAlignment(FlowLayout.LEFT);
        m_preview = new JPanel(layout);
        m_preview.addMouseListener(new PopupListener(createPopupMenu()));
        ps.setViewportView(m_preview);
        add(ps, BorderLayout.CENTER);
    }

    /**
     * Set a target Printable using the default PageFormat. Printables are only
     * suitable for printing a small number of pages, for more pages use a
     * Pageable instead.
     *
     * @param target The Printable to preview
     * @throws PrinterException If an exception occurs while trying to figure
     * out how many pages are available
     */
    public void setPrintable(Printable target) throws PrinterException {
        setPrintable(target, PrinterJob.getPrinterJob().defaultPage());
    }

    /**
     * Set a target Printable. Printables are only suitable for printing a small
     * number of pages, for more pages use a Pageable instead.
     *
     * @param target The Printable to preview
     * @param pf The PageFormat to use for the pages
     * @throws PrinterException If an exception occurs while trying to figure
     * out how many pages are available
     */
    public void setPrintable(Printable target, PageFormat pf) throws PrinterException {
        setPageable(new PageableFromPrintable(target, pf));
    }

    /**
     * Set a target Pageable.
     *
     * @param target The Pageable to preview
     */
    public void setPageable(Pageable target) {
        m_pageable = target;
        m_preview.removeAll();
        for (int i = 0; i < target.getNumberOfPages(); i++) {
            m_preview.add(new PagePreview(100 / m_scale, i));
        }
    }

    private void setScale(int i) {
        if (m_props != null) {
            PropertyUtilities.setInteger(m_props, "printPreviewScale", i);
        }

        Component[] comps = m_preview.getComponents();
        for (int k = 0; k < comps.length; k++) {
            if (!(comps[k] instanceof PagePreview)) {
                continue;
            }
            PagePreview pp = (PagePreview) comps[k];
            pp.setScale(100 / i);
        }
        m_preview.revalidate();
    }

    /**
     * Create a standard dialog that wraps the PrintPreview
     *
     * @param owner The owner of the dialog
     */
    public JDialog createDialog(Component owner) {
        return createDialog(owner, "Print Preview");
    }

    /**
     * Create a standard dialog that wraps the PrintPreview
     *
     * @param owner The owner of the dialog
     * @param title The title of the dialog
     */
    public JDialog createDialog(Component owner, String title) {
        Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, owner);
        JDialog dlg = w instanceof Frame ? new JDialog((Frame) w, title, true) : new JDialog((Dialog) w, title, true);
        dlg.setContentPane(this);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return dlg;
    }

    /**
     * Called if the user chooses Print. Override for custom printing.
     *
     * @return true if printing was completed, false if cancelled by user
     */
    protected boolean onPrint(Pageable document) throws PrinterException {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPageable(document);
        pj.print();
        return true;
    }

    protected void onError(PrinterException x) {
        JOptionPane.showMessageDialog(this, "Print Error: " + x.getMessage(), "Print Error...", JOptionPane.ERROR_MESSAGE);
    }

    protected final JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(getPrintAction());
        menu.add(getCloseAction());
        return menu;
    }

    /**
     * Called to create the toolbar. Override to customize the toolbar.
     */
    protected final JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.add(createCloseButton());
        tb.add(createPrintButton());
        tb.addSeparator();
        tb.add(createScaleChooser());
        tb.setFloatable(false); // Doesn't work in a dialog
        tb.setRollover(true);
        return tb;
    }

    private void done() {
        // If we are in a Dialog, dispose the dialog
        JDialog dlg = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, this);
        dlg.dispose();
    }

    public Action getCloseAction() {
        return new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                done();
            }
        };
    }

    public Action getPrintAction() {
        return new AbstractAction("Print", ImageHandler.getIcon("/toolbarButtonGraphics/general/Print16.gif", getClass())) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cursor oldCursor = getCursor();
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (onPrint(m_pageable)) {
                        done();
                    }
                } catch (PrinterException ex) {
                    onError(ex);
                } finally {
                    setCursor(oldCursor);
                }
            }
        };
    }

    /**
     * Creates the Close button
     */
    protected JComponent createCloseButton() {
        return new JButton(getCloseAction());
    }

    /**
     * Creates the Print Button
     */
    protected JComponent createPrintButton() {
        return new JButton(getPrintAction());
    }

    /**
     * Create the Scale chooser
     */
    protected JComponent createScaleChooser() {
        String[] scales = {"10 %", "25 %", "50 %", "100 %"};
        final JComboBox m_cbScale = new JComboBox(scales);
        m_cbScale.setMaximumSize(m_cbScale.getPreferredSize());
        m_cbScale.setEditable(true);
        m_cbScale.setSelectedItem(m_scale + " %");
        m_cbScale.addActionListener(new ActionListener() {
            private boolean isRentrant = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRentrant) {
                    return;
                }
                try {
                    isRentrant = true;
                    String str = m_cbScale.getSelectedItem().toString();
                    if (str.endsWith("%")) {
                        str = str.substring(0, str.length() - 1);
                    }
                    str = str.trim();
                    int scale;
                    try {
                        scale = Integer.parseInt(str);
                        if (scale <= 0) {
                            scale = 10;
                        } else if (scale > 100) {
                            scale = 100;
                        }
                    } catch (NumberFormatException ex) {
                        scale = 10;
                    }
                    String scaleString = scale + " %";
                    if (!scaleString.equals(str)) {
                        m_cbScale.setSelectedItem(scaleString);
                    }

                    setScale(scale);
                } finally {
                    isRentrant = false;
                }
            }
        });
        return m_cbScale;
    }

    private class PagePreview extends JPanel {

        private int m_page, m_scale, m_h, m_w;

        public PagePreview(int scale, int page) {
            m_page = page;
            m_scale = scale;
            PageFormat pf = m_pageable.getPageFormat(page);
            m_w = (int) (pf.getWidth() / scale);
            m_h = (int) (pf.getHeight() / scale);
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void setScale(int scale) {
            m_scale = scale;
            PageFormat pf = m_pageable.getPageFormat(m_page);
            m_w = (int) (pf.getWidth() / scale);
            m_h = (int) (pf.getHeight() / scale);
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            Insets ins = getInsets();
            return new Dimension(m_w + ins.left + ins.right, m_h + ins.top + ins.bottom);
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            paintBorder(g);
            Printable print = m_pageable.getPrintable(m_page);
            PageFormat pf = m_pageable.getPageFormat(m_page);
            try {
                Graphics2D g2 = (Graphics2D) g;
                Shape clip = new Double(pf.getImageableX(), pf.getImageableY(), pf.getImageableWidth(), pf.getImageableHeight());
                //g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                //					  RenderingHints.VALUE_TEXT_ANTIALIAS_ON ));
                g2.scale(1. / m_scale, 1. / m_scale);
                g2.clip(clip);
                print.print(g, pf, m_page);
            } catch (PrinterException x) {
            };
        }
    }

    private class PageableFromPrintable implements Pageable {

        PageableFromPrintable(Printable input, PageFormat pf) throws PrinterException {
            this.pf = pf;
            this.printable = input;

            // We create a 1 pixel buffered image, just so we can loop over
            // the pages to find out how many there are
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            for (nPages = 0;; nPages++) {
                if (input.print(g, pf, nPages) != Printable.PAGE_EXISTS) {
                    break;
                }
            }
        }
        private Printable printable;
        private PageFormat pf;
        private int nPages;

        @Override
        public int getNumberOfPages() {
            return nPages;
        }

        @Override
        public PageFormat getPageFormat(int pages) {
            return pf;
        }

        @Override
        public Printable getPrintable(int page) {
            return printable;
        }
    }
}
