package jas.plot;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPopupMenu;
/**
 * TextBlock is the base class for creating moveable text boxes in the jas display.
 */
public abstract class TextBlock extends MovableObject
{
	public TextBlock(String prefix)
	{
		super(prefix);
		setBorderType(LINE);
		setFont(new Font("SansSerif",Font.PLAIN,10));
	}
	
	/**When implemented, getNLines() should return an integer count of the 
	 * number of lines of text to be displayed in the text block.
	 */
	public abstract int getNLines();
	
	/**When implemented, getLine(int n) should return a String corresponding to the nth
	 * text line to be displayed in the text block.  
	 */
	public abstract String getLine(int n);
	
	/**When implemented, getSplitStringAlignment() should return an integer between 1 and 3
	 * corresponding to the chosen alignment for the second half of strings split by '\t'.
	 * For leftalignment: return 1.  For rightaignment: return 2. For noalignment: return 3.
	 * */  
	public abstract int getSplitStringAlign();
	
	/**Sets the horizontal and vertical dimensions of the text block.
	 */
	public Dimension getPreferredSize()
	{	
		if (fm == null) fm = getToolkit().getFontMetrics(getFont());
		allocateSize();
		Insets i = getInsets();
		return new Dimension(blockwidth+i.right+i.left,i.top+i.bottom+blockheight);
	}
	/**Draws each line of text in the text block. If "\n" is returned by getLine() a 
	 * seperator line will be drawn in the block. All other strings are formatted by
	 * formatLine() before being drawn. 
	 */
	public void paintComponent(Graphics g)
	{	
		super.paintComponent(g); // paint the background (perhaps)
		g.setColor(getForeground());
		if (fm == null) fm = g.getFontMetrics(getFont());
		Insets insets = getInsets();
		int y = fm.getAscent()+insets.top;
		String line;
		int fmheight=fm.getHeight();
		int splitalign=getSplitStringAlign();
		boolean callrevalidate = false;
		if(getNLines() != numberlinesallocated)callrevalidate = true;	
		for (int i=0; i<getNLines(); i++)
		{
			line = getLine(i);
			if(line !=null){
				if(line.equals("\n")){
					if(i>0){
					y-=fmheight/2;
					g.drawLine(insets.left,y,blockwidth+insets.right,y);
					y += fmheight;
					}
				}else{
					String[] splitline = formatLine(line);
						
					if(fm.stringWidth(splitline[0])>maxleftsplitwidth ||(splitline[1]!=null && fm.stringWidth(splitline[1])>maxrightsplitwidth))callrevalidate = true;
						
					g.drawString(splitline[0],insets.left,y);
					if(splitline[1]!=null && splitalign==LEFTALIGNSPLIT){
						g.drawString(splitline[1],insets.left+maxleftsplitwidth,y);
					}else if(splitline[1]!=null && splitalign==RIGHTALIGNSPLIT){
						g.drawString(splitline[1],insets.left+rightAlignSplitWidth(splitline[1]),y);
					}else if(splitline[1]!=null && splitalign==NOALIGNSPLIT){
						g.drawString(splitline[1],insets.left+fm.stringWidth(splitline[0]),y);
					}
					y += fmheight;
				}
			}
		}
		if (callrevalidate) revalidate();
	}
	public void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		menu.add(new FontMenuItem(this,getPrefix()));
		super.modifyPopupMenu(menu,source);
	}
	
	/**If the input string contains a '\t' character then it is seperated into two strings
	 * , which allows for the setting of accurate spacings between the two strings in 
	 * the text block.
	 */
	private String[] formatLine(String s){
		
		String[] splitline= new String[2];
				
		if(s!=null){
			int index = s.indexOf('\t');
			if(index>=0){ 
				splitline[0] = "  " + s.substring(0,index)+ " : ";
				splitline[1] = s.substring(index+1)+ " " ;
			}else splitline[0] =s;
		}
		return splitline;
	}
	
	/**Base method for determining the width (blockwidth) of the text block according 
	 * to the max length of text lines to be drawn in the block, and the height
	 * according to the number of lines to be displayed. 
	 */
	protected void allocateSize(){
		String s;
		maxleftsplitwidth=0;
		maxrightsplitwidth=0;
		blockwidth=30;
		blockheight=0;
		int fmheight=fm.getHeight();
		numberlinesallocated = getNLines();
		for (int i=0; i<getNLines(); i++)
		{
			s =getLine(i);
			if(s!= null){
				if(s.equals("\n")){
					if(i>0)blockheight+=fmheight/2;
				}else{
					blockheight+=fmheight;
					blockWidth(s);
				}
				
			}
		}
		if (blockheight==0) blockheight=fmheight;
		
		
	}
	
	/** For strings that are to be displayed as two seperate strings to allow the setting of
	 * accurate spacings between the two strings in  the text block. If strings contains 
	 * '\t' then checks to see if fontmetrics size of input string is greater than the namewidth. If so then namewidth is set to fm size of
	 * current string.
	 */
	private int rightAlignSplitWidth(String s){
		int width=0;
		int rightwidth=0;
		
		if(s!=null){
			rightwidth=fm.stringWidth(s);
			width = blockwidth - rightwidth-WIDTHSPACE;
		}
		
		return width;		
	}
	
	/**Checks to see if fontmetrics size of input string is greater than the blockwidth.
	 * If so then blockwidth is set to fm size of current string.
	 */
	private void blockWidth(String s){
		String[] splitline = formatLine(s);		
		int width=0;
		int leftwidth=0;
		int rightwidth=0;
		int splitalign=getSplitStringAlign();
		
		if(splitline[0]!=null)leftwidth=fm.stringWidth(splitline[0]);
		
		if(splitline[1]!=null)rightwidth=fm.stringWidth(splitline[1]);
			
			
		
		if(splitalign==1 && (splitline[1] !=null)){
					
			if(leftwidth>maxleftsplitwidth)maxleftsplitwidth=leftwidth;
			if(rightwidth>maxrightsplitwidth)maxrightsplitwidth=rightwidth;
			
			width=maxrightsplitwidth +maxleftsplitwidth+WIDTHSPACE;
		
			
		}else{
			
			width=rightwidth +leftwidth+WIDTHSPACE;
		}
		if(width > blockwidth) blockwidth = width;
	}
	
	protected FontMetrics fm;
	private int blockwidth;
	private int blockheight;
	private int maxleftsplitwidth;
	private int maxrightsplitwidth;
	private int numberlinesallocated;
	
	final private static int WIDTHSPACE=5;	
	final private static int LEFTALIGNSPLIT=1;
	final private static int RIGHTALIGNSPLIT=2;
	final private static int NOALIGNSPLIT=3;
	
	
	public void setFont(Font p1)
	{	
		super.setFont(p1);
		fm=null;
		this.revalidate();
		this.repaint();
				
	}
}
