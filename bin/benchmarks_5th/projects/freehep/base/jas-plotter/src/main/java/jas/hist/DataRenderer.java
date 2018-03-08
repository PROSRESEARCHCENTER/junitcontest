package jas.hist;
import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

final class DataRenderer extends DefaultListCellRenderer
{
    public Component getListCellRendererComponent(
		JList list, 
        Object value,          
		int index,          
		boolean isSelected, 
        boolean cellHasFocus)      
	{   
		String s;
                if (value instanceof Rebinnable1DHistogramData)
		{
			s = ((Rebinnable1DHistogramData) value).getTitle();
		}
		else s = value.toString();

        return super.getListCellRendererComponent(list,s,index,isSelected,cellHasFocus);
	}
	static DataRenderer createRenderer()
	{
		if (singleton == null) singleton = new DataRenderer();
		return singleton;
	}
	private static DataRenderer singleton = null;
	private Color f = getForeground();
	private Color b = getBackground();
}
