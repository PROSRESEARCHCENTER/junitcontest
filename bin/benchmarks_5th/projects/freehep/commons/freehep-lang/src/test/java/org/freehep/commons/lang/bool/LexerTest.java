
package org.freehep.commons.lang.bool;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java_cup.runtime.Symbol;
import junit.framework.TestCase;
import org.freehep.commons.lang.AST;

/**
 *
 * @author bvan
 */
public class LexerTest extends TestCase{
    
    static String[] allowed = {
        "IN 42,34",
        "in 'hello' -> 'world'  ",
        "in 'hello'->'world'  ",
        "in 'hello' to 'world'  ",
        "in 'hello'to'world'  ",
        "in 'hello' : 'world'  ",
        "in ('hello' -> 'world')  ",
        "in ('hello' to 'world')  ",
        "in ('hello' : 'world') ",
        "in\n('hello' : 'world') ",
        "in\n('hello' : 'world')\n",
        "in\t('hello' : 'world')\n",
        "in 'hello' , 'world'  ",
        "in 'hello','world'  ",
        "in 'hello' , 'world'  ",
        "in ('hello' , 'world')  ",
        "in ('hello' , 'world') ",
        "in\n('hello' , 'world') ",
        "in\n('hello' , 'world')\n",
        "in\t('hello' , 'world')\n",
        "in 1  ",
        "in (1) ",
        "in 1,2  ",
        "in (1,2) ",
        "in 1:2  ",
        "in 1:.3  ",
        "in 1 -> 0.4  ",
        "in 1.->.5  ",
        "in 1 to 0.6  ",
        "in 1to 987  ",
        "in 1.to 789  ",
        "in 1.to .890  ",
        "in (1:9) ",
        "in (1:.10) ",
        "in (1->1.1) ",
        "in (1->.2) ",
        "in (1to 1.3) ",
        "in (1to .4) ",
        "in 1,2,3  ",
        "in (1,2,3) ",
        "in 1,2,3,4  ",
        "in (1,2,3,4) ",
        "in 1  ",
        "in ( 1 ) ",
        "in 1, 2  ",
        "in ( 1 , 2 ) ",
        "in  1 , 2 , 3  ",
        "in ( 1 , 2 , 3 ) ",
        "in 1 , 2 , 3 , 4  ",
        "in  ( 1  , 2 , 3  , 4 ) ",
        "in 'hello'  ",
        "in ('hello') ",
        "in 'hello', 'world'  ",
        "in ('hello','world') ",
        "in 'hello','world','hi'  ",
        "in ('hello','world','hi') ",
        "in 'hello','world','hi','how are you'  ",
        "in ('hello','world','hi','how are you') ",
        "in 'hello':'world'  ",
        "not in 'hello'->'world' ",
        "not in 'hello'to'world' ",
        "ne true",
        "neq TRUE",
        "ne false",
        "neq FALSE",
        "!eq 1",
        "!equal 'hello'",
        "!equals 'world'",
        "is null",
        "is None",
        "eq None",
        "= None",
        "== None",
        "is not d'2014-01-01T12:00'",
        "is not null ",
        "IS NOT NULL ",
        "is not none ",
        "IS NOT NONE ",
        "IS NOT None ",
        "!= null ",
        "neq NULL ",
        "not equal none ",
        "not equals NONE ",
        "!= None ",
        "is not false ",
        "IS NOT FALSE ",
        "in 42:.34  ",
        "in d'2014-01-01T12:00' -> d'2014-02-01T12:00' ",
        "in d\"2014-01-01T12:00\" -> d\"2014-02-01T12:00\" ",
        "in d'2014-01-01T12:00' : d'2014-02-01T12:00' ",
        "in d\"2014-01-01T12:00\" : d\"2014-02-01T12:00\" ",
        "in d'2014-01-01T12:00' to d'2014-02-01T12:00' ",
        "in d\"2014-01-01T12:00\" to d\"2014-02-01T12:00\" ",
        "=~ 'something*' "
    };
    
    static String[] disallowed = {
        "^= 'hello' ",
        "eq 'hello'->'world' ",
        "xin 'hello'->'world' ",
        "!in 'hello'->'world' ",
        "!!in 'hello'->'world' ",
        "nequal 1",
        "nequals 'hello'",
        "in 'hello', ",
        "in ('hello',) ",
        "in ('hello',null) ",
        "in 'hello',null  ",
        "in ('hello',1) ",
        "in 'hello',1  ",
        "in ('hello',d'2014-01-01') ",
        "in 'hello',d'2014-01-01'  ",
        "in (4,'world','hi','how are you') ",
    };
    
    public void testAllowed() throws Exception{
        for(int i = 0; i < allowed.length; i++){
            String test = String.format( "a%d %s", i, allowed[i]);
            InputStream stream = new ByteArrayInputStream( test.getBytes() );
            Lexer scanner = new Lexer( stream );
            Parser p = new Parser( scanner );
            AST ast;
            if((ast = (AST) p.parse().value) != null){
                System.out.println( ast.getRoot().toString() );
            }
        }
    }
    
    public void testDisallowed() throws Exception{
        for(int i = 0; i < disallowed.length; i++){
            String test = String.format( "a%d %s", i, disallowed[i]);
            InputStream stream = new ByteArrayInputStream( test.getBytes() );
            Lexer scanner = new Lexer( stream );
            Parser p = new Parser( scanner );
            AST ast; 
            try {
                if((ast = (AST) p.parse().value) != null){
                    System.out.println( ast.getRoot().toString() );
                    System.out.println(ast.getRoot().getLeft().getValue().toString());
                    System.out.println(ast.getRoot().getValue());
                    System.out.println(ast.getRoot().getRight().getValue().toString());
                }
                fail("Disallowed success: '" + test + "'");
            } catch (Exception e){ }
            
        }
    }

}
