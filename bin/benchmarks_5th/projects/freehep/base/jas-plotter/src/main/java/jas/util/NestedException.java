package jas.util;
public class NestedException extends Exception implements HasNestedException 
{
    private Throwable detail;

    /**
     * Create a remote exception
     */
    public NestedException() {}

    /**
     * Create a remote exception with the specified string
     */
    public NestedException(String s) {
	super(s);
    }

    /**
     * Create a remote exception with the specified string, and the
     * exception specified.
     */
    public NestedException(String s, Throwable ex) {
	super(s);
	detail = ex;
    }

	public Throwable getNestedException()
	{
		return detail;
	}
    /**
     * Produce the message, include the message from the nested
     * exception if there is one.
     */
    public String getMessage() 
	{
		return formatNestedException(this);
    }
	/**
	 * Return just the super classes message
	 */
	public String getSimpleMessage()
	{
		return super.getMessage();
	}
	public static String formatNestedException(HasNestedException t)
	{
		Throwable nest = t.getNestedException();
		if (nest == null) return t.getSimpleMessage();
		else return t.getSimpleMessage() + 
			"; nested exception is: \n\t" +
			nest.toString();
	}
    public void printStackTrace()
	{
		super.printStackTrace();
		if (detail != null)
		{
			System.err.println("Nested Exception is:");
			detail.printStackTrace();
		}
	}
    public void printStackTrace(java.io.PrintStream s)
	{
		super.printStackTrace(s);
		if (detail != null)
		{
			s.println("Nested Exception is:");
			detail.printStackTrace(s);
		}
	}
    public void printStackTrace(java.io.PrintWriter s)
	{
		super.printStackTrace(s);
		if (detail != null)
		{
			s.println("Nested Exception is:");
			detail.printStackTrace(s);
		}
	}
}
