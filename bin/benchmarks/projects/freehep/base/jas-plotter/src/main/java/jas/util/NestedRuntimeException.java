package jas.util;
public class NestedRuntimeException extends RuntimeException implements HasNestedException
{
    private Throwable detail;

    /**
     * Create a nested exception with the specified string
     */
    
	public NestedRuntimeException(Throwable ex)
	{
		super();
		detail = ex;
    }

    /**
     * Create a remote exception with the specified string, and the
     * exception specified.
     */
    public NestedRuntimeException(String s, Throwable ex) 
	{
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
		return NestedException.formatNestedException(this);
    }
	/**
	 * Return just the super classes message
	 */
	public String getSimpleMessage()
	{
		return super.getMessage();
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
