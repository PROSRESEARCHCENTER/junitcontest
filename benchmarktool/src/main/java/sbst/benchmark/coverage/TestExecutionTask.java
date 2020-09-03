package sbst.benchmark.coverage;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import sbst.benchmark.Main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TestExecutionTask implements Callable<List<Result>> {

    private URL[] urls;
    private List<String> testClasses;
    private List<Result> results = new ArrayList<>();

    public TestExecutionTask(String cp, List<String> pTestClasses) {
        try {
            // Load the jar
            urls = TestUtil.createURLs(cp);
            testClasses = pTestClasses;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<Result> call() throws ClassNotFoundException, IOException {
        URLClassLoader cl = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());

        Main.debug("Running the tests: " + testClasses);
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

            // Here we execute our test target class through its Runnable interface:
            JUnitCore junit = new JUnitCore();
            Result result = junit.run(testClass);

            results.add(result);

            //Main.debug("Failure: "+result.getFailures());
            //for (Failure fail : result.getFailures()){
            //	Main.debug("Failing Tests: "+fail.getTestHeader()+"\n"+fail.getException()+"\n"+fail.getDescription()+"\n"+fail.getMessage());
            //}
        }
        Main.debug("Executions terminated");
        try {
            cl.close();
        } catch (SecurityException ex) {
            // Print an error message and continue. Exception occurred due to wrong configuration of the JVM security policy.
            Main.info("Was unable to close the URLClassLoader after execution of the tests due to security policy!");
            ex.printStackTrace();
            Main.info("Will continue execution");
        } catch (IOException ex) {
            // Print an error message and continue. Remaining opened files will be closed at the exit of the application.
            Main.info("An exception occurred during closing of the URLClassLoader!");
            ex.printStackTrace();
            Main.info("Will continue execution");
        }
        return results;
    }

    /**
     * Count the number of failing test methods
     *
     * @return number of failing test methods
     */
    public int countFailingTests() {
        int count = 0;

        for (Result result : this.results) {
            count += result.getFailureCount();
        }
        return count;
    }

    public List<Result> getExecutionResults() {
        return this.results;
    }

}
