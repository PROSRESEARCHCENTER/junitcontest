package hep.aida.jni;

public class AIDAJNIUtil {

	private AIDAJNIUtil() {
	}

	public static String convert(Class clazz) {
		if (Integer.class.isAssignableFrom(clazz)) {
			return "int";
		} else if (Short.class.isAssignableFrom(clazz)) {
			return "short";
		}else if (Long.class.isAssignableFrom(clazz)) {
			return "long";
		}else if (Float.class.isAssignableFrom(clazz)) {
			return "float";
		}else if (Double.class.isAssignableFrom(clazz)) {
			return "double";
		}else if (Boolean.class.isAssignableFrom(clazz)) {
			return "boolean";
		}else if (Byte.class.isAssignableFrom(clazz)) {
			return "byte";
		}else if (Character.class.isAssignableFrom(clazz)) {
			return "char";
		}else if (String.class.isAssignableFrom(clazz)) {
			return "string";
		}else if (hep.aida.ITuple.class.isAssignableFrom(clazz)) {
			// FIXME check no package ?
			return "ITuple";
		}else {
			return "java.lang.Object";
		}
	}

	public static String[] convert(Class[] classes) {
		String[] result = new String[classes.length];
		for (int i=0; i<result.length; i++) {
			result[i] = convert(classes[i]);
		}
		return result;
	}

	public static Class convert(String type) {
		type = type.intern();
	    if (type == "int") return Integer.class;
	    if (type == "short") return Short.class;
	    if (type == "long") return Long.class;
	    if (type == "float") return Float.class;
	    if (type == "double") return Double.class;
	    if (type == "boolean") return Boolean.class;
	    if (type == "byte") return Byte.class;
	    if (type == "char") return Character.class;
	    if (type == "string") return String.class;
	    if (type == "java.lang.Object") return Object.class;
	    // FIXME check, also for package name
	    if (type == "ITuple") return hep.aida.ITuple.class;

	    System.err.println("AIDAJNIUtil.convert(type) could not find jclass for type: "+type);

	    return null;
	}
	
	public static Class[] convert(String[] types) {
		Class[] result = new Class[types.length];
		for (int i=0; i<result.length; i++) {
			result[i] = convert(types[i]);
		}
		return result;
	}
	
	public static long toPtr(String[] arg) {
		// FIXME
		System.err.println("WARNING toPtr(String[]) called");
		return 0;
	}
	
	public static String[] toStringArray(long ptr) {
		// FIXME
		System.err.println("WARNING toStringArray(long) called");
		return null;
	}
	
	public static long toPtr(hep.aida.ITuple[] arg) {
		// FIXME
		System.err.println("WARNING toPtr(hep.aida.ITuple[]) called");
		return 0;
	}
	
	public static hep.aida.ITuple[] toITupleArray(long ptr) {
		// FIXME
		System.err.println("WARNING toITupleArray(long) called");
		return null;
	}

	public static long toPtr(hep.aida.IEvaluator[] arg) {
		// FIXME
		System.err.println("WARNING toPtr(hep.aida.IEvaluator[]) called");
		return 0;
	}
	
	public static hep.aida.IEvaluator[] toIEvaluatorArray(long ptr) {
		// FIXME
		System.err.println("WARNING toIEvaluatorArray(long) called");
		return null;
	}

	public static Class[] toClassArray(long ptr) {
		// FIXME
		System.err.println("WARNING toClassArray(long) called");
		return null;
	}

	public static long toPtr(Class[] arg) {
		// FIXME
		System.err.println("WARNING toPtr(Class[]) called");
		return 0;
	}
}
