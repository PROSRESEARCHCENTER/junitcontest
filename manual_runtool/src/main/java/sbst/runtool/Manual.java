package sbst.runtool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Manual implements ITestingTool {

    List<File> classPath;
    String extracp;

    public Manual(String extraCP) {
        this.extracp = extraCP;
    }


    public List<File> getExtraClassPath() {
        List<File> ret = null;
        if (extracp != null) {
            ret = new ArrayList<File>();
            ret.add(new File(extracp));
        }
        return ret;
    }

    public void initialize(File src, File bin, List<File> classPath) {
        this.classPath = classPath;
    }

    public void run(String cName) {
        try {
            File path = new File("./" + cName.replace('.', '/'));
            String pathPref = path.getParent();
            File testFile1 = new File(pathPref + "/" + "Test" + path.getName() + ".java");
            File testFile2 = new File(pathPref + "/" + path.getName() + "Test" + ".java");

            if (testFile1.exists()) {
                CopyToDirectory(testFile1, new File("./temp/testcases/" + pathPref), null);
            } else if (testFile2.exists()) {
                CopyToDirectory(testFile2, new File("./temp/testcases/" + pathPref), null);
            } else {
                throw new IOException("No corresponding JUnit test found which could be copied!!");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Failed to copy unit test!");
        }
    }

    public static void CopyToDirectory(File fileOrDirectory, File destDir, String targetName) throws IOException {

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

}
