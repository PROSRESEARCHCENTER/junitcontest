package hep.aida.web.taglib;

/**
 * The <code>&lt;style&gt;</code> tag is used to control plot styles (colors,
 * labels, etc). A <code>&lt;style&gt;</code> tag can appear inside
 * <code>&lt;plotter&gt;</code>,<code>&lt;region&gt;</code> and
 * <code>&lt;plot&gt;</code> tags, and can be arbitrarily nested. For example,
 * a <code>&lt;style&gt;</code> element for an axis can occur inside a
 * <code>&lt;style&gt;</code> element corresponding to a
 * <code>&lt;region&gt;</code>.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface StyleTag extends StyleProvider {

    /**
     * Set the optional AIDA style type (xAxis, yAxis, etc). This attribute
     * is optional, and only used when one style is nested within another. Legal
     * values depend on the context. For example, for a style inside a region
     * tag, legal types are xAxis, yAxis. See the examples and AIDA
     * documentation for more details.
     */
    public void setType(String type);
}