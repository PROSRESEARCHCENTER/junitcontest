package hep.aida.ref.pdf;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A List of Variables. The list can contain multiple instances of the same Variable
 * but different Variable objects cannot have the same name.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class VariableList {
    
    public static int FUNCTION  = 0;
    public static int DEPENDENT = 1;
    public static int PARAMETER = 2;
    public static int ANY = 3;
    
    private ArrayList list;
    private Hashtable hash = new Hashtable();

    private int type;
    
    public VariableList() {
        this(ANY);
    }
    
    public VariableList(int type) {
        list = new ArrayList();
        this.type = type;
    }
    
    public VariableList(int type, int dimension) {
        list = new ArrayList(dimension);
        this.type = type;
    }
    
    public void set(int index, Variable var) {
        if ( index < 0 || index > list.size() )
            throw new IllegalArgumentException("Index out of bound "+index);
        checkVariable(var);
        
        Variable v = get(index);
        if ( v != null )
            hash.remove(v.name());        
        list.set(index,var);
        if ( ! hash.containsKey(var.name()) )
            hash.put(var.name(),var);
    }
    
    public Variable get(int index) {
        if ( index < 0 || index >= list.size() )
            throw new IllegalArgumentException("Index out of bound "+index);
        return (Variable) list.get(index);
    }
    
    public Variable get(String varName) {
        Variable var = (Variable) hash.get(varName);
        if ( var == null)
            throw new IllegalArgumentException("Variable with name "+varName+" is not in this list");
        return var;
    }
    
    public void add(Variable var) {
        add(size(),var);
    }
    
    public void add(int index, Variable var) {
        if ( index < 0 || index > list.size() )
            throw new IllegalArgumentException("Index out of bound "+index);
        checkVariable(var);
        list.add(index,var);
        if ( ! hash.containsKey(var.name()) )
            hash.put(var.name(),var);
    }
    
    public void remove(Variable var) {
        list.remove(var);
        if ( ! list.contains(var) )
            hash.remove(var.name());
    }
    
    public void remove(int index) {
        Variable var = get(index);
        remove(var);
    }
    
    public void clear() {
        list.clear();
        hash.clear();
    }
    
    public int size() {
        return list.size();
    }
    
    public int indexOf(Variable var) {
        return list.indexOf(var);
    }
    
    public int indexOf(String varName) {
        return indexOf(get(varName));
    }
    
    public boolean contains(Variable var) {
        return list.contains(var);
    }
    
    public boolean contains(String varName) {
        return hash.containsKey(varName);
    }
    
    public int type() {
        return type;
    }
    
    private void checkVariable(Variable var) {
        boolean wrongType = false;
        if ( type != ANY ) {
            if ( type == FUNCTION && ! (var instanceof Function) )
                wrongType = true;
            else if ( type == DEPENDENT && ! (var instanceof Dependent) )
                wrongType = true;
            else if ( type == PARAMETER && ! (var instanceof Parameter) )
                wrongType = true;
        }
        if ( wrongType )
            throw new IllegalArgumentException("Wrong type "+var.getClass()+". Cannot add to list of type "+type);
        if ( hash.containsKey(var.name()) && hash.get(var.name()) != var )
            throw new IllegalArgumentException("A Variable with name "+var.name()+" is already in the list. It cannot be added.");
    }
    
    
}
