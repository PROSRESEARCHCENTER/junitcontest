package sbst.benchmark;

import org.junit.Assert;
import org.junit.Test;
import sbst.benchmark.pitest.MockResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestDeserialize {

    @Test
    public void testLoadXML() throws FileNotFoundException {
        File tempResultFile = new File("src/test/resources/sample.result.pitest");
        MockResult result = new MockResult();

        Scanner sc = new Scanner(tempResultFile);

        int totalCount = Integer.parseInt(sc.nextLine());

        result.setRunCount(totalCount);

        int totalFail = Integer.parseInt(sc.nextLine());

        // Parse the results Each fai
        String h = null;
        String t = null;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) {
                result.addFailure(h, t);
                h = null;
                t = null;
            } else if (h == null) {
                h = line.split(":")[0];
                t = line.split(":")[1] + "\n";
            } else {
                t = t + line + "\n";
            }
        }

        // TODO Somehow an error here is silently ignored ?
        if (totalFail != result.getFailureCount()) {
            Assert.fail("Wrong fail count !");
        }

    }

}
