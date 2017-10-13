package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.xrootd.Callback.DefaultCallback;

/**
 * Write data to an open file.
 * @author tonyj
 */
class WriteOperation extends Operation<Void> {

    private OpenFile file;

    WriteOperation(OpenFile file, long fileOffset, byte[] buffer, int offset, int length) {
        super("write", new WriteMessage(file, buffer, offset, length, fileOffset), new DefaultCallback());
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

    private static class WriteMessage extends Message {

        WriteMessage(OpenFile file, byte[] buffer, int offset, int length, long fileOffset) {
            super(XrootdProtocol.kXR_write);
            writeInt(file.getHandle());
            writeLong(fileOffset);
            setData(buffer,offset,length);
        }
    }
}