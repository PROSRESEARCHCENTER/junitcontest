package hep.aida.ref.plotter;

import hep.aida.ref.AidaUtils;

public class RevolvingStyleParameter extends AbstractStyleParameter {
    protected int currentIndex = 0;
    protected int defaultIndex = 0;
    protected String[] values = null;
    protected String[] defaultValues = null;
    
    RevolvingStyleParameter(String name) {
        this(name, null);
    }
    
    RevolvingStyleParameter(String name, String defaultValue) {
        super(name, defaultValue, null);
    }
    
    public String[] allowedValues() {
        return super.allowedValues();
    }
    
    protected boolean setAllowedValues(String[] possibleValues) {
        return false;
    }
    
    protected boolean isParameterValueSet() {
        return super.isParameterValueSet();
    }
    
    protected String parameterValue() {
        return super.parameterValue();
    }
    
    protected void setDefaultValue(String defaultValue) {
        if (defaultValue == null || defaultValue.trim().equals("")) {
            defaultValues = null;
            super.setDefaultValue(null);
        } else {
                defaultValues = AidaUtils.parseString(defaultValue);
                super.setDefaultValue(defaultValue);
        }
    }
    
    public String defaultValue() {
        return super.defaultValue();
    }
    
    protected void reset() {
        super.reset();
        currentIndex = defaultIndex;
    }
    
    protected boolean setParameter(String parValue) {
        if (parValue == null || parValue.trim().equals("")) {
            values = null;
            return super.setParameter(null);
        } else {
            values = AidaUtils.parseString(parValue);
            return super.setParameter(parValue);
        }
    }
    
    protected String parameterValue(int index) {
        if (!isParameterValueSet()) return defaultValue(index);
        int k = values.length;
        index = index % k;
        return values[index];
    }
    
    protected String defaultValue(int index) {
        if (defaultValues == null) return null;
        int k = values.length;
        index = index % k;
        return defaultValues[index];
    }
    
    public int getCurrentIndex() { return currentIndex; }
    
    public void setCurrentIndex(int index) {
        if (isParameterValueSet()) {
            int k = values.length;
            index = index % k;
        }
        currentIndex = index;
    }
    
    public void incrementCurrentIndex() {
        setCurrentIndex(currentIndex+1);
    }
    
    public void incrementDefaultIndex() {
        setDefaultIndex(defaultIndex+1);
    }
    
    public int getDefaultIndex() { return defaultIndex; }
    
    public void setDefaultIndex(int index) {
        if (isParameterValueSet()) {
            int k = values.length;
            index = index % k;
        }
        defaultIndex = index;
    }
    
    private int indexForValue(String val) {
        int index = -1;
        if (!isParameterValueSet()) return index;
        String[] tmp = allowedValues();
        for (int i=0; i<tmp.length; i++) {
            if (tmp[i].equals(val)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public Class type() {
        return RevolvingStyleParameter.class;
    }
}
