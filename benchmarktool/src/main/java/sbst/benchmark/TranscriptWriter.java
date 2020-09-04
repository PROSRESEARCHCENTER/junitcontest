package sbst.benchmark;

import java.io.*;
import java.util.List;

public class TranscriptWriter implements IToolListener {
    private static final String PREPARATION_TIME = "prepTime";
    private static final String GENERATION_TIME = "genTime";
    private static final String TIMING_FILENAME = "timing.txt";
    private final PrintWriter transcript;
    private long time;
    private final IBenchmarkTask task;
    private String toolName, benchmark;
    private int runNumber;
    private long prepTime;

    private boolean computeMetrics = true;

    public void enableComputeMetrics() {
        computeMetrics = true;
    }

    public void disableComputeMetrics() {
        computeMetrics = false;
    }

    private boolean generateTests = true;
    private int timeBudget;

    public void enableGenerateTests() {
        generateTests = true;
    }

    public void disableGenerateTests() {
        generateTests = false;
    }

    public TranscriptWriter(IBenchmarkTask task, Writer transcriptWriter) {
        this.task = task;
        transcript = new PrintWriter(transcriptWriter);
    }

    @Override
    public void startPreprocess() {
        time = System.currentTimeMillis();
    }

    @Override
    public void endPreprocess() {
        prepTime = System.currentTimeMillis() - time;

        transcript.println("tool," + "benchmark," + "class," + "run," +
                // timing metrics
                "preparationTime," + "generationTime," + "executionTime," +
                // test cases
                "testcaseNumber," + "uncompilableNumber," + "brokenTests," +
                // fault detection score
                "failTests," +
                // coverage metrics
                "linesTotal," + "linesCovered," + "linesCoverageRatio," + // linesCovered
                // /
                // linesTotal
                // (this
                // is
                // statement
                // coverage
                // ratio)
                "conditionsTotal," + "conditionsCovered," + "conditionsCoverageRatio," + // conditionsCovered
                // /
                // conditionsTotal
                // (this
                // is
                // branch
                // coverage
                // ratio)
                // mutation score
                "mutantsTotal," + "mutantsCovered," + "mutantsCoverageRatio," + // mutantsCovered
                // /
                // mutantsTotal
                "mutantsKilled," + "mutantsKillRatio," + // mutantsKilled /
                // mutantsTotal
                "mutantsAlive," + // mutantsTotal - mutantsKilled

                "timeBudget," + "totalTestClasses"
        );
    }

    @Override
    public void startClass(String cname) {
        time = System.currentTimeMillis();
    }

    @Override
    public void endClass(String cname, File testcaseDir, List<File> extraCP) {
        long genTime, execTime;
        int numTestCases = 0;

        int d4jBrokenTests = 0, d4jFailTests = 0, d4jLinesTotal = 0, d4jLinesCovered = 0, d4jConditionsTotal = 0,
                d4jConditionsCovered = 0, d4jMutantsGenerated = 0, d4jMutantsCovered = 0, d4jMutantsKilled = 0,
                d4jMutantsLive = 0;

        genTime = execTime = 0;

        try {

            // give the tested tool some time after the READY signal
            Util.pause(1.0);

            if (generateTests && !computeMetrics) {
                // write compute genTime and write it to file
                genTime = System.currentTimeMillis() - time;
                writeTimingFile(testcaseDir, genTime, prepTime);

                if (Main.testCaseCopyDir != null) {
                    File archiveDir = new File(Main.testCaseCopyDir);
                    if (archiveDir.exists()) {
                        // Copy generated test cases (and generation-time.txt
                        // file) to archive folder
                        Util.CopyToDirectory(testcaseDir, archiveDir, cname);
                    }
                }

            } else if (!generateTests && computeMetrics) {
                // Use archive folder to read test cases
                testcaseDir = new File(Main.testCaseCopyDir);

                TimingData t = readTimingFile(testcaseDir);
                this.prepTime = t.preparationTime;
                genTime = t.generationTime;
            } else {
                // simply compute generation time
                genTime = System.currentTimeMillis() - time;
            }

            if (computeMetrics) {
                TestSuite testSuite = new TestSuite(task, toolName, benchmark, testcaseDir, extraCP, cname);

                // numTestCases = testSuite.getTestSrcFiles().size() - 1;
                // -1 because of the dummy class
                numTestCases = testSuite.getTestcaseNumber(testcaseDir) - 1;
                execTime = testSuite.getExecTime();

                int uncompilableNumber = testSuite.getNumberOfUncompilableTestClasses();
                d4jBrokenTests = testSuite.getD4jBrokenTests();
                d4jFailTests = testSuite.getD4jFailTests();
                d4jLinesTotal = testSuite.getD4jLinesTotal();
                d4jLinesCovered = testSuite.getD4jLinesCovered();
                d4jConditionsTotal = testSuite.getD4jConditionsTotal();
                d4jConditionsCovered = testSuite.getD4jConditionsCovered();
                d4jMutantsGenerated = testSuite.getD4jMutantsGenerated();
                d4jMutantsCovered = testSuite.getD4jMutantsCovered();
                d4jMutantsKilled = testSuite.getD4jMutantsKilled();
                d4jMutantsLive = testSuite.getD4jMutantsLive();

                transcript.println(toolName + "," + benchmark + "," + cname + "," + runNumber + "," + prepTime + ","
                        + genTime + "," + execTime + "," + numTestCases + "," + uncompilableNumber + ","
                        + d4jBrokenTests + "," + d4jFailTests + "," + d4jLinesTotal + "," + d4jLinesCovered + ","
                        + (d4jLinesTotal == 0 ? 0.0f : (float) d4jLinesCovered / (float) d4jLinesTotal * 100.0f) + ","
                        + d4jConditionsTotal + "," + d4jConditionsCovered + ","
                        + (d4jConditionsTotal == 0 ? 0.0f
                        : (float) d4jConditionsCovered / (float) d4jConditionsTotal * 100.0f)
                        + "," + d4jMutantsGenerated + "," + d4jMutantsCovered + ","
                        + (d4jMutantsGenerated == 0 ? 0.0f
                        : (float) d4jMutantsCovered / (float) d4jMutantsGenerated * 100.0f)
                        + "," + d4jMutantsKilled + ","
                        + (d4jMutantsGenerated == 0 ? 0.0f
                        : (float) d4jMutantsKilled / (float) d4jMutantsGenerated * 100.0f)
                        + "," + d4jMutantsLive + "," + timeBudget + "," + testSuite.getNumberOfTestClasses());

                transcript.flush();

                Main.info("\n>>> RESULTS:");
                Main.info("\tTool name: " + toolName);
                Main.info("\tBenchmark: " + benchmark);
                Main.info("\tClass Under Test: " + cname);
                Main.info("\tRun number: " + runNumber);
                Main.info("\tTool preparation time (ms): " + prepTime);
                Main.info("\tTool test cases generation time (ms): " + genTime);
                Main.info("\tTest cases execution time (ms): " + execTime);
                Main.info("\tTest case number: " + numTestCases);
                Main.info("\tUncompilable Test Classes: " + uncompilableNumber);
                Main.info("\tBroken tests number: " + d4jBrokenTests);
                Main.info("\tFailing tests number: " + d4jFailTests);
                Main.info("\tLines total: " + d4jLinesTotal);
                Main.info("\tLines covered: " + d4jLinesCovered);
                Main.info("\tLines coverage ratio (%): "
                        + (d4jLinesTotal == 0 ? 0.0f : (float) d4jLinesCovered / (float) d4jLinesTotal * 100.0f));
                Main.info("\tConditions total: " + d4jConditionsTotal);
                Main.info("\tConditions covered: " + d4jConditionsCovered);
                Main.info("\tConditions coverage ratio (%): " + (d4jConditionsTotal == 0 ? 0.0f
                        : (float) d4jConditionsCovered / (float) d4jConditionsTotal * 100.0f));
                Main.info("\tMutants total: " + d4jMutantsGenerated);
                Main.info("\tMutants covered: " + d4jMutantsCovered);
                Main.info("\tMutants coverage ratio (%): " + (d4jMutantsGenerated == 0 ? 0.0f
                        : (float) d4jMutantsCovered / (float) d4jMutantsGenerated * 100.0f));
                Main.info("\tMutants killed: " + d4jMutantsKilled);
                Main.info("\tMutants killed ratio (%): " + (d4jMutantsGenerated == 0 ? 0.0f
                        : (float) d4jMutantsKilled / (float) d4jMutantsGenerated * 100.0f));
                Main.info("\tMutants alive: " + d4jMutantsLive);
            }
        } catch (Throwable e) {
            Main.info("ERROR: Something went wrong! Consult log.txt for more infos!");
            e.printStackTrace(Main.debugStr);
        }

    }

    private static class TimingData {
        Long generationTime;
        Long preparationTime;
    }

    private TimingData readTimingFile(File testcaseDir) throws FileNotFoundException, IOException {
        final String generationTimeFilename = testcaseDir.getAbsolutePath() + File.separator + TIMING_FILENAME;
        BufferedReader bfr = new BufferedReader(new FileReader(new File(generationTimeFilename)));
        try {
            String line = bfr.readLine();
            TimingData t = new TimingData();
            while (line != null) {
                if (!line.contains("=")) {
                    throw new RuntimeException("Malformed line " + line);
                }
                String[] parts = line.split("=");
                if (parts.length != 2) {
                    throw new RuntimeException("Malformed line " + line);
                }
                String key = parts[0];
                String value = parts[1];
                if (key.equals(GENERATION_TIME)) {
                    long generationTime = Long.valueOf(value);
                    t.generationTime = generationTime;
                } else if (key.equals(PREPARATION_TIME)) {
                    long preparationTime = Long.valueOf(value);
                    t.preparationTime = preparationTime;
                } else {
                    throw new RuntimeException("Unknown key= " + key);

                }

                line = bfr.readLine();
            }
            return t;
        } finally {
            bfr.close();
        }
    }

    private void writeTimingFile(File testcaseDir, long genTime, long preparationTime) throws IOException {
        final String generationTimeFilename = testcaseDir.getAbsolutePath() + File.separator + TIMING_FILENAME;
        BufferedWriter out = new BufferedWriter(new FileWriter(generationTimeFilename));
        out.write(String.format("%s=%s\n", GENERATION_TIME, String.valueOf(genTime)));
        out.write(String.format("%s=%s\n", PREPARATION_TIME, String.valueOf(preparationTime)));
        out.close();
    }

    @Override
    public void start(String toolName, int timeBudget, String benchmark, int runNumber) {
        this.toolName = toolName;
        this.benchmark = benchmark;
        this.timeBudget = timeBudget;
        this.runNumber = runNumber;
    }

    @Override
    public void finish() {
        transcript.println(" \n \n \n");
        transcript.flush();
    }
}
