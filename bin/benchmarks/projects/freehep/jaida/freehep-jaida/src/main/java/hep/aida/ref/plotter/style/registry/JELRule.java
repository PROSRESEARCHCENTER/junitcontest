package hep.aida.ref.plotter.style.registry;

public class JELRule implements IStyleRule {
    
    private String description;
    private String type;
    private JELRuleCompiledExpression compiledExpression;
    
    /** Creates a new instance of JELRule */
    public JELRule() {
        this("");
    }
    public JELRule(String description) {
        this(description, null);
    }
    public JELRule(String description, String type) {
        this.description = description;
        this.type = type;
        if (description != null) recompile();
    }
 
    // Service methods
    
    void recompile() {
        if (description == null || description.trim().equals("")) compiledExpression = null;
        else compiledExpression = new JELRuleCompiledExpression(description);
    }
    
    
    public void setType(String d) { type = d; }
    public String getType() { return type; }
    
    
    // IStyleRule methods
    
    public void setDescription(String d) { 
        String tmp = description;
        description = d;
        try {
            recompile();
        } catch (RuntimeException e1) {
            description = tmp;
            throw e1;
        } catch (Exception e2) {
            description = tmp;
            throw new RuntimeException("Can not set Description for JELRule: \n\t"+d, e2);
        }
    }
    
    public String getDescription() { return description; }
    
    // Evaluates the Rule    
    public boolean ruleApplies(IPlotterState state) {
        if (compiledExpression == null) return true;
        else return compiledExpression.evaluate(state);
    }
    
}
