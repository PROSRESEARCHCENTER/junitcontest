package sbst.benchmark;

import java.io.File;
import java.util.List;

public interface IToolListener {
    public void finish();

    public void startPreprocess();

    public void endPreprocess();

    public void startClass(String cname);

    public void endClass(String cname, File testcaseDir, List<File> extraCP);

    public void start(String toolName, int timeBudget, String benchmark, int runNumber);
}
