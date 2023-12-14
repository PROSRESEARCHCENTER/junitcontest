package sbst.benchmark;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Benchmark implements IBenchmarkTask {
    private final File sourceDirectory;
    private final File binDirectory;
    private final List<File> classPath;
    private final List<String> classNames;

    public Benchmark(File source, File bin, List<File> cp, List<String> classes) {
        sourceDirectory = source;
        binDirectory = bin;
        classPath = new ArrayList<File>(cp);
        classNames = new ArrayList<String>(classes);
        Collections.sort(classNames);
    }

    @Override
    public File getSourceDirectory() {
        return sourceDirectory;
    }

    @Override
    public File getBinDirectory() {
        return binDirectory;
    }

    @Override
    public List<File> getClassPath() {
        return classPath;
    }

    @Override
    public List<String> getClassNames() {
        return classNames;
    }
}
