/*
 * RmiSerializableObject.java
 *
 * Created on December 7, 2005, 2:32 PM
 */

package hep.aida.ref.remote.test.rmiConnection;

import java.io.Serializable;

public class RmiSerializableObject implements Serializable {
    
    static final long serialVersionUID = 7189275018229714343L;
    private double[] doubleArray;
    
    public RmiSerializableObject(int size) {
        doubleArray = new double[size];
        
        java.util.Random r = new java.util.Random();
        for (int i=0; i<size; i++) {
            doubleArray[i] = r.nextDouble();
        }
    }
 
    // Return size of array in Bytes
    public long getByteSize() {
        long size = doubleArray.length * 8;
        return size;
    }
    
    // Return size of array in KBytes
    public double getKByteSize() {
        long size = doubleArray.length * 8;
        return size/1024;
    }
    
    // Return length of array
    public int getArrayLength() {
        return doubleArray.length;
    }
    
    public static void main(String[] args) {
        
        RmiSerializableObject so = null;
        for (int i=1; i<=101; i=i+4) {
            so = new RmiSerializableObject(i * 1000);
            System.out.println(i*1000 +"\t size: "+so.getByteSize());
        }
    }
}
