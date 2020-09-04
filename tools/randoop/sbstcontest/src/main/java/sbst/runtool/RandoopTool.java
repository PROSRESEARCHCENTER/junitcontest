package sbst.runtool;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.util.ArrayList;
import java.util.List;

public class RandoopTool implements ITestingTool {

    private PrintStream logOut = null;

    public RandoopTool() {
        super();
    }

    private boolean useClassList = true;

    public void setUseClassList(boolean isMandatory) {
        this.useClassList = isMandatory;
    }

    private File binFile;
    private List<File> classPathList;

    public List<File> getExtraClassPath() {
        return new ArrayList<File>();
    }

    public void initialize(File src, File bin, List<File> classPath) {
        this.binFile = bin;
        this.classPathList = classPath;
    }

    public void run(String cName, long timeBudget) {

        this.cleanLatestRandoopRetVal();

        final String homeDirName = "."; // current folder
        new File(homeDirName + "/temp").mkdir();

        final String tempDirName = String.join(File.separator, homeDirName, "temp");
        File tempDir = new File(tempDirName);
        if (!tempDir.exists()) {
            throw new RuntimeException("Expected Temporary folder " + tempDirName + " was not found");
        }

        if (logOut == null) {
            final String logRandoopFileName = String.join(File.separator, tempDirName, "log_randoop.txt");
            PrintStream outStream;
            try {
                outStream = new PrintStream(new FileOutputStream(logRandoopFileName, true));
                this.setLoggingPrintWriter(outStream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(
                        "FileNotFoundException signaled while creating output stream for  " + logRandoopFileName);
            }
        }

        log("Checking environment variable JAVA_HOME ");
        if (System.getenv("JAVA_HOME") == null) {
            throw new RuntimeException("JAVA_HOME must be configured in order to run this program");
        }

        log("Execution of tool Randoop STARTED");
        log("user.home=" + homeDirName);
        final String randoopJarFilename = String.join(File.separator, homeDirName, "lib", "randoop.jar");

        File randoopJarFile = new File(randoopJarFilename);
        if (!randoopJarFile.exists()) {
            throw new RuntimeException("File randoop.jar was not found at folder "
                    + String.join(File.separator, homeDirName, "randoop", "dist"));
        }

        final String junitOutputDirName = String.join(File.separator, homeDirName, "temp", "testcases");
        File junitOutputDir = new File(junitOutputDirName);
        if (!junitOutputDir.exists()) {
            log("Creating directory " + junitOutputDir);
            junitOutputDir.mkdirs();
        }

        final String testClass = cName;
        final long timeLimit = timeBudget;

        final String junitPackageName;
        if (!testClass.contains(".")) {
            junitPackageName = null;
        } else {
            final int lastIndexOfDot = testClass.lastIndexOf(".");
            junitPackageName = testClass.substring(0, lastIndexOfDot);
        }

        final String classPath = createClassPath(binFile, classPathList, randoopJarFile);
        StringBuffer cmdLine = new StringBuffer();

        String javaCommand = buildJavaCommand();
        cmdLine.append(String.format("%s -cp %s randoop.main.Main gentests ", javaCommand, classPath));
        cmdLine.append(String.format("--testclass=%s ", testClass));
        cmdLine.append(String.format("--timelimit=%s ", timeLimit));
        cmdLine.append(String.format("--junit-output-dir=%s ", junitOutputDirName));

        final String regressionTestFileName;
        if (junitPackageName != null) {
            cmdLine.append(String.format("--junit-package-name=%s ", junitPackageName));

            regressionTestFileName = String.join(File.separator, junitOutputDirName,
                    junitPackageName.replace(".", File.separator), "RegressionTest.java");
        } else {
            regressionTestFileName = String.join(File.separator, junitOutputDirName, "RegressionTest.java");

        }

        cmdLine.append("--clear=10000 ");
        cmdLine.append("--string-maxlen=5000 ");
        cmdLine.append("--forbid-null=false ");
        cmdLine.append("--null-ratio=0.1 ");
        cmdLine.append("--no-error-revealing-tests=true ");
        cmdLine.append("--omitmethods=random ");
        cmdLine.append("--silently-ignore-bad-class-names=true ");
        cmdLine.append("--testsperfile=100 ");
        cmdLine.append("--ignore-flaky-tests=true ");

        String cmdToExecute = cmdLine.toString();

        File homeDir = new File(homeDirName);
        int retVal = launch(homeDir, cmdToExecute);
        log("Randoop execution finished with exit code " + retVal);
        this.setLatestRandoopRetVal(retVal);

        File regressionTestFile = new File(regressionTestFileName);
        if (regressionTestFile.exists()) {
            log("Deleting regression test suite file " + regressionTestFileName);
            regressionTestFile.delete();
        } else {
            log("Regression test suite file " + regressionTestFileName
                    + " could not be deleted since it does not exist");
        }

        log("Execution of tool Randoop FINISHED");
    }

    private void setLatestRandoopRetVal(int retVal) {
        this.latestRandoopRetVal = Integer.valueOf(retVal);
    }

    private void cleanLatestRandoopRetVal() {
        this.latestRandoopRetVal = null;
    }

    private String buildJavaCommand() {
        String cmd = String.join(File.separator, System.getenv("JAVA_HOME"), "bin", "java");
        return cmd;
    }

    private static String createClassPath(File subjectBinFile, List<File> subjectClassPathList, File randoopJarFile) {
        StringBuffer buff = new StringBuffer();
        buff.append(subjectBinFile.getAbsolutePath());
        for (File classPathFile : subjectClassPathList) {
            buff.append(File.pathSeparator);
            buff.append(classPathFile.getAbsolutePath());
        }
        buff.append(File.pathSeparator);
        buff.append(randoopJarFile.getAbsolutePath());
        return buff.toString();
    }

    public void setLoggingPrintWriter(PrintStream w) {
        this.logOut = w;
    }

    private void log(String msg) {
        if (logOut != null) {
            logOut.println(msg);
        }
    }

    private int launch(File baseDir, String cmdString) {
        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler streamHandler;
        streamHandler = new PumpStreamHandler(logOut, logOut, null);
        executor.setStreamHandler(streamHandler);
        if (baseDir != null) {
            executor.setWorkingDirectory(baseDir);
        }

        int exitValue;
        try {
            log("Spawning new process of command " + cmdString);
            exitValue = executor.execute(CommandLine.parse(cmdString));
            log("Execution of subprocess finished with ret_code " + exitValue);
            return exitValue;
        } catch (IOException e) {
            log("An IOException occurred during the execution of Randoop");
            return -1;
        }
    }

    private Integer latestRandoopRetVal = null;

    public Integer getLatestRandoopRetVale() {
        return latestRandoopRetVal;
    }

}
