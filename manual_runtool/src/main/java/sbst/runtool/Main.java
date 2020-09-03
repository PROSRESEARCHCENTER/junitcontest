package sbst.runtool;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main {

    @SuppressWarnings("checkstyle:systemexit")
    public static void main(String[] args) throws IOException {
        RunTool runtool = new RunTool(new Manual(args.length > 0 ? args[0] : null), new InputStreamReader(System.in), new OutputStreamWriter(System.out));
        runtool.run();
        System.exit(0);
    }
}
