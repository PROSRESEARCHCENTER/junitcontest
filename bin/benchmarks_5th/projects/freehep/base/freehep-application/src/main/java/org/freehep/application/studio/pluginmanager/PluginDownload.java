package org.freehep.application.studio.pluginmanager;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.freehep.application.studio.Studio;

/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: PluginDownload.java 15922 2014-03-04 22:41:44Z onoprien $
 */
class PluginDownload extends JPanel implements Runnable, ActionListener {

    private Map<File,String> files;
    private Map<File,ByteArrayOutputStream> downloads;
    private Throwable status;
    private int size = 1;
    private int read = 0;
    private int iFile = 0;
    private JLabel label1;
    private JLabel label2;
    private JProgressBar progress1;
    private JProgressBar progress2;
    static final Logger logger = Logger.getLogger(PluginDownload.class.getName());


    PluginDownload(Map<File,String> files) {
        super(new GridLayout(4, 1));

        this.files = files;
        int n = files.size();
        label1 = new JLabel("File 1/" + n);
        label2 = new JLabel("Downloading ...");
        progress1 = new JProgressBar(0, n * 100);
        progress2 = new JProgressBar();
        progress1.setStringPainted(true);
        progress2.setStringPainted(true);

        add(label1);
        add(progress1);
        add(label2);
        add(progress2);
    }

    Throwable getStatus() {
        return status;
    }

    void cleanUp() {
        downloads = null;
    }

    void commit() {
        File f = null;
        try {
            for (Map.Entry<File,ByteArrayOutputStream> entry : downloads.entrySet()) {
                ByteArrayOutputStream bytes = entry.getValue();
                f = entry.getKey();
                File fDir = f.getParentFile();
                if (!fDir.exists()) {
                    if (!fDir.mkdirs()) throw new IOException("Cannot create "+ fDir.getAbsolutePath());
                }
                OutputStream out = new FileOutputStream(f);
                bytes.writeTo(out);
                out.close();
            }
        } catch (IOException x) {
            Studio.getApplication().error("Unable to save the downloaded library to "+ f, x);
            logger.log(Level.SEVERE,"Error during download",x);
        } finally {
            downloads = null;
        }
    }

    @Override
    public void run() {
        Timer timer = new Timer(100, this);
        timer.start();

        try {
            downloads = new HashMap<File,ByteArrayOutputStream>();
            iFile = 0;
            for (Map.Entry<File,String> entry : files.entrySet()) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                read = 0;
                final URL url = new URL(entry.getValue());

                SwingUtilities.invokeLater(new Update(iFile, url, "Downloading"));

                java.net.URLConnection connect = url.openConnection();
                size = connect.getContentLength();
                InputStream in = connect.getInputStream();
                byte[] buffer = new byte[8196];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    for (;;) {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        int l = in.read(buffer);
                        if (l < 0) {
                            break;
                        }
                        out.write(buffer, 0, l);
                        read += l;
                    }
                    out.close();
                    // Verify that this really is a Jar file
                    read = size;
                    SwingUtilities.invokeLater(new Update(iFile, url, "Verify"));
                    InputStream verifyStream = new ByteArrayInputStream(out.toByteArray());
                    JarInputStream verify = new JarInputStream(verifyStream);
                    int nEntries = 0;
                    for (;;) {
                        ZipEntry jarEntry = verify.getNextEntry();
                        if (jarEntry == null) {
                            break;
                        }
                        nEntries++;
                    }
                    verify.close();
                    if (nEntries == 0) {
                        throw new IOException("Downloaded jar had no entries " + ((File) entry.getKey()).getName());
                    }
                    downloads.put(entry.getKey(), out);
                } finally {
                    in.close();
                }
                iFile++;
            }
        } catch (Throwable t) {
            status = t;
        } finally {
            SwingUtilities.invokeLater(new Update(files.size(), null, null));
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        progress2.setValue(read);
        progress2.setMaximum(size);
        progress1.setValue(100 * iFile + 100 * read / size);
    }

    private class Update implements Runnable {

        private int n;
        private URL url;
        private String message;

        Update(int n, URL url, String message) {
            this.n = n;
            this.url = url;
            this.message = message;
        }

        @Override
        public void run() {
            label1.setText("File " + (n + 1) + "/" + files.size());
            progress1.setValue(100 * n);
            if (url == null) {
                // Download complete
                JDialog dlg = (JDialog) SwingUtilities.getAncestorOfClass(JDialog.class, PluginDownload.this);
                if (dlg != null) {
                    dlg.dispose();
                }
            } else {
                String file = url.getFile();
                int pos = file.lastIndexOf('/');
                if (pos > 0) {
                    file = file.substring(pos + 1);
                }
                label2.setText(message + " " + file + "...");
            }
            actionPerformed(null);
        }
    }
}
