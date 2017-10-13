// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.Color;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Displays a particular image in a separate frame for a given period
 * of time, then destroys the frame.
 *
 * @author Charles Loomis
 * @version $Id: SplashFrame.java 8584 2006-08-10 23:06:37Z duns $  */
public class SplashFrame
    extends JDialog 
    implements Runnable {

    /**
     * The time in milliseconds to wait before disposing of the
     * SplashScreen. */
    private int sleepTime;

    /**
     * The default time (in milliseconds) to wait before disposing of
     * the SplashScreen. */
    final private int defaultTime = 3000;

    public SplashFrame(String title, URL imageURL) {

        // Set the title.
        setTitle(title);

        // Make this window dispose of itself if it is closed. 
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Make a JLabel to hold the image.
        ImageIcon image = new ImageIcon(imageURL);
        JLabel label = new JLabel(image);

        // Put a black border around the label.
        label.setBorder(BorderFactory.createLineBorder(Color.black,3));

        // Make this the content pane.
        setContentPane(label);

        // Pack this frame and make it non-resizable.
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Cause this frame to disappear after the given delay (in ms). */
    public void disposeAfter(int millis) {
        sleepTime = Math.max(0,millis);

        // Start the thread.
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Cause this frame to disappear after the default delay (3 s). */
    public void disposeAfter() {
        disposeAfter(defaultTime);
    }

    /**
     * This routine just waits for the given time and then disposes of
     * the SplashFrame. */
    public void run() {

        // Make it visible and move it to the front.
        setVisible(true);
        toFront();

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
        this.dispose();
    }

}
