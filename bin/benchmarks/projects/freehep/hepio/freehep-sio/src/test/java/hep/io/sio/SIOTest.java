package hep.io.sio;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class SIOTest extends TestCase {

    public SIOTest(String testName) {
        super(testName);
    }

    public void testReadWriteStreamStreamCompressed() throws Exception {
        testReadWrite(false, false, true);
    }

    public void testReadWriteStreamFileCompressed() throws Exception {
        testReadWrite(false, true, true);
    }

    public void testReadWriteFileStreamCompressed() throws Exception {
        testReadWrite(true, false, true);
    }

    public void testReadWriteFileFileCompressed() throws Exception {
        testReadWrite(true, true, true);
    }

    public void testReadWriteStreamStreamUncompressed() throws Exception {
        testReadWrite(false, false, false);
    }

    public void testReadWriteStreamFileUncompressed() throws Exception {
        testReadWrite(false, true, false);
    }

    public void testReadWriteFileStreamUncompressed() throws Exception {
        testReadWrite(true, false, false);
    }

    public void testReadWriteFileFileUncompressed() throws Exception {
        testReadWrite(true, true, false);
    }

    private void testReadWrite(boolean readFile, boolean writeFile, boolean compress) throws Exception {
        File tempFile = File.createTempFile("test", "sio");
        tempFile.deleteOnExit();

        List<Double> values = new ArrayList<Double>();
        List<Long> recordPos = new ArrayList<Long>();
        writeFile(writeFile, tempFile, compress, recordPos, values);
        readFile(readFile, tempFile, values);
        tempFile.delete();
    }

    public void testRandomAccessReadFileCompressed() throws Exception {
        testRandomAccessRead(true, false);
    }

    public void testRandomAccessReadStreamCompressed() throws Exception {
        testRandomAccessRead(false, false);
    }

    public void testRandomAccessReadFileUnCompressed() throws Exception {
        testRandomAccessRead(true, true);
    }

    public void testRandomAccessReadStreamUnCompressed() throws Exception {
        testRandomAccessRead(false, true);
    }

    private void testRandomAccessRead(boolean writeFile, boolean compress) throws Exception {
        File tempFile = File.createTempFile("test", "sio");
        tempFile.deleteOnExit();

        List<Double> values = new ArrayList<Double>();
        List<Long> recordPos = new ArrayList<Long>();
        writeFile(writeFile, tempFile, compress, recordPos, values);

        Integer[] index = new Integer[10];
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }
        Collections.shuffle(Arrays.asList(index));

        SIOReader reader = new SIOReader(tempFile);
        for (int i : index) {
            SIORecord record = reader.readRecord(recordPos.get(i));
            assertEquals("record" + i, record.getRecordName());
            SIOBlock block = record.getBlock();
            assertEquals("blockA" + i, block.getBlockName());
            SIOInputStream in = block.getData();
            double newValue = in.readDouble();
            assertEquals(values.get(i), newValue, 1e-15);
            long position = reader.getNextRecordPosition();
            if (i<9) assertEquals(recordPos.get(i+1).longValue(),position);
        }

        tempFile.delete();
    }

    public void testRewrite() throws IOException {
        File tempFile = File.createTempFile("test", "sio");
        tempFile.deleteOnExit();

        List<Double> values = new ArrayList<Double>();
        List<Long> recordPos = new ArrayList<Long>();
        SIOWriter writer = new SIOWriter(tempFile);
        for (int i = 0; i < 10; i++) {
            recordPos.add(writer.createRecord("record" + i, false));
            SIOOutputStream sio = writer.createBlock("blockA" + i, 1, 0);
            double value = Math.random();
            sio.writeDouble(value);
            values.add(value);
            sio.close();
            sio = writer.createBlock("blockB" + i, 1, 0);
            value = Math.random();
            sio.writeDouble(value);
            sio.close();
        }

        writer.rewriteRecord(recordPos.get(5), false);
        SIOOutputStream sio = writer.createBlock("blockA" + 5, 1, 0);
        double value = Math.random();
        sio.writeDouble(value);
        values.set(5, value);
        sio.close();
        sio = writer.createBlock("blockB" + 5, 1, 0);
        value = Math.random();
        sio.writeDouble(value);
        sio.close();

        writer.close();
        readFile(false, tempFile, values);
        tempFile.delete();
    }

    public void testReserveSpace() throws IOException {
        File tempFile = File.createTempFile("test", "sio");
        tempFile.deleteOnExit();

        List<Double> values = new ArrayList<Double>();
        List<Long> recordPos = new ArrayList<Long>();
        SIOWriter writer = new SIOWriter(tempFile);
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                recordPos.add(writer.reserveSpaceForRecord("record"+i, 100));
                values.add(0.0);
            } else {
                recordPos.add(writer.createRecord("record" + i, false));
                SIOOutputStream sio = writer.createBlock("blockA" + i, 1, 0);
                double value = Math.random();
                sio.writeDouble(value);
                values.add(value);
                sio.close();
                sio = writer.createBlock("blockB" + i, 1, 0);
                value = Math.random();
                sio.writeDouble(value);
                sio.close();
            }
        }

        writer.rewriteRecord(recordPos.get(5), false);
        SIOOutputStream sio = writer.createBlock("blockA" + 5, 1, 0);
        double value = Math.random();
        sio.writeDouble(value);
        values.set(5, value);
        sio.close();
        sio = writer.createBlock("blockB" + 5, 1, 0);
        value = Math.random();
        sio.writeDouble(value);
        sio.close();

        writer.close();
        readFile(false, tempFile, values);
        tempFile.delete();
    }

    private void readFile(boolean readFile, File tempFile, List<Double> values) throws IOException {
        SIOReader reader = readFile ? new SIOReader(tempFile) : new SIOReader(new FileInputStream(tempFile));
        for (int i = 0;; i++) {
            try {
                SIORecord record = reader.readRecord();
                assertEquals("record" + i, record.getRecordName());
                SIOBlock block = record.getBlock();
                assertEquals("blockA" + i, block.getBlockName());
                SIOInputStream in = block.getData();
                double newValue = in.readDouble();
                assertEquals(values.get(i), newValue, 1e-15);
            } catch (EOFException x) {
                assertEquals(10, i);
                break;
            }
        }
        reader.close();
    }

    private void writeFile(boolean writeFile, File tempFile, boolean compress, List<Long> recordPos, List<Double> values) throws IOException {
        SIOWriter writer = writeFile ? new SIOWriter(tempFile) : new SIOWriter(new FileOutputStream(tempFile));
        for (int i = 0; i < 10; i++) {
            recordPos.add(writer.createRecord("record" + i, compress));
            SIOOutputStream sio = writer.createBlock("blockA" + i, 1, 0);
            double value = Math.random();
            sio.writeDouble(value);
            values.add(value);
            sio.close();
            sio = writer.createBlock("blockB" + i, 1, 0);
            value = Math.random();
            sio.writeDouble(value);
            sio.close();
        }
        writer.close();
    }
}
