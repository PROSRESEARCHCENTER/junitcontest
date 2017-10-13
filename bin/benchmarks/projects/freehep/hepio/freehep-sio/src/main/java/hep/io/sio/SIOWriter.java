package hep.io.sio;

import hep.io.xdr.XDRBufferedRandomAccessFile;
import hep.io.xdr.XDRDataOutput;
import hep.io.xdr.XDROutputStream;
import hep.io.xdr.XDRRandomAccessFile;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Class for writing out SIO files. Includes limited support for rewriting
 * existing records, which is useful for supporting record based random access.
 * @author tonyj
 */
public class SIOWriter implements Closeable, Flushable {

    private XDRDataOutput xdr;
    //FIXME: This is inherantly single threaded, not ideal for random access
    private SIOByteArrayOutputStream blockBytes = new SIOByteArrayOutputStream();
    private SIOByteArrayOutputStream recordBytes = new SIOByteArrayOutputStream();
    private SIOOutputStream block = new SIOOutputStream(blockBytes);
    private XDROutputStream record = new XDROutputStream(recordBytes);
    // Reused to avoid memory leak, http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4797189 
    private Deflater deflater = new Deflater(); 
    private DeflaterOutputStream compressor;
    private final static int recordFrame = 0xabadcafe;
    private final static int blockFrame = 0xdeadbeef;
    private String blockName;
    private int blockVersion;
    private String recordName;
    private boolean recordCompress = false;
    private static int bufferSize = Integer.getInteger("hep.io.sio.BufferSize", 32768).intValue();
    private int originalLength; // Gives length of original record when a rewrite is in progress
    private long originalPosition; // Gives position of next record when a rewrite is in progress

    /** Creates an SIOWriter which writes to an OutputStream. The resulting SIOWriter
     will not support random access.
     * @param out The output stream to write to
     * @throws IOException If an error occurs
     */
    public SIOWriter(OutputStream out) throws IOException {
        this.xdr = new XDROutputStream(out);
    }

    /** Creates an SIOWriter which writes to a file. The resulting SIOWriter will support
     * random access.
     * @param file The file to write to
     * @throws IOException If an errors occurs.
     * @since 2.1
     */
    public SIOWriter(String file) throws IOException {
        this.xdr = new XDRBufferedRandomAccessFile(file, false, bufferSize);
    }

    /** Creates an SIOWriter which writes to a file. The resulting SIOWriter will support
     * random access.
     * @param file The file to write to
     * @throws IOException If an error occurs
     * @since 2.1
     */
    public SIOWriter(File file) throws IOException {
        this.xdr = new XDRBufferedRandomAccessFile(file, false, bufferSize);
    }
    /**
     * Create a new record.
     * @param name The name if the newly created record.
     * @param compress <code>true</code> if the record should be compressed.
     * @return The position of the record in the file. This position can be used to
     * rewrite the record later if the writer supports random access.
     * @throws IOException If an error occurs
     * @see #rewriteRecord(long, boolean)
     * @since 2.1
     */

    public long createRecord(String name, boolean compress) throws IOException {
        flushRecord();
        prepareRecord(name, compress);
        return getPosition();
    }

    /**
     * Rewrite a record previously created with createRecord or reserveSpaceForRecord. The
     * new record must be no bigger than the existing record or reserved space at the specified
     * position.
     * @param position The position in the file to rewrite the record. The position must
     * correspond to an existing record or reserved space for a record.
     * @param compress If the record should be compressed. Since predicting the size
     * of compressed records is difficult it is not recommended to rewrite compressed
     * records.
     * @throws IOException If an error occurs, or if the specified position does not
     * correspond to a record, or if the new record will not fit in the reserved space.
     * @see #reserveSpaceForRecord(java.lang.String, int)
     * @since 2.1
     */
    public void rewriteRecord(long position, boolean compress) throws IOException {
        flushRecord();

        XDRRandomAccessFile raf = checkRandomAccess();
        originalPosition = raf.getFilePointer();
        raf.seek(position);

        int headerLength = raf.readInt();
        // Now read the record header for the next record
        int frame = raf.readInt();
        if (frame != recordFrame) {
            throw new IOException("Framing error");
        }
        int control = raf.readInt();
        if ((control & 0xfffc) != 0) {
            throw new IOException("Bad control word");
        }
        int compressedLength = raf.readInt();
        int uncompressedLength = raf.readInt();

        int l = raf.readInt();
        if (l > headerLength - raf.getFilePointer() + originalPosition) {
            throw new IOException("Record name is insane");
        }
        String originalName = raf.readString(l);
        originalLength = compressedLength;

        prepareRecord(originalName,compress);
        raf.seek(position);
    }

    /**
     * Reserve space for writing a record in the future.
     * @param name The name of the record for which the space is reserved
     * @param size The size in bytes to reserve for the record
     * @return The position in the file of the reserved space
     * @throws IOException If an errors occurs or if the writer does not support random access.
     * @see #rewriteRecord(long, boolean)
     * @since 2.1
     */
    public long reserveSpaceForRecord(String name, int size) throws IOException {
        flushRecord();

        XDRRandomAccessFile raf = checkRandomAccess();
        long recordPosition = raf.getFilePointer();
        writeRecordHeader(name, 2, size, size);
        raf.seek(raf.getFilePointer() + size);
        xdr.pad();

        return recordPosition;
    }
    /**
     * Add a block within the current record
     * @param name The name of the block to be created
     * @param major The major version number of the created block
     * @param minor The minor version number of the created block
     * @return The stream to be used to write the content of the block
     * @throws IOException If an errors occurs, or it no current record exists.
     */

    public SIOOutputStream createBlock(String name, int major, int minor) throws IOException {
        if (recordName == null) {
            throw new IOException("No record currently exists");
        }
        flushBlock();
        blockName = name;
        blockVersion = (major << 16) + minor;
        return block;
    }

    private void flushBlock() throws IOException {
        if (blockName == null) {
            return;
        }
        block.flush();
        int blockLength = pad(blockBytes.size()) + 16 + pad(blockName.length());
        record.writeInt(blockLength);
        record.writeInt(blockFrame);
        record.writeInt(blockVersion);
        record.writeString(blockName);
        blockBytes.writeTo((DataOutput) record);
        blockBytes.reset();
        blockName = null;
    }

    private void flushRecord() throws IOException {
        if (recordName == null) {
            return;
        }
        try {
            flushBlock();
            block.clear();
            record.flush();
            if (recordCompress) {
                compressor.finish();
            }
            if (originalPosition != 0) {
                // We are rewriting a record, we need to check if the new record fits
                if (recordBytes.size() > originalLength) {
                    throw new IOException("Rewritten record does not fit");
                }

            }
            writeRecordHeader(recordName,recordCompress ? 1 : 0,
                    originalPosition != 0 ? originalLength : recordBytes.size(),
                    (int) record.getBytesWritten());
            recordBytes.writeTo(xdr);
            recordBytes.reset();
            xdr.pad();
        } finally {
            recordName = null;
            if (originalPosition != 0) {
                checkRandomAccess().seek(originalPosition);
                originalPosition = 0;
                originalLength = 0;
            }
        }
    }

    @Override
    public void close() throws IOException {
        flushRecord();
        if (xdr instanceof XDRRandomAccessFile)
        {
            XDRRandomAccessFile raf = (XDRRandomAccessFile) xdr;
            raf.setLength(raf.getFilePointer());
        }
        xdr.close();
    }

    @Override
    public void flush() throws IOException {
        flushRecord();
        xdr.flush();
    }

    /**
     * Test of the writer supports random access
     * @return <code>true</code> if random access is supported
     * @since 2.1
     */
    public boolean isRandomAccess() {
        return xdr instanceof XDRRandomAccessFile;
    }

    private XDRRandomAccessFile checkRandomAccess() throws IOException {
        if (!isRandomAccess()) {
            throw new IOException("File does not support random access");
        }
        return (XDRRandomAccessFile) xdr;
    }

    private long getPosition() throws IOException {
        if (xdr instanceof XDRRandomAccessFile) {
            return ((XDRRandomAccessFile) xdr).getFilePointer();
        } else {
            return ((XDROutputStream) xdr).getBytesWritten();
        }
    }

    private int pad(int size) {
        int r = size % 4;
        if (r == 0) {
            return size;
        }
        return size + 4 - r;
    }

    private void prepareRecord(String name, boolean compress) {
        recordName = name;
        if (compress) {
             deflater.reset();
             compressor = new DeflaterOutputStream(recordBytes,deflater);
             record = new XDROutputStream(compressor);
        } else {
            compressor = null;
            record = new XDROutputStream(recordBytes);
        }
        recordCompress = compress;
    }

    private static class SIOByteArrayOutputStream extends ByteArrayOutputStream {

        void writeTo(DataOutput out) throws IOException {
            out.write(buf, 0, count);
        }
    }

    private void writeRecordHeader(String name, int mode, int compressedSize, int uncompressedSize) throws IOException {
        int headerLength = 24 + pad(name.length());
        xdr.writeInt(headerLength);
        xdr.writeInt(recordFrame);
        xdr.writeInt(mode);
        xdr.writeInt(compressedSize);
        xdr.writeInt(uncompressedSize);
        xdr.writeString(name);
        xdr.pad();
    }
}
