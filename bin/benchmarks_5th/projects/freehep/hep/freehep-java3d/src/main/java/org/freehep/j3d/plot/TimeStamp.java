package org.freehep.j3d.plot;
import java.util.*;
import java.io.*;

/** This is class TimeStamp - it has a print method which will keep track of the last 
 *  time it was called and print a message along with the elapsed time since the last call.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: TimeStamp.java 8584 2006-08-10 23:06:37Z duns $
 */

public class TimeStamp {

	private long pastTime;
	private boolean silent = false;

	public TimeStamp() 
	{
		pastTime = System.currentTimeMillis();
	}

	public TimeStamp(String str) 
	{
		this();
		System.out.println(str + " - begun at: " + new Date(pastTime));
	}
	
	public void print(String str) 
	{
		if (!silent) 
		{
			long now = System.currentTimeMillis();
			System.out.println(str + " - elapsed time: " + (now-pastTime) + " milliseconds.");
			pastTime = now;
		}
	}
	
	public void setSilent() 
	{
		silent = true;
	}
	/**
	 * A static method for getting a single, global instance of TimeStamp.
	 */
	public static TimeStamp sharedInstance()
	{
		if (sharedInstance == null) sharedInstance = new TimeStamp("Shared Timestamp");
		return sharedInstance;
	}
	private static TimeStamp sharedInstance;
}
