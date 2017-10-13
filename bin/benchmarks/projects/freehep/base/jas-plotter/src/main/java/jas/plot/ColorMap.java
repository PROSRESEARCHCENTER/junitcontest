package jas.plot;

import jas.hist.JASHist2DHistogramStyle;
import java.awt.Color;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ColorMap {
    
    private JASHist2DHistogramStyle style;
    
    public ColorMap(JASHist2DHistogramStyle style) {
        this.style = style;
    }
    
    public Color getColor(double value) {
        int colorMode = style.getColorMapScheme();
        
        int Red;
        int Green;
        int Blue;
        Color tempColor;
        
        if (colorMode == 0) //warm
        {
            if (value <= 0) {
                return new Color(255, 0, 0);
            } else if (value >= 1) {
                return new Color(255, 255, 0);
            } else {
                return new Color(255, 155 + (int) (100 * value), 0);
            }
        } else if (colorMode == 1) { //cool
            if (value <= 0) {
                return new Color(0, 0, 255);
            } else if (value >= 1) {
                return new Color(0, 255, 255);
            } else {
                return new Color(0, 155 + (int) (100 * value), 255);
            }
        } else if (colorMode == 2) { //thermal
            if (value <= 0) {
                return new Color(0, 0, 0);
            } else if (value >= 1) {
                return new Color(255, 0, 0);
            } else {
                return new Color(155 + (int) (100 * value), 0, 0);
            }
        } else if (colorMode == 3) { 
            return getRainbowColor(value,1);
        } else if (colorMode == 4) { //GrayScale
            if (value <= 0) {
                return new Color(0, 0, 0);
            } else if (value >= 1) {
                return new Color(255, 255, 255);
            } else {
                return new Color(155 + (int) (100 * value), 155 + (int) (100 * value), 155 + (int) (100 * value));
            }
        } else { //User Defined
            ///////////Calculate Red Values///////////
            if (style.getStartDataColor().getRed() > style.getEndDataColor().getRed()) {
                Red = style.getStartDataColor().getRed() - (int) ((style.getStartDataColor().getRed() - style.getEndDataColor().getRed()) * value);
            } else if (style.getStartDataColor().getRed() < style.getEndDataColor().getRed()) {
                Red = style.getStartDataColor().getRed() + (int) ((style.getEndDataColor().getRed() - style.getStartDataColor().getRed()) * value);
            } else {
                Red = style.getStartDataColor().getRed();
            }
            
            ///////////Calculate Green Values///////////
            if (style.getStartDataColor().getGreen() > style.getEndDataColor().getGreen()) {
                Green = style.getStartDataColor().getGreen() - (int) ((style.getStartDataColor().getGreen() - style.getEndDataColor().getGreen()) * value);
            } else if (style.getStartDataColor().getGreen() < style.getEndDataColor().getGreen()) {
                Green = style.getStartDataColor().getGreen() + (int) ((style.getEndDataColor().getGreen() - style.getStartDataColor().getGreen()) * value);
            } else {
                Green = style.getStartDataColor().getGreen();
            }
            
            ///////////Calculate Blue Values///////////
            if (style.getStartDataColor().getBlue() > style.getEndDataColor().getBlue()) {
                Blue = style.getStartDataColor().getBlue() - (int) ((style.getStartDataColor().getBlue() - style.getEndDataColor().getBlue()) * value);
            } else if (style.getStartDataColor().getBlue() < style.getEndDataColor().getBlue()) {
                Blue = style.getStartDataColor().getBlue() + (int) ((style.getEndDataColor().getBlue() - style.getStartDataColor().getBlue()) * value);
            } else {
                Blue = style.getStartDataColor().getBlue();
            }
            
            if (value <= 0) {
                return style.getStartDataColor();
            } else if (value >= 1) {
                return style.getEndDataColor();
            } else {
                return new Color(Red, Green, Blue);
            }
        }
        //UserDefined
    }
    //getHistColor
    
    
    /** The red values. */
    
    private final static int[] red =
    { 120, 115, 111, 106, 102,  97,  93,  88,  84,  79,  75,  70,  66,  61,
      57,  52,  48,  43,  39,  34,  30,  25,  21,  16,  12,   7,   3,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   5,
      10,  14,  19,  23,  28,  32,  37,  41,  46,  50,  55,  59,  64,  68,  73,  77,
      82,  86,  91,  95, 100, 104, 109, 113, 118, 123, 127, 132, 136, 141, 145, 150,
      154, 159, 163, 168, 172, 177, 181, 186, 190, 195, 199, 204, 208, 213, 217, 222,
      226, 231, 235, 240, 244, 249, 253, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };
    
    /** The green values. */
    private final static int[] green =
    {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
       0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   2,   6,  11,
       15,  20,  24,  29,  33,  38,  42,  47,  51,  56,  60,  65,  69,  74,  78,  83,
       87,  92,  96, 101, 105, 110, 114, 119, 123, 128, 132, 137, 141, 146, 150, 155,
       159, 164, 168, 173, 177, 182, 186, 191, 195, 200, 204, 209, 213, 218, 222, 227,
       231, 236, 241, 245, 250, 254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
       255, 255, 255, 255, 255, 255, 255, 252, 248, 243, 239, 234, 230, 225, 221, 216,
       212, 207, 203, 198, 194, 189, 185, 180, 176, 171, 167, 162, 158, 153, 149, 144,
       140, 135, 131, 126, 122, 117, 113, 108, 104,  99,  95,  90,  86,  81,  77,  72,
       68,  63,  59,  54,  50,  45,  41,  36,  32,  27,  23,  18,  14,   9,   5,   0 };
    
    /** The blue values. */
    private final static int[] blue =
    { 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
      255, 255, 255, 255, 255, 255, 251, 247, 242, 238, 233, 229, 224, 220, 215, 211,
      206, 202, 197, 193, 188, 184, 179, 175, 170, 166, 161, 157, 152, 148, 143, 139,
      134, 130, 125, 121, 116, 112, 107, 103,  98,  94,  89,  85,  80,  76,  71,  67,
      62,  58,  53,  49,  44,  40,  35,  31,  26,  22,  17,  13,   8,   4,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0 };
    
    
    private Color getRainbowColor(double value, float alpha) {
        if (value < 0 || value > 1) throw new IllegalArgumentException("Value must be in range [0,1]: "+value);
        int bin = (int) Math.floor(value*red.length);
        if (bin >= red.length) bin = red.length-1;
        int a = (int) (alpha*256);
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        return new Color(red[bin],green[bin],blue[bin],a);
    }
    
}
