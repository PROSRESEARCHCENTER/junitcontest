package jas.util;
/**
 * This class is a utility provided for generating arbitrary names.  It
 * follows the pattern A, B, C, D, ... , Z, AA, BB, CC, DD, ... , ZZ, AAA, ...
 * <p>
 * It is used by some local data interface modules when field names are
 * not specified, and it is used to assign names to the coefficents of
 * polynomial functions.
 *
 *  @author Jonas Gifford
 */
public class ArbitraryNameGenerator
{
	/**
	 * Fills a String array with names.
	 *  @param s the array to fill
	 */
	public void fillArray(String[] s)
	{
		char c = 'A';

		if (s.length < 26) // the simple and predominant case
			for (int i = 0; i < s.length; i++)
				s[i] = String.valueOf(c++);
		else // the highly unlikely case that there
			 // are more than 26 items
		{
			int nLetters = 1;
			for (int i = 0; i < s.length; i++)
			{
				s[i] = "";
				for (int j = 0; j < nLetters; j++)
					s[i] += (c);
				if (c == 'Z')
				{
					c = 'A';
					nLetters++;
				}
				else
					c++;
			}
		}
	}
}
