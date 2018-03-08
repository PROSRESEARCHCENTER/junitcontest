#ifndef WBXML_INCLUDE
#define WBXML_INCLUDE 1

namespace WBXML {

class WBXML {

public: 
	enum Codes {
		// Change the code page for the current token state. Followed by a single u_int8 indicating the new code page number.
		SWITCH_PAGE 	= 0x00,
		// Indicates the end of an attribute list or the end of an element.
		END				= 0x01,
		// A character entity. Followed by a mb_u_int32 encoding the character entity number.
		ENTITY 			= 0x02, 	
		// Inline string. Followed by a termstr.
		STR_I 			= 0x03, 	
		// An unknown tag or attribute name. Followed by an mb_u_int32 that encodes an offset into the string table.
		LITERAL 		= 0x04, 	
		// Inline string document-type-specific extension token. Token is followed by a termstr.
		EXT_I_0 		= 0x40, 	
		// Inline string document-type-specific extension token. Token is followed by a termstr.
		EXT_I_1 		= 0x41, 	
		// Inline string document-type-specific extension token. Token is followed by a termstr.
		EXT_I_2 		= 0x42, 	
		// Processing instruction.
		PI 				= 0x43, 	
		// Unknown tag, with content.
		LITERAL_C 		= 0x44, 	
		// Inline integer document-type-specific extension token. Token is followed by a mb_uint_32.
		EXT_T_0 		= 0x80, 	
		// Inline integer document-type-specific extension token. Token is followed by a mb_uint_32.
		EXT_T_1 		= 0x81, 	
		// Inline integer document-type-specific extension token. Token is followed by a mb_uint_32.
		EXT_T_2 		= 0x82, 	
		// String table reference. Followed by a mb_u_int32 encoding a byte offset from the beginning of the string table.
		STR_T 			= 0x83, 	
		// Unknown tag, with attributes.
		LITERAL_A 		= 0x84, 	
		// Single-byte document-type-specific extension token.
		EXT_0 			= 0xC0, 	
		// Single-byte document-type-specific extension token.
		EXT_1 			= 0xC1, 	
		// Single-byte document-type-specific extension token.
		EXT_2 			= 0xC2, 	
		// Opaque document-type-specific data.
		OPAQUE 			= 0xC3, 	
		// Unknown tag, with content and attributes.
		LITERAL_AC 		= 0xC4
	};


    /**
     * WBXML version number
     */
	#define WBXML_VERSION 0x03
    /**
     * WBXML Constant for Unknown PID
     */
	#define WBXML_UNKNOWN_PID 0x01
    /**
     * WBXML Constant for Indexed PID
     */
	#define WBXML_INDEXED_PID 0x00	
    /**
     * WBXML Constant for UTF 8
     */
	#define WBXML_UTF8 0x6a

    /**
     * Content Mask
     */
	#define WBXML_CONTENT 0x40
    /**
     * Attribute Mask
     */
	#define WBXML_ATTRIBUTE 0x80

    /**
     * Number of reserved tag codes
     */
	#define WBXML_RESERVED_CODES 5
    
    /**
     * Maximum attribute or tag codes
     */
	#define WBXML_MAX_CODES (WBXML_CONTENT - WBXML_RESERVED_CODES)
};

}

#endif
