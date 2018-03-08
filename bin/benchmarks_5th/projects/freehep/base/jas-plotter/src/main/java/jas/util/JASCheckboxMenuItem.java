package jas.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBoxMenuItem;

public class JASCheckboxMenuItem 
	extends JCheckBoxMenuItem 
	implements Observer,ActionListener,JASCheckboxState, CommandSource
{
	public JASCheckboxMenuItem()
	{
		super();
		setEnabled(false);
		Application.getApplication().getCommandManager().add(this);
		addActionListener(this);		
	}
	public JASCheckboxMenuItem(String s, char mnemonic)
	{
		super(s);
		setMnemonic(mnemonic);
		setEnabled(false);
		Application.getApplication().getCommandManager().add(this); // register with CommandTargetManager
		addActionListener(this);
	}
	public void actionPerformed(ActionEvent evt)
	{
		m_target.invoke(getState());
	}
	public void setCheckbox(boolean state)
	{
		setState(state);
	}
	public boolean setTarget(CommandTarget t)
	{
		if (t instanceof BooleanCommandTarget)
		{
			m_target = (BooleanCommandTarget) t;
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
		return getText();
	}
	public void update(Observable o, Object arg)
	{
		m_target.enable(this);
	}
	public void setEnabled(boolean state)
	{
		if (correspondingToolBarFloating) {
			//We need to do this since when the menu is refreshed calling setEnabled
			//wipes out the previous setting, which we might have adjusted based on whether
			//the user has undocked the corresponding toolbar IF this JASCheckboxMenuItem
			//controls the visibility of a toolbar.  (Yes, this is a bit ugly but when we
			//move to JDK 1.2 this issue will go away.)
			super.setEnabled(false);
		} else {
			super.setEnabled(state);
		}
	}
	public void setCorrespondingToolBarFloating(boolean state) {
		correspondingToolBarFloating = state;
	}
	private boolean correspondingToolBarFloating = false;
	private BooleanCommandTarget m_target = null;
}
