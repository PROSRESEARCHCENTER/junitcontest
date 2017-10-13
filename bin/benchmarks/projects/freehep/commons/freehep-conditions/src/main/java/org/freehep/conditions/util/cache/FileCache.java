package org.freehep.conditions.util.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * A utility for downloading and caching files
 * @author tonyj
 */
public class FileCache {
    private static final File home = new File(getCacheRoot(), ".cache");
    private static final ByteFormat format = new ByteFormat();
    private File cache;
    private PrintStream print = System.out;

    public static File getCacheRoot() {
        String cacheDir = System.getProperty("org.freehep.cacheDir");
        if (cacheDir == null)
            cacheDir = System.getProperty("user.home");
        return new File(cacheDir);
    }

    /**
     * Create a FileCache using the default cache location
     * @throws java.io.IOException If the cache directory cannot be created
     */
    public FileCache() throws IOException {
        this(home);
    }

    /**
     * Create a file cache using an explicit cache location
     * @param cacheDirectory The directory to use for storing cached files
     * @throws java.io.IOException If the cache directory cannot be created
     */
    public FileCache(File cacheDirectory) throws IOException {
        setCacheDirectory(cacheDirectory);
    }

    /**
     * Get a file from the cache. If the file is already in the cache and
     * up-to-date the existing file will be returned. Otherwise the file will be
     * downloaded first, and then moved to the cache.
     * @param url The URL of the file to download
     * @throws java.io.IOException If the file cannot be downloaded, or if an
     *             error occurs writing to the cache.
     * @return The location of the cached file
     */
    public File getCachedFile(URL url) throws IOException {
        return getCachedFile(url, null);
    }

    /**
     * Get a file from the cache. If the file is already in the cache and
     * up-to-date the existing file will be returned. Otherwise the file will be
     * downloaded first, validated, and then moved to the cache.
     * @param url The URL of the file to download
     * @param validator A Validator that will be used to check the integrity of
     *            the downloaded file before it is placed in the cache
     * @throws java.io.IOException If the file cannot be downloaded, or if an
     *             error occurs writing to the cache, or if the file fails
     *             validation.
     * @return The location of the cached file
     */
    public File getCachedFile(URL url, Validator validator) throws IOException {
        File cacheFile = new File(cache, URLEncoder.encode(url.toExternalForm(), "UTF-8"));
        boolean downloadRequired = !cacheFile.exists();
        if (cacheFile.exists() && !Boolean.getBoolean("org.freehep.offline")) {
            // If we can access the URL, check if the cachefile is up-to-date
            try {
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                long updated = connection.getLastModified();
                long cached = cacheFile.lastModified();
                if (updated > cached)
                    downloadRequired = true;
            } catch (IOException x) {
                // if (print != null)
                // print.println("Warning: file cache could not access "+url+", "+x.getMessage());
            }
        }
        if (downloadRequired) {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            long size = connection.getContentLength();
            InputStream in = connection.getInputStream();
            if (in != null) {
                if (print != null)
                    print.println("Downloading..." + url);
                File temp = File.createTempFile("det", null, cache);
                temp.deleteOnExit();
                OutputStream out = new FileOutputStream(temp);
                byte[] buffer = new byte[8096];
                try {
                    long got = 0;
                    for (;;) {
                        int l = in.read(buffer);
                        if (l < 0)
                            break;
                        out.write(buffer, 0, l);
                        if (out != null) {
                            got += l;
                            if (print != null) {
                                print.print("Got ");
                                print.print(format.format(got));
                                print.print('/');
                                print.print(format.format(size));
                                print.print('\r');
                                print.flush();
                            }
                        }
                    }
                } finally {
                    in.close();
                    out.close();
                }
                if (validator != null)
                    validator.checkValidity(url, temp);
                // File looks ok, move it into the cache
                if (cacheFile.exists()) {
                    boolean ok = cacheFile.delete();
                    if (!ok)
                        throw new IOException("Error while deleting old cache file");
                }
                boolean ok = temp.renameTo(cacheFile);
                if (!ok)
                    throw new IOException("Error while moving file to cache");
            } else
                throw new IOException("Could not open " + url);
        }
        return cacheFile;
    }

    /**
     * Get the directory used for caching
     * @return The cache directory
     */
    public File getCacheDirectory() {
        return this.cache;
    }

    /**
     * Set a new cache directory location
     * @param cacheDirectory The directory to use.
     * @throws java.io.IOException If the specified directory cannot be created,
     *             or already exists and is not a directory.
     */
    public void setCacheDirectory(File cacheDirectory) throws IOException {
        if (!cacheDirectory.exists()) {
            boolean ok = cacheDirectory.mkdirs();
            if (!ok)
                throw new IOException("Unable to create: " + cacheDirectory.getAbsoluteFile());
        } else if (!cacheDirectory.isDirectory()) {
            throw new IOException("Not a directory");
        }
        this.cache = cacheDirectory;
    }

    /**
     * Gets the print stream used for diagnostic messages if a file is
     * downloaded.
     * @return The current diagnostic print stream, or <CODE>null</CODE> if none
     *         exists.
     */
    public PrintStream getPrintStream() {

        return this.print;
    }

    /**
     * Set the print stream to be used for diagnostic messages
     * @param printStream The print stream to use, or <CODE>null</CODE> to
     *            disable diagnostic messages
     */
    public void setPrintStream(PrintStream printStream) {
        this.print = printStream;
    }

    /**
     * An interface to be implemented by cache validators
     * @see #getCachedFile(URL,Validator)
     */
    public static interface Validator {
        /**
         * Called after a file has been downloaded but before it is placed in
         * the cache, This method should throw an IOException if the file fails
         * to validate.
         * @param url The URL being downloaded
         * @param file The file that needs to be validated
         * @throws java.io.IOException If validation fails
         */
        void checkValidity(URL url, File file) throws IOException;
    }
}