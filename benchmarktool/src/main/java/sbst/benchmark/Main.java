package sbst.benchmark;

import org.apache.commons.configuration.ConfigurationException;

import java.io.*;

public class Main {

    public static class NullOutputStream extends OutputStream {
        public void write(int b) throws IOException {
        }
    }

    // private static final String USAGE = "sbstcontest <benchmark>
    // <tooldirectory> [true|false] [TestCaseCopyDirectory]";
    private static final String USAGE = "sbstcontest <toolname> <benchmark> <tooldirectory> <run_number> <timebudget> [--only-compute-metrics|--only-generate-tests] [TestCaseCopyDirectory]";
    public static final String TOOLEXECUTABLE = "runtool";
    public static final String BENCHMARK_CONFIG = System.getProperty("sbst.benchmark.config");
    public static final String JAVAC = System.getProperty("sbst.benchmark.javac");
    public static final String JAVA = System.getProperty("sbst.benchmark.java");
    public static final String PITEST_JAR = System.getProperty("sbst.benchmark.pitest");
    public static final String JUNIT_JAR = System.getProperty("sbst.benchmark.junit");
    public static final String JUNIT_DEP_JAR = System.getProperty("sbst.benchmark.junit.dependency");
    public static final String JACOCO_JAR = System.getProperty("sbst.benchmark.jacoco");

    private static final String ONLY_COMPUTE_METRICS = "--only-compute-metrics";
    private static final String ONLY_GENERATE_TESTS = "--only-generate-tests";

    public static PrintStream debugStr, infoStr;
    public static boolean debugFlag = false;
    public static String testCaseCopyDir = null;

    public static void info(String info) {
        System.out.println(info);
        infoStr.println(info);
        debug(info);
    }

    public static void debug(String info) {
        debugStr.println(info);
    }

    public static void main(String[] args) throws IOException {
        BenchmarkCollection bmcollection = null;
        String toolName = null, benchmark = null;
        IBenchmarkTask task = null;
        File toolDirectory = null;
        int runNumber = -1;
        int timeBudget = -1;
        String mode = null;

        if (BENCHMARK_CONFIG != null) {
            try {
                bmcollection = new BenchmarkCollection(new File(BENCHMARK_CONFIG));
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Undefined property: sbst.benchmark.config");
        }
        if (args.length >= 1) {
            toolName = args[0];
            if (toolName == null || toolName.isEmpty()) {
                System.out.println("Invalid tool name!");
                return;
            }
        }
        if (args.length >= 2) {
            benchmark = args[1];
            if (bmcollection != null) {
                task = bmcollection.forName(benchmark);
            }
        }
        if (args.length >= 3) {
            toolDirectory = new File(args[2]);
        }
        if (args.length >= 4) {
            try {
                runNumber = new Integer(args[3]).intValue();
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid run number: " + args[3]);
                return;
            }
        }

        if (args.length >= 5) {
            try {
                timeBudget = new Integer(args[4]).intValue();
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid run number: " + args[3]);
                return;
            }
        }

        if (args.length >= 6) {
            mode = args[5];
            if (!mode.equals(ONLY_GENERATE_TESTS) && !mode.equals(ONLY_COMPUTE_METRICS)) {
                System.out.println("Invalid mode type " + mode);
                System.out.println("Allowed mode types are " + ONLY_GENERATE_TESTS + " and " + ONLY_COMPUTE_METRICS);
                return;
            }

            if (mode.equals(ONLY_COMPUTE_METRICS) && args.length < 7) {
                System.out.println("Using mode " + ONLY_COMPUTE_METRICS
                        + " requires setting an archive folder to read the test cases");
                return;
            }
            if (args.length >= 7) {
                testCaseCopyDir = args[6];
            }
        }

        if (bmcollection == null || benchmark == null || task == null || toolDirectory == null) {
            System.err.println(USAGE);
            if (bmcollection != null) {
                if (benchmark != null && task == null) {
                    System.out.println("Invalid benchmark name: " + benchmark);
                }
                System.out.println("Available benchmarks: " + bmcollection.getBenchmarks());
            }
            return;
        }

        debugStr = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(new File(toolDirectory, "log_detailed.txt"), true)), true,
                "UTF-8");
        infoStr = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(new File(toolDirectory, "log.txt"), true)), true,
                "UTF-8");

        try {
            if (toolDirectory.exists() && toolDirectory.isDirectory()) {
                File executable = new File(toolDirectory, TOOLEXECUTABLE);
                if (executable.exists() && executable.isFile() && executable.canExecute()) {
                    Main.info("\n>>> TOOL NAME:  " + toolName);
                    Main.info("\n>>> BENCHMARK:  " + benchmark);
                    Main.info("\n>>> RUN NUMBER: " + runNumber);
                    if (mode != null) {
                        Main.info("\n>>> MODE: " + mode);
                    }
                    File transcriptFile = new File(toolDirectory, "transcript.csv");
                    Writer transcriptWriter = new OutputStreamWriter(new FileOutputStream(transcriptFile, true));
                    TranscriptWriter transcript = new TranscriptWriter(task, transcriptWriter);
                    transcript.start(toolName, timeBudget, benchmark, runNumber);
                    RunTool tool = new RunTool(toolDirectory, executable, transcript, timeBudget);

                    if (mode != null && mode.equals(ONLY_GENERATE_TESTS)) {
                        transcript.enableGenerateTests();
                        tool.enableGenerateTests();
                        transcript.disableComputeMetrics();
                    } else if (mode != null && mode.equals(ONLY_COMPUTE_METRICS)) {
                        transcript.disableGenerateTests();
                        tool.disableGenerateTests();
                        transcript.enableComputeMetrics();
                    } else {
                        transcript.enableGenerateTests();
                        tool.enableGenerateTests();
                        transcript.enableComputeMetrics();
                    }
                    tool.run(task);
                    transcript.finish();
                    transcriptWriter.close();
                } else {
                    info("No executable found at: '" + executable + "'");
                    return;
                }
            } else {
                info("Not a valid directory: '" + args[2] + "'");
                return;
            }
        } finally {
            debugStr.flush();
            debugStr.close();
        }
        // Some thread seems to be blocking the exit
        // Thats why we used this instead of return
        System.exit(0);

    }
}
