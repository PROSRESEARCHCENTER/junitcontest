package jas.util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JRadioButtonMenuItem;

public class JASRadioMenuItem 
	extends JRadioButtonMenuItem 
	implements Observer,ActionListener,JASCheckboxState, CommandSource
{
	public JASRadioMenuItem()
	{
		super();
		setEnabled(false);
		Application.getApplication().getCommandManager().add(this); // register with CommandTargetManager
		addActionListener(this);		
	}
	public void actionPerformed(ActionEvent evt)
	{
		m_target.invoke();
	}
	public void setCheckbox(boolean state)
	{
		setSelected(state);
	}
	public boolean setTarget(CommandTarget t)
	{
		if (t instanceof SimpleCommandTarget)
		{
			m_target = (SimpleCommandTarget) t;
			m_target.enable(this);
			return true;
		}
		else return false;
	}
	public void clearTarget()
	{
		m_target = null;
		setEnabled(false);
	}
	public CommandTarget getTarget()
	{
		return m_target;
	}
	public String getCommand()
	{
		return getActionCommand();
	}
	public void update(Observable o, Object arg)
	{
		m_target.enable(this);
	}
	public void setEnabled(boolean state)
	{
		super.setEnabled(state);
	}
	private SimpleCommandTarget m_target = null;
}

