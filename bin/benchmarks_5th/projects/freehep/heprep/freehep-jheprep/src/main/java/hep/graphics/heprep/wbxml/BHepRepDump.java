// Copyright 2005, FreeHEP.
package hep.graphics.heprep.wbxml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Mark Donszelmann
 * @version $Id: BHepRepDump.java 8584 2006-08-10 23:06:37Z duns $
 */
public class BHepRepDump extends BHepRepParser {
 
    private long offset;
    private int bytes;
 
    /**
     * Create a BHepRep Dumper
     */
    public BHepRepDump() {
    }

    protected int readVersion() throws IOException {
        int version = super.readByte();
        print("VERSION "+version);
        return version;
    }
    
    protected int readPublicIdentifierId() throws IOException {
        int id = super.readPublicIdentifierId();

        if (id == 0) {
            print("PUBIDREF");
        } else {
            print("PUBID "+id);
        }
        
        return id;
    }

    protected int readCharSet() throws IOException {
        int charSet = super.readCharSet();
        print("CHARSET "+charSet);
        return charSet;
    }

    protected String readStringTable() throws IOException {
        String table = super.readStringTable();
        print("STRTABLE ("+table.length()+") "+table);        
        return table;
    }
    
	protected void selectPage(int nr, boolean tags) throws XmlPullParserException{
        super.selectPage(nr, tags);
        print("SWITCHPAGE "+nr);
    }

    protected void parseElement(int id)
        throws IOException, XmlPullParserException {

		int tagId = id & 0x03f;
        String name = "TAG";
        if ((id & 0xC0) != 0) name += "_";
        if ((id & 0x80) != 0) name += "A";
        if ((id & 0x40) != 0) name += "C";
        name += " ("+tagId+") ";
        name += resolveId("TAG", tagTable, tagId);
        print(name);    
        super.parseElement(id);
    }

    protected String resolveId(String type, String[] tab, int id) throws IOException {
        String s = super.resolveId(type, tab, id);
        if (type.equals("TAG")) return s;
        
        print(type+" "+s);   
        return s;
    }

    protected void endAttributes() throws IOException, XmlPullParserException {
        print("ATTREND (01)");
    }

    protected void endTag(Tag tag) throws IOException, XmlPullParserException {
        print("TAGEND (01) "+tag.name);
    }

    protected void processInstruction() throws IOException, XmlPullParserException  {
        print("PI");
        super.processInstruction();
    }
    
    protected Object parseExtension(int id, int tagId, int attId) throws IOException, XmlPullParserException {
        Object obj = super.parseExtension(id, tagId, attId);
        print(getWapExtension(id)+" "+(obj != null ? obj.toString() : ""));        
        return obj;
    }

    protected Object parseOpaque(int len, int tagId, int attId) 
        throws IOException, XmlPullParserException {
        print("OPAQUE len="+len);
        return super.parseOpaque(len, tagId, attId);
    }
    
    protected String readStrI() throws IOException {
        String s = super.readStrI();
//        print("STR_I "+s);
        return s;
    }

    protected float readFloat() throws IOException {
        float f = super.readFloat();
        print("float "+f);        
        return f;
    }

    protected double readDouble() throws IOException {
        double d = super.readDouble();
        print("double "+d);        
        return d;
    }

    protected int readInt32() throws IOException {
        int i = super.readInt32();
        print("int32 "+i);        
        return i;
    }

    protected long readInt64() throws IOException {
        long l = super.readInt64();
        print("int64 "+l);        
        return l;
    }

    private String hex(long i, int w) {
        String hex = "00000000"+Long.toHexString(i);
        return hex.substring(hex.length()-w, hex.length());
    }

    private void print(String s) {        
        if (s != null) {
            while (bytes < 8) {
                System.out.print("   ");
                bytes++;
            }
            System.out.print(" ; ");
            System.out.print(s);
        }
        System.out.println();
        bytes = 0;
        
        System.out.print("0x");
        System.out.print(hex(offset, 8));
        System.out.print(":");
    }
 
    private void dump(final InputStream in) throws IOException, XmlPullParserException {
        InputStream dumpIn = new InputStream() {
            public int read() throws IOException {
                int b = in.read();
                if (b < 0) return b;
                
                offset++;

                if (bytes >= 8) print(null);
                System.out.print(" ");
                System.out.print(hex(b, 2));
                bytes++;
                return b;
            }
        };
        
        offset = 0;
        bytes = 0;
        print(null);

		setInput(dumpIn, null);

        int eventType = getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            while (eventType != XmlPullParser.END_DOCUMENT) {            
                try {
                    eventType = next();
                } catch (Exception e) {
                    System.out.println(getPositionDescription());
                }
            }
            print("**************");
            print("End of BHepRep");
            print("**************");
            eventType = next();
        }
    }
 
	/**
     * Dumps an binary heprep file
	 * @param args see usage
	 * @throws IOException in case of an I/O error
	 * @throws XmlPullParserException if the file cannot be parsed
	 * @throws FileNotFoundException if the file is not found
	 */
	public static void main(String[] args) throws IOException, XmlPullParserException, FileNotFoundException {
        if (args.length != 1) {
            System.err.println("Usage: BHepRepDump binaryheprepfile");
            System.exit(1);
        }
		BHepRepDump p = new BHepRepDump();
		InputStream in = new FileInputStream(args[0]);
		if (args[0].endsWith(".gz")) {
		    in = new GZIPInputStream(in);
		}
	    p.dump(in);
	}
}