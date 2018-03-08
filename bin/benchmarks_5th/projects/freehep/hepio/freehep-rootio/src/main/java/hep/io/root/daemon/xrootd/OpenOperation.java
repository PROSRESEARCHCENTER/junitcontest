package hep.io.root.daemon.xrootd;

import java.io.IOException;

/**
 * Open a file.
 * @author tonyj
 */
class OpenOperation extends Operation<OpenFile> {

    OpenOperation(String path, int mode, int options) {
        this(new OpenFile(path, mode, options));
    }

    OpenOperation(OpenFile file) {
        super("open", new OpenMessage(file), new OpenCallback(file));
    }

    private static class OpenMessage extends Message {

        private OpenFile file;

        OpenMessage(OpenFile file) {
            super(XrootdProtocol.kXR_open, file.getPath());
            writeShort(file.getMode());
            writeShort(file.getOptions());
            this.file = file;
        }
    }

    private static class OpenCallback extends Callback<OpenFile> {

        private OpenFile file;

        OpenCallback(OpenFile file) {
            this.file = file;
        }

        public OpenFile responseReady(Response response) throws IOException {
            int handle = response.readInt();
            file.setHandleAndDestination(handle, response.getDestination(), response.getMultiplexor());

            if (response.getLength()>4) file.setCompressionSize(response.readInt());
            if (response.getLength()>8) file.setCompressionType(response.readInt());
            
            if (response.getLength()>12) {
               String info = response.getDataAsString();
               file.setStatus(new FileStatus(info,response.getDestination()));
            }
            return file;
        }
    }  
}
