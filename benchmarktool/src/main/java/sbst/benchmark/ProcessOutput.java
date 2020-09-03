package sbst.benchmark;

import org.apache.commons.exec.LogOutputStream;

import java.util.ArrayList;
import java.util.List;

public class ProcessOutput extends LogOutputStream {
    private final List<String> lines = new ArrayList<String>();
    boolean reroute;

    public ProcessOutput(boolean rerouteToStdOut) {
        reroute = rerouteToStdOut;
    }

    protected void processLine(String line, int level) {
        if (line != null) {
            lines.add(line);
            if (reroute) {
                System.out.println(line);
            }
        }
    }

    public List<String> getLines() {
        return lines;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String line : getLines()) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
