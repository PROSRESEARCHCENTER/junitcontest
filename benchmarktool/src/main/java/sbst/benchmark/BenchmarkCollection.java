package sbst.benchmark;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.plist.PropertyListConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.util.*;

public class BenchmarkCollection {
    Map<String, IBenchmarkTask> benchmarks;

    public BenchmarkCollection(File file) throws ConfigurationException {
        PropertyListConfiguration benchmarkList = new PropertyListConfiguration();
        benchmarkList.load(file);
        benchmarks = new HashMap<String, IBenchmarkTask>();
        for (ConfigurationNode child : benchmarkList.getRoot().getChildren()) {
            String key = child.getName();
            SubnodeConfiguration conf = benchmarkList.configurationAt(key);

            String src = conf.getString("src");
            if (src == null || src.isEmpty()) {
                throw new ConfigurationException("Missing field: src");
            }
            String bin = conf.getString("bin");
            if (bin == null || bin.isEmpty()) {
                throw new ConfigurationException("Missing field: bin");
            }
            String[] classpath = conf.getStringArray("classpath");
            if (classpath == null) {
                classpath = new String[0];
            }
            String[] classes = conf.getStringArray("classes");
            if (classes == null || classes.length == 0) {
                throw new ConfigurationException("Missing or empty field: classes");
            }
            List<File> cp = new ArrayList<File>(classpath.length);
            for (String path : classpath) {
                cp.add(new File(path));
            }
            benchmarks.put(key, new Benchmark(new File(src), new File(bin), cp, Arrays.asList(classes)));
        }
    }

    public IBenchmarkTask forName(String benchmark) {
        return benchmarks.get(benchmark);
    }

    public IBenchmarkTask forName(String benchmark, int size, long seed) throws Exception {
        Random rand = new Random(seed);
        IBenchmarkTask result = forName(benchmark);
        if (result != null) {
            List<String> names = result.getClassNames();
            if (size > 0 && size < names.size()) {
                List<String> newnames = new ArrayList<String>(names);
                Collections.shuffle(newnames, rand);
                newnames = new ArrayList<String>(newnames.subList(0, size));
                result = new Benchmark(result.getSourceDirectory(), result.getBinDirectory()
                        , result.getClassPath(), names);
            }
        }
        return result;
    }

    public Set<String> getBenchmarks() {
        return benchmarks.keySet();
    }
}
