package sbst.benchmark.junit;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import sbst.benchmark.Main;
import sbst.benchmark.pitest.TestInfo;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

// For very large number of test classes this might not work as you can not easily specify tons of inputs on the commandline !
public class StoppingJUnitCore {

    final AtomicBoolean globalTimeout = new AtomicBoolean(false);

    class StoppingListener extends RunListener {
        private RunNotifier runNotifier = null;
        private Set<TestInfo> flakyTests = null;
        private RunListener listener = null;

        public StoppingListener(RunNotifier runNotifier, Set<TestInfo> flakyTests, Result theResult) {
            this.runNotifier = runNotifier;
            this.flakyTests = flakyTests;
            // This let us collect the results of the run and keep it there also
            // after we "kill" JUnitCore
            this.listener = theResult.createListener();

        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            super.testFailure(failure);
            //
            String header = failure.getTestHeader();
            Main.debug("Failed test " + header);
            if (header.contains("(")) {
                String testMethod = header.substring(0, header.indexOf('('));
                String tc = header.substring(header.indexOf('(') + 1, header.length());
                TestInfo info = new TestInfo(tc, testMethod);
                // If the failed test is flaky
                if (flakyTests.contains(info)) {
                    // Keep going
                    Main.debug("\t flaky test, ignore !");
                    listener.testAssumptionFailure(failure);
                    return;
                } else
                    // If the failed test is a timeout
                    if (failure.getTrace().contains("java.lang.Exception: test timed out after")) {
                        // Keep going
                        Main.debug("\t timeout test, ignore !");
                        listener.testAssumptionFailure(failure);
                    } else if (failure.getException() instanceof InterruptedException) {
                        // Keep going
                        Main.debug(
                                "\t test execution reached (probably GLOABL timeout), will stop test execution but do not report killing test !");
                        globalTimeout.set(true);
                        runNotifier.pleaseStop();
                    } else {
                        Main.debug("\t actual test, will stop test execution !");
                        listener.testFailure(failure);
                        runNotifier.pleaseStop();
                    }
            } else {
                // This might be a test suite ...
            }
        }

        @Override
        public void testStarted(Description description) throws Exception {
            super.testStarted(description);
            listener.testRunStarted(description);
            // Main.debug(">>SBST: " + description.getClassName() + "." +
            // description.getMethodName() + " started");

        }

        @Override
        public void testFinished(Description description) throws Exception {
            super.testFinished(description);
            listener.testFinished(description);
            // Main.debug(">>SBST: " + description.getClassName() + "." +
            // description.getMethodName() + " finished");
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            super.testAssumptionFailure(failure);
            listener.testAssumptionFailure(failure);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            super.testIgnored(description);
            listener.testIgnored(description);
        }
    }

    /**
     * Changes the annotation value for the given key of the given annotation to
     * newValue and returns the previous value.
     */
    @SuppressWarnings("unchecked")
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key, newValue);
        return oldValue;
    }

    public Result run(Class testClass, //
                      long timeout, //
                      final Set<TestInfo> flakyTests) {
        return this.run(Collections.singletonList(testClass), timeout, flakyTests);
    }

    public Result run(List<Class> testClasses, //
                      long timeout, //
                      final Set<TestInfo> flakyTests) {
        JUnitCore junit = new JUnitCore();
        final Result theResult = new Result();
        try {
            // Make it possible to stop the execution by invoking the
            // pleaseStop()
            // method.
            for (Field field : junit.getClass().getDeclaredFields()) {
                if (field.getName().equals("notifier") || field.getName().equals("fNotifier")) {
                    field.setAccessible(true);
                    RunNotifier runNotifier;
                    runNotifier = (RunNotifier) field.get(junit);
                    junit.addListener(new StoppingListener(runNotifier, flakyTests, theResult));
                }
            }

            // NOTE: This assumes that tests are annotated using @Test !
            // TODO This is fragile, one should instead enforce it in the source
            // code before compiling the class !
            for (Class testClass : testClasses) {
                for (Method testMethod : testClass.getDeclaredMethods()) {
                    final Test methodAnnotation = testMethod.getAnnotation(Test.class);
                    // Consider ONLY the test methods annotated using @Test
                    if (methodAnnotation != null && methodAnnotation.timeout() == 0) {
                        changeAnnotationValue(methodAnnotation, "timeout", timeout);
                    }
                }
            }
            // If no tests fail, this will return the Result object
            return junit.run(testClasses.toArray(new Class[]{}));
        } catch (StoppedByUserException e) {
            // If this is triggered because of global timeout we should not
            // report any result !
            if (globalTimeout.get()) {
                Main.info("Global timeout reached while executing tests.");
                return null;
            } else {
                // If a test failed we forced JUnitCore to stop. So we need to
                // "recreate" a suitable Result object
                return theResult;
            }
        } catch (Throwable t) {
            Main.info("Failed Test execution with JUNIT CORE: ");
            t.printStackTrace(Main.infoStr);
            throw new RuntimeException("Cannot execute test with StoppingJUnitCore");
        }
    }

    // // Tweak JUnitCore to be more user friendly and stop at the first error
    // // Should we enforce some timeout/error ?!
    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        Main.debugStr = System.out;
        Main.infoStr = System.out;

        // Some DEBUG INFO
        Main.debug("StoppingJUnitCore.main() CP: " + System.getProperty("java.class.path"));
        Main.debug("StoppingJUnitCore.main() ARGS : " + Arrays.toString(args));

        // Parse the command line: WE ASSUME THE EXACT ORDER OF PARAMETERS HERE
        // --output-to " + tempResultFile + " --test " + testClasses + "
        // --flaky-tests " + flakyTests ;
        String outputTo = null;
        List<String> testClasses = null;

        int i = 0;
        if (args[i].equals("--output-to")) {
            i++;
            outputTo = args[i];
        } else {
            System.exit(-120);
        }

        Main.debug("Output result of test execution to : " + outputTo);

        i++;
        if (args[i].equals("--tests")) {
            i++;
            testClasses = new ArrayList<>();
            while (!args[i].trim().equals("--flaky-tests") && i < args.length) {
                // Main.debug("StoppingJUnitCore.main() Parsing " + i + ") " +
                // args[i] + " out of " + args.length );
                testClasses.add(args[i]);
                i++;
            }
        } else {
            System.exit(-120);
        }
        Main.debug("StoppingJUnitCore.main() TEST CLASSES " + testClasses);

        Set<TestInfo> flakyTests = new HashSet<>();
        if (args[i].equals("--flaky-tests") && i < args.length) {
            i++;
            for (int index = i; index < args.length; index++) {
                if (!args[i].equals("--flaky-tests")) {
                    Main.debug("TODO Flaky test " + args[index]);
                    // // TODO Parse flaky test data
                    // // flakyTests.add(args[index]);
                }
            }
        }
        Main.debug("StoppingJUnitCore.main() FLAKY-TESTS " + flakyTests);

        List<Class> classes = new ArrayList<Class>();
        // Load the classes
        for (String each : testClasses) {
            try {
                classes.add(Class.forName(each));
            } catch (ClassNotFoundException e) {
                Main.info(" ERROR: Could not find test class: " + each);
                System.exit(2);
            }
        }

        // TODO: By running all the test tof

        // Run the tests (all of them at once!)
        StoppingJUnitCore junit = new StoppingJUnitCore();
        // Use default values:
        long timeout = 5000;

        try {
            Result result = junit.run(classes, timeout, flakyTests);

            // Was the mutant killed ?
            if (result.getFailureCount() > 0) {
                boolean killed = false;
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
                        killed = true;
                        break;
                    }
                }

                if (!killed) {
                    Main.debug("\n Mutant Ignored !\n");
                } else {
                    Main.debug("\n Mutant Killed !\n");
                }
            }

            Main.debug("Writing serialized JUnit results to " + outputTo);
            // TODO I hoped that XStream could handle the serialization of the
            // result but apparently it does not
            // So we need to take care of it
            try (FileWriter fileWriter = new FileWriter(outputTo);
                 PrintWriter printWriter = new PrintWriter(fileWriter);) {
                printWriter.println(result.getRunCount());
                printWriter.println(result.getFailureCount());
                for (Failure failure : result.getFailures()) {
                    printWriter.println(failure.getTestHeader() + ": " + failure.getTrace());
                    // Leave an empty line
                    printWriter.println("");
                }
            }
            System.exit(0);
        } catch (Throwable e) {
            Main.info("ERROR WHILE RUNNING TESTS !");
            e.printStackTrace();
            System.exit(2);
        }

    }
}
