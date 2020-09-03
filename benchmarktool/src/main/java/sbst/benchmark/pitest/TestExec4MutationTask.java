package sbst.benchmark.pitest;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.pitest.mutationtest.engine.MutationIdentifier;
import sbst.benchmark.Main;
import sbst.benchmark.coverage.TestUtil;
import sbst.benchmark.junit.StoppingJUnitCore;
import sbst.benchmark.pitest.MutationResults.State;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class TestExec4MutationTask implements Callable<MutationResults> {

    String cp;
    URL[] urls;
    List<String> testClasses;
    MutationResults results;
    Set<TestInfo> flakyTests;

    public TestExec4MutationTask(String cp, List<String> pTestClasses, Set<TestInfo> pFlakyTests,
                                 MutationIdentifier id) {
        try {
            // Load the jar
            this.cp = cp;
            urls = TestUtil.createURLs(cp);
            testClasses = pTestClasses;
            this.flakyTests = pFlakyTests;
            results = new MutationResults(new ArrayList<Result>(), id);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    MutationResults processTestResults(Result result) {
        if (result.getFailures().size() > 0) {
            for (Failure fail : result.getFailures()) {
                String header = fail.getTestHeader();
                if (header.contains("(")) {
                    String testMethod = header.substring(0, header.indexOf('('));
                    String tc = header.substring(header.indexOf('(') + 1, header.length());
                    TestInfo info = new TestInfo(tc, testMethod);
                    if (!this.flakyTests.contains(info)
                            && !fail.getTrace().contains("java.lang.Exception: test timed out after")) {
                        Main.debug("TestExec4MutationTask: Mutant " + results.getMutation_id() + " killed by test "
                                + info);
                        results.setState(State.KILLED);
                        return results;
                    }
                }
            }
            // If we are here we did not find a killing test !

            if (result.getRunCount() == result.getFailureCount()) {
                Main.debug("TestExec4MutationTask: All the executed tests triggered timeout. Ignore mutant: "
                        + results.getMutation_id().hashCode());
                Main.debug("=======================================");
                Main.debug("Ignored mutant " + results.getMutation_id());
                Main.debug("CP " + this.cp);
                Main.debug("=======================================");
                results.setState(State.IGNORED);
                return results;
            } else {
                // Some tests triggered timeout other did not
                Main.debug("TestExec4MutationTask: Only some tests triggered the timeout. Kill the mutant: "
                        + results.getMutation_id().hashCode());
                results.setState(State.KILLED);
                return results;
            }
        } else {
            /*
             * The mutant survives THIS test so it ran at least once. However,
             * other tests might change this state later to KILLED.
             */
            results.setState(State.SURVIVED);
            return results;
        }
    }

    @Override
    public MutationResults call() throws ClassNotFoundException, IOException {

        try (URLClassLoader cl = URLClassLoader.newInstance(urls, this.getClass().getClassLoader())) {

            Main.debug("Evaluating mutant " + results.getMutation_id().hashCode() + " using tests: " + testClasses);
            Main.debug("\n CP : " + this.cp + "\n");
            // Execute all the test at once:

            List<Class> actualTestClasses = new ArrayList<>();
            for (String test : testClasses) {

                if (test.contains("_scaffolding")) {
                    Main.debug("Skipped scaffolding test " + test);
                    continue;
                }

                // remove the prefix "testcases." added by our tool
                if (test.startsWith("testcases.")) {
                    test = test.replaceFirst("testcases.", "");
                }

                // load the test case
                final Class<?> testClass = cl.loadClass(test);
                actualTestClasses.add(testClass);
            }

            try {
                StoppingJUnitCore junit = new StoppingJUnitCore();

                long timeout = 5000;
                Result result = junit.run(actualTestClasses, timeout, flakyTests);

                results.addJUnitResult(result);

                return processTestResults(result);

            } catch (Throwable e) {
                Main.debug("ERROR Failed Evaluating mutant " + results.getMutation_id().hashCode() + " using tests: "
                        + testClasses);
                e.printStackTrace(Main.infoStr);
                results.setState(State.IGNORED);
                return results;
            }
        }
    }

    /**
     * Count the number of failing test methods
     *
     * @return number of failing test methods
     */
    public int countFailingTests() {
        int count = 0;

        for (Result result : this.results.getJUnitResults()) {
            count += result.getFailureCount();
        }
        return count;
    }

    public List<Result> getExecutionResults() {
        return this.results.getJUnitResults();
    }

}
