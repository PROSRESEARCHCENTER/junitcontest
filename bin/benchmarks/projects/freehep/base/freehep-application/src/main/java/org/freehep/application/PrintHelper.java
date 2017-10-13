package org.freehep.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Properties;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

import org.freehep.application.studio.Studio;

/**
 *
 * @author tonyj
 */
public class PrintHelper implements Printable {

    private static final String rootName = PrintHelper.class.getName() + ".";
    public final static int ORIENTATION_BEST_FIT = 0;
    public final static int ORIENTATION_PORTRAIT = 1;
    public final static int ORIENTATION_LANDSCAPE = 2;
    private Component target;
    private Font headerFooterFont = new Font("SansSerif", Font.PLAIN, 10);
    private PrintRequestAttributeSet atts = new HashPrintRequestAttributeSet();
    private PrinterJob pj;
    private String footer;
    private String header;
    private Studio app;
    private boolean drawBorder;
    private boolean scaleUp;
    private boolean showFooter;
    private boolean showHeader;
    private int orientation;

    public PrintHelper(Component target, Studio app) {
        this.target = target;
        this.app = app;

        Properties user = app.getUserProperties();

        drawBorder = PropertyUtilities.getBoolean(user, rootName + "drawBorder", false);
        scaleUp = PropertyUtilities.getBoolean(user, rootName + "scaleUp", false);
        showHeader = PropertyUtilities.getBoolean(user, rootName + "showHeader", true);
        showFooter = PropertyUtilities.getBoolean(user, rootName + "showFooter", false);
        footer = PropertyUtilities.getString(user, rootName + "footer", null);
        header = PropertyUtilities.getString(user, rootName + "header", "JAS3&b&d");
        orientation = PropertyUtilities.getInteger(user, rootName + "orientation", ORIENTATION_BEST_FIT);

        pj = PrinterJob.getPrinterJob();
        pj.setPrintable(PrintHelper.this);

        String defaultPrinter = user.getProperty(rootName + "defaultPrinter");
        PrintService ps = pj.getPrintService();
        if (ps == null) {
            throw new RuntimeException("No Print Service Found");
        }
        if ((defaultPrinter != null) && !ps.getName().equals(defaultPrinter)) {
            PrintService[] services = PrinterJob.lookupPrintServices();
            for (int i = 0; i < services.length; i++) {
                if (services[i].getName().equals(defaultPrinter)) {
                    try {
                        pj.setPrintService(services[i]);
                        ps = services[i];
                        break;
                    } catch (PrinterException x) {
                        x.printStackTrace();
                    }
                }
            }
        }

        atts.add(new JobName(user.getProperty(rootName + "printJobName", "JAS3 Plots"), null));

        Media media = (Media) ps.getDefaultAttributeValue(Media.class);

        float x = PropertyUtilities.getFloat(user, rootName + "printArea.top", 0);
        float y = PropertyUtilities.getFloat(user, rootName + "printArea.left", 0);
        float w = PropertyUtilities.getFloat(user, rootName + "printArea.right", 0);
        float h = PropertyUtilities.getFloat(user, rootName + "printArea.bottom", 0);
        if (x > 0) {
            atts.add(new MediaPrintableArea(x, y, w, h, MediaPrintableArea.INCH));
        }
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getFooter() {
        return footer;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setScaleUp(boolean scaleUp) {
        this.scaleUp = scaleUp;
    }

    public boolean getScaleUp() {
        return scaleUp;
    }

    public void setShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
    }

    public boolean isShowFooter() {
        return showFooter;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D orig = (Graphics2D) g;
        Graphics2D g2 = (Graphics2D) orig.create();
        g2.translate(pf.getImageableX(), pf.getImageableY());

        Dimension size = target.getSize();

        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        double portraiteRatio = Math.min(pageWidth / size.width, pageHeight / size.height);
        double landscapeRatio = Math.min(pageWidth / size.height, pageHeight / size.width);

        if (!scaleUp) {
            portraiteRatio = Math.min(portraiteRatio, 1);
            landscapeRatio = Math.min(landscapeRatio, 1);
        }

        boolean useLandscape = (orientation == ORIENTATION_BEST_FIT) ? (landscapeRatio > portraiteRatio) : (orientation == ORIENTATION_LANDSCAPE);

        if (useLandscape) {
            g2.rotate(Math.PI / 2);
            g2.translate(0, -pageWidth);
            g2.scale(landscapeRatio, landscapeRatio);
        } else {
            g2.scale(portraiteRatio, portraiteRatio);
        }
        target.print(g2);
        g2.dispose();
        if (drawBorder) {
            orig.setColor(Color.BLACK);
            orig.draw(new Rectangle2D.Double(pf.getImageableX(), pf.getImageableY(), pageWidth, pageHeight));
        }
        return Printable.PAGE_EXISTS;
    }

    void pageDialog(Component parent) {
        pj.pageDialog(atts);
    }

    public void print() throws PrinterException {
        pj.print(atts);
        commitChanges();
    }

    void printDialog(Component parent) {
        pj.printDialog(atts);
    }

    public void printPreview(Component parent) throws PrinterException {
        PrintPreview pp = new PrintPreview() {
            @Override
            protected boolean onPrint(Pageable document) throws PrinterException {
                PrintHelper.this.print();
                return true;
            }

            @Override
            protected void onError(PrinterException x) {
                Application.error(this, "Print Error", x);
            }
        };

        // We need to get a PageFormat object corresponding to the current attributes
        PageFormat pf = pj.defaultPage();
        Media media = (Media) atts.get(Media.class);
        if (media instanceof MediaSizeName) {
            MediaSize ms = MediaSize.getMediaSizeForName((MediaSizeName) media);
            MediaPrintableArea ma = (MediaPrintableArea) atts.get(MediaPrintableArea.class);

            //Orientation
            Paper paper = pf.getPaper();

            paper.setSize(72 * ms.getX(MediaSize.INCH), 72 * ms.getY(MediaSize.INCH));
            paper.setImageableArea(72 * ma.getX(MediaPrintableArea.INCH), 72 * ma.getY(MediaPrintableArea.INCH), 72 * ma.getWidth(MediaPrintableArea.INCH), 72 * ma.getHeight(MediaPrintableArea.INCH));
            pf.setPaper(paper);
        }
        pp.setPrintable(this, pf);
        app.showDialog(pp.createDialog(parent), "printPreview");
    }

    public void showOptionsDialog(Component parent) {
        PrintDialog pd = new PrintDialog(app, this);
        int rc = pd.showDialog(parent);
        if (rc == PrintDialog.OK_OPTION) {
            commitChanges();
        }
    }

    private void commitChanges() {
        Properties user = app.getUserProperties();
        PropertyUtilities.setBoolean(user, rootName + "drawBorder", drawBorder);
        PropertyUtilities.setBoolean(user, rootName + "scaleUp", scaleUp);
        PropertyUtilities.setBoolean(user, rootName + "showHeader", showHeader);
        PropertyUtilities.setBoolean(user, rootName + "showFooter", showFooter);
        PropertyUtilities.setString(user, rootName + "footer", footer);
        PropertyUtilities.setString(user, rootName + "header", header);
        PropertyUtilities.setInteger(user, rootName + "orientation", orientation);
        PropertyUtilities.setString(user, rootName + "defaultPrinter", pj.getPrintService().getName());

        JobName jobName = (JobName) atts.get(JobName.class);
        if (jobName != null) {
            PropertyUtilities.setString(user, rootName + "printJobName", jobName.getName());
        }

        MediaPrintableArea mp = (MediaPrintableArea) atts.get(MediaPrintableArea.class);
        if (mp != null) {
            PropertyUtilities.setFloat(user, rootName + "printArea.x", mp.getX(MediaPrintableArea.INCH));
            PropertyUtilities.setFloat(user, rootName + "printArea.y", mp.getY(MediaPrintableArea.INCH));
            PropertyUtilities.setFloat(user, rootName + "printArea.w", mp.getWidth(MediaPrintableArea.INCH));
            PropertyUtilities.setFloat(user, rootName + "printArea.h", mp.getHeight(MediaPrintableArea.INCH));
        }
        Media media = (Media) atts.get(Media.class);
        if (media instanceof MediaSizeName) {
            MediaSize ms = MediaSize.getMediaSizeForName((MediaSizeName) media);
            PropertyUtilities.setString(user, rootName + "mediaSize", media.getName());
        }

    }
}
