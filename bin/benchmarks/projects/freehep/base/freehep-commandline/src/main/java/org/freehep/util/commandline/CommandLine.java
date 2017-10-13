// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;


/**
 * CommandLine parses a command line for parameters and options.
 *
 * @author Mark Donszelmann (CommandLine)
 * @version $Id: CommandLine.java 8584 2006-08-10 23:06:37Z duns $
 */
public class CommandLine {

    // requested
    private String name;
    private String description;
    private int numberOfParameters;
    private Hashtable options;          // (name or shortName, Option)
    private TreeMap   sortedOptions;    // (name, Option);
    private Vector parameters;
    private CommandLineException exception;
    private boolean multiLevel;
    private boolean check = true;

    // supplied
    private Vector arguments = new Vector();
    private Hashtable flags = new Hashtable();

    /**
     * Creates a CommandLine object to be used for parsing a set of arguments
     *
     * @param name name of the command
     * @param description description of the command
     * @param numberOfParameters minumum number of parameters
     * @param multiLevel forces the command line to be multi-level (i.e. does not check for
     *                   max number of parameters
     */
    public CommandLine(String name, String description, int numberOfParameters, boolean multiLevel) {
        this.name = name;
        this.description = description;
        this.numberOfParameters = numberOfParameters;
        this.multiLevel = multiLevel;
        this.parameters = new Vector();
        this.exception = null;
        this.options = new Hashtable();

        this.sortedOptions = new TreeMap(new Comparator() {
            public int compare(Object obj1, Object obj2) {
                return ((String)obj1).compareTo((String)obj2);
            }
        });
    }

    /**
     * Creates a CommandLine object to be used for parsing a set of arguments
     *
     * @param name name of the command
     * @param description description of the command
     * @param numberOfParameters minumum number of parameters
     */
    public CommandLine(String name, String description, int numberOfParameters) {
        this(name, description, numberOfParameters, false);
    }

    /**
     * enables checking of the options and parameters
     *
     * @param check sets checking
     */
    public void enableChecking(boolean check) {
        this.check = check;
    }

	/**
	 * Adds an option to be recognized by parse.
	 *
     * @param longName the full name of the option
     * @param shortName the short name of the option (may be set to null)
     * @param comment description of the arguments to the option
	 */
	public void addOption(String longName, String shortName, String comment) {
	    addOption(new Option(longName, shortName, comment));
	}

	/**
	 * Adds an option to be recognized by parse.
	 *
     * @param longName the full name of the option
     * @param shortName the short name of the option (may be set to null)
     * @param qualifiers gives a list of qualifiers you can use
     * @param comment description of the arguments to the option
	 */
	public void addOption(String longName, String shortName, String[] qualifiers, String comment) {
	    addOption(new Option(longName, shortName, qualifiers, comment));
	}

	/**
	 * Adds an option to be recognized by parse.
	 *
     * @param longName the full name of the option
     * @param shortName the short name of the option (may be set to null)
     * @param valueDescription description of the value
     * @param comment description of the arguments to the option
	 */
	public void addOption(String longName, String shortName, String valueDescription, String comment) {
	    addOption(new Option(longName, shortName, valueDescription, comment));
	}

	/**
	 * Adds an option to be recognized by parse, without doing parameter or option checking.
	 * This is normally used for options such as -? -help and -version
	 *
     * @param longName the full name of the option
     * @param shortName the short name of the option (may be set to null)
     * @param comment description of the arguments to the option
	 */
	public void addBailOutOption(String longName, String shortName, String comment) {
	    addOption(new Option(longName, shortName, comment, true));
	}

	/**
	 * Adds an option to be recognized by parse. This option can be specified multiple times
	 * on the commandline, and is immediately followed by its value. Usage (-Iincludedir)
	 *
     * @param name the full name of the option (preferably one character)
     * @param valueDescription what the value after the option means
     * @param comment description of the argument to the option
	 */
	public void addMultiOption(String name, String valueDescription, String comment) {
	    addOption(new Option(name, name, valueDescription, comment, true));
	}

	private void addOption(Option option) {
        options.put(option.getName(), option);
        sortedOptions.put(option.getName(), option);
        if (option.getShortName() != null) options.put(option.getShortName(), option);
	}

    /**
     * Adds a parameter to be recognized by parse.
     *
     * @param name name of the parameter
     * @param comment comment describing the parameter
     */
    public void addParameter(String name, String comment) {
        Parameter par = new Parameter(name, comment, (parameters.size() >= numberOfParameters));
        parameters.addElement(par);
    }

    /**
     * @return name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * @return description of the command
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return an enumeration of possible options (CommandLine.Option)
     */
    public Enumeration getOptions() {
        return options.keys();
    }

    /**
     * @return an enumeration of mandatory and optional parameters (CommandLine.Parameter)
     */
    public Enumeration getParameters() {
        return parameters.elements();
    }

    /**
     * @return true if the specified option is legal
     */
    public boolean isLegalOption(String name) {
        return options.containsKey(name);
    }

    /**
     * Parses the supplied arguments.
     *
     * @param args arguments for the command, including all options
     * @return true if the arguments were parsed correctly. False if a bailOut option was specified.
     * @throws CommandLineException in case the parsing failed.
     */
    public boolean parse(String[] args) throws CommandLineException {
        boolean bailOut = false;
        int i = 0;
        while (i < args.length) {
            if (args[i].charAt(0) == '-') {
                // option
                int j = (args[i].charAt(1) != '-') ? 1 : 2;
                StringBuffer flag = new StringBuffer();
                boolean qualified = false;
                while (j < args[i].length()) {
                    switch(args[i].charAt(j)) {
                        case '-':
                            // multiple options in one argument (never the last)
                            checkflag(flag.toString(), null, qualified);
                            bailOut |= ((Option)options.get(flag.toString())).isBailOut();
                            flag = new StringBuffer();
                            qualified = false;
                            break;
                        case ':':
                            qualified = true;
                            String qualifier = args[i].substring(j+1);
                            j = args[i].length() - 1;
                            checkflag(flag.toString(), qualifier, qualified);
                            flag = new StringBuffer();
                            qualified = false;
                            break;
                        case '=':
                            String value = args[i].substring(j+1);
                            j = args[i].length() - 1;
                            checkflag(flag.toString()+"="+value, value, qualified);
                            flag = new StringBuffer();
                            qualified = false;
                            break;
                        default:
                            flag.append(args[i].charAt(j));
                            break;
                    }
                    j++;
                }

                // (last) option could be set, but value may follow...
                if (flag.length() > 0) {
                    if (checkflag(flag.toString(), (i+1<args.length) ? args[i+1] : null, qualified)) {
                        i++;
                    }
                    Option option  = (Option)options.get(flag.toString());
                    bailOut |= (option == null) ? false : option.isBailOut();
                }

            } else {
                // arguments from here on
                while (i < args.length) {
                    arguments.addElement(args[i]);
                    i++;
                }
            }
            i++;
        }

        if (!bailOut) {
            // check for too few arguments here...
            if (arguments.size() < numberOfParameters) {
                exception = new MissingArgumentException("Too few arguments: "+arguments.size()+
                               ", while "+numberOfParameters+" "+
                                   ((parameters.size() == 1) ? "was" : "were")+" expected.");
                throw exception;
            }

            if ((check) && (!multiLevel)) {
                // check for too many arguments here ...
                if ((parameters.size() == 0) ||
                    !((Parameter)parameters.elementAt(parameters.size()-1)).getName().endsWith("...")) {
                    if (arguments.size() > parameters.size()) {
                        exception = new TooManyArgumentsException("Too many arguments: "+arguments.size()+
                                       ", while "+parameters.size()+" "+
                                       ((parameters.size() == 1) ? "was" : "were")+" expected.");
                        throw exception;
                    }
                }
            }
        }

        exception = null;
        return !bailOut;
    }

    private boolean checkflag(String flag, String value, boolean qualified) throws CommandLineException {
        if ((check) && (flag.length() <= 0)) {
            exception = new NoSuchOptionException("Option has zero length");
            throw exception;
        }

        Option option;
        if ((option = ((Option)options.get(flag)))!= null) {
            if (qualified) {
                // check qualifiers
                String[] qualifiers = option.getQualifiers();
                for (int j=0; j<qualifiers.length; j++) {
                    if (value.equals(qualifiers[j])) {
                        setFlag(option, value);
                        return false;
                    }
                }
                exception = new NoSuchQualifierException("Qualifier '"+value+"' for option '"+flag+"' does not exist.");
                throw exception;
            } else {
                // did we expect a value
                if (option.getValue() == null) {
                    setFlag(option, "true");
                    return false;
                } else {
                    if (value == null) {
                        exception = new MissingArgumentException("Missing argument <"+option.getValue()+"> for option '"+flag+"'.");
                        throw exception;
                    }
                    setFlag(option, value);
                    return true;
                }
            }
        }

        // check for special multi options
        for (Enumeration e = options.elements(); e.hasMoreElements(); ) {
            Option opt = (Option)e.nextElement();
            if (opt.isMulti() && flag.startsWith(opt.getName())) {
                if (opt.getName().length() < flag.length()) {
                    setFlag(opt, flag.substring(opt.getName().length()));
                    return false;
                } else {
                    exception = new MissingArgumentException("Missing argument <"+opt.getValue()+"> for option '"+opt.getName()+"'.");
                    throw exception;
                }
            }
        }

        // option does not exist
        if (check) {
            exception = new NoSuchOptionException("Option '"+flag+"' does not exist.");
            throw exception;
        }
        return false;
    }

    private void setFlag(Option option, String value) {
        if (option.isMulti()) {
            if (flags.get(option.getName()) == null) {
                flags.put(option.getName(), new Vector());
            }
            Vector values = (Vector)flags.get(option.getName());
            values.addElement(value);
        } else {
            flags.put(option.getName(), value);
        }
        if (option.getShortName() != null) flags.put(option.getShortName(), value);
    }

    /**
     * @return the last thrown exception
     */
    public CommandLineException getException() {
        return exception;
    }

    /**
     * @return a textual representation of the parsed arguments
     */
    public String toString() {
        StringBuffer s = new StringBuffer("CommandLine [\n");
        // line 1
        s.append("    ");
        s.append(name);
        s.append(" - ");
        s.append(description);
        s.append("\n");

        // line 2
        for (Enumeration e = flags.keys(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            s.append("    option[");
            s.append(key);
            s.append("] = ");
            Option option = (Option)options.get(key);
            if (option.isMulti()) {
                Vector values = (Vector)flags.get(key);
                for (int i=0; i<values.size(); i++) {
                    s.append((String)values.elementAt(i)+", ");
                }
            } else {
                s.append(flags.get(key));
            }
            s.append("\n");
        }

        // line 3
        for (Enumeration e = arguments.elements(); e.hasMoreElements(); ) {
            String arg = (String)e.nextElement();
            s.append("    arg = ");
            s.append(arg);
            s.append("\n");
        }

        // line 4
        if (exception != null) {
            s.append(exception.getMessage());
            s.append("\n");
        }

        s.append("\n]");
        return s.toString();
    }

    /**
     * @param name name of the option
     * @return the value of a specified option. null is returned in case the option was not specified.
     * true if this option was a flag.
     */
    public String getOption(String name) {
        return getOption(name, null);
    }

    /**
     * @param name name of the option
     * @return the value of a specified option. An empty vector is returned in case the option was not specified.
     */
    public Vector getMultiOption(String name) {
        Object flag = flags.get(name);
        return (flag == null) ? new Vector() : (Vector)flag;
    }

    /**
     * @param name name of the option
     * @param defaultValue default value if option was not set
     * @return the value of a specified option. null is returned in case the option was not specified.
     * true if this option was a flag.
     */
    public String getOption(String name, String defaultValue) {
        Object flag = flags.get(name);
        return (flag == null) ? defaultValue : flag.toString();
    }

    /**
     * @param name name of the option
     * @return true if the option was specified.
     */
    public boolean hasOption(String name) {
        return flags.containsKey(name);
    }

    /**
     * @param name name of the argument
     * @return a named argument. null in case the named argument was not supplied.
     */
    public String getArgument(String name) {
        for (int i=0; i<parameters.size(); i++) {
            Parameter parameter = (Parameter)parameters.elementAt(i);
            if (name.equals(parameter.getName())) {
                return (String)arguments.elementAt(i);
            }
        }
        return null;
    }

    /**
     * @return string array of parameters
     */
    public String[] getArguments() {
        String[] s = new String[arguments.size()];
        arguments.copyInto(s);
        return s;
    }

    /**
     * @return straing array of parameters which were not used by the command line
     */
    public String[] getUnparsedArguments() {
        String[] s = new String[arguments.size() - parameters.size()];
        int j = 0;
        for (int i=parameters.size(); i<arguments.size(); i++) {
            s[j] = (String)arguments.elementAt(i);
        }
        return s;
    }

    private String pad(String str, int length) {
        String padding = "                                                             ";
        int l = str.length();
        if (l == length) return str;
        else if (l < length) return str + padding.substring(0,length-l);
        else return str.substring(length);
    }
    /**
     * Get the help message, suitable for printing in response to a -help option
     */
    public String getHelp() {
        StringBuffer s = new StringBuffer();

        // line 1
        s.append(name);
        s.append(" - ");
        s.append(description);
        s.append("\n");

        // line 2
        s.append("Usage: ");
        s.append(name);
        if (options.size() > 0) {
            s.append(" [-options]");
        }
        int len = 0;
        for (int i=0; i<parameters.size(); i++) {
            Parameter parameter = (Parameter)parameters.elementAt(i);
            len = Math.max(len, parameter.getName().length());
            if (i<numberOfParameters) {
                s.append(" <");
                s.append(parameter.getName());
                s.append(">");
            } else {
                s.append(" [");
                s.append(parameter.getName());
            }
        }
        for (int i=numberOfParameters; i<parameters.size(); i++) {
            s.append("]");
        }
        s.append("\n\n");

        if (!parameters.isEmpty())
        {
            // line 3
            s.append("where:\n");

            // line 4
            for (int i=0; i<parameters.size(); i++) {
                Parameter parameter = (Parameter)parameters.elementAt(i);
                s.append("    ");
                s.append(pad(parameter.getName(), len));
                s.append("  ");
                s.append(parameter.getDescription());
                s.append("\n");
            }
            s.append("\n");
        }

        if (!sortedOptions.isEmpty())
        {
            // line 5
            s.append("where options include:\n");

            // line 6
            len = 0;
            int slen = 0;
            for(Iterator iterator = sortedOptions.entrySet().iterator(); iterator.hasNext(); ) {
                Option option = (Option)((Entry)iterator.next()).getValue();
                len = Math.max(len, option.getString().length());
                slen = Math.max(slen, (option.getShortName() == null) ? 0 : option.getShortName().length());
            }

            boolean multi = false;
            for(Iterator iterator = sortedOptions.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String)iterator.next();
                Option option = (Option)sortedOptions.get(key);
                s.append("    ");
                if (slen > 0) {
                    if (option.getShortName() == null) {
                        s.append(option.isMulti() ? "*" : " ");
                        multi |= option.isMulti();
                        s.append(pad("", slen));
                    } else {
                        s.append("-");
                        s.append(pad(option.getShortName(), slen));
                    }
                    s.append(" ");
                }
                s.append(pad(option.getString(), len));
                s.append("  ");
                s.append(option.getDescription());
                s.append("\n");
            }

            s.append("\n");
            if (multi) {
                s.append("(* option can be specified multiple times).\n");
                s.append("\n");
            }
        }
        return s.toString();
    }

    /**
     * Keeps mandatory and optional parameter names.
     */
    public static class Parameter {
        private String name;
        private String description;
        private boolean optional;

        public Parameter(String name, String description, boolean optional) {
            this.name = name;
            this.description = description;
            this.optional = optional;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isOptional() {
            return optional;
        }
    }

    /**
     * Keeps option names and types.
     */
    public static class Option {
        private String name;
        private String shortName;
        private String description;
        private String[] qualifiers = null;
        private String value = null;
        private boolean multi = false;
        private boolean bailOut;

        public Option(String name, String shortName, String description) {
            this(name, shortName, description, false);
        }

        public Option(String name, String shortName, String description, boolean bailOut) {
            this.name = name;
            this.shortName = shortName;
            this.description = description;
            this.bailOut = bailOut;
        }

        public Option(String name, String shortName, String[] qualifiers, String description) {
            this(name, shortName, description);
            this.qualifiers = qualifiers;
        }

        public Option(String name, String shortName, String value, String description) {
            this(name, shortName, description);
            this.value = value;
        }

        public Option(String name, String shortName, String value, String description, boolean multi) {
            this(name, (multi) ? null : shortName, description);
            this.value = value;
            this.multi = multi;
        }

        public String getName() {
            return name;
        }

        public String getShortName() {
            return shortName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isBailOut() {
            return bailOut;
        }

        public String[] getQualifiers() {
            return qualifiers;
        }

        public String getValue() {
            return value;
        }

        public boolean isMulti() {
            return multi;
        }

        public String getString() {
            StringBuffer s = new StringBuffer("-");
            s.append(name);
            if (value != null) {
                if (!isMulti()) s.append(" ");
                s.append("<");
                s.append(value);
                s.append(">");
            } else if (qualifiers != null) {
                s.append(":{");
                for (int i=0; i<qualifiers.length; i++) {
                    if (i > 0) {
                        s.append("|");
                    }
                    s.append(qualifiers[i]);
                }
                s.append("}");
            }
            return s.toString();
        }
    }
}
