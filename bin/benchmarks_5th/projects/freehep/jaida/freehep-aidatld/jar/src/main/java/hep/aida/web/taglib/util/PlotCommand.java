package hep.aida.web.taglib.util;



/**
 * A command to make plots.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotCommand {

    private String name;

    private int width = 600;

    private int height = 400;

    private String format = "png";

    private boolean allowDownload = false;

    private boolean createImageMap = false;

    /**
     * Return the plot token under which the IPLotter is stored in session
     * scope.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the plot token under which the IPLotter is stored in session scope.
     */
    public void setName(String plotterID) {
        this.name = plotterID;
    }

    /**
     * Return the width of the plot in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width of the plot in pixels.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Return the height of the plot in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height of the plot in pixels.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Return the format of the plot.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the format of the plot.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Return whether to show the "allow download" links beneath the plot.
     */
    public boolean isAllowDownload() {
        return allowDownload;
    }

    /**
     * Set whether to show the "allow download" links beneath the plot.
     */
    public void setAllowDownload(boolean allowDownload) {
        this.allowDownload = allowDownload;
    }

    /**
     * Return whether to create an image map on the plot.
     */
    public boolean createImageMap() {
        return createImageMap;
    }

    /**
     * Set whether to create an image map on the plot.
     */
    public void setCreateImageMap(boolean createImageMap) {
        this.createImageMap = createImageMap;
    }
}