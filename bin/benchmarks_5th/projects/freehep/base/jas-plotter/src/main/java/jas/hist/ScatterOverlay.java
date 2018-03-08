package jas.hist;

import jas.plot.CoordinateTransformation;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.OverlayContainer;
import jas.plot.PlotGraphics;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;


/**
 * Horrendously complicated class for painting scatterplots. It is designed
 * to deal with scatter plots containing up to about a million points, and
 * uses Java's Image classes to deal with drawing the image reasonably
 * efficiently. The class is designed to allow the plot to be redrawn efficiently
 * when more points are added to the scatter plot (a common occurence).
 *
 * The class is also designed to deal reasonably with the scatter plot being
 * resized.
 */
class ScatterOverlay extends TwoDOverlay implements ImageObserver
{
   private Image imageCache = null;
   private Image newImage = null;
   private JASHist2DScatterData parent;
   private boolean async = false;

   ScatterOverlay(JASHist2DScatterData parent)
   {
      super(parent);
      this.parent = parent;
   }

   public void containerNotify(OverlayContainer c)
   {
      if (c == null)
      {
         Image image = imageCache;
         if (image != null)
         {
            ((ScatterImage) image.getSource()).abort();
         }
         image = newImage;
         if (image != null)
         {
            ((ScatterImage) image.getSource()).abort();
         }
      }
      super.containerNotify(c);
   }

   public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
   {
      //Attention, probably run in ImageProducer thread
      //System.out.println("image="+img+" infoflags="+Integer.toHexString(infoflags));
      if (img == imageCache)
      {
         if ((infoflags & ABORT) != 0)
         {
            imageCache = null;
         }
         else if ((infoflags & FRAMEBITS) != 0)
         {
            container.repaint();

            //System.out.println("REPAINT");
         }
      }
      else if (img == newImage)
      {
         if ((infoflags & ABORT) != 0)
         {
            newImage = null;
         }
      }

      return true;
   }

   public void paint(PlotGraphics g, boolean isPrinting)
   {
      async = !isPrinting;

      JASHistScatterPlotStyle style = (JASHistScatterPlotStyle) parent.style;
      if (style.getDisplayAsScatterPlot())
      {
         CoordinateTransformation xp = container.getXTransformation();
         final CoordinateTransformation yp = container.getYTransformation();

         if (xp instanceof DateCoordinateTransformation)
         {
            xp = new DateTransformationConverter((DateCoordinateTransformation) xp);
         }
         if (xp instanceof DoubleCoordinateTransformation && yp instanceof DoubleCoordinateTransformation)
         {
            final DoubleCoordinateTransformation xt = (DoubleCoordinateTransformation) xp;
            final DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) yp;

            final double x1 = xt.convert(xt.getPlotMin());
            final double x2 = xt.convert(xt.getPlotMax());
            final double y1 = yt.convert(yt.getPlotMin());
            final double y2 = yt.convert(yt.getPlotMax());
            final int height = (int) (y1 - y2);
            final int width = (int) (x2 - x1);

            //Image cache is the "current" image, if it doesnt exist
            //create it.
            Image current = imageCache;
            if (current == null)
            {
               current = container.createImage(new ScatterImage(width, height, xt, yt));
               imageCache = current;
               g.drawImage(current, x1, y2, this);

               //System.out.println("Imagecache created"+imageCache);
            }
            else
            {
               int oldWidth = current.getWidth(null);
               int oldHeight = current.getHeight(null);
               if ((oldWidth == width) && (oldHeight == height))
               {
                  // Size unchanged, simple paint
                  //System.out.println("Draw "+imageCache+" unscaled");
                  g.drawImage(current, x1, y2, this);
               }

               // height, width will be negative if the ImageProducer has
               // not filled them in yet.
               else if ((oldWidth > 0) && (oldHeight > 0))
               {
                  // Size changed, if we do not have an new image
                  // already in preparation create one now
                  Image next = newImage;
                  if (next == null)
                  {
                     next = container.createImage(new ScatterImage(width, height, xt, yt));
                     newImage = next;
                     container.prepareImage(next, this);

                     //System.out.println("newImage created"+newImage);
                  }
                  else
                  {
                     int newWidth = next.getWidth(null);
                     int newHeight = next.getHeight(null);
                     if ((newWidth > 0) && (newHeight > 0) && ((newWidth != width) || (newHeight != height)))
                     {
                        // the new image is already the wrong size
                        // TODO: Signal the image to start over with the new size
                        //next.setSize(width,height);
                     }
                  }

                  // Until the new image is ready we will draw a
                  // scaled version of the current image
                  //System.out.println("Draw "+imageCache+" scaled to "+width+","+height);
                  g.drawImage(current, x1, y2, width, height, this); // approximation for now
               }
            }
         }
      }
      else
      {
         super.paint(g,isPrinting);
      }
   }

   public void paintIcon(final PlotGraphics g, final int width, final int height)
   {
      JASHistScatterPlotStyle style = (JASHistScatterPlotStyle) parent.style;
      if (style.getDisplayAsScatterPlot())
      {
         g.setColor(style.getDataPointColor());
         g.fillRect(1, 1, width - 2, height - 2);
      }
      else
      {
         super.paintIcon(g, width, height);
      }
   }

   /**
    * Called if more points have been added to image
    */
   void continueImage()
   {
      //System.out.println("continueImage");
      Image image = imageCache;
      if (image != null)
      {
         ((ScatterImage) image.getSource()).continueDrawing();
      }
      image = newImage;
      if (image != null)
      {
         ((ScatterImage) image.getSource()).continueDrawing();
      }
   }

   void restartImage(boolean newEnumNeeded)
   {
      //System.out.println("restartImage"+newEnumNeeded);
      Image image = imageCache;
      if (image != null)
      {
         ((ScatterImage) image.getSource()).restart(newEnumNeeded);
      }
      image = newImage;
      if (image != null)
      {
         ((ScatterImage) image.getSource()).restart(newEnumNeeded);
      }
   }

   private void imageReallyComplete(ImageProducer producer)
   {
      // Attention, executed in thread of ImageProducer
      Image next = newImage;
      if ((next != null) && (next.getSource() == producer))
      {
         Image oldImage = imageCache;
         imageCache = next;
         oldImage.flush();
         newImage = null;
         container.repaint();

         //System.out.println("Image Replace");
      }
   }

   final class ScatterImage implements ImageProducer, Runnable
   {
      final private DoubleCoordinateTransformation xt;
      final private DoubleCoordinateTransformation yt;
      private ImageConsumer consumer;
      private ScatterEnumeration enumer;
      private Thread thread;
      private boolean abort = false;
      private boolean pastPointOfNoContinue = false;
      final private int height;
      final private int width;

      ScatterImage(int width, int height, DoubleCoordinateTransformation xt, DoubleCoordinateTransformation yt)
      {
         this.width = width;
         this.height = height;

         // TODO: Better coordinate transforms
         // These are live references to the coordinate transformation, so they
         // can change if the size of the plot changes while the image is
         // being produced :-(
         this.xt = xt;
         this.yt = yt;
      }

      public boolean isConsumer(ImageConsumer c)
      {
         return consumer.equals(c);
      }

      public void addConsumer(ImageConsumer c)
      {
         //System.out.println("addConsumer "+c);
         if (consumer != null)
         {
            throw new RuntimeException("Only single consumer supported");
         }
         consumer = c;
      }

      public void removeConsumer(ImageConsumer c)
      {
         consumer = null;
      }

      public void requestTopDownLeftRightResend(ImageConsumer c)
      {
         // forget it!
      }

      public void run()
      {
         ImageConsumer consumer = this.consumer;
         if (consumer != null)
         {
            deliverImage(consumer);
         }
         thread = null;

         //System.out.println("Thread stopped");
      }

      public void startProduction(ImageConsumer c)
      {
         //System.out.println("start");
         consumer = c;
         if (async)
         {
            thread = new Thread(this);
            thread.start();
         }
         else
         {
            run();
         }
      }

      void abort()
      {
         try
         {
            Thread t = thread;
            if (t != null)
            {
               //System.out.println("-----------Interrupt");
               abort = true;
               t.join();

               //System.out.println("-----------join");
            }
         }
         catch (InterruptedException x)
         {
         }
         finally
         {
            abort = false;
         }
      }

      /**
       * A continue message means more data points have been
       * added to the enumeration.
       */
      void continueDrawing()
      {
         Thread t = thread;
         if (t != null)
         {
            // Check if we are past the point of no continue
            // If not we don't need to do anything, because we
            // will naturally continue.
            synchronized (this)
            {
               if (!pastPointOfNoContinue)
               {
                  return;
               }
            }

            // If we are then we must wait for this thread to
            // complete before starting a new one
            try
            {
               t.join();
            }
            catch (InterruptedException x)
            {
            }
         }
         if (async)
         {
            thread = new Thread(this);
            thread.start();
         }
         else
         {
            run();
         }
      }

      /**
       * Restart means we need to start drawing the image again,
       * from the beginning.
       */
      void restart(final boolean newEnumNeeded)
      {
         abort(); // Stop the current thread
         if (newEnumNeeded)
         {
            enumer = null;
         }
         else if (enumer != null)
         {
            enumer.restart();
         }
         if (async)
         {
            thread = new Thread(this);
            thread.start();
         }
         else
         {
            run();
         }
      }

      private void deliverImage(ImageConsumer consumer)
      {
         JASHistScatterPlotStyle style = (JASHistScatterPlotStyle) parent.style;
         final Color c = style.getDataPointColor();
         final byte[] r = { 0, (byte) c.getRed() };
         final byte[] g = { 0, (byte) c.getGreen() };
         final byte[] b = { 0, (byte) c.getBlue() };

         //Fix to JAS-94 and JAS-232
         //TO-DO Figure out why setting the alpha value to (byte)255
         //the problems in the bugs above reappear.
         final byte[] a = { 0, (byte) 254 };
         final ColorModel model = new IndexColorModel(1, 2, r, g, b, a);
         byte[] pixels = new byte[width * height];

         consumer.setDimensions(width, height);
         consumer.setColorModel(model);

         final double[] d = new double[2];

         // for one image the following five variables are constant
         // (if any of them changes we have to start a new thread)
         final int point = style.getDataPointStyle();
         final int size = style.getDataPointSize();
         final int size2 = size / 2; // just to keep things fast
         final double x1 = xt.convert(xt.getPlotMin());
         final double y2 = yt.convert(yt.getPlotMax());

         if (enumer == null)
         {
            final double data_xMin = parent.dataSource.getXMin();
            final double data_xMax = parent.dataSource.getXMax();
            final double data_yMin = parent.dataSource.getYMin();
            final double data_yMax = parent.dataSource.getYMax();
            final double plot_xMin = xt.getPlotMin();
            final double plot_xMax = xt.getPlotMax();
            final double plot_yMin = yt.getPlotMin();
            final double plot_yMax = yt.getPlotMax();
            if ((data_xMin < plot_xMin) || (data_xMax > plot_xMax) || (data_yMin < plot_yMin) || (data_yMax > plot_yMax))
            {
               final double xMin = Math.max(data_xMin, plot_xMin);
               final double xMax = Math.min(data_xMax, plot_xMax);
               final double yMin = Math.max(data_yMin, plot_yMin);
               final double yMax = Math.min(data_yMax, plot_yMax);

               // if a point is beyond our regular x bounds by amount "xExtra"
               // or beyond our regular y bounds by amount "yExtra"
               // we want to include it anyway because at least part of
               // its dot will appear within the bounds
               final double xExtra = ((double) size2 * (plot_xMax - plot_xMin)) / (double) (xt.convert(plot_xMax) - x1);
               final double yExtra = ((double) size2 * (plot_yMax - plot_yMin)) / (double) (yt.convert(plot_yMin) - y2);

               if (((data_xMin + xExtra) < plot_xMin) || ((data_xMax - xExtra) > plot_xMax) || ((data_yMin + yExtra) < plot_yMin) || ((data_yMax - yExtra) > plot_yMax))
               {
                  enumer = parent.dataSource.startEnumeration(xMin - xExtra, xMax + xExtra, yMin - yExtra, yMax + yExtra);
               }
               else
               {
                  enumer = parent.dataSource.startEnumeration();
               }
            }
            else
            {
               enumer = parent.dataSource.startEnumeration();
            }
         }

         long nextUpdate = System.currentTimeMillis() + 200L;

         for (int n = 0; !abort; n++)
         {
            boolean ok = enumer.getNextPoint(d);
            if (!ok)
            {
               synchronized (this)
               {
                  // We need to double check, just in case
                  // more data was added since we checked a millisecond ago
                  // (arent threads fun)
                  ok = enumer.getNextPoint(d);
                  if (!ok)
                  {
                     pastPointOfNoContinue = true;

                     break;
                  }
               }
            }

            final int col = Math.round((float) (xt.convert(d[0]) - x1));
            final int row = Math.round((float) (yt.convert(d[1]) - y2));
            int i;
            int col_start;
            int col_end;
            int row_start;
            int row_end;

            if (size < 2)
            {
               if ((col < 0) || (col >= width))
               {
                  continue;
               }
               try
               {
                  pixels[(row * width) + col] = 1;
               }
               catch (final ArrayIndexOutOfBoundsException e)
               {
               }
            }
            else
            {
               switch (point)
               {
               case JASHistScatterPlotStyle.SYMBOL_BOX:
                  col_end = Math.min(width, col + size2);
                  row_start = Math.max(0, row - size2);
                  row_end = Math.min(height, row + size2);
                  for (i = row_start * width; row_start < row_end;
                        i += width, row_start++)
                     for (col_start = Math.max(0, col - size2);
                           col_start < col_end; col_start++)
                        try
                        {
                           pixels[i + col_start] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_TRIANGLE:
                  row_start = row - ((size * 64) / 100); // approximates the top of the triangle
                  i = Math.max(0, row_start) * width;
                  for (int j = Math.max(0, -row_start); j < size;
                        j++, i += width)
                  {
                     final int extra = j / 2;
                     col_end = Math.min(width - 1, col + extra);
                     for (col_start = Math.max(0, col - extra);
                           col_start <= col_end; col_start++)
                        try
                        {
                           pixels[i + col_start] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                  }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_DIAMOND:
                  row_start = Math.max(0, row - size2);
                  row_end = Math.min(height, row + size2);
                  i = row_start * width;
                  for (int j = Math.max(size2 - row, 0); j < size2;
                        j++, i += width)
                  {
                     col_end = Math.min(width - 1, col + j);
                     for (col_start = Math.max(0, col - j);
                           col_start <= col_end; col_start++)
                        try
                        {
                           pixels[i + col_start] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                  }
                  for (int j = size2; j >= 0; j--, i += width)
                  {
                     col_end = Math.min(width - 1, col + j);
                     for (col_start = Math.max(0, col - j);
                           col_start <= col_end; col_start++)
                        try
                        {
                           pixels[i + col_start] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                  }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_STAR:
                  i = row * width;
                  col_end = Math.min(width - 1, col + size2);
                  for (col_start = Math.max(0, col - size2);
                        col_start <= col_end; col_start++)
                     try
                     {
                        pixels[i + col_start] = 1;
                     }
                     catch (final ArrayIndexOutOfBoundsException e)
                     {
                     }
                  i -= (size2 * width);
                  for (int j = -size2; j <= size2; j++, i += width)
                  {
                     if ((col >= 0) && (col < width))
                     {
                        try
                        {
                           pixels[i + col] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                     }
                     if (((col + j) >= 0) && ((col + j) < width))
                     {
                        try
                        {
                           pixels[i + col + j] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                     }
                     if (((col - j) >= 0) && ((col - j) < width))
                     {
                        try
                        {
                           pixels[(i + col) - j] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                     }
                  }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_VERT_LINE:
                  if ((col >= 0) && (col < width))
                  {
                     row_start = Math.max(0, row - size2);
                     row_end = Math.min(height, row + size2);
                     i = (row_start * width) + col;
                     for (; row_start < row_end; row_start++, i += width)
                        try
                        {
                           pixels[i] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                  }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_HORIZ_LINE:
                  if ((row >= 0) && (row < height))
                  {
                     i = row * width;
                     col_end = Math.min(width, col + size2);
                     for (col_start = Math.max(0, col - size2);
                           col_start < col_end; col_start++)
                        try
                        {
                           pixels[i + col_start] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                  }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_CROSS:
                  i = row * width;
                  col_end = Math.min(width - 1, col + size2);
                  for (col_start = Math.max(0, col - size2);
                        col_start <= col_end; col_start++)
                     try
                     {
                        pixels[i + col_start] = 1;
                     }
                     catch (final ArrayIndexOutOfBoundsException e)
                     {
                     }
                  row_start = Math.max(0, row - size2);
                  row_end = Math.min(height - 1, row + size2);
                  i -= (((row - row_start) * width) - col);
                  for (; row_start <= row_end; row_start++, i += width)
                     try
                     {
                        pixels[i] = 1;
                     }
                     catch (final ArrayIndexOutOfBoundsException e)
                     {
                     }

                  break;

               case JASHistScatterPlotStyle.SYMBOL_SQUARE:
                  row_start = Math.max(0, row - size2);
                  row_end = Math.min(height, row + size2);
                  col_end = Math.min(width, col + size2);
                  i = row_start * width;
                  col_start = Math.max(0, col - size2);
                  if (row_start > 0)
                  {
                     for (; col_start < col_end; col_start++)
                        try
                        {
                           pixels[i + col_start] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                  }

                  final boolean l_Line = (col - size2) >= 0;
                  final boolean r_Line = (col + size2) < width;
                  i += col;
                  for (; row_start < row_end; row_start++, i += width)
                  {
                     if (l_Line)
                     {
                        try
                        {
                           pixels[i - size2] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                     }
                     if (r_Line)
                     {
                        try
                        {
                           pixels[i + size2] = 1;
                        }
                        catch (final ArrayIndexOutOfBoundsException e)
                        {
                        }
                     }
                  }
                  i -= col;
                  for (col_start = Math.max(0, col - size2);
                        col_start <= col_end; col_start++)
                     try
                     {
                        pixels[i + col_start] = 1;
                     }
                     catch (final ArrayIndexOutOfBoundsException e)
                     {
                     }

                  break;
               }
            }

            final long now = System.currentTimeMillis();
            if (now > nextUpdate)
            {
               consumer.setPixels(0, 0, width, height, model, pixels, 0, width);
               consumer.imageComplete(consumer.SINGLEFRAMEDONE);
               nextUpdate = now + 200;

               //System.out.println("Thread running n="+n);
            }
         }
         if (abort)
         {
            consumer.imageComplete(consumer.IMAGEABORTED);
         }
         else
         {
            // Note, we may still have more frames, for example if the style changes,
            // or if more points need to be displayed
            consumer.setPixels(0, 0, width, height, model, pixels, 0, width);
            consumer.imageComplete(consumer.SINGLEFRAMEDONE);
            imageReallyComplete(this);
         }
      }
   }
}
