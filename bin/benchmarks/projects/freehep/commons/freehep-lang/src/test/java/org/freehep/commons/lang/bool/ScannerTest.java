
package org.freehep.commons.lang.bool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java_cup.runtime.Symbol;
import junit.framework.TestCase;

/**
 *
 * @author Brian Van Klaveren<bvan@slac.stanford.edu>
 */
public class ScannerTest extends TestCase {
    
    public String[] disallowed = {
        "!=",
        "!matches",
        "^matches",
        "!in",
    };
    
    
    public void testTokens() throws IOException{
        for(int i = 0; i < disallowed.length; i++){
            String test = disallowed[i];
            InputStream stream = new ByteArrayInputStream( test.getBytes() );
            Lexer scanner = new Lexer( stream );
            Symbol s;
            while((s = scanner.next_token()) != null){
                System.out.println( s.value.toString() );
            }
        }
    }

}
