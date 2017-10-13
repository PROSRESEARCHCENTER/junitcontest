package jas.hist;
import jas.util.JASDialog;
import jas.util.JASIcon;
import jas.util.JASState;
import jas.util.NestedRuntimeException;
import jas.util.ObjectFactory;
import jas.util.ObjectFactoryException;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

/**
 *
 * A class which maintains a list of functions which can be overlayed
 *
 * on a plot.
 *
 */

public class FunctionRegistry
{
    private FunctionRegistry()
    {
    }
    
   /**
    * Get the (unique) instance of FunctionRegistry
    */
    static public FunctionRegistry instance()
    {
        return theFunctionRegistry;   
    }
    
   /**
    * Register a function given its class and name
    * @param c The class of the function
    * @param name The name of the function
    */
    
    public void registerFunction(Class c, String name)
    {
        registerFunction(createFunctionFactory(c,name));   
    }
    
   /**
    * Create a default function factory from a class and a name
    * @param c The class of the function
    * @param name The name of the function
    * @return The function factory
    */

    public FunctionFactory createFunctionFactory(Class c, String name)    
    {
        try   
        { 
            return new DefaultFunctionFactory(c,name);
        }
        catch (FunctionFactoryError e)
        {
            // convert the error to a runtime error   
            throw new NestedRuntimeException(e);
        }
    }
 
   /**
    * Add a function by specifing a FunctionFactory
    */   
    public void registerFunction(FunctionFactory f)
    {
        m_functions.addElement(f);   
    }
    
   /**
    * Remove a function factory from the list
    */
    public void removeFunctionFactory(FunctionFactory f)
    {
        m_functions.removeElement(f);   
    }
    
   /**
    * Clear the list
    */
    public void removeAllFunctions()
    {
        m_functions.removeAllElements();   
    }
 
   /**
    * Get an enumeration of all the FunctionFactories in the registry
    */
    
    public Enumeration elements()   
    {
        return m_functions.elements();   
    }
    
   /**
    * Get the size of the registry
    */
    public int size()
    {
        return m_functions.size();   
    }
    public FunctionFactory find(String name)
    {
        Enumeration e = m_functions.elements();
        while (e.hasMoreElements())
        {
            FunctionFactory ff = (FunctionFactory) e.nextElement();
            if (ff.getFunctionName().equals(name)) return ff;
        }
        return null;
    }
   /**
    * Replace the contents of the list
    * @param e An enumeration of function factories
    */
    
    public void setContents(Vector v)
    {
        m_functions = v;   
    }
    FunctionFactory chooseFunction(Frame f)
    {
        // These lines need to be excised in applet version
        ChooseFunctionDialog dlg = new ChooseFunctionDialog(f);
        if (dlg.doModal())
        {
            return dlg.getSelectedFunction();   
        }
        return null;
    }
    private Vector m_functions = new Vector();
    private static FunctionRegistry theFunctionRegistry = new FunctionRegistry();
    private class ChooseFunctionDialog extends JASDialog implements ItemListener, ActionListener
    
    {
        
        ChooseFunctionDialog(Frame f)
        
        {
            
            super(f,"Choose Function...");
            
            m_list = new javax.swing.JList(m_functions);
            
            m_list.setCellRenderer(new FunctionListCellRenderer());
            
            getContentPane().add("Center",m_list);
            
            pack();
            
        }
        
        public void itemStateChanged(ItemEvent evt)
        
        {
            
            callEnable();
            
        }
        
        public void actionPerformed(ActionEvent evt)
        
        {
            
            onOK();
            
        }
        
        public void enableOK(JASState state)
        
        {
            
            //state.setEnabled(m_list.getSelectedValue()!=null);
            
        }
        
        public void onOK()
        
        {
            
            m_selection = (FunctionFactory) m_list.getSelectedValue();
            
            super.onOK();
            
        }
        
        public FunctionFactory getSelectedFunction()
        
        {
            
            return m_selection;
            
        }
        
    }
    
    private FunctionFactory m_selection;
    
    private javax.swing.JList m_list;
    
}

class DefaultFunctionFactory extends ObjectFactory implements FunctionFactory

{
    
    DefaultFunctionFactory(Class c, String name) throws FunctionFactoryError
    
    {
        
        super(c);
        
        this.name = name;
        
        
        
        // Class must be a subclass of Basic1DFunction
        
        
        
        if (!inheritsFrom(Basic1DFunction.class))
            
            throw new FunctionFactoryError("Function "+name+" does not inherit from Basic1DFunction");
        
        
        
        // Class must be declared public
        
        
        
        if (!checkAccess())
            
            throw new FunctionFactoryError("Function "+name+" is not declared public");
        
        
        
        // The function needs to have a suitable constructor
        
        
        
        if (!canBeCreatedFrom(Double.TYPE,Double.TYPE,Double.TYPE,Double.TYPE))
            
            throw new FunctionFactoryError("Function "+name+" does not have a suitable constructor");
        
    }
    
    public Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError
    
    {
        
        try
        
        {
            
            JASHistAxis xAxis = h.getXAxis();
            
            JASHistAxis yAxis = h.getYAxis(); // which Y axis?
            
            return (Basic1DFunction) create(new Double(xAxis.getMin()),
            
            new Double(xAxis.getMax()),
            
            new Double(yAxis.getMin()),
            
            new Double(yAxis.getMax()));
            
        }
        
        catch (ObjectFactoryException e)
        
        {
            
            throw new FunctionFactoryError("Unexpected failure to create function");
            
        }
        
    }
    
    public String getFunctionName()
    
    {
        
        return name;
        
    }
    
    public Icon getFunctionIcon()
    
    {
        
        return icon;
        
    }
    
    private String name;
    
    private Icon icon = JASIcon.create(this,"function.gif");
    
}

class FunctionListCellRenderer extends DefaultListCellRenderer

{
    
    public Component getListCellRendererComponent(JList list,
    
    Object value,            // value to display
    
    int index,               // cell index
    
    boolean isSelected,      // is the cell selected
    
    boolean cellHasFocus)    // the list and the cell have the focus
    
    {
        
        super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
        
        if (value instanceof FunctionFactory)
            
        {
            
            FunctionFactory ff = (FunctionFactory) value;
            
            setText(ff.getFunctionName());
            
            setIcon(ff.getFunctionIcon());
            
        }
        
        return this;
        
    }
    
}
