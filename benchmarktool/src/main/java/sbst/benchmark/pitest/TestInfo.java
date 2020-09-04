package sbst.benchmark.pitest;

public class TestInfo {

    public String testClass;
    public String testMethod;

    public TestInfo(String tClass, String tMethod) {
        this.testClass = tClass;
        this.testMethod = tMethod;
    }

    public String getTestClass() {
        return testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    @Override
    public String toString() {
        return "(" + testClass + "," + testMethod + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof TestInfo) {
            TestInfo other = (TestInfo) o;
            if (this.testClass.equals(other.testClass) && this.testMethod.equals(other.testMethod)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + testClass.hashCode();
        result = 31 * result + testMethod.hashCode();
        return result;
    }

}
