package hep.io.root.daemon.xrootd;

import java.util.Date;

public class FileStatus {

    private String id;
    private long size;
    private int flags;
    private Date modTime;
    private Destination destination;

    FileStatus(String response, Destination destination) {
        super();
        String[] tokens = response.replace("\000", "").split(" +");
        id = tokens[0];
        size = Long.parseLong(tokens[1]);
        flags = Integer.parseInt(tokens[2]);
        modTime = new Date(Long.parseLong(tokens[3]) * 1000);
        this.destination = destination;
    }

    public int getFlags() {
        return flags;
    }

    public String getId() {
        return id;
    }

    public Date getModTime() {
        return modTime;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("location=%s id=%s\nsize=%,d lastModified=%s flags=%d", destination, id, size, modTime, flags);
    }

    public Destination getFileLocation() {
        return destination;
    }
}
