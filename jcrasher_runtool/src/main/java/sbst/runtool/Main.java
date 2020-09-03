package sbst.runtool;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main {
    public static final String JCRASHER_JAR = System.getProperty("sbst.benchmark.jcrasher");

    public static void main(String[] args) throws IOException {
        RunTool runtool = new RunTool(new JCrasher(), new InputStreamReader(System.in), new OutputStreamWriter(System.out));
        runtool.run();
    }
}
