package org.freehep.j3d.plot;

/**
 *	MouseDownUpBehavior.java - used to switch a switch node to display a simpler
 *								graph during rotations, panning, zooming, etc.
 */

import java.awt.event.*;
import java.awt.AWTEvent;
import javax.media.j3d.*;
import java.util.Enumeration;
import javax.vecmath.*;

/**
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: MouseDownUpBehavior.java 8584 2006-08-10 23:06:37Z duns $
 */
class MouseDownUpBehavior extends Behavior
{
    WakeupOnAWTEvent w1 = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
    WakeupCriterion[] w2 = {w1};
    WakeupCondition w = new WakeupOr(w2);
    WakeupOnAWTEvent wu1 = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
    WakeupCriterion[] wu2 = {wu1};
    WakeupCondition wu = new WakeupOr(wu2);
	Switch sw;
	
	private TimeStamp timeStamp = TimeStamp.sharedInstance();	
    
    public void initialize() {
		// Establish initial wakeup criteria
		wakeupOn(wu);
    }

    /**
     *  Override Behavior's stimulus method to handle the event.
     */
    public void processStimulus(Enumeration criteria) {
		WakeupOnAWTEvent ev;
		WakeupCriterion genericEvt;
		AWTEvent[] events;
		
		// return if switch is disabled (user data is false)
		if (!(((Boolean)sw.getUserData()).booleanValue())) {	
			wakeupOn(wu);
			return;
		}
   
		while (criteria.hasMoreElements()) {
			genericEvt = (WakeupCriterion) criteria.nextElement();
			if (genericEvt instanceof WakeupOnAWTEvent) {
				ev = (WakeupOnAWTEvent) genericEvt;
				events = ev.getAWTEvent();
				processSwitchEvent(events);
			}
		}
    }

    
    /**
     *  Process a mouse up or down event to switch a switch node.
     */
    void processSwitchEvent(AWTEvent[] events) {
		for (int i = 0; i < events.length; ++i) {
			if (events[i] instanceof MouseEvent) {
				MouseEvent event = (MouseEvent)events[i];
				if (event.getID() == MouseEvent.MOUSE_PRESSED) {
					sw.setWhichChild(1);	// we assume here that zero is the default
					// Set wakeup criteria for next time
					wakeupOn(w);
					break;
				}
				else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
					sw.setWhichChild(0);	// we assume here that zero is the default
					// Set wakeup criteria for next time
					wakeupOn(wu);
					break;
				}
			}
		}
    }

  
    /**
     *  Constructor 
     */
    public MouseDownUpBehavior(Bounds bound, Switch s) {  // change to switch
		this.setSchedulingBounds(bound);
		sw = s;
    }
}



