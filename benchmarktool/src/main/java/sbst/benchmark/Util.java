package sbst.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;

public class Util {

    public static class CPBuilder {
        private final StringBuilder sb = new StringBuilder();
        private final String seperator;

        public CPBuilder() {
            this(":");
        }

        public CPBuilder(String seperator) {
            this.seperator = seperator;
        }


        private CPBuilder append(String s) {
            if (sb.length() > 0) {
                sb.append(seperator);
            }
            sb.append(s);
            return this;
        }

        public CPBuilder and(String f) {
            return append(f);
        }

        public CPBuilder and(File f) {
            return append(f.getAbsolutePath());
        }

        public CPBuilder and(Collection<File> lf) {
            for (File f : lf) {
                append(f.getAbsolutePath());
            }
            return this;
        }

        public CPBuilder andStrings(Collection<String> lf) {
            for (String f : lf) {
                append(f);
            }
            return this;
        }

        public String build() {
            return sb.toString();
        }
    }

    public static String readFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }


    public static void cleanDirectory(File dir) throws IOException {
        if (dir.exists()) {
            delete(dir);
        }

        Main.debug("Creating directory " + dir);
        if (!dir.mkdir()) {
            throw new IOException("Could not create directory: " + dir);
        }
    }

    private static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        Main.debug("Deleting file or directory " + f);
        if (!f.delete()) {
            throw new IOException("Failed to delete file: " + f);
        }
    }

    public static void CopyToDirectory(File fileOrDirectory, File destDir, String targetName) throws IOException {
        Main.debug("Copying '" + fileOrDirectory + "' to '" + destDir + "'");

        if (targetName == null) {
            targetName = fileOrDirectory.getName();
        }

        if (!destDir.exists()) {
            if (!destDir.mkdirs()) {
                throw new IOException("Unable to create directory " + destDir.getAbsolutePath());
            }
        }

        if (fileOrDirectory.isFile()) {
            File destFile = new File(destDir.getAbsolutePath() + File.separator + targetName);
            if (!destFile.exists()) {
                if (!destFile.createNewFile()) {
                    throw new IOException("Unable to create file " + destFile.getAbsolutePath());
                }
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(fileOrDirectory).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } else if (fileOrDirectory.isDirectory()) {
            File copyDir = new File(destDir.getAbsolutePath() + File.separator + targetName);

            if (!copyDir.exists()) {
                if (!copyDir.mkdir()) {
                    throw new IOException("Unable to create directory " + copyDir.getAbsolutePath());
                }
            }

            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File f : files) {
                    CopyToDirectory(f, copyDir, null);
                }
            }
        }
    }

    public static void pause(double time) {
        try {
            Thread.sleep((int) (time * 1000));
        } catch (Throwable t) {
        }
    }
}
