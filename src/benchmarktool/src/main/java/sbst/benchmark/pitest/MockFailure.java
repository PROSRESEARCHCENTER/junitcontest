package sbst.benchmark.pitest;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class MockFailure extends Failure {

    private String trace = null;
    private String header = null;

    MockFailure(Description description, Throwable thrownException) {
        super(description, thrownException);
    }

    private static final long serialVersionUID = 1L;

    public void setTrace(String trace) {
        this.trace = trace;
    }

    @Override
    public String getTrace() {
        return this.trace;
    }

    public void setTestHeader(String header) {
        this.header = header;
    }

    @Override
    public String getTestHeader() {
        return this.header;
    }

}