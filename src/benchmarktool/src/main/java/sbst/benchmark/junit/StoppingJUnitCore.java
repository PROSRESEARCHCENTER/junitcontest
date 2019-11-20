package sbst.benchmark.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
            // This let us collect the results of the run and keep it there also after we "kill" JUnitCore
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
                } else if (failure.getException() instanceof InterruptedException ){
                 // Keep going
                    Main.debug("\t test execution reached (probably GLOABL timeout), will stop test execution but do not report killing test !");
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
//            Main.debug(">>SBST: " + description.getClassName() + "." + description.getMethodName() + " started");
            
        }

        @Override
        public void testFinished(Description description) throws Exception {
            super.testFinished(description);
            listener.testFinished(description);
//            Main.debug(">>SBST: " + description.getClassName() + "." + description.getMethodName() + " finished");
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
            for (Method testMethod : testClass.getDeclaredMethods()) {
                final Test methodAnnotation = testMethod.getAnnotation(Test.class);
                // Consider ONLY the test methods annotated using @Test
                if (methodAnnotation != null && methodAnnotation.timeout() == 0) {
                    changeAnnotationValue(methodAnnotation, "timeout", timeout);
                }
            }
            // If no tests fail, this will return the Result object
            return junit.run(testClass);
        } catch (StoppedByUserException e) {
            // If this is triggered because of global timeout we should not report any result !
            if( globalTimeout.get() ){
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
    // public static void main(String[] args) throws NoSuchFieldException,
    // SecurityException, IllegalArgumentException,
    // IllegalAccessException, InvocationTargetException {
    // JUnitCore junit = new JUnitCore();
    //
    // // Make it possible to stop the execution by invoking the pleaseStop()
    // // method.
    // Field field = junit.getClass().getDeclaredField("notifier");
    // field.setAccessible(true);
    // final RunNotifier runNotifier = (RunNotifier) field.get(junit);
    //
    // junit.addListener(new RunListener() {
    // @Override
    // public void testFailure(Failure failure) throws Exception {
    // super.testFailure(failure);
    // System.out.println("SBST:" + " " + failure.getTestHeader());
    // // If the error cause is really needed we can parse it as well:
    // System.err.println(">>SBST:" + " " + failure.getTrace());
    // runNotifier.pleaseStop();
    //
    // }
    //
    // @Override
    // public void testStarted(Description description) throws Exception {
    // super.testStarted(description);
    // System.err.println(
    // ">>SBST: " + description.getClassName() + "." +
    // description.getMethodName() + " started");
    // }
    //
    // @Override
    // public void testFinished(Description description) throws Exception {
    // super.testFinished(description);
    // System.err.println(
    // ">>SBST: " + description.getClassName() + "." +
    // description.getMethodName() + " finished");
    // }
    // });
    //
    // // The following code is similar to JUnitCore.runMain except for
    // // registering the default TextListener
    // List<Class<?>> classes = new ArrayList<Class<?>>();
    // List<Failure> missingClasses = new ArrayList<Failure>();
    // for (String each : args) {
    // try {
    // classes.add(Class.forName(each));
    // } catch (ClassNotFoundException e) {
    // System.err.println("Could not find test class: " + each);
    // System.exit(2);
    // }
    // }
    //
    // // Enforce all the tests to specify a timeout of 5000 if no timeout is
    // // already there
    // // NOTE: This assumes that tests are annotated using @Test !
    // // TODO This is fragile, one should instead enforce it in the source
    // // code before compiling the class !
    // for (Class testClass : classes) {
    // for (Method testMethod : testClass.getDeclaredMethods()) {
    // final Test methodAnnotation = testMethod.getAnnotation(Test.class);
    // // Consider ONLY the test methods annotated using @Test
    // if (methodAnnotation != null && methodAnnotation.timeout() == 0) {
    // changeAnnotationValue(methodAnnotation, "timeout", new Long(5000));
    // }
    // }
    // }
    //
    // // Execute the tests
    // Result result = junit.run(classes.toArray(new Class[0]));
    // for (Failure each : missingClasses) {
    // result.getFailures().add(each);
    // }
    // // TODO Serialize result to file using XStream
    // System.exit(result.wasSuccessful() ? 0 : 1);
    // }
}
