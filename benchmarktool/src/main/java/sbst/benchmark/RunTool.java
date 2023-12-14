package sbst.benchmark;

import org.apache.commons.lang.StringUtils;
import sbst.runtool.SBSTChannel;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

public class RunTool {
    private static final double EXTRA_TIME_FACTOR = 1.00;

    private final File homeDir;
    private final File executable;
    private final IToolListener listener;
    private int timeBudget = -1;

    private boolean generateTests = true;

    public void enableGenerateTests() {
        generateTests = true;
    }

    public void disableGenerateTests() {
        generateTests = false;
    }

    public RunTool(File directory, File executable, IToolListener listener, int timeBudget) {
        this.homeDir = directory;
        this.executable = executable;
        this.listener = listener;
        this.timeBudget = timeBudget;
    }

    public void run(IBenchmarkTask task) throws IOException {
        File tempDir = new File(homeDir, "temp");
        Util.cleanDirectory(tempDir);
        File dataDir = new File(tempDir, "data");
        Util.cleanDirectory(dataDir);
        File testcasesDir = new File(tempDir, "testcases");
        Util.cleanDirectory(testcasesDir);

        ProcessBuilder pbuilder = new ProcessBuilder(executable.getAbsolutePath());
        pbuilder.redirectErrorStream(false);
        Process toolSubprocess = null;
        InputStreamReader stdout = null;
        InputStream stderr = null;
        OutputStreamWriter stdin = null;

        Main.debug("Executing " + executable.getAbsolutePath());
        toolSubprocess = pbuilder.start();
        try {
            stderr = toolSubprocess.getErrorStream();
            stdout = new InputStreamReader(toolSubprocess.getInputStream());
            stdin = new OutputStreamWriter(toolSubprocess.getOutputStream());

            Thread drainThread = new Thread(new Drain(stderr));
            drainThread.setDaemon(true);
            drainThread.start();

            execute(task, testcasesDir, new SBSTChannel(stdout, stdin, Main.debugStr));

        } finally {
            Main.debug("Stopping process...");
            killUnixProcess(toolSubprocess);
            Main.debug("Process was destroyed");
        }
    }

    private void execute(IBenchmarkTask task, File testcases, SBSTChannel channel) throws IOException {
        channel.emit("BENCHMARK");
        listener.startPreprocess();
        channel.emit(task.getSourceDirectory());
        channel.emit(task.getBinDirectory());
        channel.emit(task.getClassPath().size());

        for (File cp_entry : task.getClassPath()) {
            channel.emit(cp_entry.getAbsolutePath());
        }

        if (!generateTests) {
            channel.emit(0);
        } else {
            channel.emit(task.getClassNames().size());
        }
        Main.debug("expecting CLASSPATH or READY");
        String line = channel.readLine();
        List<File> extraCP = new ArrayList<File>();
        if ("CLASSPATH".equals(line)) {
            int n = channel.number();
            for (int i = 0; i < n; i++) {
                File entry = channel.directory_jarfile();
                extraCP.add(entry);
            }
            line = channel.readLine();
        }

        if ("READY".equals(line)) {
            listener.endPreprocess();
            if (timeBudget != -1) {
                channel.emit(timeBudget);
            }

            if (generateTests) {
                for (String cname : task.getClassNames()) {
                    Util.cleanDirectory(testcases);
                    Main.info("\n\n### CLASS UNDER TEST ###: " + cname);
                    channel.emit(cname);
                    listener.startClass(cname);
                    if (this.timeBudget != -1) {
                        // enfore time budget
                        try {
                            long budget_millis = timeBudget * 1000;
                            long extra_time_millis = (long) ((double) budget_millis * EXTRA_TIME_FACTOR);
                            long timeout_millis = budget_millis + extra_time_millis;

                            Main.info(String.format(
                                    "\n\n Executing with internal budget of %s sec and external budget of %s sec ",
                                    timeBudget, (timeout_millis / 1000)));
                            token(channel, "READY", timeout_millis);
                            Main.info("\n\n Execution finished with no timeout");
                        } catch (TimeoutException ex) {
                            Main.info("\n\nA timeout occurred waiting for signal READY");
                            // kill subprocess
                        }
                    } else {
                        // no time limit
                        channel.token("READY");
                    }
                    listener.endClass(cname, testcases, extraCP);

                    Main.info(">> >>" + testcases.getAbsolutePath());
                }
            } else {
                for (String cname : task.getClassNames()) {
                    Main.info("\n\n### COMPUTING METRICS ###: " + cname);
                    Main.debug("\n\nStarting class " + cname);
                    listener.startClass(cname);
                    Main.debug("\n\nEnding class " + cname);
                    listener.endClass(cname, testcases, extraCP);
                    Main.debug("\n\nClass " + cname + " ended.");
                }
            }
        }
    }

    private static class Drain implements Runnable {
        private BufferedInputStream stream;

        private Drain(InputStream stream) {
            this.stream = new BufferedInputStream(stream);
        }

        @Override
        public void run() {
            byte[] buff = new byte[1024];
            try {
                int len;
                while ((len = stream.read(buff)) != -1) {
                    System.err.write(buff, 0, len);
                    Main.debugStr.write(buff, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Expects the token in <code>string</code> for at most timeout
     * milliseconds. If it does not arrive, it throws a TimeOutException
     */
    private void token(SBSTChannel channel, String string, long timeout_millis) throws IOException, TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TaskExpectReady future_task = new TaskExpectReady(channel, string);
        Future<Void> future = executor.submit(future_task);

        try {
            future.get(timeout_millis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            for (StackTraceElement trace : e.getStackTrace()) {
                Main.debug("" + trace);
            }
            Main.debug("Error: \n" + e.getCause());
            e.printStackTrace();
        }
    }

    private class TaskExpectReady implements Callable<Void> {
        private final String string;
        private final SBSTChannel channel;

        public TaskExpectReady(SBSTChannel channel, String expected_string) {
            this.string = expected_string;
            this.channel = channel;
        }

        @Override
        public Void call() throws Exception {
            this.channel.token(string);
            return null;
        }
    }

    private static int getUnixPID(Process process)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Class<?> cl = process.getClass();
        Field field = cl.getDeclaredField("pid");
        field.setAccessible(true);
        Object pidObject = field.get(process);
        return (Integer) pidObject;
    }

    private static void killUnixProcess(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Main.debug("Tool subprocess is an instance of java.lang.UNIXProcess");
                int toolSubprocessPID = getUnixPID(process);
                Main.debug("Tool subprocess Id is " + toolSubprocessPID);

                // execute ps -eo pid,ppid
                final String psOutput = executePS();

                // build <PPID,Set<PID>> map
                final Map<Integer, Set<Integer>> children_of_pids = buildParentPIDToChildrenPIDMap(psOutput);

                // kill all descendant processes of pid
                killSubprocesses(toolSubprocessPID, children_of_pids);

            } catch (InterruptedException | IOException | NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException e) {
                Main.debug("An exception occurred while killing tool subprocess:");
                Main.debug(e.getMessage());
            }
        } else {
            Main.debug("Tool subprocess is NOT an instance of java.lang.UNIXProcess");
            Main.debug("calling Process.destroy()");
            process.destroy();
            Main.debug("Process.destroy() finished");
        }
    }

    private static void killSubprocesses(int toolSubprocessPID, final Map<Integer, Set<Integer>> children_of_pids)
            throws InterruptedException, IOException {
        Set<Integer> killed_pids = new HashSet<Integer>();
        Queue<Integer> killing_queue = new PriorityQueue<Integer>();
        killing_queue.add(toolSubprocessPID);
        while (!killing_queue.isEmpty()) {
            int pid = killing_queue.poll();
            if (killed_pids.contains(pid)) {
                continue; // already dead
            } else {
                if (children_of_pids.containsKey(pid)) {
                    Set<Integer> children_pids = children_of_pids.get(pid);
                    killing_queue.addAll(children_pids);
                }

                if (pid == toolSubprocessPID) {
                    Main.debug("Killing master process " + pid);
                } else {
                    Main.debug("Killing child process " + pid);
                }

                final String kill_cmd = "kill -9 " + pid;
                Main.debug("Executing command " + kill_cmd);
                Runtime.getRuntime().exec(kill_cmd).waitFor();
                Main.debug("Execution of command " + kill_cmd + " finished.");
                killed_pids.add(pid);
            }
        }
    }

    private static String executePS() throws IOException, InterruptedException {
        final ProcessBuilder pBuilder = new ProcessBuilder();
        pBuilder.command("ps", "-eo", "pid,ppid");
        final Process psProcess = pBuilder.start();
        psProcess.waitFor();
        InputStream pOutput = psProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(pOutput));
        String line = null;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        String psOutput = builder.toString();
        return psOutput;
    }

    private static Map<Integer, Set<Integer>> buildParentPIDToChildrenPIDMap(String psOutput) throws IOException {
        String line;
        final Map<Integer, Set<Integer>> childs_of_pid = new HashMap<Integer, Set<Integer>>();
        BufferedReader strReader = new BufferedReader(new StringReader(psOutput));
        while ((line = strReader.readLine()) != null) {
            final String[] line_parts = StringUtils.split(line);

            if (line_parts.length < 2) {
                continue; // unsupported format
            }
            final String child_pid_str = line_parts[0];
            final String parent_pid_str = line_parts[1];

            if (child_pid_str.equals("PID")) {
                continue; // skip the header
            }

            int child_pid = Integer.valueOf(child_pid_str);
            int parent_pid = Integer.valueOf(parent_pid_str);

            if (!childs_of_pid.containsKey(parent_pid)) {
                childs_of_pid.put(parent_pid, new HashSet<Integer>());
            }
            childs_of_pid.get(parent_pid).add(child_pid);
        }
        return childs_of_pid;
    }
}
