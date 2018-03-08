package hep.aida.web.taglib;

import hep.aida.IManagedObject;

/**
 * An individual plot within a <code>&lt;region&gt;</code>. A
 * <code>&lt;region&gt;</code> may contain multiple <code>&lt;plot&gt;</code>
 * tags, in which case the image for each <code>&lt;plot&gt;</code> tag will
 * be overlaid in the <code>&lt;region&gt;</code>.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface PlotTag extends StyleProvider {

    /**
     * Set the object to plot. This must either be an {@link IManagedObject}or
     * the name of a variable in a JSP scope holding an {@link IManagedObject}.
     * 
     * @param var
     *            the object to plot, which must be an {@link IManagedObject}
     */
    public void setVar(Object var);
    
}