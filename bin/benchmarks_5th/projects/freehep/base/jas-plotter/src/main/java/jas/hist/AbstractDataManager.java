package jas.hist;

import jas.plot.DataArea;
import jas.plot.DataAreaLayout;
import jas.plot.Legend;
import jas.plot.LegendEntry;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

abstract class AbstractDataManager
extends DataManager
implements Runnable
{
    AbstractDataManager(JASHist plot, DataArea da, Legend l, StatisticsBlock stats)
    {
        
        super(plot,da);
        this.legend = l;
        this.stats = stats;
        nVisible = 0;
        nVisibleLegend = 0;
        xm.setFixed(false);
        xm.setRangeAutomatic(true);
        ym[0].setFixed(false);
        ym[0].setRangeAutomatic(true);
        
        timer = new Timer(1000,new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                doUpdate();
            }
        });
        timer.setRepeats(false);
    }
    void init()
    {
        computeXAxisRange();
        XAxisUpdated();
        computeYAxisRange();
        
        // Add a mouse listener to the Axes
        
        //new AxisListener(xAxis);
        //new AxisListener(yAxis);
        
        isInit = true;
    }
    final void invalidate()
    {
        if (isInit) SwingUtilities.invokeLater(this);
    }
    final public void run()
    {
        doUpdate();
    }
    boolean isRealized()
    {
        return isInit;
    }
    void setRealized(boolean b)
    {
        isInit = b;
    }
    void requestShow(JASHistData data)
    {
        da.add(data.getOverlay());
        nVisible++;
        if (legend != null)
        {
            LegendEntry le = data.getLegendEntry();
            if (le != null)
            {
                legend.add(le);
                nVisibleLegend++;
                showLegend();
            }
        }
        if (stats != null)
        {
            stats.add(data);
        }
        if (isInit)
        {
            xm.setAttentionNeeded();
            computeXAxisRange();
            XAxisUpdated();
            computeYAxisRange();
            da.validate();
            da.repaint();
        }
    }
    void requestHide(JASHistData data)
    {
        da.remove(data.getOverlay());
        nVisible--;
        if (legend != null)
        {
            LegendEntry le = data.getLegendEntry();
            if (le != null)
            {
                legend.remove(le);
                nVisibleLegend--;
                showLegend();
            }
        }
        if (stats != null)
        {
            stats.remove(data);
        }
        
        if (isInit)
        {
            xm.setAttentionNeeded();
            computeXAxisRange();
            XAxisUpdated();
            computeYAxisRange();
            da.validate();
            da.repaint();
        }
    }
    protected void showLegend()
    {
        int showLegend = plot.getShowLegend();
        boolean show = (showLegend == JASHist.LEGEND_ALWAYS) || ( showLegend == JASHist.LEGEND_AUTOMATIC && nVisibleLegend>1 );
        legend.setVisible(show);
        legend.revalidate();
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        timer = new Timer(1000,new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                doUpdate();
            }
        });
        timer.setRepeats(false);
        da.add(xm, DataAreaLayout.X_AXIS);
        da.add(ym[0], DataAreaLayout.Y_AXIS_LEFT);
        da.add(ym[1], DataAreaLayout.Y_AXIS_RIGHT);
    }
    transient protected Timer timer;
    transient protected boolean isInit = false;
    protected Legend legend;
    protected StatisticsBlock stats;
    transient protected int nVisible;
    transient protected int nVisibleLegend;
    protected transient Vector data = new Vector();
    
    abstract void doUpdate();
    
    void remove(JASHistData d)
    {
        d.show(false);
        d.deleteNormalizationObserver();
        data.removeElement(d);
    }
    
    int numberOfDataSources()
    {
        return data.size();
    }
    
    void destroy() // detaches data, but doesn't set up the plot for further use
    {
        final Enumeration e = data.elements();
        while (e.hasMoreElements())
        {
            JASHistData d = (JASHistData) e.nextElement();
            d.show(false);
            d.deleteNormalizationObserver();
        }
        data.removeAllElements();
        destroyYAxis(1); // In case second Y Axis was created
        stats.clear();
    }
    
    Enumeration getDataSources()
    {
        return data.elements();
    }
    
    JMenu addPerDataSourceMenu(String name, DataSourceMenuFactory f)
    {
        int n = numberOfDataSources();
        if (n == 0)
        {
            JMenu result = new JMenu(name);
            result.setEnabled(false);
            return result;
        }
        if (n == 1)
        {
            JASHistData ds = (JASHistData) getDataSources().nextElement();
            return f.createMenu(name,ds);
        }
        else
        {
            JMenu result = new JMenu(name);
            Enumeration e = getDataSources();
            for (int i=0; e.hasMoreElements(); i++)
            {
                JASHistData ds = (JASHistData) e.nextElement();
                JMenu sub = f.createMenu(ds.getTitle(),ds);
                sub.setMnemonic('0' + (char) i);
                result.add(sub);
            }
            return result;
        }
    }
}
