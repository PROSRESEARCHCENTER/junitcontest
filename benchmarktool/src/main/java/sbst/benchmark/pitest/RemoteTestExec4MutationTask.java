package sbst.benchmark.pitest;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.pitest.mutationtest.engine.MutationIdentifier;
import sbst.benchmark.Main;
import sbst.benchmark.junit.StoppingJUnitCore;
import sbst.benchmark.pitest.MutationResults.State;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RemoteTestExec4MutationTask extends TestExec4MutationTask {

    public RemoteTestExec4MutationTask(String cp, List<String> pTestClasses, Set<TestInfo> pFlakyTests,
                                       MutationIdentifier id) {
        super(cp, pTestClasses, pFlakyTests, id);
    }

    @Override
    public MutationResults call() throws ClassNotFoundException, IOException {

        Main.debug("Evaluating mutant (using remote test executor) " + results.getMutation_id().hashCode()
                + " using tests: " + testClasses);

        // TODO create the command line: pass the arguments or set the env
        // set resources
        File tempResultFile = File.createTempFile("sbst-", ".pitest");
        tempResultFile.deleteOnExit();

        List<String> actualTestClasses = new ArrayList<>();
        for (String test : testClasses) {
            if (test.contains("_scaffolding")) {
                Main.debug("Skipped scaffolding test " + test);
                continue;
            }

            // remove the prefix "testcases." added by our tool
            if (test.startsWith("testcases.")) {
                test = test.replaceFirst("testcases.", "");
            }
            actualTestClasses.add(test);
        }

        String cmdLine = String.format("%s -cp %s %s --output-to %s --tests %s --flaky-tests %s", Main.JAVA,
                cp + File.pathSeparatorChar + System.getProperty("java.class.path"), //
                StoppingJUnitCore.class.getName(), tempResultFile.getAbsolutePath(), //
                actualTestClasses.toString().replaceAll(",", " ").replace("[", "").replace("]", ""), //
                flakyTests.toString().replaceAll(",", " ").replace("[", "").replace("]", "") //
        );

        CommandLine cl = CommandLine.parse(cmdLine);

        Main.debug("\n===\njava command line:\n" + cmdLine + "\n");

        PumpStreamHandler psh = new PumpStreamHandler(Main.debugStr);
        DefaultExecutor exec = new DefaultExecutor();
        // Redirect output of the remote process to Main.debug
        psh.start();
        exec.setStreamHandler(psh);
        // We do not really need the exit value since we'll read results
        int exitValud = exec.execute(cl);
        //
        psh.stop();

        if (exitValud == 120) {
            // TODO
            Main.info("ERROR ! Cannot execute remote command !");
            // throw new RuntimeException("RemoteTestExec4MutationTask: error
            // while running tests!");
            results.setState(State.IGNORED);
            return results;
        } else {
            Main.debug("Read JUnit result from " + tempResultFile);
            // Manually parse the file !
            MockResult result = new MockResult();

            try (Scanner sc = new Scanner(tempResultFile);) {

                int totalCount = Integer.parseInt(sc.nextLine());

                if (totalCount == 0) {
                    results.setState(State.NEVER_RUN);
                    Main.debug("RemoteTestExec4MutationTask: No test? Mutant survived "
                            + results.getMutation_id().hashCode());
                    return results;
                }

                result.setRunCount(totalCount);
                results.setState(State.SURVIVED);

                int totalFail = Integer.parseInt(sc.nextLine());

                // Parse the results Each fai
                String h = null;
                String t = null;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.isEmpty()) {

                        if (h == null) {
                            Main.debug("Empty line !");
                        } else {
                            Main.debug("Register a Fail: " + h);
                            result.addFailure(h, t);
                            h = null;
                            t = null;
                        }
                    } else if (h == null) {
                        h = line.split(":")[0];
                        t = line.split(":")[1] + "\n";
                    } else {
                        t = t + line + "\n";
                    }
                }
                results.addJUnitResult(result);
                //
                return processTestResults(result);
            }

        }

    }

}
