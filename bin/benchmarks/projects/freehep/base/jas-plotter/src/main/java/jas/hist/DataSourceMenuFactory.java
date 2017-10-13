package jas.hist;
import javax.swing.JMenu;

interface DataSourceMenuFactory
{
	JMenu createMenu(String name,JASHistData ds);
}

