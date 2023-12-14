package sbst.runtool;

import org.evosuite.EvoSuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BBCTestingTool implements ITestingTool {

    /**
     * The EvoSuite instance for this execution
     */
    private EvoSuite bbc;

    /**
     * The class under test classpath
     */
    private String cutClassPath;

    /**
     * List of additional class path entries required by a testing tool
     *
     * @return List of directories/jar files
     */
    @Override
    public List<File> getExtraClassPath() {
        List<File> files = new ArrayList();
        File bbc = new File("lib", "evosuite-bbc.jar");
        if (!bbc.exists()) {
            System.err.println("Wrong EvoSuite-BBC jar setting, jar is not at: " + bbc.getAbsolutePath());
        } else {
            files.add(bbc);
        }
        return files;
    }

    /**
     * Initialize the testing tool, with details about the code to be tested (SUT)
     * Called only once.
     *
     * @param src       Directory containing source files of the SUT
     * @param bin       Directory containing class files of the SUT
     * @param classPath List of directories/jar files (dependencies of the SUT)
     */
    @Override
    public void initialize(File src, File bin, List<File> classPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(bin.getAbsolutePath());
        for (File f : classPath) {
            sb.append(File.pathSeparator);
            sb.append(f.getAbsolutePath());
        }
        this.cutClassPath = sb.toString();
        this.bbc = new EvoSuite();
    }

    /**
     * Run the test tool, and let it generate test cases for a given class
     *
     * @param cName      Name of the class for which unit tests should be generated
     * @param timeBudget How long the tool must run to test the class (in miliseconds)
     */
    @Override
    public void run(String cName, long timeBudget) {
        if (this.bbc == null) {
            System.err.println("EvoSuite-BBC has not been initialized with the target classpath!");
        } else {
            long halfTime = timeBudget / 2L;
            System.err.println("TimeBudget: " + timeBudget);
            long initialization = halfTime / 6L;
            long minimization = (long) (1.4D * (double) (halfTime / 6L));
            long assertions = (long) (0.8D * (double) (halfTime / 6L));
            long junit = halfTime / 6L;
            long write = (long) (0.8D * (double) (halfTime / 6L));
            if (halfTime > 720L) {
                initialization = 120L;
                minimization = 120L;
                assertions = 120L;
                junit = 120L;
                write = 120L;
            } else if (halfTime > 360L) {
                initialization = 60L;
                minimization = 60L;
                assertions = 60L;
                junit = 60L;
                write = 60L;
            }
            long search = timeBudget - (initialization + minimization + assertions + junit + write);

            // BBC Configuration
            int bbcUsagePerc = 50; // activation probability in percents
            long bbcSleep; // Sleep time before activation for one objective in seconds
            if (timeBudget <= 60L){
                bbcSleep = 10L;
            } else if (timeBudget <= 300L) {
                bbcSleep = 30L;
            } else {
                bbcSleep = 60L;
            }

            System.err.println("Search    : " + search);
            System.err.println("Init      : " + initialization);
            System.err.println("Min       : " + minimization);
            System.err.println("Ass       : " + assertions);
            System.err.println("Extra     : " + timeBudget);
            System.err.println("JUnit     : " + junit);
            System.err.println("Write     : " + write);
            System.err.println("BBC usage : " + bbcUsagePerc);
            System.err.println("BBC sleep : " + bbcSleep);
            List<String> commands = new ArrayList();
            commands.addAll(Arrays.asList("-generateMOSuite",
                    "-Dalgorithm=DynaMOSA",
                    "-projectCP=" + this.cutClassPath,
                    "-class", cName,
                    "-Dshow_progress=false",
                    "-Dstopping_condition=MaxTime",
                    "-Dassertion_strategy=all",
                    "-Dtest_comments=false",
                    "-mem", "1200",
                    "-Dminimize=true",
                    "-Dinline=false",
                    "-Dcoverage=false",
                    "-Dvariable_pool=true",
                    "-Dsearch_budget=" + search,
                    "-Dglobal_timeout=" + search,
                    "-Dnew_statistics=false",
                    "-Dstatistics_backend=NONE",
                    "-Dminimization_timeout=" + minimization,
                    "-Dassertion_timeout=" + assertions,
                    "-Dinitialization_timeout=" + initialization,
                    "-Djunit_check_timeout=" + junit,
                    "-Dextra_timeout=" + (timeBudget + (long) ((int) Math.floor((double) timeBudget / 11.0D))),
                    "-Dwrite_junit_timeout=" + write,
                    "-Dtest_dir=temp/testcases",
                    "-Dp_functional_mocking=0.8",
                    "-Dfunctional_mocking_percent=0.5",
                    "-Dp_reflection_on_private=0.5",
                    "-Dreflection_start_percent=0.8",
                    "-Dreuse_leftover_time=true",
                    // BBC Specific commands
                    "-Dsecondary_objectives=BBCOVERAGE:TOTAL_LENGTH",
                    "-DBBC_USAGE_PERCENTAGE=" + bbcUsagePerc,
                    "-DBBC_SLEEP_TIME=" + bbcSleep
                ));
            String[] command = new String[commands.size()];
            commands.toArray(command);
            this.bbc.parseCommandLine(command);
        }
    }
}
