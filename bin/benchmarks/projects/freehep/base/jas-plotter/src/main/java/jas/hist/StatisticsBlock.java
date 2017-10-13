package jas.hist;
import jas.plot.TextBlock;
import jas.util.DoubleWithError;
import jas.util.ScientificFormat;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.text.Format;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * The main class used to display and format statistics.
 * When a user selects showStatistics from the plots popupmenu, a statistics text block
 * is displayed showing each displayed plots' statistics (Vector stats) and name. The statistics block
 * requires a number formatting method to be set (setFormat), the default method is
 * implemented by jas.util.ScientificFormat.
 */

public class StatisticsBlock extends TextBlock
{
   
   public StatisticsBlock()
   {
      this(new ScientificFormat());
   }
   
   public StatisticsBlock(Format g)
   {
      super("Statistics");
      //set default format types
      initializeDefaultFormats(g);
      setFormat(g);
   }
   /**
    * Calculates and returns the total number of lines to be displayed in the statistics block,
    * including a draw line seperator, plot title (if available) and formatted
    * statistics for each data set displayed in the plot area.
    */
   public int getNLines()
   {
      NothingToShow =false;
      if (stats.isEmpty()) return 1;
      
      int n = 0;
      for (int i=0; i<stats.size(); i++)
      {
         JASHistData data = (JASHistData) stats.elementAt(i);
         Statistics stat = data.getStatistics();
         
         if (selectedentries != null )
         {
            n += addDisplayLines(compareEntries(stat)).length;
         }else
         {
            n += addDisplayLines(stat.getStatisticNames()).length;
         }
      }
      
      if(n==0)
      {
         NothingToShow =true;
         return n=1;
      }
      
      return n;
   }
   
   /**Returns a line to be displayed in the statistics block as a String. If the line
    * corresponding to index j is a statisic, the stat (double) is formated by the set
    * formatting method and returned with the stat name to be displayed. Also returned
    * are Strings that generate a line Seperator ("\n") in the statistics block and the
    * data set	title.
    */
   public String getLine(int j)
   {
      if (stats.isEmpty() || NothingToShow)
      {
         return "No Statistics";
      }
      
      int n = 0;
      for (int i=0; i<stats.size(); i++)
      {
         JASHistData data = (JASHistData) stats.elementAt(i);
         Statistics stat = data.getStatistics();
         
         String [] lines_selectedentries = addDisplayLines(compareEntries(stat));
         String[] lines_noselected = addDisplayLines(stat.getStatisticNames());
         
         if(selectedentries!=null)
         {
            
            if (j-n < lines_selectedentries.length)
            {
               
               line = (String) lines_selectedentries[j-n];
               
               if(line.equals("addplottitle"))return data.getLegendText();
               
               if(line.equals("\n"))return line;
               
               String returnline;
               
               if(stat instanceof ExtendedStatistics)
               {
                  Object obj = ((ExtendedStatistics)stat).getExtendedStatistic(line);
                  if(obj == null) obj = new Double(stat.getStatistic(line));
                  
                  Format extendf = (Format) formats.get(obj.getClass());
                  if(extendf != null)returnline = line+ "\t"+ extendf.format(obj);
                  else returnline = line+ "\t"+ obj.toString();
                  
               }else
               {
                  Double d = new Double( stat.getStatistic(line));
                  returnline =line +"\t"+ f.format(d);
               }
               return returnline;
            }
            n += lines_selectedentries.length;
         }else
         {
            if (j-n < lines_noselected.length)
            {
               line = lines_noselected[j-n];
               
               if(line.equals("addplottitle"))	return data.getLegendText();
               
               
               if(line.equals("\n"))return line;
               
               String returnline;
               
               if(stat instanceof ExtendedStatistics)
               {
                  Object obj = ((ExtendedStatistics)stat).getExtendedStatistic(line);
                  if(obj == null) obj = new Double(stat.getStatistic(line));//can getStatistic be overriden to return a non double?
                  
                  Format extendf = (Format) formats.get(obj.getClass());
                  if(extendf != null)returnline = line+ "\t"+ extendf.format(obj);
                  else returnline = line+ "\t"+ obj.toString();
                  
               }else
               {
                  Double d = new Double( stat.getStatistic(line));
                  returnline =line +"\t"+ f.format(d);
               }
               
               return returnline;
               
            }
            n += lines_noselected.length;
         }
      }
      throw new IllegalArgumentException();
   }
   
   
   /**Passed in to this method is the array of stat names to be displayed for a single
    * Statistics set. This method adds "\n" (indicating a line seperator), and "addplottitle"
    * to the beginning of the stats array.
    */
   private String[] addDisplayLines(String[] s)
   {
      String[] addline;
      if(showtitles == SHOWTITLES_ALWAYS || (showtitles == SHOWTITLES_AUTOMATIC && stats.size()>1 && s.length>0))
      {
         addline = new String[s.length+2];
         addline[0] = "\n";
         addline[1] = "addplottitle";
         for(int i=0;i<s.length;i++)
         {
            addline[i+2]=s[i];
         }
         return addline;
      }else if(s.length>0)
      {
         addline = new String[s.length+1];
         addline[0] = "\n";
         for(int i=0;i<s.length;i++)
         {
            addline[i+1]=s[i];
         }
         return addline;
      }else
      {
         return s;
      }
   }
   /**Compares stats names to those selected in gui, returns 0 length array if non selected
    */
   private String[] compareEntries(Statistics stat)
   {
      String[] compare = stat.getStatisticNames();
      Vector vcompare = new Vector();
      
      for(int i=0;i<compare.length;i++)
      {
         if(selectedentries!=null)
         {
            for(int ii=0;ii<selectedentries.length;ii++)
            {
               if(compare[i].equals(selectedentries[ii]))
                  vcompare.addElement(selectedentries[ii]);
            }
         }
      }
      compare = new String[vcompare.size()];
      vcompare.copyInto(compare);
      return compare;
      
   }
   
   
   /**Sets the statistics to be formatted and displayed in the block as a String of
    *stat names. If called, only the stats corresponding to these names are
    *formatted and displayed. This method is invoked when "Statistics Properties.."
    *popup is invoked (base.jas.hist.StatsWindow).
    */
   public void setSelectedEntries(String[] s)
   {
      selectedentries = s;
   }
   
   /**Returns the statistics to be formatted and displayed in the block as a String of
    *stat names. If selectedentries != null, only the stats corresponding to
    *these names are formatted and displayed.
    */
   public String[] getSelectedEntries()
   {
      return selectedentries;
   }
   
   /**Sets user selection from stats window of allways show all stats or show a subset selection
    * from check box list of stats. Used so that when user regenerates stats window the previous
    * setting will be displayed. Calles on ok/apply from stats window.
    */
   void set_AllwaysAll_Subset(boolean alwaysall)
   {
      this.alwaysall = alwaysall;
   }
   /**Get previous settting from stats window of allways show all stats or show a subset selection
    * from check box list of stats. Used so that when user regenerates stats window the previous
    * setting will be displayed.
    */
   boolean get_AllwaysAll_Subset()
   {
      return alwaysall;
   }
   /**Returns an array of stat names corresponding to every different stat name in the
    * Vector stats. These are the stat names listed in the popup window base.jas.util.StatsWindow
    */
   public String[] getStatNames()
   {
      Vector vlistednames = new Vector();
      listnames=null;
      for (int i=0; i<stats.size(); i++)
      {
         JASHistData data = (JASHistData) stats.elementAt(i);
         Statistics stat = data.getStatistics();
         if (stat!=null) listnames = stat.getStatisticNames();
         
         if(listnames!=null)
         {
            for (int ii=0;ii<listnames.length;ii++)
            {
               if( !(vlistednames.contains(listnames[ii])) )
               {
                  vlistednames.addElement(listnames[ii]);
               }
            }
         }
      }
      
      listnames = new String[vlistednames.size()];
      vlistednames.copyInto(listnames);
      
      return listnames;
   }
   /**
    * Adds a Statistcs element to the Vector stats if not null
    */
   public void add(JASHistData data)
   {
      if(data.getStatistics() != null)stats.addElement(data);
   }
   
   /**
    * Removes a Statistcs element from the Vector stats and the corresponding data set
    * title from the titles Vector.
    */
   public void remove(JASHistData data)
   {
      stats.removeElement(data);
   }
   
   /**Clears the Vector stats and titles. No statistics are available
    */
   public void clear()
   {
      stats.removeAllElements();
   }
   
   /**
    * Sets the number formating object. If a statistics window is user generated the format
    * is set to base.jas.util.ScientificFormat and this method is invoked by
    * each click of the apply and ok buttons
    */
   public void setFormat(Format g)
   {
      f = g;
      updateFormatsInstances(f);
      this.revalidate();
      this.repaint();
   }
   
   /**Returns the current formatting object;
    */
   public Format getFormat()
   {
      return f;
   }
   
   public void putFormat(Class c,Format f)
   {
      formats.put(c,f);
   }
   
   private void updateFormatsInstances(Format f)
   {
      
      Enumeration e = formats.keys();
      while(e.hasMoreElements())
      {
         Object obj = e.nextElement();
         if( formats.get(obj).getClass() == f.getClass())
         {
            putFormat(obj.getClass(),f);//all values of same class get argumet format instance
         }
      }
      
   }
   
   private void initializeDefaultFormats(Format f)
   {
      if(f instanceof ScientificFormat)
      {
         putFormat(Double.class,f);
         putFormat(DoubleWithError.class,f);
      }else
      {
         ScientificFormat defaultsciformat = new ScientificFormat();
         putFormat(Double.class,defaultsciformat);
         putFormat(DoubleWithError.class,defaultsciformat);
      }
   }
   
   /**When implemented, getSplitStringAlignment() should return an integer between 1 and 3
    * corresponding to the chosen alignment for the second half of strings split by '\t'.
    * For leftalignment: return 1.  For rightaignment: return 2. For noalignment: return 3.
    * */
   public int getSplitStringAlign()
   {
      return splitstringalign;
   }
   
   /**When implemented, setSplitStringAlignment() should set an integer between 1 and 3
    * corresponding to the chosen alignment for the second half of strings split by '\t'.
    * For leftalignment: return 1.  For rightalignment: return 2. For noalignment: return 3.
    * */
   public void setSplitStringAlign(int a)
   {
      if((a<4) && (a >0))
      {
         splitstringalign=a;
      }else throw new IllegalArgumentException("Integer splitstringalign must be set to 1,2 or 3");
   }
   
   /**Sets the (int) showtitles to one of three values:SHOWTITLES_ALWAYS,
    * SHOWTITLES_NEVER, SHOWTITLES_AUTOMATIC. The value controls when a plot title is
    * displayed in the stat block. If showtitles = SHOWTITLES_AUTOMATIC then titles will
    * be displayed if there is more than one plot displayed.
    */
   public void setShowTitles(int settitles)
   {
      if((settitles<4) && (settitles >0))
      {
         showtitles = settitles;
      }else throw new IllegalArgumentException("Integer showtitles must be set to 1,2 or 3");
   }
   /**Returns the (int) showtitles which has three values:SHOWTITLES_ALWAYS,
    * SHOWTITLES_NEVER, SHOWTITLES_AUTOMATIC. The value controls when a plot title is
    * displayed in the stat block. If showtitles = SHOWTITLES_AUTOMATIC then titles will
    * be displayed if there is more than one plot displayed.
    */
   public int getShowTitles()
   {
      return showtitles;
   }
   
   
   
   public void modifyPopupMenu(final JPopupMenu menu, final Component source)
   {
      if (menu.getComponentCount() > 0) menu.addSeparator();
      statpropertiesitem = new JMenuItem(getPrefix()+" Properties...")
      {
         protected void fireActionPerformed( final ActionEvent e)
         {
            statwin = new StatsWindow(StatisticsBlock.this);
            final Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
            statwin.showStatsWindow();
         }
      };
      menu.add(statpropertiesitem);
      super.modifyPopupMenu(menu,source);
   }
   
   
   private boolean NothingToShow;
   private Format f;
   private Hashtable formats = new Hashtable();
   private StatsWindow statwin;
   private Vector stats = new Vector();
   private String[] selectedentries;
   private String[] listnames;
   private int showtitles = SHOWTITLES_AUTOMATIC;
   private int splitstringalign=RIGHTALIGNSPLIT;
   private String line;
   private JMenuItem statpropertiesitem;
   private boolean alwaysall=true;
   final public static int SHOWTITLES_ALWAYS = 1;
   final public static int SHOWTITLES_NEVER = 2;
   final public static int SHOWTITLES_AUTOMATIC = 3;
   final public static int LEFTALIGNSPLIT = 1;
   final public static int RIGHTALIGNSPLIT = 2;
   final public static int NOALIGNSPLIT = 3;
   
}
