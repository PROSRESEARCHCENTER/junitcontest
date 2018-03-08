package hep.aida.ref.plotter.style.registry;

public interface IGlobalIndexProvider {
    
    // Returns current index and increments cout 
    int getIndex();
    
    void resetIndex();
    
}
