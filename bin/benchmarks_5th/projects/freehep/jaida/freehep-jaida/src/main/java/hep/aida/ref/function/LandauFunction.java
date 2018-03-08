package hep.aida.ref.function;


public class LandauFunction extends AbstractIFunction {

    LandauPdf landauPdf = new LandauPdf();
    
    public LandauFunction() {
        this("");
    }
    
    public LandauFunction(String title) {
        
        super();
        
        variableNames = new String[1];
        variableNames[0] = "x0";
        
        parameterNames = new String[2];
        parameterNames[0] = "mean";
        parameterNames[1] = "sigma";

        init(title);
    }
    
    @Override
    public double value(double[] v) {
        return landauPdf.getValue(v[0]);
    }

    @Override
    public void setParameter(String key, double value) throws IllegalArgumentException {
        super.setParameter(key, value);
        if (key.equals("mean")) {
            System.out.println("set mean = " + value);
            landauPdf.setMean(value);
        } else if (key.equals("sigma")) {
            System.out.println("set sigma = " + value);
            landauPdf.setSigma(value);
        }        
    }

    @Override
    public void setParameters(double[] parameters) throws IllegalArgumentException {        
        super.setParameters(parameters);
        landauPdf.setMean(parameters[0]);
        landauPdf.setSigma(parameters[1]);
    }
}
