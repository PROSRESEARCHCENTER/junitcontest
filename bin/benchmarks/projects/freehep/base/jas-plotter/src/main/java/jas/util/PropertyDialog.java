package jas.util;
import java.awt.Frame;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PropertyDialog extends JASDialog implements PropertySite, ChangeListener
{
	protected PropertyDialog(Frame f,String title,Object bean)
	{
		super(f,title,true,OK_BUTTON|APPLY_BUTTON|CANCEL_BUTTON|HELP_BUTTON);

		m_tab_manager = new JTabbedPane();
		m_tab_manager.addChangeListener(this);
		setContentPane(m_tab_manager);

		m_currentPage = null;
		m_bean = bean;
	}
	protected void addPage(final String name, final PropertyPage p, final boolean select)
	{
		m_tab_manager.addTab(name,p);
		p.setPropertySite(this);
		if (select) m_tab_manager.setSelectedComponent(p);
	}
	public void stateChanged(ChangeEvent evt)
	{
		if (m_suppressChangeEvents) return; // don't receive change events that this method creates
		if (m_currentPage == null || m_currentPage.hasValidInput())
		// notification of invalid input is the responsibility of the field binding
		{
			doDataExchange(true);
			if (m_currentPage != null) m_currentPage.deactivate();

			m_currentPage = (PropertyPage) m_tab_manager.getSelectedComponent();
			m_currentPage.activate();
			doDataExchange(false);
			setHelpTopic(m_currentPage.getHelpTopic());
			super.callEnable();
		}
		else
		{
			m_suppressChangeEvents = true; // ignore change event from the call below
			m_tab_manager.setSelectedComponent(m_currentPage); // return to page that was not valid
			m_suppressChangeEvents = false; // allow change events again
		}
	}
	private void doDataExchange(boolean set)
	{
		// guaranteed by this point to be valid input
		if (m_currentPage != null) m_currentPage.doDataExchange(set,m_bean);
	}
	public void enableApply(JASState state)
	{
		state.setEnabled(m_currentPage.hasChanged());
	}
	public void callEnable()
	{
		super.callEnable();
	}
	public void onApply()
	{
		if (m_currentPage.hasValidInput())
		// notification of invalid input is the responsibility of the field binding
		{
			doDataExchange(true);
			doDataExchange(false); // In case other values change
			super.onApply();
			super.callEnable();
		}
	}
	public void onOK()
	{
		if (m_currentPage.hasValidInput())
		{
			doDataExchange(true);
			m_currentPage.deactivate();
			super.onOK();
		}
	}
	public void onCancel()
	{
		m_currentPage.deactivate();
		super.onCancel();
	}
	private Object m_bean;
	private JTabbedPane m_tab_manager;
	private PropertyPage m_currentPage;
	private boolean m_suppressChangeEvents = false;
}
