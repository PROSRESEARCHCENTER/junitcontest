package sbst.benchmark;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class Score {

    static class ToolBudgetKey {
        final String toolName;
        final int timeBudget;

        public ToolBudgetKey(String toolName, int timeBudget) {
            super();
            this.toolName = toolName;
            this.timeBudget = timeBudget;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + timeBudget;
            result = prime * result + ((toolName == null) ? 0 : toolName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ToolBudgetKey other = (ToolBudgetKey) obj;
            if (timeBudget != other.timeBudget) {
                return false;
            }
            if (toolName == null) {
                if (other.toolName != null) {
                    return false;
                }
            } else if (!toolName.equals(other.toolName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "[toolName=" + toolName + ", timeBudget=" + timeBudget + "]";
        }
    }

    static class ToolBudgetBenchmarkKey {
        public ToolBudgetBenchmarkKey(String toolName, int timeBudget, String benchmarkName) {
            super();
            this.toolName = toolName;
            this.timeBudget = timeBudget;
            this.benchmarkName = benchmarkName;
        }

        final String toolName;
        final int timeBudget;
        final String benchmarkName;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((benchmarkName == null) ? 0 : benchmarkName.hashCode());
            result = prime * result + timeBudget;
            result = prime * result + ((toolName == null) ? 0 : toolName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ToolBudgetBenchmarkKey other = (ToolBudgetBenchmarkKey) obj;
            if (benchmarkName == null) {
                if (other.benchmarkName != null) {
                    return false;
                }
            } else if (!benchmarkName.equals(other.benchmarkName)) {
                return false;
            }
            if (timeBudget != other.timeBudget) {
                return false;
            }
            if (toolName == null) {
                if (other.toolName != null) {
                    return false;
                }
            } else if (!toolName.equals(other.toolName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "[toolName=" + toolName + ", timeBudget=" + timeBudget + ", benchmarkName=" + benchmarkName + "]";
        }

    }

    static class ToolBudgetBenchmarkRunIdKey {
        public ToolBudgetBenchmarkRunIdKey(String toolName, int timeBudget, String benchmarkName, int runId) {
            super();
            this.toolName = toolName;
            this.timeBudget = timeBudget;
            this.benchmarkName = benchmarkName;
            this.runId = runId;
        }

        private final String toolName;
        private final int timeBudget;
        private final String benchmarkName;
        private final int runId;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((benchmarkName == null) ? 0 : benchmarkName.hashCode());
            result = prime * result + runId;
            result = prime * result + timeBudget;
            result = prime * result + ((toolName == null) ? 0 : toolName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ToolBudgetBenchmarkRunIdKey other = (ToolBudgetBenchmarkRunIdKey) obj;
            if (benchmarkName == null) {
                if (other.benchmarkName != null) {
                    return false;
                }
            } else if (!benchmarkName.equals(other.benchmarkName)) {
                return false;
            }
            if (runId != other.runId) {
                return false;
            }
            if (timeBudget != other.timeBudget) {
                return false;
            }
            if (toolName == null) {
                if (other.toolName != null) {
                    return false;
                }
            } else if (!toolName.equals(other.toolName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "[toolName=" + toolName + ", timeBudget=" + timeBudget + ", benchmarkName=" + benchmarkName
                    + ", runId=" + runId + "]";
        }
    }

    private static final double WEIGHT_LINE_COVERAGE = 1.0;
    private static final double WEIGHT_CONDITION_COVERAGE = 2.0;
    private static final double WEIGHT_MUTATION_SCORE = 4.0;
    private static final double WEIGHT_REAL_FAULT_WAS_FOUND = 4.0;

    private static Map<String, Double> standardDeviations = new HashMap<String, Double>();

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage java " + Score.class.getName() + " <FILENAME>.csv <SCORE.csv>");
            return;
        }

        final String csvFileName = args[0];

        File csvFile = new File(csvFileName);
        if (!csvFile.exists()) {
            System.err.println("File " + csvFileName + " was not found");
            return;
        }

        final String csvScoreFileName = args[1];
        File csvScoreFile = new File(csvScoreFileName);
        if (csvScoreFile.exists()) {
            log("Deleting existing " + csvScoreFileName);
            csvScoreFile.delete();
        }
        Map<ToolBudgetKey, Double> scoreMap = computeScore(csvFile);
        PrintStream writer = new PrintStream(csvScoreFile);
        log("Writing results to new file " + csvScoreFileName);
        printScore(writer, scoreMap);
    }

    static void printScore(final PrintStream out, Map<ToolBudgetKey, Double> scoreMap) {
        final String headline = String.format("%1$10s, %2$10s, %3$7s, %4$13s", "TOOL", "TIMEBUDGET", "SCORE", "STD.DEVIATION");
        out.println(headline);
        Map<String, Double> toolScore = new HashMap<String, Double>();
        Double timeScore, stdev;
        Map<String, Double> toolStdDev = new HashMap<String, Double>();
        for (Entry<ToolBudgetKey, Double> entry : scoreMap.entrySet()) {
            final String toolName = entry.getKey().toolName;
            final int timeBudget = entry.getKey().timeBudget;
            final double score = entry.getValue();
            timeScore = toolScore.get(toolName);
            if (timeScore == null) {
                timeScore = new Double(score);
            } else {
                timeScore = timeScore.doubleValue() + score;
            }
            toolScore.put(toolName, timeScore);
            final String line = String.format("%1$10s, %2$10s, %3$7g, %4$13g", toolName, timeBudget, score, standardDeviations.get(toolName + timeBudget));
            out.println(line);
            stdev = toolStdDev.get(toolName);
            if (stdev == null) {
                stdev = new Double(0.0d);
            } else {
                stdev = new Double(stdev.doubleValue() + standardDeviations.get(toolName + timeBudget));
            }
            toolStdDev.put(toolName, stdev);
        }
        out.println("--");
        for (String tool : toolScore.keySet()) {
            out.println(tool + " scores: " + toolScore.get(tool).doubleValue() + " [std.deviation = " + toolStdDev.get(tool) + "]");
        }
    }

    static Map<ToolBudgetKey, Double> computeScore(File csvFile) throws FileNotFoundException, IOException {
        log("");
        log("Reading .csv execution results");
        log("==============================");
        Map<ToolBudgetBenchmarkRunIdKey, ExecutionResult> m = Score.readResults(csvFile);

        log("");
        log("Computing score per execution result");
        log("====================================");
        Map<ToolBudgetBenchmarkRunIdKey, Double> scorePerRun = Score.computeScorePerRun(m);

        log("");
        log("Computing avergare of execution results");
        log("=======================================");
        Map<ToolBudgetBenchmarkKey, Double> scorePerBenchmark = Score.computeBenchmarkAverage(scorePerRun);

        log("");
        log("Adding all averages per tool");
        log("============================");
        Map<ToolBudgetKey, Double> score = Score.aggregateScorePerTool(scorePerBenchmark);
        return score;
    }

    private static class SumCountPair {
        private double sum = 0.0;
        private int count = 0;
        private Vector<Double> scores;

        public SumCountPair(double sum) {
            this.sum = sum;
            this.count = 1;
            scores = new Vector<Double>();
        }

        public void add(double new_sum) {
            this.sum += new_sum;
            this.count++;
            scores.add(new Double(new_sum));
        }

        public double getAverage() {
            return (double) this.sum / (double) this.count;
        }

        public double getStdDeviation() {
            double avgScore = getAverage();
            double stdDevScore = 0.0;
            for (Double score : scores) {
                stdDevScore += Math.pow(avgScore - score, 2.0);
            }
            return Math.sqrt(stdDevScore / (this.count - 1));
        }

    }

    static Map<ToolBudgetBenchmarkKey, Double> computeBenchmarkAverage(
            Map<ToolBudgetBenchmarkRunIdKey, Double> scorePerRun) {
        Map<ToolBudgetBenchmarkKey, SumCountPair> sumCountPairs = new HashMap<ToolBudgetBenchmarkKey, SumCountPair>();
        for (Entry<ToolBudgetBenchmarkRunIdKey, Double> entry : scorePerRun.entrySet()) {

            final double score = entry.getValue();
            final String toolName = entry.getKey().toolName;
            final int timeBudget = entry.getKey().timeBudget;
            final String benchmarkName = entry.getKey().benchmarkName;
            ToolBudgetBenchmarkKey toolBenchmarkKey = new ToolBudgetBenchmarkKey(toolName, timeBudget, benchmarkName);
            if (!sumCountPairs.containsKey(toolBenchmarkKey)) {
                sumCountPairs.put(toolBenchmarkKey, new SumCountPair(score));
            } else {
                SumCountPair sumCountPair = sumCountPairs.get(toolBenchmarkKey);
                sumCountPair.add(score);
            }
        }

        Double stdev;
        String key;
        Map<ToolBudgetBenchmarkKey, Double> scorePerBenchmark = new HashMap<ToolBudgetBenchmarkKey, Double>();
        for (Entry<ToolBudgetBenchmarkKey, SumCountPair> entry : sumCountPairs.entrySet()) {
            ToolBudgetBenchmarkKey k = entry.getKey();
            SumCountPair v = entry.getValue();
            double avg = v.getAverage();
            log("New Avg Score for " + k + " is " + avg + " [std. deviation = " + v.getStdDeviation() + "]");
            scorePerBenchmark.put(k, avg);
            key = k.toolName + k.timeBudget;
            stdev = standardDeviations.get(key);
            if (stdev == null) {
                stdev = new Double(0.0d);
            } else {
                stdev = new Double(stdev.doubleValue() + v.getStdDeviation());
            }
            standardDeviations.put(key, stdev);
        }
        return scorePerBenchmark;
    }

    static Map<ToolBudgetBenchmarkRunIdKey, Double> computeScorePerRun(
            Map<ToolBudgetBenchmarkRunIdKey, ExecutionResult> results) {
        Map<ToolBudgetBenchmarkRunIdKey, Double> scorePerRun = new HashMap<ToolBudgetBenchmarkRunIdKey, Double>();
        for (Entry<ToolBudgetBenchmarkRunIdKey, ExecutionResult> entry : results.entrySet()) {
            ToolBudgetBenchmarkRunIdKey k = entry.getKey();
            ExecutionResult v = entry.getValue();
            double score = computeScorePerRun(v);
            log("New Run Score for " + k + " is " + score);
            scorePerRun.put(k, score);
        }
        return scorePerRun;
    }

    private static double computeScorePerRun(ExecutionResult r) {
        final double lineCoverageRatio = r.linesCoverageRatio;
        final double conditionCoverageRatio = r.conditionsCoverageRatio;
        final double mutationScore = r.mutantsKillRatio;
        final int generationTime = r.generationTime;
        final int uncompilableTestClasses = r.uncompilableNumber;
        final int flakyTests = r.brokenTests;
        final int faultRevelingTests = r.failTests;
        final int testSuiteSize = r.testcaseNumber;
        final int timeBudget = r.timeBudget;
        final int totalNumberOfTestClasses = r.totalTestClasses;

        double coverageScore = 0;
        coverageScore += WEIGHT_LINE_COVERAGE * (double) lineCoverageRatio;
        coverageScore += WEIGHT_CONDITION_COVERAGE * (double) conditionCoverageRatio;
        coverageScore += WEIGHT_MUTATION_SCORE * (double) mutationScore;
        if (faultRevelingTests > 0) {
            coverageScore += WEIGHT_REAL_FAULT_WAS_FOUND;
        }
        final double overtime_generation_penalty;
        if (generationTime == 0) {
            overtime_generation_penalty = 1.0;
        } else {
            final int timeBudgetMillis = timeBudget * 1000;
            final double generationTimeRatio = computeRatio(timeBudgetMillis, generationTime);
            overtime_generation_penalty = Math.min(1, generationTimeRatio);
        }

        final double uncompilableFlakyPenalty;
        if (testSuiteSize == 0) {
            // no tests!
            return 0.0;
        } else {

            if (uncompilableTestClasses == totalNumberOfTestClasses) {
                // no compilable test classes!
                uncompilableFlakyPenalty = 2.0;
            } else {
                // assert testSuiteSize>0
                final double flakyTestRatio = computeRatio(flakyTests, testSuiteSize);
                // assert totalNumberOfTestClasses !=0
                final double uncompilableTestClassesRatio = computeRatio(uncompilableTestClasses,
                        totalNumberOfTestClasses);
                uncompilableFlakyPenalty = flakyTestRatio + uncompilableTestClassesRatio;
            }

            final double score = (coverageScore * overtime_generation_penalty) - uncompilableFlakyPenalty;
            return score;
        }
    }

    private static double computeRatio(final int a, final int b) {
        return (double) a / (double) b;
    }

    static Map<ToolBudgetBenchmarkRunIdKey, ExecutionResult> readResults(File csvFile)
            throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        try {
            Map<ToolBudgetBenchmarkRunIdKey, ExecutionResult> results = new HashMap<ToolBudgetBenchmarkRunIdKey, ExecutionResult>();
            String line;
            br.readLine(); // discard header
            while ((line = br.readLine()) != null) {
                // use comma or tab as separator
                final String trimmedLine = line.trim();
                if (trimmedLine.equals("")) {
                    continue; // skip blank line
                }

                final String regexp = "[,\\t]+";
                final String[] fields = trimmedLine.split(regexp);
                if (fields.length != ExecutionResult.FIELD_LENGTH) {
                    throw new IllegalStateException("Incorrect number of fields " + fields.length + "(expected was "
                            + ExecutionResult.FIELD_LENGTH + ")");
                }

                final ExecutionResult r;
                try {
                    r = readFields(fields);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Offending line is " + trimmedLine, e);
                }
                ToolBudgetBenchmarkRunIdKey key = new ToolBudgetBenchmarkRunIdKey(r.tool, r.timeBudget, r.benchmark,
                        r.runId);
                results.put(key, r);
            }
            log(results.keySet().size() + " execution results were successfully retrieved.");
            return results;
        } finally {
            br.close();
        }
    }

    private static ExecutionResult readFields(String[] fields) throws IllegalArgumentException {
        ExecutionResult r = new ExecutionResult();
        r.tool = fields[0];
        r.timeBudget = Integer.valueOf(fields[23]);
        r.benchmark = fields[1];
        r.className = fields[2];
        r.runId = Integer.valueOf(fields[3]);
        if (!fields[4].trim().equals("?")) {
            r.preparationTime = Integer.valueOf(fields[4]);
            r.generationTime = Integer.valueOf(fields[5]);
            r.executionTime = Integer.valueOf(fields[6]);
            r.testcaseNumber = Integer.valueOf(fields[7]);
            r.uncompilableNumber = Integer.valueOf(fields[8]);
            r.brokenTests = Integer.valueOf(fields[9]);
            r.failTests = Integer.valueOf(fields[10]);
            r.linesTotal = Integer.valueOf(fields[11]);
            r.linesCovered = Integer.valueOf(fields[12]);
            r.linesCoverageRatio = Double.valueOf(fields[13]) / 100;
            r.conditionsTotal = Integer.valueOf(fields[14]);
            r.conditionsCovered = Integer.valueOf(fields[15]);
            r.conditionsCoverageRatio = Double.valueOf(fields[16]) / 100;
            r.mutantsTotal = Integer.valueOf(fields[17]);
            r.mutantsCovered = Integer.valueOf(fields[18]);
            r.mutantsCoverageRatio = Double.valueOf(fields[19]) / 100;
            r.mutantsKilled = Integer.valueOf(fields[20]);
            r.mutantsKillRatio = Double.valueOf(fields[21]) / 100;
            r.mutantsAlive = Integer.valueOf(fields[22]);
            r.totalTestClasses = Integer.valueOf(fields[24]);

            checkValidRow(r);
        }
        return r;
    }

    private static void checkValidRow(ExecutionResult r) throws IllegalArgumentException {
        if (r.preparationTime < 0) {
            throw new IllegalArgumentException("preparationTime cannot be negative!");
        }
        if (r.generationTime < 0) {
            throw new IllegalArgumentException("generationTime cannot be negative!");
        }
        if (r.executionTime < 0) {
            throw new IllegalArgumentException("executionTime cannot be negative!");
        }
        if (r.testcaseNumber < 0) {
            throw new IllegalArgumentException("testcaseNumber cannot be negative!");
        }
        if (r.uncompilableNumber < 0) {
            throw new IllegalArgumentException("uncompilableNumber cannot be negative!");
        }
        if (r.brokenTests < 0) {
            throw new IllegalArgumentException("brokenTests cannot be negative!");
        }
        if (r.failTests < 0) {
            throw new IllegalArgumentException("failTests cannot be negative!");
        }
        if (r.linesTotal < 0) {
            throw new IllegalArgumentException("linesTotal cannot be negative!");
        }
        if (r.linesCovered < 0) {
            throw new IllegalArgumentException("linesCovered cannot be negative!");
        }
        if (r.linesCoverageRatio < 0 || r.linesCoverageRatio > 1.0) {
            throw new IllegalArgumentException("linesCoverageRatio is not a ratio!");
        }
        if (r.conditionsTotal < 0) {
            throw new IllegalArgumentException("conditionsTotal cannot be negative!");
        }
        if (r.conditionsCovered < 0) {
            throw new IllegalArgumentException("conditionsCovered cannot be negative!");
        }
        if (r.conditionsCoverageRatio < 0 || r.conditionsCoverageRatio > 1.0) {
            throw new IllegalArgumentException("conditionsCoverageRatio is not a ratio!");
        }
        if (r.mutantsTotal < 0) {
            throw new IllegalArgumentException("mutantsTotal cannot be negative!");
        }
        if (r.mutantsCovered < 0) {
            throw new IllegalArgumentException("mutantsCovered cannot be negative!");
        }
        if (r.mutantsCoverageRatio < 0 || r.mutantsCoverageRatio > 1.0) {
            throw new IllegalArgumentException("mutantsCoverageRatio is not a ratio!");
        }
        if (r.mutantsKilled < 0) {
            throw new IllegalArgumentException("mutantsKilled cannot be negative!");
        }
        if (r.mutantsKillRatio < 0 || r.mutantsKillRatio > 1.0) {
            throw new IllegalArgumentException("mutantsKillRatio is not a ratio!");
        }
        if (r.mutantsAlive < 0) {
            throw new IllegalArgumentException("mutantsAlive cannot be negative!");
        }
        if (r.totalTestClasses < 0) {
            throw new IllegalArgumentException("totalTestClasses cannot be negative!");
        }
        if (!(r.brokenTests + r.failTests <= r.testcaseNumber)) {

            if (r.testcaseNumber == 0 && r.brokenTests == 1 && r.failTests == 1 && r.totalTestClasses == 1) {
                /*
                 * This means that the number of flaky/failing test cases is
                 * wrong (due to java.lang.Exception: No runnable methods)
                 * instead of actual failure/flakyness
                 */
                r.brokenTests = 0;
                r.failTests = 0;
                log("Warning, assuming no test cases in single test class for " + r.tool + "," + r.timeBudget + ","
                        + r.benchmark + "," + r.runId);
            } else {

                throw new IllegalArgumentException(
                        "number of broken tests and failed tests cannot be greater than total number of test cases");
            }
        }

        if (r.uncompilableNumber > r.totalTestClasses) {
            throw new IllegalArgumentException(
                    "number of uncompilable test classes cannot be greater than total number of test classes!");
        }

        if (r.testcaseNumber > 0 && (r.uncompilableNumber == r.totalTestClasses)) {
            /*
             * This is not a problem since the number of test cases (test suite
             * size) is computed from the original test suite (in other words,
             * it does include uncompilable classes) The test suite size is only
             * used for flaky test ratio.
             */
        }
    }

    public static Map<ToolBudgetKey, Double> aggregateScorePerTool(
            Map<ToolBudgetBenchmarkKey, Double> scorePerBenchmark) {
        Map<ToolBudgetKey, Double> scorePerTool = new HashMap<ToolBudgetKey, Double>();
        for (Entry<ToolBudgetBenchmarkKey, Double> entry : scorePerBenchmark.entrySet()) {
            final String toolName = entry.getKey().toolName;
            final int timeBudget = entry.getKey().timeBudget;
            ToolBudgetKey key = new ToolBudgetKey(toolName, timeBudget);
            final double new_score;
            if (!scorePerTool.containsKey(key)) {
                new_score = entry.getValue();
            } else {
                final double old_score = scorePerTool.get(key);
                new_score = old_score + entry.getValue();
            }
            scorePerTool.put(key, new_score);
        }
        for (Entry<ToolBudgetKey, Double> entry : scorePerTool.entrySet()) {
            log("Final score for " + entry.getKey() + " is " + entry.getValue() + " [std. deviation = " + standardDeviations.get(entry.getKey().toolName + entry.getKey().timeBudget).doubleValue() + "]");
        }

        return scorePerTool;
    }

}
