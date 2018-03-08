package org.freehep.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

/**
 * A SplashScreen for use when starting applications. The splash screen contains
 * a title, and image and a status bar. The status bar can display messages as
 * well as the percentage of completion using a progress bar.
 *
 * @author Tony Johnson
 * @version $Id: SplashScreen.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public class SplashScreen extends JWindow {

    private JProgressBar progress;

    /**
     * Create a new SplashScreen
     *
     * @param coolPicture The image to display in the splash screen
     * @param initialMessage The initial message to display in the progress area
     * @param title The title of the splash screen window, may be null
     */
    public SplashScreen(Icon coolPicture, String initialMessage, String title) {
        // Create a JPanel so we can use a BevelBorder
        JPanel panelForBorder = new JPanel(new BorderLayout());
        panelForBorder.setBackground(Color.white);
        panelForBorder.setLayout(new BorderLayout());
        panelForBorder.add(new JLabel(coolPicture), BorderLayout.CENTER);
        if (title != null) {
            panelForBorder.add(new JLabel(title, JLabel.CENTER), BorderLayout.NORTH);
        }

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        progress.setString(initialMessage);

        panelForBorder.add(progress, BorderLayout.SOUTH);
        panelForBorder.setBorder(new BevelBorder(BevelBorder.RAISED));

        getContentPane().add(panelForBorder);
    }

    /**
     * Show or hide the splash screen.
     *
     * @param show True to display the splash screen
     */
    @Override
    public void setVisible(boolean show) {
        if (show) {
            pack();

            // Plonk it on center of screen
            Dimension WindowSize = getSize(),
                    ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds((ScreenSize.width - WindowSize.width) / 2,
                    (ScreenSize.height - WindowSize.height) / 2, WindowSize.width,
                    WindowSize.height);
        }
        super.setVisible(show);
    }

    /**
     * Updates the status bar. This method is thread safe and can be called from
     * any thread.
     *
     * @param message The message to display
     * @param percent The percentage towards completion
     */
    public void showStatus(String message, int percent) {
        if (isVisible()) {
            SwingUtilities.invokeLater(new UpdateStatus(message, percent));
        }
    }

    /**
     * Close the splash screen and free any resources associated with it. This
     * method is thread safe and can be called from any thread.
     */
    public void close() {
        if (isVisible()) {
            SwingUtilities.invokeLater(new CloseSplashScreen());
        }
    }

    private class UpdateStatus implements Runnable {

        public UpdateStatus(String status, int pc) {
            message = status;
            value = pc;
        }

        @Override
        public void run() {
            progress.setValue(value);
            progress.setString(message);
        }
        private String message;
        private int value;
    }

    private class CloseSplashScreen implements Runnable {

        @Override
        public void run() {
            setVisible(false);
            dispose();
        }
    }
}
