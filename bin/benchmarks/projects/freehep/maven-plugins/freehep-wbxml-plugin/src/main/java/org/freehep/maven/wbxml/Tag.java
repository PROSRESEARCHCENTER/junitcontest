package org.freehep.maven.wbxml;

public class Tag {

	private int number;
	private String constant;
	private String name;
	private boolean empty;
	private String comment;
	
	public Tag(String[] word) {
		number = Integer.parseInt(word[0]);
		constant = word[1];
		name = word[2];
		empty = word[3].equalsIgnoreCase("true");
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

	public boolean isEmpty() {
		return empty;
	}
	
	public int getPage() {
		return number / WBXMLConstants.MAX_CODES;
	}

	public int getCode() {
		return number % WBXMLConstants.MAX_CODES + WBXMLConstants.RESERVED_CODES;
	}

	public String getComment() {
		return comment;
	}
}
