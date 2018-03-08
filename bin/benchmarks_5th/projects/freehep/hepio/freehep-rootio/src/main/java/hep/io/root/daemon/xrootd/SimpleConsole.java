package hep.io.root.daemon.xrootd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;
import jline.ConsoleReader;
import jline.History;
import jline.Completor;
import jline.ConsoleOperations;

/**
 * A simple command line interface to xrootd
 * @author tonyj
 */
public class SimpleConsole {

    @Option(metaVar = "host", name = "-h", usage = "Host to connect to")
    private String host;
    @Option(metaVar = "port", name = "-p", usage = "Port to connect to")
    private int port = 1094;
    @Option(metaVar = "level", name = "-l", usage = "Logging level")
    private String level;
    @Argument
    private List<String> arguments = new ArrayList<String>();
    private Session session;
    private static Map<String, Command> commandMap = new TreeMap<String, Command>();
    private static final ByteFormat format = new ByteFormat();


    static {
        commandMap.put("open", new OpenCommand());
        commandMap.put("close", new CloseCommand());
        commandMap.put("ping", new PingCommand());
        commandMap.put("locate", new LocateCommand());
        commandMap.put("level", new LevelCommand());
        commandMap.put("stat", new StatCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("quit", new ExitCommand());
        commandMap.put("dirList", new DirListCommand());
        commandMap.put("checksum", new ChecksumCommand());
        commandMap.put("stats", new StatsCommand());
        commandMap.put("get", new GetCommand());
        commandMap.put("put", new PutCommand());
        commandMap.put("connect", new ConnectCommand());
        commandMap.put("disconnect", new DisconnectCommand());
        commandMap.put("protocol", new ProtocolCommand());
        commandMap.put("help", new HelpCommand());
        commandMap.put("remove", new RemoveCommand());
    }

    public static void main(String[] args) throws IOException {
        new SimpleConsole().doMain(args);
    }

    private void doMain(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            // parse the arguments.
            parser.parseArgument(args);

            if (host != null) {
                session = new Session(host, port, System.getProperty("user.name"));
            }
            if (level != null) {
                setLoggingLevel(level);
            }
            if (!arguments.isEmpty()) {
                handleCommand(arguments, this, new PrintWriter(System.out, true));
            } else {
                ReadAheadConsole console = new ReadAheadConsole();
                for (;;) {
                    try {
                        String line = console.readLine(String.format("scalla%s>", session == null ? "" : "(" + session + ")"));
                        if (line == null) {
                            break;
                        }
                        if (line.trim().length() == 0) {
                            continue;
                        }
                        String[] tokens = line.trim().split("\\s+");
                        handleCommand(Arrays.asList(tokens), this, new PrintWriter(System.out, true));

                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            }
        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.printf("java %s [options...] arguments...\n", SimpleConsole.class.getName());
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.printf("  Example: java %s %s\n", SimpleConsole.class.getName(), parser.printExample(ExampleMode.ALL));

            return;
        }
    }

    private Session getSession() {
        return session;
    }

    private void setSession(Session newSession) throws IOException {
        if (session != null) {
            session.close();
        }
        session = newSession;
    }

    private void handleCommand(List<String> tokens, SimpleConsole session, PrintWriter console) throws SecurityException, IOException, IllegalArgumentException {
        String commandName = tokens.get(0);
        Command command = commandMap.get(commandName);
        if (command == null) {
            console.printf("Unknown command: %s\n", commandName);
        } else {
            command.doCommand(commandName, tokens.subList(1, tokens.size()), session, console);
        }
    }

    private void setLoggingLevel(String token) throws IllegalArgumentException, SecurityException {
        Level logLevel = Level.parse(token);
        Logger.getLogger("").setLevel(logLevel);
        Logger.getLogger("").getHandlers()[0].setLevel(logLevel);
    }

    private String getLoggingLevel() {
        return Logger.getLogger("").getLevel().getName();
    }

    static class CommandCompletor implements Completor {

        public int complete(String buffer, int position, List candidates) {
            if (buffer.contains(" ")) {
                return 0;
            } else {
                for (String command : commandMap.keySet()) {
                    if (command.startsWith(buffer)) {
                        candidates.add(command + " ");
                    }
                }
                return 0;
            }
        }
    }

    static class ControlCListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            System.err.println("ctrlc");
        }
    }

    static abstract class Command {

        @Option(metaVar = "level", name = "-l", usage = "Set logging level for this command")
        private String level;
        private SimpleConsole simpleConsole;

        Session getSession() {
            if (simpleConsole.getSession() == null) {
                throw new RuntimeException("No session");
            }
            return simpleConsole.getSession();
        }

        void setLoggingLevel(String level) {
            simpleConsole.setLoggingLevel(level);
        }

        void setSession(Session session) throws IOException {
            simpleConsole.setSession(session);
        }

        String printExample() {
            return new CmdLineParser(this).printExample(ExampleMode.ALL);
        }

        abstract void doCommand(PrintWriter console) throws IOException;

        void doCommand(String command, List<String> args, SimpleConsole session, PrintWriter console) throws IOException {
            reset();
            CmdLineParser parser = new CmdLineParser(this);
            try {
                // parse the arguments.
                parser.parseArgument(args.toArray(new String[args.size()]));
                this.simpleConsole = session;
                if (level != null) {
                    String oldLevel = simpleConsole.getLoggingLevel();
                    try {
                        setLoggingLevel(level);
                        doCommand(console);
                    } finally {
                        setLoggingLevel(oldLevel);
                    }
                } else {
                    doCommand(console);
                }
            } catch (CmdLineException e) {
                // if there's a problem in the command line,
                // you'll get this exception. this will report
                // an error message.
                System.err.println(e.getMessage());
                System.err.printf("%s [options...] arguments...\n", command);
                // print the list of available options
                parser.printUsage(System.err);
                System.err.println();

                // print option sample. This is useful some time
                System.err.printf("  Example: %s %s\n", command, parser.printExample(ExampleMode.ALL));

            }
        }

        private void printHelp(Writer writer) {
            new CmdLineParser(this).printUsage(writer, null);
        }

        void reset() {
            level = null;
        }
    }

    static class HelpCommand extends Command {

        @Argument(metaVar = "command", index = 0, usage = "Command for which help is requested")
        private String commandName;

        @Override
        void reset() {
            super.reset();
            commandName = null;
        }

        @Override
        void doCommand(PrintWriter console) throws IOException {
            if (commandName == null) {
                for (Map.Entry<String, Command> entry : commandMap.entrySet()) {
                    console.printf("%s %s\n", entry.getKey(), entry.getValue().printExample());
                }
            } else {
                Command command = commandMap.get(commandName);
                if (command == null) {
                    console.printf("Unknown command: %s\n", commandName);
                } else {
                    command.printHelp(console);
                }
            }
        }
    }

    static class OpenCommand extends Command {

        @Argument(metaVar = "path", index = 0, required = true, usage = "Path to open")
        private String path;
        @Option(name = "-y", usage = "Open the file for asynchronous i/o")
        private boolean async;
        @Option(name = "-c", usage = "Open a file even when compressed")
        private boolean compress;
        @Option(name = "-d", usage = "Open a new file, deleting any existing file")
        private boolean delete;
        @Option(name = "-f", usage = "Ignore file usage rules")
        private boolean force;
        @Option(name = "-m", usage = "Create directory path if it does not already exist")
        private boolean mkpath;
        @Option(name = "-n", usage = "Open a new file only if it does not already exist")
        private boolean newFile;
        @Option(name = "-w", usage = "Open the file only if it does not cause a wait")
        private boolean nowait;
        @Option(name = "-a", usage = "Open only for appending")
        private boolean open_apnd;
        @Option(name = "-r", usage = "Open only for reading")
        private boolean open_read;
        @Option(name = "-u", usage = "Open for reading and writing")
        private boolean open_updt;
        @Option(name = "-e", usage = "Update cached information on the file's location ")
        private boolean refresh;
        @Option(name = "-p", usage = "The file is being opened for replica creation")
        private boolean replica;
        @Option(name = "-s", usage = "Return file status information in the response")
        private boolean retstat;
        @Option(name = "-h", usage = "Hide the file until successfully closed")
        private boolean ulterior;

        @Override
        void reset() {
            super.reset();
            async = compress = delete = force = mkpath = newFile = nowait = open_apnd = open_read = open_updt = refresh = replica = retstat = ulterior = false;
        }

        void doCommand(PrintWriter console) throws IOException {
            int options = 0;
            if (async) {
                options |= XrootdProtocol.kXR_async;
            }
            if (compress) {
                options |= XrootdProtocol.kXR_compress;
            }
            if (delete) {
                options |= XrootdProtocol.kXR_delete;
            }
            if (force) {
                options |= XrootdProtocol.kXR_force;
            }
            if (mkpath) {
                options |= XrootdProtocol.kXR_mkpath;
            }
            if (newFile) {
                options |= XrootdProtocol.kXR_new;
            }
            if (nowait) {
                options |= XrootdProtocol.kXR_nowait;
            }
            if (open_apnd) {
                options |= XrootdProtocol.kXR_open_apnd;
            }
            if (open_read) {
                options |= XrootdProtocol.kXR_open_read;
            }
            if (open_updt) {
                options |= XrootdProtocol.kXR_open_updt;
            }
            if (refresh) {
                options |= XrootdProtocol.kXR_refresh;
            }
            if (replica) {
                options |= XrootdProtocol.kXR_replica;
            }
            if (retstat) {
                options |= XrootdProtocol.kXR_retstat;
            }
            if (ulterior) {
                options |= XrootdProtocol.kXR_ulterior;
            }
//            int handle = getSession().open(path, 0, options);
//            console.printf("file handle=%d\n", handle);
        }
    }

    static class CloseCommand extends Command {

        @Argument(metaVar = "handle", index = 0, required = true, usage = "Handle to close")
        private int handle;

        void doCommand(PrintWriter console) throws IOException {
//            getSession().close(handle);
        }
    }

    static class PingCommand extends Command {

        void doCommand(PrintWriter console) throws IOException {
            getSession().ping();
        }
    }

    static class ExitCommand extends Command {

        void doCommand(PrintWriter console) throws IOException {
            System.exit(0);
        }
    }

    static class StatCommand extends Command {

        @Argument(metaVar = "path", index = 0, required = true, usage = "Path to file")
        private String path;

        void doCommand(PrintWriter console) throws IOException {
            FileStatus status = getSession().stat(path);
            console.printf("%s\n", status);
        }
    }

    static class RemoveCommand extends Command {

        @Argument(metaVar = "path", index = 0, required = true, usage = "Path to file")
        private String path;

        void doCommand(PrintWriter console) throws IOException {
            getSession().remove(path);
        }
    }

    static class DirListCommand extends Command {

        @Argument(metaVar = "path", index = 0, required = true, usage = "Path to directory")
        private String path;

        @Override
        void doCommand(PrintWriter console) throws IOException {
            List<String> list = getSession().dirList(path);
            for (String file : list) {
                console.printf("%s\n", file);
            }
        }
    }

    static class LevelCommand extends Command {

        @Argument(metaVar = "level", index = 0, required = true, usage = "Logging level")
        private String level;

        @Override
        void doCommand(PrintWriter console) throws IOException {
            setLoggingLevel(level);
        }
    }

    static class LocateCommand extends Command {

        @Argument(metaVar = "path", index = 0, required = true, usage = "Path to locate")
        private String path;

        @Override
        void doCommand(PrintWriter console) throws IOException {
            String[] result = getSession().locate(path, false, false);
            for (String file : result) {
                console.printf("%s\n", file);
            }
        }
    }

    static class ChecksumCommand extends Command {

        @Argument(metaVar = "path", index = 0, required = true, usage = "Path to file")
        private String path;

        @Override
        void doCommand(PrintWriter console) throws IOException {
            String checksum = getSession().query(XrootdProtocol.kXR_Qcksum, path);
            console.printf("%s\n", checksum);
        }
    }

    static class StatsCommand extends Command {

        @Argument(metaVar = "arg", index = 0, required = false, usage = "Optional list of letters, each indicating the statistical components to be returned (default all)")
        private String arg = "a";

        @Override
        void doCommand(PrintWriter console) throws IOException {
            String result = getSession().query(XrootdProtocol.kXR_QStats,arg);
            console.printf("%s\n", result);
        }

        @Override
        void reset() {
            super.reset();
            arg = "a";
        }

    }

    static class ConnectCommand extends Command {

        @Argument(metaVar = "host", index = 0, required = true, usage = "Host to connect to")
        private String host;
        @Option(name = "-p", usage = "Port to connect to")
        private int port = 1094;

        @Override
        void reset() {
            super.reset();
            port = 1094;
        }

        @Override
        void doCommand(PrintWriter console) throws IOException {
            Session session = new Session(host, port, System.getProperty("user.name"));
            setSession(session);
        }
    }

    static class DisconnectCommand extends Command {

        @Override
        void doCommand(PrintWriter console) throws IOException {
            setSession(null);
        }
    }

    static class ProtocolCommand extends Command {

        @Override
        void doCommand(PrintWriter console) throws IOException {
            String protocol = getSession().protocol();
            console.println(protocol);
        }
    }

    static class GetCommand extends Command {

        @Argument(metaVar = "source", index = 0, required = true, usage = "Source file")
        private String path;
        @Argument(metaVar = "target", index = 1, required = false, usage = "Destination file")
        private File dest;
        @Option(name = "-q", usage = "Quiet mode")
        private boolean quiet;

        @Override
        void reset() {
            super.reset();
            quiet = false;
            dest = null;
        }

        @Override
        void doCommand(PrintWriter console) throws IOException {
            File file = new File(path);
            File local = dest == null ? new File(file.getName()) : dest.isDirectory() ? new File(dest, file.getName()) : dest;
            OpenFile openFile = getSession().open(path, 0, XrootdProtocol.kXR_open_read + XrootdProtocol.kXR_retstat);
            FileOutputStream out = new FileOutputStream(local);
            FileChannel fileChannel = out.getChannel();
            try {
                long lTotal = 0;
                long tStart = System.currentTimeMillis();
                long tNext = tStart;
                int bufferSize = 1000000;
                // Note, this may be null for older servers
                FileStatus status = openFile.getStatus();
                if (status == null) {
                    status = getSession().stat(path);
                }
                long fileSize = status.getSize();
                for (long offset = 0;; offset += bufferSize) {
                    ReadOperation ro = new ReadOperation(openFile, fileChannel, offset, bufferSize);
                    int l = getSession().send(ro).getResponse();
                    if (l <= 0) {
                        if (!quiet) {
                            tNext = updateProgress(tNext, tStart, console, lTotal, fileSize, true);
                        }
                        break;
                    }
                    lTotal += l;
                    if (!quiet) {
                        tNext = updateProgress(tNext, tStart, console, lTotal, fileSize, false);
                    }
                }
            } finally {
                getSession().close(openFile);
                fileChannel.close();
                if (!quiet) {
                    console.println();
                }
            }
        }

        private long updateProgress(long tNext, long tStart, PrintWriter console, long current, long size, boolean finalUpdate) {
            long tNow = System.currentTimeMillis();
            if (finalUpdate || tNow > tNext) {
                int progress = (int) (40 * current / size);

                tNext = tNow + 100; // Update at 10Hz max
                long elapsed = tNow - tStart;
                console.print('[');
                for (int i = 0; i < progress; i++) {
                    console.print('*');
                }
                for (int i = progress; i < 40; i++) {
                    console.print(' ');
                }
                console.print("]  ");
                console.print(format.format(current));
                console.print('/');
                console.print(format.format(size));
                console.print("  ");
                if (finalUpdate || elapsed > 500) {
                    console.print(format.format(1000 * current / elapsed));
                    console.print("/sec");
                }
                console.print("    \r");
                console.flush();
            }
            return tNext;
        }
    }

    static class PutCommand extends Command {

        @Argument(metaVar = "file", index = 0, required = true, usage = "Local file path")
        private File local;
        @Argument(metaVar = "path", index = 1, required = true, usage = "Scalla file path")
        private String path;
        @Option(name = "-d", usage = "Open a new file, deleting any existing file")
        private boolean delete;
        @Option(name = "-f", usage = "Ignore file usage rules")
        private boolean force;
        @Option(name = "-m", usage = "Create directory path if it does not already exist")
        private boolean mkpath;
        @Option(name = "-n", usage = "Open a new file only if it does not already exist")
        private boolean newFile;

        @Override
        void reset() {
            super.reset();
            delete = force = mkpath = newFile = false;
        }

        @Override
        void doCommand(PrintWriter console) throws IOException {
            int options = 0;
            if (delete) {
                options |= XrootdProtocol.kXR_delete;
            }
            if (force) {
                options |= XrootdProtocol.kXR_force;
            }
            if (mkpath) {
                options |= XrootdProtocol.kXR_mkpath;
            }
            if (newFile) {
                options |= XrootdProtocol.kXR_new;
            }
            OpenFile file = getSession().open(path, 0, options);
            InputStream in = new FileInputStream(local);
            try {
                byte[] buffer = new byte[65536];
                int lTotal = 0;
                for (;;) {
                    int l = in.read(buffer);
                    if (l <= 0) {
                        break;
                    }
                    getSession().write(file, lTotal, buffer, 0, l);
                    lTotal += l;
                }
            } finally {
                getSession().close(file);
                in.close();
            }
        }
    }

    private class ReadAheadConsole implements Runnable {

        private ConsoleReader console;
        private Thread readAheadThread = new Thread(this,"ReadAheadConsole");
        private BlockingQueue<String> readAhead = new LinkedBlockingQueue<String>();
        private final String END_OF_DATA = new String("EOD");

        ReadAheadConsole() throws IOException {
            console = new ConsoleReader(System.in, new PrintWriter(System.out), ReadAheadConsole.class.getResourceAsStream("keybindings.properties"));
            File historyDir = new File(new File(System.getProperty("user.home")), ".scalla");
            historyDir.mkdir();
            File historyFile = new File(historyDir, "command.history");
            History history = historyDir.canWrite() ? new History(historyFile) : new History();
            console.setHistory(history);
            console.addCompletor(new CommandCompletor());
            console.addTriggeredAction(ConsoleOperations.CTRL_C, new ControlCListener());
            readAheadThread.setDaemon(true);
            readAheadThread.start();
        }

        private String readLine(String prompt) throws IOException {
            try
            {
                if (readAhead.isEmpty())
                {
                    console.setDefaultPrompt(prompt);
                    console.redrawLine();
                    console.flushConsole();
                }
                String result = readAhead.take();
                // Don't use .equals
                if (result == END_OF_DATA) {
                    console.printNewline();
                    console.flushConsole();
                    return null;
                }
                return result;
            }
            catch (InterruptedException x)
            {
                throw new InterruptedIOException("IO Error reading console");
            }
        }

        public void run() {
            for (;;)
            {
                try
                {
                    console.setDefaultPrompt("");
                    String result = console.readLine();
                    readAhead.offer(result == null ? END_OF_DATA : result);
                }
                catch (IOException x) {

                }
            }
        }
    }
}
