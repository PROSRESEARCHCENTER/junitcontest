package hep.aida.ref.plotter.style.registry;

import gnu.jel.CompiledExpression;
import gnu.jel.DVMap;
import gnu.jel.Evaluator;
import gnu.jel.Library;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITupleColumn;

import java.util.Vector;


public class JELRuleCompiledExpression {
    private String  expression;
    private String  parsedExpression;
    private CompiledExpression compExpression;
    private Library lib;
    private ValueProvider pro;
    private Object[] context;
    private Resolver res;
    private Vector attributes;
    
    public JELRuleCompiledExpression(String expression) {
        this.expression = expression;
        attributes = new Vector();
        
        // Create a compiled expression
        parse();
        compile();
    }
    
    private int getAttributeIndex(String key) {
        return pro.getAttributeIndex(key);
    }
    
    public void parse() {
        parsedExpression = expression;

        //System.out.println("parse :: parsedExpression="+parsedExpression);
    }
    
    private void compile() {
        Class[] staticLib  = new Class[1];
        
        Class[] dynamicLib = new Class[1];
        context = new Object[1];
        Class[] dotLib     =  { Object.class, String.class, Class.class, java.util.Date.class };
        try {
            staticLib[0]  = java.lang.Class.forName("java.lang.Math");
            
            pro = new ValueProvider();
            dynamicLib[0] = pro.getClass();
            context[0] = pro;
            
            res = new Resolver();
            
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("Can not find class for JEL!", e);
        }
        
        try {
            lib = new Library(staticLib, dynamicLib, dotLib, res, null);
            lib.markStateDependent("random", null);
        } catch (gnu.jel.CompilationException ec1) {
            throw new RuntimeException("Can not compile JEL Library!", ec1);
        }
        
        try {
            compExpression = Evaluator.compile(parsedExpression, lib, Boolean.TYPE);
        } catch (gnu.jel.CompilationException ec2) {
            throw new RuntimeException("Can not compile JEL Expression: "+expression, ec2);
        }
    }
    
    public boolean evaluate(IPlotterState state) {
        boolean ruleApplies = false;
        pro.setState(state);
        try {
            ruleApplies =  compExpression.evaluate_boolean(context);
        } catch (Throwable t) {
            throw new RuntimeException("Runtime JEL Evaluation Problems!", t);
        }
        
        return ruleApplies;
    }
    
    
    public class Resolver extends DVMap {
        
        public Resolver() {}
        public String getTypeName(String name) {
            //System.out.println("Resolver.getTypeName :: name = "+name);
            
            if (name.trim().equalsIgnoreCase(IStyleRule.PATH)) return "String";
            else if (name.trim().equalsIgnoreCase(IStyleRule.OBJECT)) return "Object";
            else if (name.trim().equalsIgnoreCase(IStyleRule.OBJECT_TYPE)) return "String";
            else if (name.trim().equalsIgnoreCase(IStyleRule.NULL)) return "Object";
            //else if (name.trim().equalsIgnoreCase(IStyleRule.ATTRIBUTE)) return "String";
            else if (name.trim().equalsIgnoreCase(IStyleRule.OVERLAY_INDEX)) return "Integer";
            else if (name.trim().equalsIgnoreCase(IStyleRule.OVERLAY_TOTAL)) return "Integer";
            else if (name.trim().equalsIgnoreCase(IStyleRule.REGION_INDEX)) return "Integer";
            else if (name.trim().equalsIgnoreCase(IStyleRule.REGION_TOTAL)) return "Integer";
            else return null;
        }
        
        public Object translate(String name) {
            //System.out.println("Resolver.translate :: name = "+name);
            if (name.trim().equalsIgnoreCase(IStyleRule.PATH)) return new Integer(-1);
            else if (name.trim().equalsIgnoreCase(IStyleRule.OBJECT)) return new Integer(1);
            else if (name.trim().equalsIgnoreCase(IStyleRule.OBJECT_TYPE)) return new Integer(-2);
            else if (name.trim().equalsIgnoreCase(IStyleRule.NULL)) return new Integer(-10);
            //else if (name.trim().equalsIgnoreCase(IStyleRule.ATTRIBUTE)) return new Integer(10);
            else if (name.trim().equalsIgnoreCase(IStyleRule.OVERLAY_INDEX)) return new Integer(0);
            else if (name.trim().equalsIgnoreCase(IStyleRule.OVERLAY_TOTAL)) return new Integer(1);
            else if (name.trim().equalsIgnoreCase(IStyleRule.REGION_INDEX)) return new Integer(2);
            else if (name.trim().equalsIgnoreCase(IStyleRule.REGION_TOTAL)) return new Integer(3);
            return new Integer(100);
        }
    }
    
    public class ValueProvider {
        private IPlotterState state;
        
        public ValueProvider() {
            this(null);
        }
        public ValueProvider(IPlotterState state) {
            this.state = state;
        }
        
        int getAttributeIndex(String key) {
            return (state == null) ? 0 : ((PlotterState) state).getAttributeIndex(key);
        }
        
        public void setState(IPlotterState state) {
            this.state = state;
        }
        
        public String getStringProperty(int index) {
            String value = null;
            if (index == -10) value = null;
            else if (index == -2) value = objectAIDAType();
            else if (index == -1) value = state.getObjectPath();
            else if (index >= 0) value = state.getAttribute((String) attributes.get(index));
            //System.out.println("ValueProvider.getStringProperty :: index="+index+", value="+value);
            return value;
        }
        public Object getObjectProperty(int index) {
            Object value = null;
            if (index == 0) value = null;
            else if (index == 1) value = state.getObject();
            //System.out.println("ValueProvider.getObjectProperty :: index="+index+", value="+value);
            return value;
        }
	public int getIntegerProperty(int index) { 
            int value = -1;
            if (index == 0) return state.getOverlayIndex();
            else if (index == 1) value = state.getOverlayTotal();
            else if (index == 2) value = state.getRegionIndex();
            else if (index == 3) value = state.getRegionTotal();
            //System.out.println("ValueProvider.getIntegerProperty :: index="+index+", value="+value);
            return value;
        }
        
        public String attribute(String key) { 
            String value = state.getAttribute(key);
            if (value == null) value = "";
            return value;
        }
        
        public String objectAIDAType() { 
            Object obj = state.getObject();
            String value = null;
            if (obj == null) value = "null";
            else if (obj instanceof IDataPointSet)  value = "IDataPointSet";
            else if (obj instanceof IHistogram1D)  value = "IHistogram1D";
            else if (obj instanceof IHistogram2D)  value = "IHistogram2D";
            else if (obj instanceof IHistogram3D)  value = "IHistogram3D";
            else if (obj instanceof ICloud1D)  value = "ICloud1D";
            else if (obj instanceof ICloud2D)  value = "ICloud2D";
            else if (obj instanceof ICloud3D)  value = "ICloud3D";
            else if (obj instanceof IProfile1D)  value = "IProfile1D";
            else if (obj instanceof IProfile2D)  value = "IProfile2D";
            else if (obj instanceof IFunction)  value = "IFunction";
            else if (obj instanceof ITupleColumn)  value = "ITupleColumn";
            else if (obj instanceof IManagedObject) value = ((IManagedObject) obj).type();
            else value = obj.getClass().getName();
            return value;
        }
        
        public boolean isObjectInstanceOf(String className) {
            boolean ok = false;
            try {
                Class cl = Class.forName(className);
                //ok = (state.getObject() instanceof cl);
            } catch (Exception e) { e.printStackTrace(); }
            
            return ok;
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        //String expr = "(OBJECT.getClass().toString().equals(\"JELRule.class\")) && PATH.equalsIgnoreCase(\"MC\")";
        String expr = "overlayCount == 3";
        JELRuleCompiledExpression ce = new JELRuleCompiledExpression(expr);
        
        PlotterState state = new PlotterState();
        state.setObjectPath("mc");
        state.setAttribute("printing", "true");
        state.setAttribute("experiment", "BaBar");
        state.setOverlayIndex(3);
        state.setObject(new JELRule());
        
        boolean ok = ce.evaluate(state);
        System.out.println("ok="+ok);
        
    }
}
