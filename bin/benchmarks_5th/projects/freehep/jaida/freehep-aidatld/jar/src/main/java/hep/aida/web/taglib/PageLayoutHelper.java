package hep.aida.web.taglib;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PageLayoutHelper {
    
    private int nw, nh, height = 600, width = 800, start, end, plotsInPage;

    public PageLayoutHelper(int plotsInPage, int start, int end, int nh, int nw) {
        this.plotsInPage = plotsInPage;
        this.start = start;
        this.end = end;
        this.nh = nh;
        this.nw = nw;
    }
    
    public PageLayoutHelper(int plotsInPage, int start, int end) {        
        this.plotsInPage = plotsInPage;
        this.start = start;
        this.end = end;
        nw = plotsInPage < 2 ? 1 : plotsInPage <= 6 ? 2 : 3;
        if ( plotsInPage > 9 )
            nw = 4;
        nh = (plotsInPage/nw);
        if ( nh * nw < plotsInPage )
            nh += 1;
        
        if ( nh > 1 )
            height = nh*200;
        
        if ( plotsInPage == 1 ) {
            width = 600;
            height = 400;
        }        
    }
    
    public int getNplotsWidth() {
        return nw;
    }
    
    public int getNplotsHeight() {
        return nh;
    }
    
    public int getPlotterWidth() {
        return width;
    }
    
    public void setPlotterWidth(int width) {
        this.width = width;
    }

    public int getPlotterHeight() {
        return height;
    }
    
    public void setPlotterHeight(int height) {
        this.height = height;
    }

    public int getStartPlotIndex() {
        return start;
    }

    public int getEndPlotIndex() {
        return end;
    }
    
    public int getPlotsInPage() {
        return plotsInPage;
    }
    
    public static void setPlotterSize(PageLayoutHelper layout, int width, int height) {
        if ( width > 0 )
            layout.setPlotterWidth(width);
        if ( height > 0 )
            layout.setPlotterHeight(height);
    }
}
