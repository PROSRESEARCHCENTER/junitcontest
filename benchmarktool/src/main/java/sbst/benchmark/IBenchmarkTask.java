package sbst.benchmark;

import java.io.File;
import java.util.List;

public interface IBenchmarkTask {
    File getSourceDirectory();

    File getBinDirectory();

    List<File> getClassPath();

    List<String> getClassNames();
}
