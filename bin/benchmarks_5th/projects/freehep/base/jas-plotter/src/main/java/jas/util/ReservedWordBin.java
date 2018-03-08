package jas.util;
import java.util.Hashtable;
public class ReservedWordBin
{
	private void buildHash()
	{
		m_hash = new Hashtable(50, 1.0f); // 50 reserved words
		// this is an arbitrary object (it won't let me pass it null)
		m_hash.put("true", this);		m_hash.put("false", this);
		m_hash.put("null", this);		m_hash.put("abstract", this);
		m_hash.put("boolean", this);	m_hash.put("break", this);
		m_hash.put("byte", this);		m_hash.put("case", this);
		m_hash.put("catch", this);		m_hash.put("char", this);
		m_hash.put("class", this);		m_hash.put("const", this);
		m_hash.put("continue", this);	m_hash.put("default", this);
		m_hash.put("do", this);			m_hash.put("double", this);
		m_hash.put("else", this);		m_hash.put("extends", this);
		m_hash.put("final", this);		m_hash.put("finally", this);
		m_hash.put("float", this);		m_hash.put("for", this);
		m_hash.put("goto", this);		m_hash.put("if", this);
		m_hash.put("implements", this);	m_hash.put("import", this);
		m_hash.put("instanceof", this);	m_hash.put("int", this);
		m_hash.put("interface", this);	m_hash.put("long", this);
		m_hash.put("native", this);		m_hash.put("new", this);
		m_hash.put("package", this);	m_hash.put("private", this);
		m_hash.put("protected", this);	m_hash.put("public", this);
		m_hash.put("return", this);		m_hash.put("short", this);
		m_hash.put("static", this);		m_hash.put("super", this);
		m_hash.put("switch", this);		m_hash.put("synchronized", this);
		m_hash.put("this", this);		m_hash.put("throw", this);
		m_hash.put("throws", this);		m_hash.put("transient", this);
		m_hash.put("try", this);		m_hash.put("void", this);
		m_hash.put("volatile", this);	m_hash.put("while", this);
	}
	public boolean isReservedWord(String s)
	{
		if (m_hash == null) buildHash();
		return m_hash.containsKey(s);
	}
	public void dispose() // promotes garbage collection
	{
		m_hash.clear();
		m_hash = null;
	}
	private Hashtable m_hash;
}
