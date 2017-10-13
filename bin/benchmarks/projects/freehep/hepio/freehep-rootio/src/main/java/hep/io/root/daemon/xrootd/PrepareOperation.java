package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.xrootd.Callback.StringCallback;

/**
 * Stages a file or files from tape to disk
 * @author tonyj
 */
class PrepareOperation extends Operation<String> {

    PrepareOperation(String[] paths, int options, int priority) {
        super("prepare", new PrepareMessage(paths, options, priority), new StringCallback());
    }

    private static class PrepareMessage extends Message {

        PrepareMessage(String[] paths, int options, int priority) {
            super(XrootdProtocol.kXR_prepare, createPathlist(paths));
            writeByte(options);
            writeByte(priority);
        }

        private static String createPathlist(String[] paths) {
            final StringBuilder plist = new StringBuilder();
            for (String path : paths) {
                plist.append(path).append('\n');
            }
            return plist.toString();
        }
    }
}
