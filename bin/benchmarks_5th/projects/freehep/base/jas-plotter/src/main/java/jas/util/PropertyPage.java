package jas.util;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;

public abstract class PropertyPage extends JPanel implements Observer
{
	public PropertyPage()
	{
	}
	public void addNotify()
	{
		super.addNotify();
		if (getParent() instanceof PropertyPage)
			((PropertyPage) getParent()).addInnerPropertyPage(this);
		// If the parent is a property page then notify the parent.
	}
	void addInnerPropertyPage(PropertyPage p)
	{
		if (getParent() instanceof PropertyPage)
			// pass the notification to the topmost page
			((PropertyPage) getParent()).addInnerPropertyPage(p);
		else
		{
			// this is the top page, so deal with the notification...
			if (m_innerPropPages == null) m_innerPropPages = new Vector();
			m_innerPropPages.addElement(p); // keep a list of inner pages
		}
	}
	public void setPropertySite(PropertySite dlg)
	{
		m_dlg = dlg;
	}
	public void doDataExchange(boolean set,Object hist)
	{
		Enumeration e = m_propertyBindings.elements();
		while (e.hasMoreElements())
		{
			PropertyBinding p = (PropertyBinding) e.nextElement();
			p.doDataExchange(set,hist);
		}
		m_changed = false;
	}
	protected void addBinding(PropertyBinding bind)
	{
		m_propertyBindings.addElement(bind);
		bind.addObserver(this);
	}
	public void update(Observable o, Object arg)
	{
		setChanged(true);
	}
	protected void setChanged(boolean set)
	{
		m_changed = set;
		if (m_dlg != null) m_dlg.callEnable();
	}
	boolean hasChanged()
	{
		return m_changed;
	}
	/**
	 * called when the page is deactivated, either due to another page
	 * being selected, or due to the dialog being disposed of.
	 */
	protected void deactivate()
	{
	}
	/**
	 * called when the page is activated, either because it is the default
	 * page, or because its tab was selected. This is called before doDataExchanged
	 */
	protected void activate()
	{
	}
	boolean hasValidInput()
	{
		Enumeration e = m_propertyBindings.elements();
		PropertyBinding b;
		while (e.hasMoreElements())
		{
			b = (PropertyBinding) e.nextElement();
			if (! b.hasValidInput())
				return false;
		}
		if (m_innerPropPages != null) // check also all inner pages
		{
			e = m_innerPropPages.elements();
			while (e.hasMoreElements())
				if (!((PropertyPage) e.nextElement()).hasValidInput())
					return false;
		}
		return true;
	}
	// default return indicates no help topic; override to specify otherwise
	public String getHelpTopic()
	{
		return null;
	}
	private PropertySite m_dlg;
	private boolean m_changed = false;
    private Vector m_propertyBindings = new Vector();
	private Vector m_innerPropPages = null;
}
