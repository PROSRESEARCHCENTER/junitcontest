package hep.aida.ref.xml.binary;

public class AidaWBXMLConverter implements AidaWBXML {

	private AidaWBXMLConverter() {
	}

	public static boolean toBoolean(int tag, int att, String value) {
		return "true".equalsIgnoreCase(value);
	}

	public static double toDouble(int tag, int att, String value) {
		if (value.equalsIgnoreCase("nan"))
			value = "NaN";
		return Double.parseDouble(value);
	}
	
	public static float toFloat(int tag, int att, String value) {
		if (value.equalsIgnoreCase("nan"))
			value = "NaN";
		return Float.parseFloat(value);
	}
	
	public static int toInt(int tag, int att, String value) {
		switch (att) {
		/*
		 * case DIRECTION: if (value.equalsIgnoreCase("x")) return 0; if
		 * (value.equalsIgnoreCase("y")) return 1; if
		 * (value.equalsIgnoreCase("z")) return 2; throw new
		 * NumberFormatException("Illegal value for direction: " + value);
		 */
		case BIN_NUM:
		case BIN_NUM_X:
		case BIN_NUM_Y:
		case BIN_NUM_Z:
			if (value.equals("UNDERFLOW"))
				return -2;
			if (value.equals("OVERFLOW"))
				return -1;
			return Integer.parseInt(value);
		default:
			return Integer.parseInt(value);
		}
	}
}
