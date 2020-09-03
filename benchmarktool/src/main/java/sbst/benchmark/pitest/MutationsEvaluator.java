package sbst.benchmark.pitest;

import org.apache.commons.io.FileUtils;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.pitest.mutationtest.engine.MutationIdentifier;
import sbst.benchmark.Main;
import sbst.benchmark.TestSuite;
import sbst.benchmark.coverage.JacocoResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class MutationsEvaluator {

    public static final int MAX_THREAD;

    static {
        int parallelism = 1;
        try {
            parallelism = Integer.parseInt(System.getProperty("sbst.benchmark.parallelism", "1"));
        } catch (NumberFormatException nfe) {
            // Ignore this
        }
        MAX_THREAD = parallelism;
    }

    private static final long GLOBAL_TIMEOUT = 300000; // global timeout for
    // mutation analysis

    public static final boolean ENABLE_REMOTE_EXECUTION;

    static {
        // TODO Will this throw an exception?
        ENABLE_REMOTE_EXECUTION = Boolean.parseBoolean(System.getProperty("sbst.benchmark.remoting", "false"));
    }

    /**
     * Folder where to save the mutated SUT
     **/
    private String tempFolder;

    private String classPath;

    private String classToMutate;

    private List<String> targetTest;

    private MutationAnalysis mutationResults;

    private Set<TestInfo> flakyTests;

    private boolean timeoutReached = false;

    /**
     * Build the infrastructure to run the generated tests against the mutations
     *
     * @param pClassPath     classpath with all required libraries to run the tests
     * @param pClassToMutate name of the class to mutate
     * @param pTargetTest    test to run against the mutations
     */
    public MutationsEvaluator(String pClassPath, String pClassToMutate, List<String> pTargetTest,
                              Set<TestInfo> pFlakyTests) {
        this.classPath = pClassPath;
        this.classToMutate = pClassToMutate;
        this.targetTest = pTargetTest;
        this.flakyTests = pFlakyTests;
    }

    public void computeCoveredMutants(MutationSet set, JacocoResult jacoco) {
        mutationResults = new MutationAnalysis(set, jacoco);
    }

    /**
     * This method run all covered mutations. It makes a copy of the SUT in the
     *
     * @param tempFolder folder where the SUT will be copied for the mutation analysis
     * @param path2SUT   path of the "original" SUT
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public void runMutations(String tempFolder, String path2SUT)
            throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
        // create a temporary copy of the SUT for mutation analysis
        this.tempFolder = tempFolder;
        if (tempFolder.equals(path2SUT)) {
            throw new IllegalArgumentException("Source and target directories should be different: \n "
                    + "source directory = " + path2SUT + "target directory = " + tempFolder);
        }

        // create a copy of the SUT
        String newSUT = this.tempFolder + "/SUT";

        createSUTCopy(path2SUT, newSUT);
        // remove the original CUT
        String CUT = newSUT + "/" + classToMutate.replace('.', '/') + ".class";
        File fcut = new File(CUT);
        if (fcut.exists()) {
            FileUtils.deleteQuietly(fcut);
        }

        // Prepare the ExecutorService
        ExecutorService service = Executors.newScheduledThreadPool(MAX_THREAD);

        // start to measure the time
        long start_time = System.currentTimeMillis();

        // iterate over all mutations
        MutationSet coveredMutants = mutationResults.getCoveredMutants();
        Set<MutationIdentifier> mutations = coveredMutants.getMutationIDs();

        LinkedList<TestExec4MutationTask> task_list = new LinkedList<TestExec4MutationTask>();

        int mutation_counter = -1;
        for (MutationIdentifier id : mutations) {
            // System.out.println(coveredMutants.getMutantionDetails(id).toString());
            // System.out.println(">> "+
            // coveredMutants.getMutantionDetails(id).getClassName());
            // System.out.println(">> "+
            // coveredMutants.getMutantionDetails(id).getMethod());

            mutation_counter++;

            // get the mutated bytecode of the CUT
            byte[] mu = coveredMutants.getMutantion(id).getBytes();

            // save the mutated bytecode of the CUT
            String newCUT = this.tempFolder + "/CUT" + mutation_counter;
            writeMutationOnDisk(mu, newCUT);

            // change the classpath to consider the new copy of the SUT
            String newCP = this.classPath.replace(path2SUT, newSUT);
            // add the mutated CUT to the classpath
            newCP = newCP + ":" + newCUT;

            // run the test against the mutated CUT
            List<String> testClasses = new ArrayList<String>();
            testClasses.addAll(this.targetTest);

            // This will run each test class against the mutant !
            // TODO Enable this using a switch !
            // Those seems to produce different results ?!

            TestExec4MutationTask executor = (ENABLE_REMOTE_EXECUTION)
                    ? new RemoteTestExec4MutationTask(newCP, testClasses, this.flakyTests, id)
                    : new TestExec4MutationTask(newCP, testClasses, this.flakyTests, id);
            // Schedule the execution
            task_list.addLast(executor);
        }

        // TODO What to do if one Mutant screw up all the
        List<Future<MutationResults>> all = service.invokeAll(task_list, GLOBAL_TIMEOUT, TimeUnit.MILLISECONDS);
        service.shutdown();

        for (Future<MutationResults> future : all) {
            // if canceled let's notify printing a file in the result
            // directory
            if (future.isCancelled()) {
                Main.debug("\n Ignoring mutant for timeout : " + future.get());
                this.timeoutReached = true;
                continue;
            }

            try {
                MutationResults mutationResult = future.get(TestSuite.TEST_TIMEOUT, TimeUnit.MILLISECONDS);
                MutationIdentifier id = mutationResult.getMutation_id();


                Main.info("mutationResult.getState() " + mutationResult.getState());

                switch (mutationResult.getState()) {
                    case KILLED:
                        TestInfo info = null;
                        boolean killed = false;

                        // Lookup the killing test information
                        List<Result> executionResults = mutationResult.getJUnitResults();
                        for (Result result : executionResults) {
                            for (Failure fail : result.getFailures()) {

                                String header = fail.getTestHeader();
                                // Skip results that have to be ignored anyway
                                if (fail.getTrace().contains("java.lang.Exception: test timed out after")
                                        || fail.getTrace().contains("java.io.FileNotFoundException")) {
                                    Main.debug("\n Discard test execution");
                                    continue;
                                }
                                // Check
                                if (header.contains("(")) {
                                    String testMethod = header.substring(0, header.indexOf('('));
                                    String testClass = header.substring(header.indexOf('(') + 1, header.length());
                                    info = new TestInfo(testClass, testMethod);

                                    if (!this.flakyTests.contains(info)) {
                                        mutationResults.addKilledMutant(coveredMutants.getMutantionDetails(id), info);
                                        killed = true;
                                        break;
                                    }

                                }
                            }
                        }
                        if (!killed) {
                            info = new TestInfo("testClass", "testMethod");
                            mutationResults.addKilledMutant(coveredMutants.getMutantionDetails(id), info);
                            // Here we have passing tests AND failing tests?
                            // throw new RuntimeException("Cannot find the test
                            // killing Mutant " + id);
                        } else {
                            // Here t
                            break;
                        }

                    case SURVIVED:
                        mutationResults.addAliveMutant(coveredMutants.getMutantionDetails(id));
                        break;

                    case IGNORED:
                        mutationResults.addIgnoreMutant(coveredMutants.getMutantionDetails(id));
                        break;

                    default: // NEVER_RUN
                        mutationResults.addAliveMutant(coveredMutants.getMutantionDetails(id));
                        break;
                }

            } catch (Throwable e) {
                e.printStackTrace(Main.infoStr);
                // TODO Auto-generated catch block
                if (e instanceof TimeoutException) {
                    Main.debug("Evaluation of the mutant stopped: it took more than " + TestSuite.TEST_TIMEOUT
                            + " milliseconds. Discard mutant.");
                } else if (e instanceof CancellationException) {
                    Main.debug("Evaluation of the mutant stopped: it took more than " + TestSuite.TEST_TIMEOUT
                            + " milliseconds. Discard mutant.");
                } else {
                    Main.debug("Error in evaluating mutant:");
                }
                continue;
            }
        }

        // remove temporary directory
        File dire2remove = new File(this.tempFolder);
        if (dire2remove.exists() && dire2remove.isDirectory()) {
            FileUtils.deleteDirectory(dire2remove);
        }
    }

    /**
     * Create a copy of the SUT for mutation analysis
     *
     * @param path2SUT   path to the SUT
     * @param tempFolder temporary file used for mutation analysis
     */
    public static void createSUTCopy(String path2SUT, String tempFolder) {
        File source = new File(path2SUT);
        File target = new File(tempFolder);

        try {
            if (path2SUT.contains(":")) {
                String[] libraries = path2SUT.split(":");
                for (String lib : libraries) {
                    lib = lib.replace(":", "");
                    if (lib.length() > 0) {
                        File fileLib = new File(lib);
                        if (fileLib.isDirectory()) {
                            FileUtils.copyDirectory(fileLib, target);
                        } else {
                            FileUtils.copyFileToDirectory(fileLib, target);
                        }
                    }
                }
            } else {
                if (!source.exists()) {
                    throw new FileNotFoundException();
                }
                FileUtils.copyDirectory(source, target);
            }

        } catch (IOException e) {
            Main.debug("Could not make a copy of the SUT " + path2SUT);
            e.printStackTrace(Main.debugStr);
        }
    }

    /**
     * Method to write the mutated CUT on disk (in the folder specified by the
     * attribute tempFolder)
     *
     * @param mu       bytecode of the mutated CUT (generated by PIT)
     * @param location temporary directory where to save the mutated CUT
     */
    public void writeMutationOnDisk(byte[] mu, String location) {

        String newCUT = location + "/" + classToMutate.replace('.', '/') + ".class";

        File changed_code = new File(newCUT);
        changed_code.getParentFile().mkdirs(); // create required folders

        FileOutputStream output;
        try {
            output = new FileOutputStream(changed_code);
            output.write(mu);
            output.flush();
            output.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public MutationAnalysis getMutationCoverage() {
        return this.mutationResults;
    }

    public boolean isTimeoutReached() {
        return timeoutReached;
    }

    public void setTimeoutReached() {
        this.timeoutReached = true;
    }
}
