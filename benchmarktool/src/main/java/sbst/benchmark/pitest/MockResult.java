package sbst.benchmark.pitest;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;

public class MockResult extends Result {
    private static final long serialVersionUID = 1L;
    List<Failure> failures = new ArrayList<Failure>();
    private int runCount = 0;

    public void addFailure(String header, String trace) {
        MockFailure f = new MockFailure(null, null);
        f.setTrace(trace);
        f.setTestHeader(header);
        this.failures.add(f);
    }

    @Override
    public int getFailureCount() {
        return this.failures.size();
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    @Override
    public int getRunCount() {
        return this.runCount;
    }

    @Override
    public List<Failure> getFailures() {
        return this.failures;
    }
}