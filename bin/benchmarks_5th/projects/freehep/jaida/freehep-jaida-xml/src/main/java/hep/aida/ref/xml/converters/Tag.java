package hep.aida.ref.xml.converters;

import org.freehep.wbxml.Attributes;
import org.freehep.wbxml.AttributesImpl;

class Tag {
	private int tagID;
	private Attributes atts;
	private boolean empty;
	
	Tag(int tagID, Attributes atts, boolean empty) {
		this.tagID = tagID;
		this.atts = new AttributesImpl(atts);
		this.empty = empty;
	}
	
	int getTagID() {
		return tagID;
	}
	
	Attributes getAttributes() {
		return atts;
	}
	
	boolean isEmpty() {
		return empty;
	}
}
