package org.freehep.maven.wbxml;

public class Attribute {

	private int number;
	private String constant;
	private String name;
	private String type;
	private String comment;
	
	public Attribute(String[] word) {
		number = Integer.parseInt(word[0]);
		constant = word[1];
		name = word[2];
		type = word[3];
		comment = word.length > 4 ? word[4] : "";
	}

	public int getNumber() {
		return number;
	}

	public String getConstant() {
		return constant;
	}

	public String getName() {
		return name;
	}

	public int getPage() {
		return number / WBXMLConstants.MAX_CODES;
	}

	public int getCode() {
		return number % WBXMLConstants.MAX_CODES + WBXMLConstants.RESERVED_CODES;
	}

	public String getType() {
		return type;
	}
	
	public String getComment() {
		return comment;
	}

}
