package jas.hist;
import jas.util.DoubleWithError;

public abstract class Fittable1DFunction extends Basic1DFunction implements ExtendedStatistics
{
    public abstract double valueAt(double x, double[] param) throws FunctionValueUndefined;
    abstract public void setFit(Fitter fit, double[] param) throws InvalidFunctionParameter;
    public boolean[] getIncludeParametersInFit()
    {
        initIncludeParametersInFit(getParameterNames().length);
        return m_fitParms;
    }
    public void clearFitParams()
    {
        m_fitParms = null;
    }
    public void setIncludeParameterInFit(int index, boolean value)
    {
        initIncludeParametersInFit(getParameterNames().length);
        m_fitParms[index] = value;
        clearFit();
        setChanged();
    }
    protected void initIncludeParametersInFit(int nParameters)
    {
        if (m_fitParms == null || m_fitParms.length != nParameters)
        {
            m_fitParms = new boolean[nParameters];
            for (int i=0; i<m_fitParms.length; i++) m_fitParms[i] = true;
        }
    }
   /**
    *  Calculates the partial derivative of the function with respect to each parameter
    *  at point x. This is a non-analytical calculation which can be overriden by subclasses
    *  which wish to provide an analytical calculation
    *  @exception FunctionValueUndefined if the function is not defined for <code>x</code>
    */
    
    public double[] getDerivatives(double x, double[] a) throws FunctionValueUndefined
    {
        int nterms = a.length;
        double[] result = new double[nterms];
        
        double[] deltaA = getParameterDeltas();
        
        for (int j=0; j<nterms; j++)
        {
            double old = a[j];
            double delta = deltaA[j];
            a[j] = old + delta;
            double y = valueAt(x,a);
            a[j] = old - delta;
            result[j] = (y - valueAt(x,a))/(2*delta);
            a[j] = old;
        }
        return result;
    }
    private double[] getParameterDeltas()
    {
        double[] a = (double[]) getParameterValues().clone();
        for (int i=0; i<a.length; i++) a[i] = 1e-7;
        return a;
    }
    public Fitter getFit()
    {
        return m_fit;
    }
    protected void setFit(Fitter fit)
    {
        m_fit = fit;
    }
    public void clearFit()
    {
        Fitter old = m_fit; // avoid race condition in case we are called recursively
        
        // We must tell the fitter that it should leave us alone
        if (old != null)
        {
            m_fit = null;
            old.dispose();
        }
    }
    protected void destroy()
    {
        clearFit();
        super.destroy();
    }
    
    public String[] getStatisticNames()
    {
        //adding Chi squared to end of statnames array
        String[] statnames = null;
        if(m_fit != null){
            if(getParameterNames()!=null){
                statnames = new String[getParameterNames().length+1];
                for(int i=0;i<getParameterNames().length;i++){
                    statnames[i]=getParameterNames()[i];
                }
            }else statnames = new String[1];
            statnames[statnames.length -1]=chi2;
            return statnames;
        }else return statnames = super.getStatisticNames();
        
        
    }
    public double getStatistic(String name)
    {
        if (name.equals(chi2)) return m_fit.getChiSquared();
        else return super.getStatistic(name);
    }
    public Object getExtendedStatistic(String name)
    {
        if(m_fit!=null) {
            boolean[] inFit = getIncludeParametersInFit();
            int count = 0;
            for ( int i = 0; i < inFit.length; i++ ) {
                if ( name.equals(getParameterNames()[i]) ) {
                    if ( inFit[i] ) 
                        return new DoubleWithError(getParameterValues()[i],m_fit.getParameterSigmas()[count]);
                    else
                        return format.format(getParameterValues()[i])+" Fixed";
                }
                if ( inFit[i] )
                    count++;
            }
        }
        return null;
    }
    private final static String chi2 = "\u03c7\u00b2/ndof";
    private Fitter m_fit;
    private boolean[] m_fitParms;
    private java.text.NumberFormat format = java.text.NumberFormat.getInstance();
}
