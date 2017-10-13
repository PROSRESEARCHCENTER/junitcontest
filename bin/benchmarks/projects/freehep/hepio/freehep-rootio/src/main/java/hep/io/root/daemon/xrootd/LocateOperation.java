package hep.io.root.daemon.xrootd;

import java.io.IOException;

/**
 * Locate a file.
 * @author tonyj
 */
class LocateOperation extends Operation<String[]> {

    /**
     * Create the LocateOperation.
     * @param path Is the path of the file to be located.
     * @param noWait Provide information as soon as possible 
     * @param refresh Update cached information on the file's location 
     */
    LocateOperation(String path, boolean noWait, boolean refresh) {
        super("locate", new LocateMessage(path, noWait, refresh), new LocateCallback());
    }

    private static class LocateMessage extends Message {

        LocateMessage(String path, boolean noWait, boolean refresh) {
            super(XrootdProtocol.kXR_locate, path);
            int options = 0;
            if (noWait) {
                options |= XrootdProtocol.kXR_nowait;
            }
            if (refresh) {
                options |= XrootdProtocol.kXR_refresh;
            }
            writeShort(options);
        }
    }

    private static class LocateCallback extends Callback<String[]> {

        String[] responseReady(Response response) throws IOException {
            String result = response.getDataAsString();
            return result.split("\\s+");
        }
    }
}
