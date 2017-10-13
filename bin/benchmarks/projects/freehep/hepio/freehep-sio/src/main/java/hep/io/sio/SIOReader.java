package hep.io.sio;

import hep.io.xdr.XDRBufferedRandomAccessFile;
import hep.io.xdr.XDRDataInput;
import hep.io.xdr.XDRInputStream;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * A class for reading SIO records. Has limited support for random access
 * to records within a file.
 */
public class SIOReader implements Closeable {

    private XDRDataInput xdr;
    private SIORecordImpl currentRecord;
    private long nextRecordPosition;
    private static int bufferSize = Integer.getInteger("hep.io.sio.BufferSize", 32768).intValue();

    /**
     * Creates an SIOReader which reads from an input stream. Random access will not be
     * supported.
     * @param in The stream to read from.
     * @throws IOException If an errors occurs.
     */
    public SIOReader(InputStream in) throws IOException {
        XDRInputStream sio = new XDRInputStream(in);
        xdr = sio;
        currentRecord = new SIORecordImpl(sio);
    }

    /**
     * Creates an SIOReader which reads from a file. Random access will be supported.
     * @param file The file to read from.
     * @throws IOException If an error occurs.
     * @since 2.1
     */
    public SIOReader(File file) throws IOException {
        XDRBufferedRandomAccessFile raf = new XDRBufferedRandomAccessFile(file, true, bufferSize);
        xdr = raf;
        currentRecord = new SIORecordImpl(raf);
    }

    /**
     * Create an SIOReader which reads from a file. Random access will be supported.
     * @param file The file to read from.
     * @throws IOException If an error occurs.
     * @since 2.1
     */
    public SIOReader(String file) throws IOException {
        XDRBufferedRandomAccessFile raf = new XDRBufferedRandomAccessFile(file, true, bufferSize);
        xdr = raf;
        currentRecord = new SIORecordImpl(raf);
    }

    /**
     * Test if this reader supports random access.
     * @return <code>true</code> if random access is supported.
     * @since 2.1
     */
    public boolean isRandomAccess() {
        return xdr instanceof RandomAccessFile;
    }

    /**
     * Read the next record from this file
     * @return The record that was read.
     * @throws IOException If an IO exception occurs
     */
    public SIORecord readRecord() throws IOException {
        nextRecordPosition = currentRecord.nextRecord();
        return currentRecord;
    }

    /**
     * Read a record from a given position in a file.
     * @param position The position of the record to read
     * @return The record that was read
     * @throws IOException If an IO exception occurs, or if the file does not support random access
     * @since 2.1
     */
    public SIORecord readRecord(long position) throws IOException {
        seek(position);
        return readRecord();
    }

    /**
     * Position the file to read the next record from the given position
     * @param position The position of the next record to read. If negative reads from end-of-file+position
     * @throws IOException If an IO exception occurs, or if the file does not support random access
     * @since 2.1
     */
    public void seek(long position) throws IOException {
        RandomAccessFile raf = checkRandomAccess();
        if (position>=0) {
            raf.seek(position);
        } else {
            raf.seek(raf.length()+position);
        }
        currentRecord.clear();
        nextRecordPosition = position;
    }

    /**
     * Returns the position at which the next record will be read
     * @return The position at which the next record will be read
     * @throws IOException If an IO Exception occurs
     * @since 2.1
     */
    public long getNextRecordPosition() throws IOException {
        return nextRecordPosition;
    }

    public void close() throws IOException {
        xdr.close();
    }

    private RandomAccessFile checkRandomAccess() throws IOException {
        if (!isRandomAccess()) {
            throw new IOException("File does not support random access");
        }
        return (RandomAccessFile) xdr;
    }

    private static class SIORecordImpl implements SIORecord {

        private SIOBlockImpl currentBlock;
        private String name;
        private int headerLength;
        private int compressedLength;
        private int uncompressedLength;
        private boolean blocksRead = true;
        private boolean compressed;
        private long startPos;
        private XDRDataInput xdr;
        private Inflater inflater = new Inflater();

        SIORecordImpl(XDRDataInput xdr) throws IOException {
            this.xdr = xdr;
        }

        private void clear() {
            blocksRead = false;
            compressedLength = 0;
            currentBlock = null;
        }

        long nextRecord() throws IOException {
            skipRemainderOfRecord();
            readRecordHeader();
            startPos = getPosition();
            return startPos + pad(compressedLength);
        }
        private int pad(int size) {
            int r = size % 4;
            if (r == 0) {
                return size;
            }
            return size + 4 - r;
        }

        public String getRecordName() {
            return name;
        }

        public int getRecordLength() {
            return uncompressedLength;
        }

        /**
         * Get the next block
         * @return the next block, or null if there are no more blocks in the record
         */
        public SIOBlock getBlock() throws IOException {
            if (currentBlock == null) {
                if (!compressed) {
                    if (xdr instanceof XDRInputStream) {
                        ((XDRInputStream) xdr).clearReadLimit();
                    }
                    currentBlock = new SIOBlockImpl(new SIOInputStream(getInputStream(compressedLength)), compressedLength);
                } else {
                    inflater.reset();
                    currentBlock = new SIOBlockImpl(new SIOInputStream(new BufferedInputStream(new InflaterInputStream(getInputStream(compressedLength),inflater))), uncompressedLength);
                }
                blocksRead = true;
            }
            try {
                currentBlock.nextBlock();
                return currentBlock;
            } catch (EOFException x) {
                return null; // no more blocks
            }
        }

        private InputStream getInputStream(int readLimit) {
            if (xdr instanceof RandomAccessFile) {
                return new RandomAccessFileInputStream((RandomAccessFile) xdr, readLimit);
            } else {
                ((XDRInputStream) xdr).setReadLimit(readLimit);
                return (XDRInputStream) xdr;
            }
        }

        private long getPosition() throws IOException {
            if (xdr instanceof RandomAccessFile) {
                return ((RandomAccessFile) xdr).getFilePointer();
            } else {
                return ((XDRInputStream) xdr).getBytesRead();
            }
        }

        private void readRecordHeader() throws IOException {
            long headerStart = getPosition();
            headerLength = xdr.readInt();
            int frame = xdr.readInt();
            if (frame != 0xabadcafe) {
                throw new IOException("Framing error");
            }
            int control = xdr.readInt();
            if ((control & 0xfffe) != 0) {
                throw new IOException("Bad control word");
            }
            compressed = (control & 1) != 0;
            compressedLength = xdr.readInt();
            uncompressedLength = xdr.readInt();
            int l = xdr.readInt();
            if (l > headerLength - getPosition() + headerStart) {
                throw new IOException("Record name is insane");
            }
            name = xdr.readString(l);
            blocksRead = false;
            currentBlock = null;
        }

        private void skipRemainderOfRecord() throws IOException {
            // Start by skipping whatever is left of the previous record
            if (xdr instanceof XDRInputStream) {
                ((XDRInputStream) xdr).clearReadLimit();
            }
            if (!blocksRead) {
                xdr.skipBytes(compressedLength);
            } else {
                int left = compressedLength - (int) (getPosition() - startPos);
                if (left < 0) {
                    throw new IOException("Record overrun error");
                } else {
                    xdr.skipBytes(left);
                }
            }
            xdr.pad();
        }
    }

    private static class SIOBlockImpl implements SIOBlock {

        private String name;
        private int recordLength;
        private int length;
        private int version;
        private SIOInputStream xdr;
        private long startPos;
        private long recordStartPos;
        private static final EOFException eof = new EOFException();

        SIOBlockImpl(SIOInputStream xdr, int recordLength) throws IOException {
            this.xdr = xdr;
            this.recordLength = recordLength;

            length = 0;
            recordStartPos = startPos = xdr.getBytesRead();
        }

        void nextBlock() throws IOException {
            // skip any data remaining from the previous block
            xdr.clearReadLimit();
            int bytesLeft = length - (int) (xdr.getBytesRead() - startPos);
            if (bytesLeft < 0) {
                throw new IOException("Block overrun error (block " + name + ")");
            } else if (bytesLeft > 0) {
                xdr.skipBytes(bytesLeft);
            }
            // Check if there are more blocks
            xdr.pad();
            startPos = xdr.getBytesRead();

            if (startPos - recordStartPos >= recordLength) {
                throw eof;
            }
            length = xdr.readInt();
            xdr.setReadLimit(length - 4);
            int frame = xdr.readInt();
            if (frame != 0xdeadbeef) {
                throw new IOException("Block framing error");
            }
            version = xdr.readInt();
            int l = xdr.readInt();
            if (l > length - xdr.getBytesRead() + startPos) {
                throw new IOException("Block name is insane");
            }
            name = xdr.readString(l);
        }

        public String getBlockName() {
            return name;
        }

        public int getBlockLength() {
            return length;
        }

        public int getBytesLeft() {
            return length - (int) (xdr.getBytesRead() - startPos);
        }

        public int getVersion() {
            return version;
        }

        public int getMajorVersion() {
            return (version & 0xffff0000) >> 16;
        }

        public int getMinorVersion() {
            return version & 0xffff;
        }

        public SIOInputStream getData() {
            return xdr;
        }
    }

    private static class RandomAccessFileInputStream extends InputStream {

        private RandomAccessFile file;
        private int readLimit;

        public RandomAccessFileInputStream(RandomAccessFile file, int readLimit) {
            this.file = file;
            this.readLimit = readLimit;
        }

        public int read() throws IOException {
            if (available() == 0) return -1;
            int c = file.read();
            readLimit--;
            return c;
        }

        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if (available() == 0) throw new EOFException();
            int n = file.read(b, off, Math.min(len, available()));
            readLimit -= n;
            return n;
        }

        public long skip(long n) throws IOException {
            long pos = file.getFilePointer();
            long nActual = Math.min(n, available());
            file.seek(pos + nActual);
            readLimit -= nActual;
            return nActual;
        }

        public int available() throws IOException {
            return (int) Math.min(readLimit, file.length() - file.getFilePointer());
        }

        @Override
        public void close() throws IOException {
            file.close();
        }
    }
}
