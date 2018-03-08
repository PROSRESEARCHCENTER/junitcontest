/*
 * RmiTestClient.java
 *
 * Created on December 7, 2005, 3:20 PM
 */

package hep.aida.ref.remote.test.rmiConnection;

import java.rmi.Naming;

public class RmiTestClient {
    private String bindName;
    private RmiTestServer server;

    public RmiTestClient(String bindName) throws Exception {
        this.bindName = bindName;
        server = (RmiTestServer) Naming.lookup(bindName);
    }
    
    public void test1() throws Exception {
        RmiSerializableObject ro = null;
        
        for (int i=1; i<=201; i=i+4) {
            int length = i*1024;
            ro = new RmiSerializableObject(length);
            
            long t0 = System.currentTimeMillis();
            server.setObject(ro);
            
            long t1 = System.currentTimeMillis();
            ro = server.getObject(length);
            
            long t2 = System.currentTimeMillis();
            
            double setTime = (t1 - t0)/1000.;
            double getTime = (t2 - t1)/1000.;
            
            System.out.println("L: "+length+",\tsize: "+ro.getKByteSize()+" (KB)  \tset: "+setTime+"\tget: "+getTime);
        }
    }
    
    public void test2() throws Exception {
            long t0 = System.currentTimeMillis();
            
            Object data = server.getRMIObject(0);
            
            long t1 = System.currentTimeMillis();
             
            double getTime0 = (t1 - t0)/1000.;
            System.out.println("\n Getting full data for Histogram 2D: \tget: "+getTime0+" (sec)\t data="+data);

            long t2 = System.currentTimeMillis();
            
            data = server.getRMIObject(1);
            
            long t3 = System.currentTimeMillis();
             
            double getTime1 = (t3 - t2)/1000.;
            
            System.out.println("\n Getting part data for Histogram 2D: \tget: "+getTime1+" (sec)\t data="+data);
    }    
    
    public static void main(String[] args) throws Exception {
        String bindName = null;
        //if (args == null || args.length == 0) bindName = "//noric01.slac.stanford.edu:1099/RmiTestServer";
        //else bindName = args[0];
        
        //bindName = "//noric01.slac.stanford.edu:1099/RmiTestServer";
        bindName = "//localhost:1099/RmiTestServer";
        //bindName = "//bbr-remjas.slac.stanford.edu:1099/RmiTestServer";
        
        RmiTestClient client = new RmiTestClient(bindName);
        
        //client.test1();
        client.test2();
    }
    
}
