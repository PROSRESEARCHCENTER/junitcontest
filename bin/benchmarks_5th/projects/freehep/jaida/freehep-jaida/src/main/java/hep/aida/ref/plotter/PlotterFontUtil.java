package hep.aida.ref.plotter;

import hep.aida.ITextStyle;

import java.awt.Font;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PlotterFontUtil {
    
    public static Font getFont( ITextStyle textStyle ) {
        String fontName = textStyle.font();
        String fontSize = String.valueOf(textStyle.fontSize());
        boolean italic  = textStyle.isItalic();
        boolean bold    = textStyle.isBold();
        int style = Font.PLAIN;
        if ( italic && bold ) style = Font.ITALIC|Font.BOLD;
        else if ( italic ) style = Font.ITALIC;
        else if ( bold ) style = Font.BOLD;
        return getFont( fontName,  String.valueOf(style), fontSize );
    }

    private static Font getFont( String fontName, String style, String size ) {
        int fontStyle;
        if ( style.equals("1") || style.equals("bold") )
            fontStyle = Font.BOLD;
        else if ( style.equals("2") || style.equals("italic") )
            fontStyle = Font.ITALIC;
        else if ( style.equals("3") || style.equals("boldItalic") )
            fontStyle = Font.ITALIC|Font.BOLD;
        else
            fontStyle = Font.PLAIN;
        float fontSize = Float.parseFloat(size);
        Font font = new Font(fontName, fontStyle, 10);
        return font.deriveFont(fontSize);
    }
/*
    private static Font getFont( String fontName, String isItalic, String isBold, String size ) {
        boolean italic  = Boolean.valueOf(isItalic).booleanValue();
        boolean bold    = Boolean.valueOf(isBold).booleanValue();
        int style = Font.PLAIN;
        if ( italic && bold ) style = Font.ITALIC|Font.BOLD;
        else if ( italic ) style = Font.ITALIC;
        else if ( bold ) style = Font.BOLD;
        return getFont( fontName,  String.valueOf(style), size );
    }
  */  
    
}
