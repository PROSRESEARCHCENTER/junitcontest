package org.freehep.j3d.plot;

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.vecmath.*;
import javax.media.j3d.*;

/**
 * This class is a simple behavior that invokes the KeyNavigator
 * to modify the view platform transform.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: KeyNavigatorBehavior.java 8584 2006-08-10 23:06:37Z duns $
 */
public class KeyNavigatorBehavior extends Behavior {
    private WakeupOnAWTEvent w1 = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    private WakeupOnAWTEvent w2 = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    private WakeupOnElapsedFrames w3 = new WakeupOnElapsedFrames(1);
    private WakeupCriterion[] warray = { w1, w2, w3};
    private WakeupCondition w = new WakeupOr(warray);
    private KeyEvent eventKey;
    private KeyNavigator keyNavigator;


    /**
     *  Override Behavior's initialize method to setup wakeup criteria.
     */
    public void initialize() {
	// Establish initial wakeup criteria
	wakeupOn(w);
    }

    /**
     *  Override Behavior's stimulus method to handle the event.
     */
    public void processStimulus(Enumeration criteria) {
	WakeupOnAWTEvent ev;
	WakeupCriterion genericEvt;
	AWTEvent[] events;
	boolean sawFrame = false;
   
	while (criteria.hasMoreElements()) {
	    genericEvt = (WakeupCriterion) criteria.nextElement();
	    if (genericEvt instanceof WakeupOnAWTEvent) {
		ev = (WakeupOnAWTEvent) genericEvt;
		events = ev.getAWTEvent();
		processAWTEvent(events);
	    } else if (genericEvt instanceof WakeupOnElapsedFrames &&
		       eventKey != null) {
		sawFrame = true;
	    }
	}
	if (sawFrame)
	    keyNavigator.integrateTransformChanges();

	// Set wakeup criteria for next time
	wakeupOn(w);
    }

    /**
     *  Process a keyboard event
     */
    private void processAWTEvent(AWTEvent[] events) {
	for (int loop = 0; loop < events.length; loop++) {
	    if (events[loop] instanceof KeyEvent) {
		eventKey = (KeyEvent) events[loop];
		//  change the transformation; for example to zoom
		if (eventKey.getID() == KeyEvent.KEY_PRESSED ||
		    eventKey.getID() == KeyEvent.KEY_RELEASED) {
		    //System.out.println("Keyboard is hit! " + eventKey);
		    keyNavigator.processKeyEvent(eventKey);
		}
	    }
	}
    }

    /**
     * Constructs a new key navigator behavior node that operates
     * on the specified transform group.
     * @param targetTG the target transform group
     */
    public KeyNavigatorBehavior(TransformGroup targetTG) {
	keyNavigator = new KeyNavigator(targetTG);
    }

}
