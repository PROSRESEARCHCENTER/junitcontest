package sbst.benchmark;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tools.ant.DirectoryScanner;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import sbst.benchmark.coverage.JaCoCoLauncher;
import sbst.benchmark.coverage.JacocoResult;
import sbst.benchmark.coverage.TestExecutionTask;
import sbst.benchmark.pitest.MutationAnalysis;
import sbst.benchmark.pitest.MutationsEvaluator;
import sbst.benchmark.pitest.PITWrapper;
import sbst.benchmark.pitest.TestInfo;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class TestSuite {
    public static final int TEST_TIMEOUT = 60000;
    public static final int MAX_THREAD = 1;
    private static final String DUMMY_JUNIT_CLASS = "SBSTDummyForCoverageAndMutationCalculation";

    private final IBenchmarkTask task;
    private final String toolName, benchmark;

    private final File testCaseDir;

    private JacocoResult jacoco_result;
    private Set<TestInfo> flakyTests = new HashSet<TestInfo>();
    private final List<File> extraClassPath;
    private long execTime;
    private List<String> compiledTestSrcFiles;

    private int d4jBrokenTests, d4jFailTests, d4jLinesTotal, d4jLinesCovered, d4jConditionsTotal, d4jConditionsCovered,
            d4jMutantsGenerated, d4jMutantsCovered, d4jMutantsKilled, d4jMutantsLive;

    private int numberOfUncompilableTestClasses;
    private int numberOfTestClasses;
    private int d4jMutantsIgnored;

    public TestSuite(IBenchmarkTask task, String toolName, String benchmark, File testCaseDir, List<File> extraCP,
                     String cut) {
        this.task = task;
        this.toolName = toolName;
        this.benchmark = benchmark;
        this.testCaseDir = testCaseDir;

        if (extraCP == null) {
            extraClassPath = Collections.emptyList();
        } else {
            extraClassPath = new ArrayList<File>(extraCP);
        }

        compiledTestSrcFiles = new ArrayList<String>();
        try {
            final File testCaseBinDir = getTestCaseBinDir(testCaseDir);
            Util.cleanDirectory(testCaseBinDir);
        } catch (IOException ioe) {
            Main.info("Unable to clean bin directory! Aborting test execution!");
            return;
        }
        compile();
        run();
        findFlakyTests();
        jacoco(cut);
        long time = System.currentTimeMillis();
        mutationAnalysis(cut);
        System.out.println("Time for mutation analysis = " + (System.currentTimeMillis() - time));
    }

    public List<File> getExtraCP() {
        return extraClassPath;
    }

    public IBenchmarkTask getTask() {
        return task;
    }

    public File getTestCaseDir() {
        return testCaseDir;
    }

    public File getTestCaseBinDir(final File testCaseDir) {
        return new File(testCaseDir, "bin");
    }

    public List<String> getTestSrcFiles(final File testCaseDir) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setFollowSymlinks(false);
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.setBasedir(testCaseDir);
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        List<String> testCaseFiles = Arrays.asList(files);
        return testCaseFiles;
    }

    public int getTestcaseNumber(final File testCaseDir) {
        int numberTC = 0;
        List<String> testSrc = getTestSrcFiles(testCaseDir);
        String line;
        for (String src : testSrc) {
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(testCaseDir.getAbsolutePath() + File.separator + src));
                try {
                    while ((line = br.readLine()) != null) {
                        if (line.contains("@Test") || line.contains("@org.junit.Test")) {
                            numberTC++;
                        }
                    }
                } finally {
                    br.close();
                }
            } catch (Throwable t) {
                Main.info("Could not calculate number of est cases!");
                t.printStackTrace(Main.debugStr);
            }
        }
        return numberTC;
    }

    public List<String> getTestClassNames() {
        List<String> result = new ArrayList<String>();
        for (String testcaseFile : getTestSrcFiles(testCaseDir)) {
            result.add(fileNameToClassName(testcaseFile));
        }
        return result;
    }

    public int getNumberOfUncompilableTestClasses() {
        return numberOfUncompilableTestClasses;
    }

    public List<String> getCompiledTestSrcFiles() {
        return compiledTestSrcFiles;
    }

    public List<String> getCompiledTestClassNames() {
        List<String> result = new ArrayList<String>();
        for (String testcaseFile : getCompiledTestSrcFiles()) {
            final String className = fileNameToClassName(testcaseFile);
            result.add(className);
        }
        return result;
    }

    private static String fileNameToClassName(String fname) {
        String result = fname;
        if (fname.endsWith(".java")) {
            result = result.substring(0, result.length() - 5);
        } else if (fname.endsWith(".class")) {
            result = result.substring(0, result.length() - 6);
        }
        return result.replace('/', '.');
    }

    public static String classsNameFileName(String cName, String suffix) {
        return cName.replace('.', '/') + suffix;
    }

    private void compile() {

        try {
            Main.debug("Creating dummy JUnit test file");
            String junitDummyCode = "import org.junit.Test;\n\npublic class " + DUMMY_JUNIT_CLASS
                    + "{\n\n@org.junit.Test\npublic void test(){}\n}";
            File dummyFile = new File(testCaseDir.getAbsolutePath(), DUMMY_JUNIT_CLASS + ".java");
            if (dummyFile.exists()) {
                dummyFile.delete();
            }
            PrintStream dummyWriter = new PrintStream(new BufferedOutputStream(new FileOutputStream(dummyFile, true)),
                    true, "UTF-8");
            dummyWriter.println(junitDummyCode);
            dummyWriter.close();
        } catch (IOException ioe) {
            Main.info("ERROR: Unable to compile dummy class! Aborting compilation!");
            return;
        }

        Main.info("\n---Compilation---");
        List<String> srcFiles = getTestSrcFiles(testCaseDir);

        if (srcFiles.isEmpty()) {
            Main.info("Did not find any JUnit source files! Skipping compilation!");
            return;
        }

        this.numberOfTestClasses = srcFiles.size() - 1; // ignore DUMMY FILE
        this.numberOfUncompilableTestClasses = srcFiles.size() - 1; // ignore DUMMY FILE

        // build classpath
        String cp = new Util.CPBuilder().and(getTask().getClassPath())
                .and(Main.JUNIT_JAR).and(Main.JUNIT_DEP_JAR)
                .and(getExtraCP()).build();

        final File testCaseBinDir = getTestCaseBinDir(testCaseDir);

        // if contain scaffolding, the compilation has to include that file
        List<String> scaffoldingFiles = new ArrayList<String>();
        for (String file : srcFiles) {
            if (file.contains("scaffolding")) {
                scaffoldingFiles.add(file);
                numberOfUncompilableTestClasses--;
            }
        }

        for (String f : srcFiles) {
            String commandLine = null;
            if (scaffoldingFiles.size() == 0 || f.contains(DUMMY_JUNIT_CLASS)) {
                commandLine = String.format("%s -sourcepath %s -cp %s -d %s %s", Main.JAVAC,
                        testCaseDir.getAbsolutePath(), cp, testCaseBinDir,
                        testCaseDir.getAbsolutePath() + File.separator + f);
                Main.debug("\n===\njavac command line:\n" + commandLine + "\n");

            } else {
                Main.info("Compiling with scaffolding tests");

                if (scaffoldingFiles.contains(f)){ //scaffolding test are already compiled with other tests
                    continue;
                }

                String files = "";
                for (String scaffTest : scaffoldingFiles) {
                    files = files + " " + testCaseDir.getAbsolutePath() + File.separator + scaffTest;
                }

                files = files + " " + testCaseDir.getAbsolutePath() + File.separator + f;

                Main.info(">> " + files);

                commandLine = String.format("%s -sourcepath %s -cp %s -d %s %s", Main.JAVAC,
                        testCaseDir.getAbsolutePath(), cp, testCaseBinDir,
                        files);
                Main.debug("\n===\njavac command line:\n" + commandLine + "\n");
            }

            try {
                PumpStreamHandler psh = new PumpStreamHandler(Main.debugStr);
                CommandLine cl = CommandLine.parse(commandLine);
                DefaultExecutor exec = new DefaultExecutor();
                exec.setStreamHandler(psh);
                exec.execute(cl);
                compiledTestSrcFiles.add(f);
                if (!f.contains(DUMMY_JUNIT_CLASS)) {
                    Main.info("Compiled '" + f + "'");
                    numberOfUncompilableTestClasses--;
                }
            } catch (Throwable t) {
                Main.info("Failed to compile '" + f + "'!");
                t.printStackTrace(Main.debugStr);
            }
        }
    }

    private void run() {
        Main.info("\n---Timing Information---");
        try {
            List<String> testClasses = getCompiledTestClassNames();

            if (testClasses.isEmpty()) {
                Main.info("There are no executable test classes! Skipping time calculation!");
                return;
            }

            // build classpath
            final File testCaseBinDir = getTestCaseBinDir(testCaseDir);
            String cp = new Util.CPBuilder().and(getTask().getClassPath())
                    .and(Main.JUNIT_JAR).and(Main.JUNIT_DEP_JAR).and(testCaseBinDir)
                    .and(getExtraCP()).build();

            Main.debug("Running tests with the following classpath: \n" + cp);
            long startTime = System.currentTimeMillis();

            // run the tests
            ExecutorService service = Executors.newFixedThreadPool(1);

            TestExecutionTask executor = new TestExecutionTask(cp, testClasses);
            FutureTask<List<Result>> task = (FutureTask<List<Result>>) service.submit(executor);
            List<Result> executionResults = task.get(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
            updateFlakyTests(executionResults);

            service.shutdown();
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);

            for (Result res : executionResults) {
                if (res.getFailureCount() > 0) {
                    Main.debug("Failure: " + res.getFailures());
                    for (Failure fail : res.getFailures()) {
                        Main.debug("Failing Tests: " + fail.getTestHeader() + "\n" + fail.getTrace());
                    }
                }
            }

            // broken test cases
            d4jBrokenTests = this.flakyTests.size();

            execTime = System.currentTimeMillis() - startTime;
        } catch (Throwable t) {
            Main.info("Could not run the compiled tests!");
            t.printStackTrace(Main.debugStr);
        }
    }

    private void findFlakyTests() {
        Main.info("\n---The tests are re-executed other four times to find the flaky ones---");
        try {
            List<String> testClasses = getCompiledTestClassNames();

            if (testClasses.isEmpty()) {
                Main.info("There are no executable test classes! Skipping time calculation!");
                return;
            }

            // build classpath
            final File testCaseBinDir = getTestCaseBinDir(testCaseDir);
            String cp = new Util.CPBuilder().and(getTask().getClassPath())
                    .and(Main.JUNIT_JAR).and(Main.JUNIT_DEP_JAR).and(testCaseBinDir)
                    .and(getTask().getClassPath()).and(getExtraCP()).build();
            long startTime = System.currentTimeMillis();

            // run the tests
            ExecutorService service = Executors.newFixedThreadPool(MAX_THREAD);
            List<FutureTask<List<Result>>> tasks = new ArrayList<FutureTask<List<Result>>>();
            for (int i = 0; i < 4; i++) {
                TestExecutionTask executor = new TestExecutionTask(cp, testClasses);
                FutureTask<List<Result>> task = (FutureTask<List<Result>>) service.submit(executor);
                tasks.add(task);
            }

            for (FutureTask<List<Result>> task : tasks) {
                List<Result> executionResults = task.get(TEST_TIMEOUT, TimeUnit.MILLISECONDS);
                updateFlakyTests(task.get());

                for (Result res : executionResults) {
                    if (res.getFailureCount() > 0) {
                        Main.debug("Failure: " + res.getFailures());
                        for (Failure fail : res.getFailures()) {
                            Main.debug("Failing Tests: " + fail.getTestHeader() + "\n" + fail.getTrace());
                        }
                    }
                }
            }
            service.shutdown();
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);

            // broken test cases
            d4jBrokenTests = this.flakyTests.size();

            execTime = System.currentTimeMillis() - startTime;
        } catch (Throwable t) {
            Main.info("Could not run the compiled tests!");
            t.printStackTrace(Main.debugStr);
        }
    }

    private void updateFlakyTests(List<Result> results) {
        for (Result result : results) {
            // if the test fail at this stage, then it should be ignored
            if (result.getFailures().size() > 0) {
                for (Failure fail : result.getFailures()) {
                    String header = fail.getTestHeader();

                    if (header.contains("(")) {
                        String testMethod = header.substring(0, header.indexOf('('));
                        String testClass = header.substring(header.indexOf('(') + 1, header.length());
                        TestInfo info = new TestInfo(testClass, testMethod);
                        this.flakyTests.add(info);
                    }
                }
            }
        }
    }

    public long getExecTime() {
        return execTime;
    }

    private void cmdExec(String cmdLine, OutputStream outStr) throws Throwable {
        PumpStreamHandler psh = new PumpStreamHandler(outStr);
        CommandLine cl = CommandLine.parse(cmdLine);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(psh);
        exec.execute(cl);
    }

    private void d4jTestArchiving(final File testCaseDir, final String archiveFileName) {
        String commandLine = "tar -cjf " + archiveFileName + " -C " + testCaseDir.getAbsolutePath() + " .";
        Main.debug("\n===\nDefects4J test cases archiving command line:\n\n" + commandLine + "\n\n");
        try {
            cmdExec(commandLine, Main.debugStr);
        } catch (Throwable t) {
            Main.info("Could not archive test cases!");
            t.printStackTrace(Main.debugStr);
        }
    }

    // metrics gathered through Jacoco:
    // - coverage metrics
    private void jacoco(String cut) {

        Main.info("\n=== Run Jacoco for Coverage ===");
        try {
            //System.out.println("HERE >>>> "+ testCaseDir);
            //System.out.println("HERE >>>> "+ testCaseDir.getParent());
            //System.out.println("HERE >>>> "+ getTestCaseBinDir(testCaseDir));
            //System.out.println("HERE >>>> "+ getTask().getClassPath());
            //System.out.println("HERE >>>> "+ getExtraCP());
            //System.out.println("HERE >>>> "+Main.JUNIT_DEP_JAR+" : "+Main.JUNIT_JAR);

            JaCoCoLauncher launcher = new JaCoCoLauncher(this.getTestCaseBinDir(testCaseDir).getParent());

            launcher.setTestFolder(getTestCaseBinDir(testCaseDir).getAbsolutePath());

            List<String> testClasses = getCompiledTestClassNames();
            launcher.setTestCase(testClasses);

            launcher.setJarInstrument(getTask().getClassPath());

            String cp = new Util.CPBuilder()
                    .and(Main.JUNIT_DEP_JAR)
                    .and(Main.JUNIT_JAR)
                    .and(getExtraCP())
                    .build();
            cp = cp.replaceAll("::", ":");
            launcher.setClassPath(cp);
            launcher.setTargetClass(cut);
            launcher.runJaCoCo();

            JacocoResult result = launcher.getResults();
            result.printResults();

            // Count number of failing tests (HOW MUCH REAL FAULTS ARE DETECTED?)
            d4jFailTests = 0;

            // coverage metrics
            if (result != null) {
                this.d4jLinesTotal = result.getLinesTotal();
                this.d4jLinesCovered = result.getLinesCovered();
                this.d4jConditionsTotal = result.getBranchesTotal();
                this.d4jConditionsCovered = result.getBranchesCovered();
            }
            // keep track of the uncovered lines for mutation analysis
            this.jacoco_result = result;

        } catch (Throwable t) {
            Main.info("Could not calculate coverage metrics!");
            t.printStackTrace(Main.debugStr);
        }
    }

    // metrics gathered through PIT:
    // - mutation coverage
    private void mutationAnalysis(String cut) {
        Main.info("\n=== Run PIT ===");
        try {
            // TODO Is this misleading? I suspect mutants are already there....
//			Main.debug("Generate mutations via PIT");
            String cp = new Util.CPBuilder()
                    .and(Main.JUNIT_JAR)
                    .and(Main.JUNIT_DEP_JAR)
                    .and(getTask().getClassPath())
                    .and(getExtraCP())
                    .and(getTestCaseBinDir(testCaseDir).getAbsolutePath())
                    .build();

            List<String> testClasses = getTestSrcFiles(testCaseDir);

            List<String> fixedTestClasses = new ArrayList<String>();
            for (int index = 0; index < testClasses.size(); index++) {
                if (!testClasses.get(index).contains(DUMMY_JUNIT_CLASS)) {
                    String element = testClasses.get(index);
                    element = element.replace("/", ".");
                    if (element.startsWith("testcases.")) {
                        element = element.replace("testcases.", "");
                    }
                    element = element.substring(0, element.length() - 5);//replace(".java","");
                    fixedTestClasses.add(element);
                }
            }

//			Main.debug("Running tests against generated Mutants");
//			Main.debug("Running PITWrapper with the following data:");
//			Main.debug("> CP = "+cp);
//			Main.debug("> CUT = "+cut);
//			Main.debug("> Test cases = "+fixedTestClasses);
            PITWrapper wrapper = new PITWrapper(cp, cut, fixedTestClasses);

            // prepare the mutant evaluator
            MutationsEvaluator evaluator = new MutationsEvaluator(cp, cut, fixedTestClasses, this.flakyTests);

            // compute mutation coverage
            evaluator.computeCoveredMutants(wrapper.getGeneratedMutants(), jacoco_result);

//			Main.debug("Run tests against the covered mutations (i.e., mutants infecting on covered lines according to Jacoco)");
            Main.info("Executing Mutation Analysis using " + wrapper.getGeneratedMutants().getNumberOfMutations() + " mutants ");
            String mutated = this.getTestCaseBinDir(testCaseDir).getParent() + "/mutated_code"; // temporary folder where to save the mutated SUT

            try {
                // compute killed mutations
                evaluator.runMutations(mutated, new Util.CPBuilder().and(getTask().getClassPath()).build());
            } catch (InterruptedException e) {
                Main.info("ERROR runMutation was interrupted !");
                e.printStackTrace(Main.infoStr);
                evaluator.setTimeoutReached();
            }

            // if timeout is reached, we create a file TIMEOUT.txt
            if (evaluator.isTimeoutReached()) {
                File time_out = new File(this.getTestCaseDir().getAbsolutePath() + File.separator + "TIMEOUT.txt");
                time_out.createNewFile();
                Main.info("ERROR Evaluation not completed ignore it.");
                return;
            }

            // mutation analysis results
            MutationAnalysis cov = evaluator.getMutationCoverage();

            // remove from the count flaky tests, i.e., those tests that already fail in the original SUT)
            cov.deleteFlakyTest(this.flakyTests);

            if (cov != null) {
                // Remove the ignored tests from the total
                this.d4jMutantsGenerated = cov.getNumberOfMutations() - cov.numberOfIgnoredMutation();
                this.d4jMutantsLive = this.d4jMutantsGenerated - cov.numberOfKilledMutation();
                // Remove the ignored tests from the covered ones, since the ignored ran
                this.d4jMutantsCovered = cov.getNumberOfCoveredMutants() - cov.numberOfIgnoredMutation();
                //
                this.d4jMutantsIgnored = cov.numberOfIgnoredMutation();
                this.d4jMutantsKilled = cov.numberOfKilledMutation();
            }

            //save data to file mutation_results.txt
            PrintWriter out = new PrintWriter(this.getTestCaseBinDir(testCaseDir).getParent() + "/mutation_results.txt");
            out.println(cov.toString());
            out.close();

        } catch (Throwable t) {
            Main.info("Could not calculate mutation metrics!");
            t.printStackTrace(Main.debugStr);
        }
    }

    public int getD4jMutantsIgnored() {
        return d4jMutantsIgnored;
    }

    public int getD4jBrokenTests() {
        return d4jBrokenTests;
    }

    public int getD4jFailTests() {
        return d4jFailTests;
    }

    public int getD4jLinesTotal() {
        return d4jLinesTotal;
    }

    public int getD4jLinesCovered() {
        return d4jLinesCovered;
    }

    public int getD4jConditionsTotal() {
        return d4jConditionsTotal;
    }

    public int getD4jConditionsCovered() {
        return d4jConditionsCovered;
    }

    public int getD4jMutantsGenerated() {
        return d4jMutantsGenerated;
    }

    public int getD4jMutantsCovered() {
        return d4jMutantsCovered;
    }

    public int getD4jMutantsKilled() {
        return d4jMutantsKilled;
    }

    public int getD4jMutantsLive() {
        return d4jMutantsLive;
    }

    public int getNumberOfTestClasses() {
        return numberOfTestClasses;
    }
}
