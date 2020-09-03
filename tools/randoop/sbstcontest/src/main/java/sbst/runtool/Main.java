package sbst.runtool;

public class Main {

    public static void main(String[] args) throws IOException {
        Writer writer = new PrintWriter(System.out);
        Reader reader = new InputStreamReader(System.in);
        RandoopTool tool = new RandoopTool();
        RunTool runTool = new RunTool(tool, reader, writer);
        runTool.run();
    }

}
