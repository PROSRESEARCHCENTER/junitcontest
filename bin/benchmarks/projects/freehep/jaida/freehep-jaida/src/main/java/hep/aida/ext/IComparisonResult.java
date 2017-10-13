package hep.aida.ext;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface IComparisonResult {
    
    double quality();

    int nDof();

    boolean isMatching();
}
