/*
 * ImageViewer.java
 *
 * Created on February 15, 2001, 2:25 PM
 */

package org.freehep.application.test;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import org.freehep.application.Application;
import org.freehep.application.PrintPreview;
import org.freehep.application.ProgressMeter;
import org.freehep.application.RecentItemTextField;
import org.freehep.application.Stoppable;
import org.freehep.application.services.FileAccess;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;

/**
 * This is just a demo of the Application framework
 * @author  tonyj
 * @version $Id: ImageViewer.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ImageViewer extends Application implements Printable
{
    private ImageViewer()
    {
        super("ImageViewer");
        setBorder(BorderFactory.createLoweredBevelBorder());
        setBackground(Color.white);
        getStatusBar().add(meter);
    }
    public static void main(String[] argv)
    {
        new ImageViewer().createFrame(argv).setVisible(true);
    }
    public void paintComponent(Graphics g)
    {
        if (image!=null) g.drawImage(image,0,0,getWidth(),getHeight(),null);
        else super.paintComponent(g);
    }   protected CommandProcessor createCommandProcessor()
    {
        return new ImageViewerCommandProcessor();
    }
    
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
    {
        // Danger Will Robinson, not on event dispatch thread
        if ((infoflags & ImageObserver.ERROR) != 0)
        {
            error("Could not load image");
            setStatusMessage(null);
            meter.setStoppable(null);
            return false;
        }
        else if ((infoflags & ImageObserver.ABORT) != 0)
        {
            setStatusMessage(null);
            meter.setStoppable(null);
            return false;
        }
        else if ((infoflags & ImageObserver.ALLBITS) != 0)
        {
            image = img;
            getCommandProcessor().setChanged();
            setStatusMessage("Image loaded");
            meter.setStoppable(null);
            repaint();
            return false;
        }
        else return true;
    }
    private void decodeImage(Image image, String message)
    {
        StoppableImageProducer sip = new StoppableImageProducer(image.getSource());
        setStatusMessage(message);
        meter.setStoppable(sip);
        prepareImage(getToolkit().createImage(sip),this);
    }
    public int print(Graphics graphics,PageFormat pageFormat,int pageIndex) throws PrinterException
    {
        if (pageIndex != 0) return NO_SUCH_PAGE;
        if (image != null) graphics.drawImage(image,0,0,null);
        return PAGE_EXISTS;
    }
    
    public class ImageViewerCommandProcessor extends ApplicationCommandProcessor
    {
       /**
        * create a dialog to open a file
        */
        public void onOpen()
        {
            
            try
            {
                FileAccess file = getServiceManager().openFileDialog(null,null,"xxx");
                // Currently there is no approved way to create an image from a InputSource.
                // See Java bug id 4236738 and the proposed Image IO Framwork (JSR 15).
                // For now we cheat and read the file into a byte array, and then decode that :-(
                
                if (file != null)
                {
                    getRecentFileList("images").add(file.getName());
                    
                    setStatusMessage("Loading "+file.getName());
                    
                    int l = (int) file.getLength();
                    byte[] buffer = new byte[l];
                    InputStream in = file.getInputStream();
                    DefaultBoundedRangeModel brm = new DefaultBoundedRangeModel(0,0,0,l);
                    meter.setModel(brm);
                    try
                    {
                        for (int i=0; i<l;)
                        {
                            int rc = in.read(buffer,i,l-i);
                            if (rc<0) break;
                            i += rc;
                            brm.setValue(i);
                        }
                    }
                    finally
                    {
                        meter.setModel(null);
                        in.close();
                    }
                    Image image = ImageViewer.this.getToolkit().createImage(buffer);
                    decodeImage(image,"Decoding "+file.getName());
                }
            }
            catch (java.io.IOException x)
            {
                error("Could not read image",x);
            }
        }
        public void onTest()
        {
            try
            {
                if (getServiceManager().makeAvailable("test"))
                {
                    Image image = ImageViewer.this.getToolkit().createImage(getClass().getResource("testImage.png"));
                    decodeImage(image,"Decoding test image");
                }
            }
            catch (Exception x)
            {
                error("Cannot load test image",x);
            }
        }
        public void onOpenURL()
        {
            try
            {
                //String u = JOptionPane.showInputDialog(ImageViewer.this,"Open URL: ");
                String u = RecentItemTextField.showInputDialog(ImageViewer.this,"Open URL: ","openURL");
                if (u != null)
                {
                    URL url = new URL(u);
                    decodeImage(ImageViewer.this.getToolkit().createImage(url),"Loading "+url);
                }
            }
            catch (MalformedURLException x)
            {
                error("Illegal URL",x);
            }
        }
        public void onClose()
        {
            image = null;
            repaint();
            setChanged();
        }
        public void enableClose(CommandState state)
        {
            state.setEnabled(image != null);
        }
        public void onPrint()
        {
            getServiceManager().print(ImageViewer.this);
        }
        public void enablePrint(CommandState state)
        {
            state.setEnabled(image != null);
        }
        public void onPrintPreview()
        {
            try
            {
                PrintPreview pp = createPrintPreview();
                pp.setPrintable(ImageViewer.this);
                showDialog(pp.createDialog(ImageViewer.this),"PrintPreview");
            }
            catch (PrinterException x)
            {
                error("Could not create Print Preview",x);
            }
        }
        public void enablePrintPreview(CommandState state)
        {
            state.setEnabled(image != null);
        }
    }
    private Image image;
    private ProgressMeter meter = new ProgressMeter();
    
    private class ImageTracker extends DefaultBoundedRangeModel implements ImageConsumer
    {
        public void setPixels(int x,int y,int w,int h,ColorModel colorModel,int[] values,int off,int scansize)
        {
            readPixels += w*h;
            setValue(readPixels);
        }
        public void setPixels(int x,int y,int w,int h,ColorModel colorModel,byte[] values,int off,int scansize)
        {
            readPixels += w*h;
            setValue(readPixels);
        }
        public void setDimensions(int width,int height)
        {
            totPixels = width*height;
            setMaximum(totPixels);
        }
        public void imageComplete(int param) {}
        public void setColorModel(java.awt.image.ColorModel colorModel) {}
        public void setHints(int param) {}
        public void setProperties(java.util.Hashtable hashtable) {}
        
        private int readPixels;
        private int totPixels;
    }
    private class StoppableImageProducer implements ImageProducer, ImageConsumer, Stoppable
    {
        private ImageProducer source;
        private List consumers = new ArrayList();
        private ImageTracker tracker = new ImageTracker();
        
        StoppableImageProducer(ImageProducer source)
        {
            this.source = source;
        }
        public void requestTopDownLeftRightResend(ImageConsumer ic) {}
        public void removeConsumer(ImageConsumer ic) { consumers.remove(ic); }
        public void addConsumer(ImageConsumer ic) { consumers.add(ic); }
        public boolean isConsumer(ImageConsumer ic) { return consumers.contains(ic); }
        public void startProduction(ImageConsumer ic)
        {
            addConsumer(ic);
            addConsumer(tracker);
            source.startProduction(this);
        }
        public void setDimensions(int width,int height)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).setDimensions(width,height);
        }
        public void setProperties(Hashtable props)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).setProperties(props);

        }
        public void setColorModel(ColorModel model)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).setColorModel(model);
        }
        public void setHints(int hintflags)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).setHints(hintflags);
        }
        public void setPixels(int x,int y,int w,int h,ColorModel model,byte[] pixels,int off,int scansize)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).setPixels(x,y,w,h,model,pixels,off,scansize);
        }
        public void setPixels(int x,int y,int w,int h,ColorModel model,int[] pixels,int off,int scansize)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).setPixels(x,y,w,h,model,pixels,off,scansize);
       }
        public void imageComplete(int status)
        {
            Iterator i = consumers.iterator();
            while (i.hasNext()) ((ImageConsumer) i.next()).imageComplete(status);
        }
        public BoundedRangeModel getModel()
        {
            return tracker;
        }
        public void stop()
        {
            //source.removeConsumer(this);
            Iterator i = consumers.iterator();
            consumers = new ArrayList();
            while (i.hasNext()) ((ImageConsumer) i.next()).imageComplete(IMAGEABORTED);
         }
    }
}
