/**
 * Copyright (c) 2017 Universitat Politècnica de València (UPV)
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the UPV nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package sbst.benchmark;

import org.junit.Assume;
import org.junit.Test;
import sbst.benchmark.Score.ToolBudgetBenchmarkKey;
import sbst.benchmark.Score.ToolBudgetBenchmarkRunIdKey;
import sbst.benchmark.Score.ToolBudgetKey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestScore {

    private static final String SINGLE_LINE_CSV = "single_line.csv";

    private static final String MULTI_LINE_CSV = "multi_line.csv";

    private static final String MULTI_RUN_CSV = "multi_run.csv";

    private static final String MULTI_RUN_INVALID_ROW_CSV = "multi_run_invalid_row.csv";

    private static final String MULTI_ROW_CSV = "multi_row.csv";

    private static final String BLANK_LINE_CSV = "blank_line.csv";

    private static final String PARTIAL_RESULTS_1_CSV = "partial_results_1.csv";

    @Test
    public void testSingleLineFlow() throws FileNotFoundException, IOException {
        String scoreFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + SINGLE_LINE_CSV;
        File scoreFile = new File(scoreFileName);
        Assume.assumeTrue(scoreFile.exists());

        Map<ToolBudgetBenchmarkRunIdKey, ExecutionResult> m = Score.readResults(scoreFile);
        assertEquals(1, m.entrySet().size());
        Map<ToolBudgetBenchmarkRunIdKey, Double> scorePerRun = Score.computeScorePerRun(m);
        assertEquals(1, scorePerRun.entrySet().size());
        Map<ToolBudgetBenchmarkKey, Double> scorePerBenchmark = Score.computeBenchmarkAverage(scorePerRun);
        assertEquals(1, scorePerBenchmark.entrySet().size());
        Map<ToolBudgetKey, Double> scorePerTool = Score.aggregateScorePerTool(scorePerBenchmark);
        assertEquals(1, scorePerTool.entrySet().size());
    }

    @Test
    public void testSingleLinePrint() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + SINGLE_LINE_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testMultiLinePrint() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + MULTI_LINE_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testMultiRunPrint() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + MULTI_RUN_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testMultiRunInvalid() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + MULTI_RUN_INVALID_ROW_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testMultiRow() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + MULTI_ROW_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testBlankLine() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + BLANK_LINE_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testOutputFile() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + BLANK_LINE_CSV;
        final String outFileName = System.getProperty("user.dir") + File.separator + "out.csv";
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        File outFile = new File(outFileName);
        try {
            if (outFile.exists()) {
                outFile.delete();
            }
            Score.main(new String[]{csvFileName, outFileName});
            assertTrue(outFile.exists());
        } finally {
            outFile.delete();
        }

    }

    @Test
    public void testVerbosity() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + BLANK_LINE_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }

    @Test
    public void testPartialResults1() throws FileNotFoundException, IOException {
        final String csvFileName = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + PARTIAL_RESULTS_1_CSV;
        File csvFile = new File(csvFileName);
        Assume.assumeTrue(csvFile.exists());

        Map<ToolBudgetKey, Double> m = Score.computeScore(csvFile);
        Score.printScore(System.out, m);
    }
}
