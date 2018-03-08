/*
 * @(#)GetOptions.java
 */

package jas.util;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
 
/**
 * GetOptions is used to help parse command line arguments.  It is loosely
 * based on Paul Raines' GetOpt module with modifications to handle multiple
 * classes wanting access to the same arguments.  The main design difference
 * is that instead of doing all the work in the constructor we do it with
 * addOption() and parseArgs().  Furthermore, the Hashtables are different, etc.
 *
 * @author      Peter Armstrong
 *
 */

public class GetOptions extends Object {
	/**
	 * Creates an empty GetOptions object.
	 * 
	 * Options are added to the object using the addOption method, unlike
	 * GetOpt which adds them in the constructor.
	 */
	public GetOptions() 
	{
		shortToLongMap = new Hashtable();
		longToIsFlagMap = new Hashtable();
		longToOptionsMap = new Hashtable();
		longToComment = new Hashtable();
		params = new String[0];
	}

	/**
	 * Adds an option to be recognized by parseArgs.
	 * 
     * @param longName the full name of the option
     * @param flag true if the option is a flag (takes 0 parameters), false otherwise
	 */
	public void addOption(String longName, boolean flag) 
	{
		longToIsFlagMap.put(longName, new Boolean(flag));
	}
	
	/**
	 * Adds an option to be recognized by parseArgs.
	 * 
     * @param longName the full name of the option
     * @param flag true if the option is a flag (takes 0 parameters), false otherwise
     * @param comment description of the arguments to the option
	 */
	public void addOption(String longName, boolean flag, String comment) 
	{
		longToIsFlagMap.put(longName, new Boolean(flag));
		longToComment.put(longName, comment);
	}

	/**
	 * Adds an option to be recognized by parseArgs.
	 * 
     * @param shortName the single character name of the option
     * @param flag true if the option is a flag (takes 0 parameters), false otherwise
	 */
	public void addOption(char shortName, boolean flag) 
	{
		addOption((new Character(shortName)).toString(), shortName, flag);
	}

	/**
	 * Adds an option to be recognized by parseArgs.
	 * 
     * @param shortName the single character name of the option
     * @param flag true if the option is a flag (takes 0 parameters), false otherwise
     * @param comment description of the arguments to the option
	 */
	public void addOption(char shortName, boolean flag, String comment) 
	{
		addOption((new Character(shortName)).toString(), shortName, flag, comment);
	}
	
	/**
	 * Adds an option to be recognized by parseArgs.
	 * 
     * @param longName the full name of the option
     * @param shortName the single character name of the option
     * @param flag true if the option is a flag (takes 0 parameters), false otherwise
	 */
	public void addOption(String longName, char shortName, boolean flag) 
	{
		addOption(longName, shortName, flag, null);
	}

	/**
	 * Adds an option to be recognized by parseArgs.
	 * 
     * @param longName the full name of the option
     * @param shortName the single character name of the option
     * @param flag true if the option is a flag (takes 0 parameters), false otherwise
     * @param comment description of the arguments to the option
	 */
	public void addOption(String longName, char shortName, boolean flag, String comment) 
	{
		shortToLongMap.put((new Character(shortName)).toString(), longName);
		longToIsFlagMap.put(longName, new Boolean(flag));
		if (comment != null) {
			longToComment.put(longName, comment);
		}
	}
	
	/**
	 * Returns true or false depending on if the option with the long name longName
	 * is in fact a real option.
	 *
	 * @param longName a string describing the option to query for.
	 * @return true or false.
	 */
	public boolean isLegalOption(String longName) 
	{
		return longToIsFlagMap.containsKey(longName);
	}
	
	/**
	 * Returns true or false depending on if the option with the long name longName was given
	 * in either the short (if one exists) or the long form.
	 *
	 * @param longName a string describing the option to query for.
	 * @return true or false.
	 */
	public boolean hasOption(String longName) 
	{
		return longToOptionsMap.containsKey(longName);
	}

	/**
	 * Returns the value given for the option with the long name longName.
	 * Returns "FLAG" if the option was given but is a flag (takes no arguments).
	 *
	 * @param longName a string describing the option to query for.
	 * @return value of option, "FLAG" or null.
	 */
	public String getOption(String longName) 
	{
		return longToOptionsMap.get(longName).toString();
	}

	/**
	 * Returns the comment given for the option with the long name longName.
	 *
	 * @param longName a string describing the option to query for.
	 * @return value of comment or null if no comment exists.
	 */
	public String getComment(String longName)
	{
		if (longToComment.containsKey(longName))
		{
			return longToComment.get(longName).toString();
		}
		return null;
	}

	/**
	 * Exception to throw when an invalid argument is encountered.
	 */
	public static class BadArguments extends Exception 
	{  
		public BadArguments() { super(); }
		public BadArguments(String s) { super(s); }
	}

	public void dumpOptions()
	{
		System.out.println("Options:");
		System.out.println();
		System.out.println("Short Long         Value    Comment");
		System.out.println("Name  Name         Required");
		System.out.println("----- ------------ -------- ---------------------------");		
		Enumeration vo = getValidOptions();
		while (vo.hasMoreElements()) System.out.println(vo.nextElement());		
	}
	/**
	 * Return an enumeration of String representations of <shortName, longName, isFlag> tuples.
	 * This is used to generate the -help message so it's not just frivolous debug code.
	 */
	private Enumeration getValidOptions() 
	{
		//first we get "normal" options which have all 3 attributes
		Enumeration shortNames = shortToLongMap.keys();
		Vector v = new Vector();
		String shortName = "";
		String longName = "";
		while (shortNames.hasMoreElements()) 
		{
			shortName = shortNames.nextElement().toString();
			longName = shortToLongMap.get(shortName).toString();
			v.addElement(formElement(shortName, longName));
		}
		
		//now we need to check if we missed any options with no shortNames
		Enumeration longNames = longToIsFlagMap.keys();
		while (longNames.hasMoreElements()) 
		{
			longName = longNames.nextElement().toString();
			if (!shortToLongMap.containsKey(shortName)) {
				v.addElement(formElement(" ", longName));//no short name so fill in the 1 char space
			}
		}
		return v.elements();
	}
		
	private String formElement(String shortName, String longName) 
	{
		String valueReqd = ((Boolean)longToIsFlagMap.get(longName)).booleanValue() ? "no" : "yes";

		return (pad(shortName,5)+" "+pad(longName,12)+" "+pad(valueReqd,8)+" "+getComment(longName));
	}
	private String pad(String value, int length)
	{
		String padding = "                                                       ";
		int l = value.length();
		if      (l == length) return value;
		else if (l  < length) return value + padding.substring(0,length-l);
		else                  return value.substring(length);
	}

	/**
	 * Parses the command line arguments.
	 * 
     * @param argv the array of arguments (eg. args as given to main())
     * @exception GetOptions.BadArguments an invalid arg or wrong # of args is encountered
	 */
	public void parseArgs(String[] argv) throws BadArguments {
		int i, j;
		String shortName, longName, exceptionMsg;
		try {
			for (i = 0; i < argv.length; i++) {
				//if we have an empty option then the rest is considered to be parameters not options
				if (argv[i] == "-" || argv[i] == "--") {
					i++;
					break;
				}
				//any option will begin with at least one -
				if (argv[i].charAt(0) == '-') {
					if (argv[i].charAt(1) == '-') {
						// longName option
						longName = argv[i].substring(2);
						try {
							Boolean isFlag = (Boolean)longToIsFlagMap.get(longName);
							if (!isFlag.booleanValue()) {
								//it's not a flag, so put the next arg in as its arguments
								//PROGRAMMER'S NOTE: WE ARE INCREMENTING THE LOOP INDEX HERE!!!
								longToOptionsMap.put(longName, argv[++i]);
							} else {
								longToOptionsMap.put(longName, "FLAG");
							}
						} catch (Exception e1) {
							if (isLegalOption(longName)) {
								exceptionMsg = "The " + longName + " option needs a different number of arguments than you provided.";
							} else {
								exceptionMsg = "There is no long option called \"" + longName + "\".";
							}
							throw new BadArguments(exceptionMsg);
						}
					} else {
						// array of character options (only the last one can have args)					
						for (j = 1; j < argv[i].length(); j++) {
							//process each character option (start at one since argv[0] == -
							//NOTE: The loop will be executed (argv[i].length() - 1) times IFF
							//		all the options are flags.  If an option takes arguments
							//		then it is defined to be the last option and we break out
							//		of the loop immediately.
							shortName = argv[i].substring(j,j+1);
							Boolean isFlag;
							try {
								longName = shortToLongMap.get(shortName).toString();
								isFlag = (Boolean)longToIsFlagMap.get(longName);
							} catch (Exception e) {
								throw new BadArguments("There is no short option called \"" + shortName + "\".");
							}
							if (isFlag == null) {
								throw new BadArguments("There is no short option called \"" + shortName + "\".");
							} else if (isFlag.booleanValue()) {
								//it is a flag so put the longName in along with the String value of the
								//boolean argument
								longToOptionsMap.put(longName,isFlag.toString());
							} else {
								//it's not a flag, so if the rest of this string is non-null
								//then it is the argument else the next arg is the argument
								//PROGRAMMER'S NOTE: WE MIGHT INCREMENT THE LOOP INDEX HERE!!!
								if (j == (argv[i].length() - 1)) {
									//the rest of this string is null (this is the last character in
									//the argument, so the next argument is the argument to this option)
									longToOptionsMap.put(longName, argv[++i]);
									//now we need to break out of this inner for loop before we do
									//something stupid (even though this *was* the last option
									//we've now messed with i)
									break;
								} else {
									//the rest of this string is the argument
									longToOptionsMap.put(longName, argv[i].substring(j+1));
									//now we need to skip to the next argument since this means
									//the end of the single character options
									break;
								}//if
							}//if
						}//for
					}//if
				} else {
					// we have reached the non-option arguments
					break;
				}//if
			}//for
			// fill in params[]
			params = new String[(argv.length - i)];      
			for (j = 0; i < argv.length; i++, j++) {
				params[j] = argv[i];
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException eek) {
			//The only way this could have happened is if the last option needed an argument
			//and none was given, causing the method to reference the argument blindly and overstep
			//the array bounds.
			throw new BadArguments("The last option you specified requires a value and you did not provide one.");
		}
	}//parseArgs
		
	/**
	 * Returns list of parameters
	 */
	public String[] getParams() {
		if (pVector != null) {
			params = new String[pVector.size()];      
			pVector.copyInto(params);
			pVector = null;
		}
		return params;
	}

	/**
	 * Returns a specific parameter
	 */
	public String getParam(int pNum) {
		if (pVector != null) {
			params = new String[pVector.size()];      
			pVector.copyInto(params);
			pVector = null;
		}
		return params[pNum];
	}
  
	/**
	 * Returns number of parameters
	 */
	public int numParams() {
		if (pVector == null) {
			return params.length;
		} else {
			return pVector.size();
		}
	}	

	/**
	 * Add a parmeter
	 */
	public void addParam(String param) {
		if (pVector == null) {
			int capacity = (params.length > 0 ? params.length : 5); 
			pVector = new Vector(capacity,5);
			for (int i = 1; i < params.length; i++)
				pVector.addElement(params[i]);
		}
		pVector.addElement(param);
	}
	
	/** The list of arguments that followed the options. */
	protected String[] params;
 
	/** Vector used to dynamically build options. */
	protected Vector pVector = null;
 
	/** Maps a short option to its equivalent long one. */
	protected Hashtable shortToLongMap; //(shortName, longName)*
	
	/** Maps a long option to whether it is a flag (takes no parameters). */
	protected Hashtable longToIsFlagMap; //(longName, Boolean)*

	/** Maps a long option to its comment. */
	protected Hashtable longToComment; //(longName, comment)*
	
	/**
	 * The storage of options.  An option can have either no value or a String value of 
	 * colon-delimited arguments.
	 */
	protected Hashtable longToOptionsMap; //(longName, arguments)*
}
