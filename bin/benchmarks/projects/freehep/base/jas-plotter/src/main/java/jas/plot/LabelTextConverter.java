package jas.plot;
import java.util.Hashtable;
final class LabelTextConverter
{
	private void init()
	{
		hash = new Hashtable();

		// Greek
		hash.put("Alpha",          new Character((char) 0x0391));
		hash.put("Beta",           new Character((char) 0x0392));
		hash.put("Gamma",          new Character((char) 0x0393));
		hash.put("Delta",          new Character((char) 0x0394));
		hash.put("Epsilon",        new Character((char) 0x0395));
		hash.put("Zeta",           new Character((char) 0x0396));
		hash.put("Eta",            new Character((char) 0x0397));
		hash.put("Theta",          new Character((char) 0x0398));
		hash.put("Iota",           new Character((char) 0x0399));
		hash.put("Kappa",          new Character((char) 0x039a));
		hash.put("Lambda",         new Character((char) 0x039b));
		hash.put("Mu",             new Character((char) 0x039c));
		hash.put("Nu",             new Character((char) 0x039d));
		hash.put("Xi",             new Character((char) 0x039e));
		hash.put("Omicron",        new Character((char) 0x039f));
		hash.put("Pi",             new Character((char) 0x03a0));
		hash.put("Rho",            new Character((char) 0x03a1));
		hash.put("Sigma",          new Character((char) 0x03a3));
		hash.put("Tau",            new Character((char) 0x03a4));
		hash.put("Upsilon",        new Character((char) 0x03a5));
		hash.put("Phi",            new Character((char) 0x03a6));
		hash.put("Chi",            new Character((char) 0x03a7));
		hash.put("Psi",            new Character((char) 0x03a8));
		hash.put("Omega",          new Character((char) 0x03a9));
		hash.put("a",              new Character((char) 0x03b1));
		hash.put("alpha",          new Character((char) 0x03b1));
		hash.put("b",              new Character((char) 0x03b2));
		hash.put("beta",           new Character((char) 0x03b2));
		hash.put("g",              new Character((char) 0x03b3));
		hash.put("gamma",          new Character((char) 0x03b3));
		hash.put("d",              new Character((char) 0x03b4));
		hash.put("delta",          new Character((char) 0x03b4));
		hash.put("epsilon",        new Character((char) 0x03b5));
		hash.put("zeta",           new Character((char) 0x03b6));
		hash.put("eta",            new Character((char) 0x03b7));
		hash.put("theta",          new Character((char) 0x03b8));
		hash.put("iota",           new Character((char) 0x03b9));
		hash.put("kappa",          new Character((char) 0x03ba));
		hash.put("lambda",         new Character((char) 0x03bb));
		hash.put("mu",             new Character((char) 0x03bc));
		hash.put("nu",             new Character((char) 0x03bd));
		hash.put("xi",             new Character((char) 0x03be));
		hash.put("omicron",        new Character((char) 0x03bf));
		hash.put("p",              new Character((char) 0x03c0));
		hash.put("pi",             new Character((char) 0x03c0));
		hash.put("rho",            new Character((char) 0x03c1));
		hash.put("sigmaf",         new Character((char) 0x03c2));
		hash.put("sigma",          new Character((char) 0x03c3));
		hash.put("tau",            new Character((char) 0x03c4));
		hash.put("upsilon",        new Character((char) 0x03c5));
		hash.put("phi",            new Character((char) 0x03c6));
		hash.put("chi",            new Character((char) 0x03c7));
		hash.put("psi",            new Character((char) 0x03c8));
		hash.put("omega",          new Character((char) 0x03c9));
		hash.put("thetasym",       new Character((char) 0x03d1));
		hash.put("upsih",          new Character((char) 0x03d2));
		hash.put("piv",            new Character((char) 0x03d6));

		// misc
		hash.put("amp",            new Character((char) 38));
		hash.put("iexcl",          new Character((char) 161));
		hash.put("cent",           new Character((char) 162));
		hash.put("pound",          new Character((char) 163));
		hash.put("curren",         new Character((char) 164));
		hash.put("yen",            new Character((char) 165));
		hash.put("copy",           new Character((char) 169));
		hash.put("ordf",           new Character((char) 170));
		hash.put("not",            new Character((char) 172));
		hash.put("reg",            new Character((char) 174));
		hash.put("ordm",           new Character((char) 186));
		hash.put("iquest",         new Character((char) 191));

		// math
		hash.put("plusmn",         new Character((char) 177));
		hash.put("sup2",           new Character((char) 178));
		hash.put("sup3",           new Character((char) 179));
		hash.put("micro",          new Character((char) 181));
		hash.put("middot",         new Character((char) 183));
		hash.put("sup1",           new Character((char) 185));
		hash.put("frac14",         new Character((char) 188));
		hash.put("frac12",         new Character((char) 189));
		hash.put("frac34",         new Character((char) 190));
		hash.put("times",          new Character((char) 215));
		hash.put("Oslash",         new Character((char) 216));
		hash.put("divide",         new Character((char) 247));
		hash.put("oslash",         new Character((char) 248));
//		hash.put("prime",          new Character((char) 0x2032));
//		hash.put("Prime",          new Character((char) 0x2033));
//		hash.put("frasl",          new Character((char) 0x2044));
//		hash.put("real",           new Character((char) 0x211c));

		// foreign
		hash.put("Agrave",         new Character((char) 192));
		hash.put("Aacute",         new Character((char) 193));
		hash.put("Acirc",          new Character((char) 194));
		hash.put("Atilde",         new Character((char) 195));
		hash.put("Auml",           new Character((char) 196));
		hash.put("Aring",          new Character((char) 197));
		hash.put("AElig",          new Character((char) 198));
		hash.put("Ccedil",         new Character((char) 199));
		hash.put("Egrave",         new Character((char) 200));
		hash.put("Eacute",         new Character((char) 201));
		hash.put("Ecirc",          new Character((char) 202));
		hash.put("Euml",           new Character((char) 203));
		hash.put("Igrave",         new Character((char) 204));
		hash.put("Iacute",         new Character((char) 205));
		hash.put("Icirc",          new Character((char) 206));
		hash.put("Iuml",           new Character((char) 207));
		hash.put("ETH",            new Character((char) 208));
		hash.put("Ntilde",         new Character((char) 209));
		hash.put("Ograve",         new Character((char) 210));
		hash.put("Oacute",         new Character((char) 211));
		hash.put("Ocirc",          new Character((char) 212));
		hash.put("Otilde",         new Character((char) 213));
		hash.put("Ouml",           new Character((char) 214));
		hash.put("Ugrave",         new Character((char) 217));
		hash.put("Uacute",         new Character((char) 218));
		hash.put("Ucirc",          new Character((char) 219));
		hash.put("Uuml",           new Character((char) 220));
		hash.put("Yacute",         new Character((char) 221));
		hash.put("THORN",          new Character((char) 222));
		hash.put("szlig",          new Character((char) 223));
		hash.put("agrave",         new Character((char) 224));
		hash.put("aacute",         new Character((char) 225));
		hash.put("acirc",          new Character((char) 226));
		hash.put("atilde",         new Character((char) 227));
		hash.put("auml",           new Character((char) 228));
		hash.put("aring",          new Character((char) 229));
		hash.put("aelig",          new Character((char) 230));
		hash.put("ccedil",         new Character((char) 231));
		hash.put("egrave",         new Character((char) 232));
		hash.put("eacute",         new Character((char) 233));
		hash.put("ecirc",          new Character((char) 234));
		hash.put("euml",           new Character((char) 235));
		hash.put("igrave",         new Character((char) 236));
		hash.put("iacute",         new Character((char) 237));
		hash.put("icirc",          new Character((char) 238));
		hash.put("iuml",           new Character((char) 239));
		hash.put("eth",            new Character((char) 240));
		hash.put("ntilde",         new Character((char) 241));
		hash.put("ograve",         new Character((char) 242));
		hash.put("oacute",         new Character((char) 243));
		hash.put("ocirc",          new Character((char) 244));
		hash.put("otilde",         new Character((char) 245));
		hash.put("ouml",           new Character((char) 246));
		hash.put("ugrave",         new Character((char) 249));
		hash.put("uacute",         new Character((char) 250));
		hash.put("ucirc",          new Character((char) 251));
		hash.put("uuml",           new Character((char) 252));
		hash.put("yacute",         new Character((char) 253));
		hash.put("thorn",          new Character((char) 254));
		hash.put("yuml",           new Character((char) 255));
	}
	String convert(final String s)
	{
		int amp_index = s.indexOf('&');
		if (amp_index != -1)
		{
			int semic_index = s.indexOf(';', amp_index + 1);
			int next_amp_index = s.indexOf('&', amp_index + 1);
			if (semic_index != -1)
			{
				final StringBuffer buffer = new StringBuffer(s.substring(0, amp_index));
				if (next_amp_index != -1 && next_amp_index < semic_index)
				{
					buffer.append(s.substring(amp_index, next_amp_index));
					amp_index = next_amp_index;
				}
				String tag = s.substring(amp_index + 1, semic_index);
				while (true)
				{
					if (tag.startsWith("#"))
					{
						try
						{
							buffer.append((char) Integer.parseInt(tag.substring(1)));
						}
						catch (final NumberFormatException e)
						{
							buffer.append('&');
							buffer.append(tag);
							buffer.append(';');
						}
					}
					else
					{
						if (hash == null) init();
						final Character unicode = (Character) hash.get(tag);
						if (unicode != null)
						{
							buffer.append(unicode.charValue());
						}
						else
						{
							buffer.append('&');
							buffer.append(tag);
							buffer.append(';');
						}
					}
					amp_index = s.indexOf('&', ++semic_index);
					if (amp_index != -1)
					{
						final int old_semic_index = semic_index;
						semic_index = s.indexOf(';', amp_index + 1);
						next_amp_index = s.indexOf('&', amp_index + 1);
						if (semic_index != -1)
						{
							if (next_amp_index != -1 && next_amp_index < semic_index)
							{
								amp_index = next_amp_index;
							}
							tag = s.substring(amp_index + 1, semic_index);
							buffer.append(s.substring(old_semic_index, amp_index));
						}
						else
						{
							buffer.append(s.substring(old_semic_index));
							break;
						}
					}
					else
					{
						buffer.append(s.substring(semic_index));
						break;
					}
				}
				return buffer.toString();
			}
		}
		return s;
	}
	private Hashtable hash;
}
