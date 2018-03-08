package org.freehep.swing;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultButtonModel;
import javax.swing.Timer;

/*
 * A ButtonModel which fires events continuously if the button
 * is help down. This is used by SpinBox, but can also be attached
 * to any button where this kind of behaviour is desired.
 *
 * @author Tony Johnson (tony_johnson@slac.stanford.edu)
 * @version $Id:
 */
class MachineGunButtonModel extends DefaultButtonModel implements ActionListener
{
	public void setPressed(boolean b)
	{
		if (!b && timer != null && timer.isRunning()) timer.stop();
			
		if((isPressed() == b) || !this.isEnabled()) {
		    return;
		}
		if (b) {
		    stateMask |= PRESSED;
		} else {
		    stateMask &= ~PRESSED;
		}
					
		if (isPressed() && isArmed()) 
		{
	        fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,getActionCommand()));
		}
		if (timer == null && isPressed() && delay > 0) 
		{
			timer = new Timer(delay,this);
			timer.setInitialDelay(initialDelay);
		}
		if (isPressed()) timer.start();
					
	    fireStateChanged();
	} 
	public void actionPerformed(ActionEvent e)
	{
		if (this.isEnabled() && isArmed() && isPressed()) 
		{
			fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
		}
	}
	public int getRepeatDelay()
	{
		return delay;
	}
	public int getInitialDelay()
	{
		return initialDelay;
	}
	/** 
	 * Sets the delay between repeat firings.
	 * @param The delay between increments in milliseconds, or 0 to disable repeat
	 */
	public void setRepeatDelay(int delay)
	{
		if (delay < 0) throw new IllegalArgumentException("Invalid repeatDelay set");
		this.delay = delay;
	}
	public void setInitialDelay(int initialDelay)
	{
		if (initialDelay < 0) throw new IllegalArgumentException("Invalid initialDelay set");
		this.initialDelay = initialDelay;
	}
	private int initialDelay = 500;
	private int delay = 50;
	private Timer timer;
}
