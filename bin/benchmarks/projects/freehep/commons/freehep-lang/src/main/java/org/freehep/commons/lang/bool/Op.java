
package org.freehep.commons.lang.bool;

/**
 *
 * @author Brian Van Klaveren<bvan@slac.stanford.edu>
 */
public class Op {
    private final int type;
   
    public Op(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
    
    public String getStringType(){
        return SymUtils.getSymName(type);
    }

    @Override
    public String toString(){
        return String.format("%s", SymUtils.getSymName(type));
    }

}