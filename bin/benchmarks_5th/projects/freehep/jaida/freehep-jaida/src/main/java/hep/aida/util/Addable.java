package hep.aida.util;

import hep.aida.IManagedObject;
import hep.aida.dev.IAddable;

public class Addable implements IAddable {
    private IManagedObject mo;
    private String path;
    
    public Addable() {
    }
    
    public void add(String path, IManagedObject mo) throws IllegalArgumentException {
        this.path = path;
        this.mo = mo;
    }
    
    public void hasBeenFilled(String path) throws IllegalArgumentException {
    }
    
    public void mkdirs(String path) throws IllegalArgumentException {
    }
    
    public String path() { return path; }
    
    public IManagedObject object() { return mo; }
}

