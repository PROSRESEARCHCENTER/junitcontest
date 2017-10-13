package hep.aida.ref.tuple;

import hep.aida.IEvaluator;
import hep.aida.IFilter;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import hep.aida.ref.tree.Tree;

/**
 *
 * @author The FreeHEP Team @ SLAC.
 *
 */
public class TupleFactory implements ITupleFactory {
    
    private Tree tree;
    
    private final static char separatorChar = '/';
    
    private String nameInPath( String path ) {
        int index = path.lastIndexOf( separatorChar );
        if ( index == -1 )
            return path;
        return path.substring( index+1 );
    }
    
    private String parentPath( String path ) {
        int index = path.lastIndexOf( separatorChar );
        if ( index == -1 )
            return null;
        return path.substring(0,index);
    }
    
    public TupleFactory(ITree tree) {
        this.tree = tree instanceof Tree ? (Tree) tree : null;
    }
    
    /**
     * Create an NTuple
     * @param path The persistency path of the n-tuple
     * @param title The title of the n-tuple
     * @param columnName The names of the columns
     * @param columnType The types of  the columns
     * @param options NTuple options (currently undefined)
     */
    public ITuple create(String path, String title, String[] columnName, Class[] columnType, String options) {
        Tuple tuple = new Tuple(nameInPath(path), title, columnName, columnType, options);
        if ( tree != null ) tree.addFromFactory(parentPath(path),tuple);
        return tuple;
    }
    
    public ITuple create(String path, String title, String[] columnName, Class[] columnType) {
        return create(path,title,columnName,columnType,"");
    }
    
    /**
     * Create an NTuple
     * @param path The persistency path of the n-tuple
     * @param title The title of the n-tuple
     * @param columns The names and types of the columns e.g. "float px, py, pz, float energy, int charge"
     * @param options NTuple options (currently undefined)
     */
    public ITuple create(String path, String title, String columns, String options) {
        Tuple tuple = new Tuple(nameInPath(path), title, columns, options);
        if ( tree != null ) tree.addFromFactory(parentPath(path),tuple);
        return tuple;
    }
    public ITuple create(String path, String title, String columns) {
        return create(path,title,columns,"");
    }
    
    /**
     * Create a logical chain of ITuples. All ITuples in the set must
     * have the same structure. Chained ITuple can not be filled.
     * @param path The persistency name of the new n-tuple
     * @param title The title of the new n-tuple
     * @param set The array of ITuples to chain
     */
    public ITuple createChained(String path, String title, ITuple[] set) {
        ITuple tuple = new ChainedTuple(nameInPath(path), title, set);
        if ( tree != null ) tree.addFromFactory(parentPath(path),(IManagedObject) tuple);
        return tuple;
    }
    public ITuple createChained(String path, String title, String[] setName) {
        if ( tree == null )
            throw new IllegalArgumentException("This TupleFactory does not have a Tree. Can not find Tuple by name.");
        
        ITuple[] tupleSet = new ITuple[setName.length];
        for (int i=0; i<setName.length; i++) {
            Object t = tree.find(setName[i]);
            if (t instanceof ITuple) tupleSet[i] = (ITuple) t;
            else throw new IllegalArgumentException("ManagedObject \""+setName[i]+"\" is not an ITuple");
        }
        return createChained(path, title, tupleSet);
    }
    
    public ITuple createFiltered(String path, ITuple tuple, IFilter filter) {
        int nColumns = tuple.columns();
        String[] columnNames = new String[nColumns];
        for (int i=0; i<nColumns; i++) {
            columnNames[i] = tuple.columnName(i);
        }
        return createFiltered(path,tuple, filter, columnNames);
    }
    
    public ITuple createFiltered(String path, ITuple tuple, IFilter filter, String[] columns) {
        int nColumns = columns.length;
        if ( nColumns > tuple.columns() )
            throw new IllegalArgumentException("Original ITuple has less columns ("+tuple.columns()+
            ") than requested for copy ("+nColumns+")");
        int[] columnId = new int[nColumns];
        String[] fullColumnNames = new String[nColumns];
        Class[] columnTypes = new Class[nColumns];
        
        // To get Default Column String
        AbstractTuple cdsTuple = null;
        if (tuple instanceof Tuple) {
            cdsTuple = (AbstractTuple) tuple;
        }
        for (int i=0; i<nColumns; i++) {
            columnId[i] = tuple.findColumn(columns[i]);
            columnTypes[i] = tuple.columnType(columnId[i]);
            
            fullColumnNames[i] = null;
            if (cdsTuple != null) {
                fullColumnNames[i] = cdsTuple.columnDefaultString(columnId[i]);
            } else {
                // This all is for just a simple ITuple
                if ( tuple.columnType(i) != ITuple.class )
                     fullColumnNames[i] = tuple.columnDefaultValue(i).toString();
                else {
                    ITuple tup = tuple.findTuple(i);
                    if ( tup != null ) {
                        String tupName = "";
                        if (tup instanceof IManagedObject) tupName = ((IManagedObject) tup).name();
                        else tupName = tup.title();
                        String tmpColumnsString = "";
                        int nCol = tup.columns();
                        for (int j=0; j<nCol; j++) {
                            Class colType = tup.columnType(j);
                            String colName =tup.columnName(j);
                            tmpColumnsString += colType + " " + colName;
                            if ( i < nCol ) tmpColumnsString += ";";
                        }
                        fullColumnNames[i] =  tupName+" = {"+tmpColumnsString+"}";
                    } else
                        fullColumnNames[i] =  "null";
                }
            }
            if ( columnTypes[i] != ITuple.class )
                fullColumnNames[i] = columns[i] + " = " + fullColumnNames[i];
        }
        String title = tuple.title();

        //Tuple newTuple = new Tuple(name,title, fullColumnNames, columnTypes, null);
        Tuple newTuple = new Tuple(nameInPath(path),title, fullColumnNames, columnTypes, null);
        if ( tree != null ) tree.addFromFactory(parentPath(path),newTuple);
        copyTuple(tuple, newTuple, filter);
        return newTuple;
    }
    
    /**
     * Create a copy of an ITuple.
     * @param path    The path of the resulting ITuple. The path can either be a relative or full path.
     *                ("/folder1/folder2/dataName" and "../folder/dataName" are valid paths).
     *                All the directories in the path must exist. The characther `/` cannot be used
     *                in names; it is only used to delimit directories within paths.
     * @param tuple   The ITuple to be copied.
     * @return        The copy of the ITuple.
     *
     */
    public ITuple createCopy(String path, ITuple tuple) throws IllegalArgumentException {
        return createFiltered(path, tuple, null);        
    }

        
    private void copyTuple(ITuple tuple, ITuple newTuple, IFilter filter) {
        // fill new n-tuple
        if (tuple.rows() > 0) {
            int nColumns = newTuple.columns();
            int[] columnId = new int[nColumns];
            Class[] columnTypes = new Class[nColumns];
            for (int i=0; i<nColumns; i++) {
                columnId[i] = tuple.findColumn(newTuple.columnName(i));
                columnTypes[i] = newTuple.columnType(i);
            }
            
            tuple.start();
            if ( filter != null ) filter.initialize(tuple);
            while (tuple.next()) {
                if (filter == null || filter.accept()) {
                    for (int i=0; i<nColumns; i++) {
                        int j = columnId[i];
                        if ( columnTypes[i] == Integer.TYPE ) newTuple.fill(i, tuple.getInt(j));
                        else if ( columnTypes[i] == Short.TYPE) newTuple.fill(i, tuple.getShort(j));
                        else if ( columnTypes[i] == Long.TYPE) newTuple.fill(i, tuple.getLong(j));
                        else if ( columnTypes[i] == Float.TYPE) newTuple.fill(i, tuple.getFloat(j));
                        else if ( columnTypes[i] == Double.TYPE) newTuple.fill(i, tuple.getDouble(j));
                        else if ( columnTypes[i] == Boolean.TYPE) newTuple.fill(i, tuple.getBoolean(j));
                        else if ( columnTypes[i] == Byte.TYPE) newTuple.fill(i, tuple.getByte(j));
                        else if ( columnTypes[i] == Character.TYPE) newTuple.fill(i, tuple.getChar(j));
                        else if ( columnTypes[i] == ITuple.class) {
                            ITuple tOld = (ITuple)tuple.getObject(j);
                            ITuple tNew = newTuple.getTuple(i);
                            copyTuple(tOld, tNew, null);
                        }
                        else newTuple.fill(i, tuple.getObject(j));
                    }
                    newTuple.addRow();
                }
            }
        }
    }
    
    
    
    
    /**
     * Create IFilter.
     *
     */
    public IFilter createFilter(String expression) {
        return new Filter(expression);
    }
    
    public IFilter createFilter(String expression, int rowsToProcess, int startingRow) {
        return new Filter(expression,rowsToProcess,startingRow);
    }
    
    public IFilter createFilter(String expression, int rowsToProcess) {
        return new Filter(expression,rowsToProcess, 0);
    }
    
    /**
     * Create IEvaluator.
     *
     */
    public IEvaluator createEvaluator(String expression) {
        return new Evaluator(expression);
    }
    
}
