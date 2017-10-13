package jas.util;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A PrintWriter that keeps track of an indentation level
 * and indents the output appropriately.
 * 
 * Warning: Only print and println methods taking strings have been overriden,
 * print, println methods taking other arguments may not be indented properly.
 */
public class IndentPrintWriter extends PrintWriter
{
	public IndentPrintWriter(Writer w)
	{
		super(w);
	}
	public void println(String s)
	{
		if (!indented) doIndent();
		super.println(s);
		indented = false;
	}
	public void print(String s)
	{
		if (!indented) doIndent();
		super.print(s);
	}
	public void println()
	{
		super.println();
		indented = false;
	}
	private void doIndent()
	{
		for (int i=0; i<indent; i++) super.print(indentString);
		indented = true;
	}
	/**
	 * Increase the indentation
	 */
	public void indent() { indent++; }
	/**
	 * Decrease the indentation
	 */
	public void outdent() { indent--; }
	/**
	 * Return the current indent count
	 */
	public int getIndent() { return indent; }
	/**
	 * Set the current indent count
	 */
	public void setIndent(int level) { indent = level; }
	/**
	 * Return the current indentString
	 * @see #setIndentString(String)
	 */
	public String getIndentString()
	{
		return indentString;
	}
	/**
	 * Set the current indentString. Default is a single tab per indent level.
	 * @param indentString The characters to prefix each line with (repeated for each indent level)
	 */
	public void setIndentString(String indentString)
	{
		this.indentString = indentString;
	}
	private int indent = 0;
	private boolean indented = false;
	private String indentString;
}
