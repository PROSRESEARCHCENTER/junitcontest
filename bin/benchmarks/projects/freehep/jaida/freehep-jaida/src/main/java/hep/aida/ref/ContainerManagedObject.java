package hep.aida.ref;

public class ContainerManagedObject extends ManagedObject {  
    private Object object;
    
    public ContainerManagedObject(String name) {
        super(name);
    }
    
    public void setObject(Object o) { this.object = o; }
    
    public Object getObject() { return object; }
    
}
