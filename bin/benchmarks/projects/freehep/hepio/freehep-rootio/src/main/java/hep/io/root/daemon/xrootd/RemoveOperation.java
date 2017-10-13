package hep.io.root.daemon.xrootd;

import hep.io.root.daemon.xrootd.Callback.DefaultCallback;

/**
 * Remove (delete) a file.
 * @author tonyj
 */
class RemoveOperation extends Operation<Void> {
    RemoveOperation(String path)
    {
       super("remove", new Message(XrootdProtocol.kXR_rm, path),new DefaultCallback());
    }
}
