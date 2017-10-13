package hep.aida.ref.function;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
abstract class CodeletUtils {
    
    private static String EMPTY_STRING = "";
    
    protected static String modelFromCodelet(String codeletString) {
        int index = codeletString.indexOf(":", FunctionCatalog.prefix.length());
        if ( index < 0 )
            return EMPTY_STRING;
	return codeletString.substring(FunctionCatalog.prefix.length(), index).trim();
    }
    
    protected static String locationFromCodelet(String codeletString) {
        int index = codeletString.indexOf(":", FunctionCatalog.prefix.length());
        if ( index < 0 )
            return EMPTY_STRING;
	return codeletString.substring(index+1);
    }
    
    protected static boolean isCodeletFromCatalog(String codeletString) {
	return (locationFromCodelet(codeletString).trim().toLowerCase().startsWith("catalog"));
    }

    protected static boolean isCodeletFromScript(String codeletString) {
	return (locationFromCodelet(codeletString).trim().toLowerCase().startsWith("verbatim:jel"));
    }
    
    protected static boolean isCodeletFromFile(String codeletString) {
	return (locationFromCodelet(codeletString).trim().toLowerCase().startsWith("file"));
    }
    
    protected static boolean isCodeletFromClass(String codeletString) {
	return (locationFromCodelet(codeletString).trim().toLowerCase().startsWith("classpath"));
    }
    
    public static String[] stringToArray(String stringList) {
        String[] result = null;
        if ( stringList == null || stringList.equals("")) return result;
        
        StringTokenizer tokenizer = new StringTokenizer(stringList, ",");
        ArrayList list = new ArrayList(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token != null && !token.equals("")) list.add(token);
        }
        result = new String[list.size()];
        list.toArray(result);
        
        return result;
    }
}
