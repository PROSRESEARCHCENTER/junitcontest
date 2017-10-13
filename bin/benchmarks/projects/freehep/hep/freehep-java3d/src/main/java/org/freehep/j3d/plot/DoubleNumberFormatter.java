package org.freehep.j3d.plot;

import java.text.NumberFormat;

/** Format a double number.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: DoubleNumberFormatter.java 8584 2006-08-10 23:06:37Z duns $
 */
final class DoubleNumberFormatter
{
   DoubleNumberFormatter(int power)
   {
	   if (formatter == null)
		   formatter = NumberFormat.getInstance();
	   this.power = power;
   }
   void setFractionDigits(int fractDigits)
   {
	   formatter.setMinimumFractionDigits(fractDigits);
	   formatter.setMaximumFractionDigits(fractDigits);
   }
   String format(final double d)
   {
	   return formatter.format(power != 0 ? d / Math.pow(10.0, power) : d);
   }
   private static NumberFormat formatter = null;
   private int power;
}

