/*
 * RmiTestServerImpl.java
 *
 * Created on December 7, 2005, 2:55 PM
 */

package hep.aida.ref.remote.test.rmiConnection;

import hep.aida.IAxis;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.remote.rmi.converters.RmiHist2DConverter;
import hep.aida.ref.remote.rmi.data.RmiHist2DData;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RmiTestServerImpl  extends UnicastRemoteObject implements RmiTestServer {
    private String bindName;
    private RmiSerializableObject ro;
    private boolean batch = false;
    private RmiHist2DData data1;
    private RmiHist2DData data2;
    
    public RmiTestServerImpl(String bindName) throws Exception {
        super();
        this.bindName = bindName;
        ro = new RmiSerializableObject(1000);
        
        IAxis xAxis = new FixedAxis(300, -3, 3);
        IAxis yAxis = new FixedAxis(300, -3, 3);
        Histogram2D h21 = new Histogram2D("Historgam 2D", "Histogram 2D - title", xAxis, yAxis);
        
        java.util.Random r = new java.util.Random();

        for (int i = 0; i < 100000; i++) {
            double xVal = r.nextGaussian();
            double yVal = r.nextGaussian();
            double w = r.nextDouble();
            h21.fill(xVal, yVal, w);
        }
        
        RmiHist2DConverter converter = RmiHist2DConverter.getInstance();
        
        Runtime rt = Runtime.getRuntime();
        long m1 = rt.totalMemory() - rt.freeMemory();
        rt.gc();
        m1 = rt.totalMemory() - rt.freeMemory();
        data1 = (RmiHist2DData) converter.extractData(h21);
        
        data2 = (RmiHist2DData) converter.extractData(h21);
        data2.setBinEntries(null);
        data2.setBinErrors(null);
        data2.setBinMeans(null);
        data2.setBinMeansX(null);
        data2.setBinMeansY(null);
        data2.setBinRmssX(null);
        data2.setBinRmssY(null);
        rt.gc();
        long m2 = rt.totalMemory() - rt.freeMemory();
        
        double memory = (m2 - m1)/1024;
        System.out.println("RmiHist2DData size: "+memory+" (kb)");
    }
    
    private void connect() throws Exception {
        int port = 1099;
        
        int index = bindName.indexOf(":");
        if (index >0) {
            String portString = bindName.substring(index+1);
            int index2 = portString.indexOf("/");
            if (index2 > 0) { portString = portString.substring(0, index2); }
            try {
                int tmpPort = Integer.parseInt(portString);
                port = tmpPort;
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                System.exit(1);
            }
            
        }
        
        try {
            Naming.rebind(bindName, this);
        } catch (ConnectException co) {
            System.out.println("RmiServer: No RMI Registry is currently available for port="+port+". Starting new RMI Registry.");
            LocateRegistry.createRegistry(port);
            Naming.rebind(bindName, this);
        }
        
        System.out.println("RmiTestServer is ready on: "+bindName);
    }
    
    public void setBatch(boolean b) { batch = b; }
    public boolean isBatch() { return batch; }
    
    // RmiTestServer methods
    
    public RmiSerializableObject getObject(int size) throws RemoteException {
        if (size != ro.getArrayLength()) ro = new RmiSerializableObject(size);
        return ro;
    }
    
    public void setObject(RmiSerializableObject obj) throws RemoteException {
        ro = obj;
    }
    
    public void close() throws RemoteException {
        try {
            Naming.unbind(bindName);
            unexportObject(this, true);
        } catch (Exception e2) { e2.printStackTrace(); }
    }
    
    
    public Object getRMIObject(int i) throws RemoteException {
        if (i == 0) return data1;
        else if (i == 1) return data2;
        else return null;
    }
    
    
    
    public static void main(String[] args) throws Exception {
        String bindName = null;
        if (args == null || args.length == 0) bindName = "//localhost:1099/RmiTestServer";
        else bindName = args[0];
        
        RmiTestServerImpl server = new RmiTestServerImpl(bindName);
        server.connect();
        
        // To cleanly exit program, hit "Return"
        if(!server.isBatch()) {
            System.out.println("\nPress ENTER to exit");
            System.in.read();
            server.close();
        }
    }    
}
