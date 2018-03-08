package hep.io.root.daemon.xrootd;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Read from an open file.
 * @author tonyj
 */
class ReadOperation extends Operation<Integer> {

    private OpenFile file;

    ReadOperation(OpenFile file, long fileOffset, byte[] buffer, int bufOffset, int size) {
        super("read", new ReadMessage(file, fileOffset, size), new ReadCallback(ByteBuffer.wrap(buffer, bufOffset, size)));
        this.file = file;
    }
    ReadOperation(OpenFile file, long fileOffset, ByteBuffer buffer) {
        super("read", new ReadMessage(file, fileOffset, buffer.remaining()), new ReadCallback(buffer));
        this.file = file;
    }
    ReadOperation(OpenFile file, FileChannel fileChannel, long fileOffset, int size) {
        super("read", new ReadMessage(file, fileOffset, size), new FileReadCallback(fileChannel, fileOffset, size));
        this.file = file;
    }

    @Override
    Operation getPrerequisite() {
        return new OpenOperation(file);
    }

    @Override
    Destination getDestination() {
        return file.getDestination();
    }

    @Override
    Multiplexor getMultiplexor() {
        return file.getMultiplexor();
    } 

    private static class ReadMessage extends Message {

        private OpenFile file;
        private long fileOffset;
        private int size;

        ReadMessage(OpenFile file, long fileOffset, int size) {
            super(XrootdProtocol.kXR_read);
            this.file = file;
            this.fileOffset = fileOffset;
            this.size = size;
        }

        @Override
        void writeExtra(ByteBuffer out) throws IOException {
            // Note, we do things this way because the file handle may have changed
            // since we were created, as a result of a redirect.
            out.putInt(file.getHandle());
            out.putLong(fileOffset);
            out.putInt(size);
        }
    }

    private static class ReadCallback extends Callback<Integer> {

        private ByteBuffer buffer;
        private int initialPosition;

        ReadCallback(ByteBuffer buffer) {
            this.initialPosition = buffer.position();
            this.buffer = buffer;
        }

        public Integer responseReady(Response response) throws IOException {
            response.readData(buffer);
            int result = buffer.position()-initialPosition;
            return result == 0 ? -1 : result;
        }

        @Override
        public void clear() {
            buffer.position(initialPosition);
        }
    }

    private static class FileReadCallback extends Callback<Integer> {

        private FileChannel fileChannel;
        private long bufOffset;
        private long readLength;
        private int bufLength;

        FileReadCallback(FileChannel fileChannel, long bufOffset, int bufLength) {
            this.bufOffset = bufOffset;
            this.readLength = 0;
            this.bufLength = bufLength;
            this.fileChannel = fileChannel;
        }

        public Integer responseReady(Response response) throws IOException {
            // FIXME: If this fails how do we know if it is a socket problem or a file problem?
            int ll = 0;
            while (ll<response.getLength()) {
                long l = fileChannel.transferFrom(response.getSocketChannel(), bufOffset+readLength+ll, response.getLength()-ll);
                if (l<=0) throw new EOFException();
                ll += l;
            }

            readLength += ll;
            return readLength == 0 ? -1 : (int) readLength;
        }

        @Override
        public void clear() {
            readLength = 0;
        }
    }
}