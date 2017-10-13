// Copyright FreeHEP, 2007
package org.freehep.wbxml;

/**
 * Tag Codes for Binary XML.
 * @author Mark Donszelmann
 * @version $Id$
 */
public interface WBXML {
	// Change the code page for the current token state. Followed by a single u_int8 indicating the new code page number.
	public final static int SWITCH_PAGE 	= 0x00;
	// Indicates the end of an attribute list or the end of an element.
	public final static int END				= 0x01;
	// A character entity. Followed by a mb_u_int32 encoding the character entity number.
	public final static int ENTITY 			= 0x02; 	
	// Inline string. Followed by a termstr.
	public final static int STR_I 			= 0x03; 	
	// An unknown tag or attribute name. Followed by an mb_u_int32 that encodes an offset into the string table.
	public final static int LITERAL 		= 0x04; 	
	// Inline string document-type-specific extension token. Token is followed by a termstr.
	public final static int EXT_I_0 		= 0x40; 	
	// Inline string document-type-specific extension token. Token is followed by a termstr.
	public final static int EXT_I_1 		= 0x41; 	
	// Inline string document-type-specific extension token. Token is followed by a termstr.
	public final static int EXT_I_2 		= 0x42; 	
	// Processing instruction.
	public final static int PI 				= 0x43; 	
	// Unknown tag, with content.
	public final static int LITERAL_C 		= 0x44; 	
	// Inline integer document-type-specific extension token. Token is followed by a mb_uint_32.
	public final static int EXT_T_0 		= 0x80; 	
	// Inline integer document-type-specific extension token. Token is followed by a mb_uint_32.
	public final static int EXT_T_1 		= 0x81; 	
	// Inline integer document-type-specific extension token. Token is followed by a mb_uint_32.
	public final static int EXT_T_2 		= 0x82; 	
	// String table reference. Followed by a mb_u_int32 encoding a byte offset from the beginning of the string table.
	public final static int STR_T 			= 0x83; 	
	// Unknown tag, with attributes.
	public final static int LITERAL_A 		= 0x84; 	
	// Single-byte document-type-specific extension token.
	public final static int EXT_0 			= 0xC0; 	
	// Single-byte document-type-specific extension token.
	public final static int EXT_1 			= 0xC1; 	
	// Single-byte document-type-specific extension token.
	public final static int EXT_2 			= 0xC2; 	
	// Opaque document-type-specific data.
	public final static int OPAQUE 			= 0xC3; 	
	// Unknown tag, with content and attributes.
	public final static int LITERAL_AC 		= 0xC4;	


    /**
     * WBXML version number
     */
    public static final int WBXML_VERSION = 0x03;
    /**
     * WBXML Constant for Unknown PID
     */
    public static final int UNKNOWN_PID = 0x01;
    /**
     * WBXML Constant for Indexed PID
     */
    public static final int INDEXED_PID = 0x00;
    /**
     * WBXML Constant for UTF 8
     */
    public static final int UTF8 = 0x6a;

    /**
     * Content Mask
     */
    public static final int CONTENT = 0x40;
    /**
     * Attribute Mask
     */
    public static final int ATTRIBUTE = 0x80;

    /**
     * Number of reserved tag codes
     */
    public static final int RESERVED_CODES = 5;
    
    /**
     * Maximum attribute or tag codes
     */
    public static final int MAX_CODES = CONTENT - RESERVED_CODES;
}
