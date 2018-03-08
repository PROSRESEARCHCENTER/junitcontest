#ifndef WBXML_DEFAULT_WRITER_INCLUDE
#define WBXML_DEFAULT_WRITER_INCLUDE 1

#include <iostream>
#include <ostream>
#include <map>
#include <vector>

#include <WBXML/IWriter.h>

namespace WBXML {

class DefaultWriter : public virtual IWriter {

private:
        struct primitive {
                union {
                        double d;
                        float f;
                        int64 l;
                        int i;
                        short s;
                        unsigned short us;
                        wchar_t ch;
                        char c[sizeof(double)];
                };
        };

	std::ostream& os;
	std::wstring dtd;
	std::wstring version;
	bool writtenHeader;
	bool lowEndian;

	// tag attributes
	bool hasAttributes;
	std::map<unsigned int, int> attributeTypes;
	
	std::map<unsigned int, unsigned char> attributeByte;
	std::map<unsigned int, bool> attributeBoolean;
	std::map<unsigned int, wchar_t> attributeChar;
	std::map<unsigned int, double> attributeDouble;
	std::map<unsigned int, float> attributeFloat;
	std::map<unsigned int, int> attributeInt;
	std::map<unsigned int, int64> attributeLong;
	std::map<unsigned int, short> attributeShort;
	std::map<unsigned int, std::wstring> attributeString;

	// Code pages
	unsigned char tagPage;
	unsigned char attributePage;

	// others
	std::string encoding;
	bool standalone;

	// methods
        inline void write(primitive& p, unsigned int n) {
                for (unsigned int i=0; i<n; i++) {
                        os.put(p.c[lowEndian ? n - 1 - i : i]);
                }
        }

        inline void writeByte(unsigned char b) {
                os.put(b);
        }

        inline void writeBoolean(bool b) {
                os.put(b ? 1 : 0);
        }

        inline void writeChar(wchar_t ch) {
                primitive p;
                p.ch = ch;
                write(p, 2);
        }

        inline void writeDouble(double d) {
                primitive p;
                p.d = d;
                write(p, 8);
        }

        inline void writeFloat(float f) {
                primitive p;
                p.f = f;
                write(p, 4);
        }

        inline void writeInt(int i) {
                primitive p;
                p.i = i;
                write(p, 4);
        }

        inline void writeLong(int64 l) {
                primitive p;
                p.l = l;
                write(p, 8);
        }

        inline void writeShort(short s) {
                primitive p;
                p.s = s;
                write(p, 2);
        }

        int writeUTF(std::wstring str, unsigned int start = 0, unsigned int len = 0);

        inline void writeString(std::wstring s, unsigned int start = 0, unsigned int len = 0) {
        	writeUTF(s, start, len);
        	writeByte(0);
	}

	void clearAttributes();
	void writeHeader();
	void writeMultiByteInt(unsigned int ui);
	void writeTag(unsigned int tag, bool hasContent);
	void writeAttribute(unsigned int tag);
	
public:
	DefaultWriter(std::ostream& out);

	virtual void openDoc(std::wstring version = L"BinaryAIDA/1.0",
			std::string encoding = "UTF-8", bool standalone = false);
	virtual void closeDoc();
	virtual void close();
	virtual void referToDTD(std::wstring name, std::wstring pid, std::wstring ref);
	virtual void referToDTD(std::wstring name, std::wstring system);

	virtual void openTag(unsigned int tag);
	virtual void closeTag();
	virtual void printTag(unsigned int tag);
	virtual void print(std::wstring text, unsigned int start, unsigned int len);
	virtual void printComment(std::wstring comment);
	
	virtual void setAttribute(unsigned int tag, unsigned char value);
	virtual void setAttribute(unsigned int tag, bool value);
	virtual void setAttribute(unsigned int tag, wchar_t value);
	virtual void setAttribute(unsigned int tag, double value);
	virtual void setAttribute(unsigned int tag, float value);
	virtual void setAttribute(unsigned int tag, int value);
	virtual void setAttribute(unsigned int tag, int64 value);
	virtual void setAttribute(unsigned int tag, short value);
	virtual void setAttribute(unsigned int tag, std::wstring value);

	static int stringUTFLength(std::wstring, unsigned int start = 0, unsigned int len = 0);
};

}

#endif
