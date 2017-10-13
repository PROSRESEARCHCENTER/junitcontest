package hep.io.root.daemon.xrootd;

import java.io.IOException;

/**
 * Obtain status information for a path.
 * @author tonyj
 */
class StatOperation extends Operation<FileStatus> {

    /**
     * Create the StatOperation.
     * @param path Is the path whose status information is to be returned.
     */
    StatOperation(String path) {
        super("stat", new Message(XrootdProtocol.kXR_stat, path),new StatCallback());
    }

    private static class StatCallback extends Callback<FileStatus> {

        FileStatus responseReady(Response response) throws IOException {
            return new FileStatus(response.getDataAsString(),response.getDestination());
        }
    }
}
