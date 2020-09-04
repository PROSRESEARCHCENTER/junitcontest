package sbst.runtool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JCrasher implements ITestingTool {

    List<File> classPath;

    public List<File> getExtraClassPath() {
        List<File> ret = new ArrayList<File>();
        ret.add(new File(Main.JCRASHER_JAR));
        return ret;
    }

    public void initialize(File src, File bin, List<File> classPath) {
        this.classPath = classPath;
    }

    public void run(String cName) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < classPath.size(); i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(classPath.get(i));
            }
            jcrasher_run(sb.toString(), cName);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Failed to run jCrasher");
        }
    }

    private boolean jcrasher_run(String cp, String cut) throws IOException {
        PrintStream logging = new PrintStream(new FileOutputStream(new File("./jcrasher_log.txt"), false));

        final String javaCmd = "/usr/bin/java";

        ProcessBuilder pbuilder = new ProcessBuilder(javaCmd,
                "-classpath", Main.JCRASHER_JAR + ":" + cp,
                "edu.gatech.cc.jcrasher.JCrasher", "-o", "./temp/testcases/",
                "-f", "10", cut);

        pbuilder.redirectErrorStream(true);
        Process process = null;
        InputStreamReader stdout = null;
        InputStream stderr = null;
        OutputStreamWriter stdin = null;
        boolean mutationExitStatus = false;

        process = pbuilder.start();
        stderr = process.getErrorStream();
        stdout = new InputStreamReader(process.getInputStream());
        stdin = new OutputStreamWriter(process.getOutputStream());

        BufferedReader reader = new BufferedReader(stdout);
        String line = null;
        while ((line = reader.readLine()) != null) {
            logging.println(line);
        }
        reader.close();
        logging.flush();
        logging.close();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int exitStatus = process.exitValue();

        mutationExitStatus = exitStatus == 0;

        if (stdout != null) {
            stdout.close();
        }
        if (stdin != null) {
            stdin.close();
        }
        if (stderr != null) {
            stderr.close();
        }
        if (process != null) {
            process.destroy();
        }
        try {
            process.waitFor();
        } catch (InterruptedException ie) {
        }


        new File("./temp/testcases/JUnitAll.java").delete();
        return mutationExitStatus;
    }
}
