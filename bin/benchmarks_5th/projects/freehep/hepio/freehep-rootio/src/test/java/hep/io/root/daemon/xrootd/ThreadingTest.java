package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.DaemonInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class ThreadingTest extends TestCase {

    public ThreadingTest(String testName) {
        super(testName);
    }
    // Disabled for now since the data files are gone.

    public void testThread1() throws InterruptedException, ExecutionException {
//        if (!XrootdConnectionTest.isAtSLAC()) return;
//        List<TestFile> files = new ArrayList<TestFile>();
//        Random r = new Random(12345);
//        for (int i=0; i<100; i++)
//        {
//            String f = String.format("root://glast-rdr.slac.stanford.edu//glast/mc/OpsSim/opssim2-GR-v13r9p3/merit/opssim2-GR-v13r9p3-%06d-merit.root",i);
//            files.add(new TestFile(f,r.nextInt(1000000), 100, 0));
//        }
//        ExecutorService pool = Executors.newFixedThreadPool(20);
//        List<Future> results = new ArrayList<Future>();
//        for (int i=0; i<10000; i++) 
//        {
//            int n = r.nextInt(files.size());
//            results.add(pool.submit(files.get(n)));
//        }
//        pool.shutdown();
//        pool.awaitTermination(100, TimeUnit.SECONDS);
//        
//        // Check for exceptions
//        for (Future result : results) {
//            result.get();
//        }
    }

    class TestFile implements Runnable {

        String file;
        long start;
        int length;
        long result;

        TestFile(String file, long start, int length, long result) {
            this.file = file;
            this.start = start;
            this.length = length;
            this.result = result;
        }

        public void run() {
            try {
                URL url = new URL(null, file, new XrootdStreamHandler());
                URLConnection conn = url.openConnection();
                conn.setRequestProperty(XrootdURLConnection.XROOT_AUTHORIZATION_SCHEME, "anonymous");
                DaemonInputStream in = (DaemonInputStream) conn.getInputStream();
                try {
                    in.setPosition(start);
                    byte[] buffer = new byte[length];
                    int l = in.read(buffer);
                    CRC32 cs = new CRC32();
                    cs.update(buffer);
                    long cksum = cs.getValue();
                    //System.out.println(file+" cksum="+cksum);
                    if (result == 0) result = cksum;
                    else assertEquals(result, cksum);
                } finally {
                    in.close();
                }
            } catch (Exception x) {
                throw new RuntimeException("Exception reading data", x);
            }

        }
    }
}
