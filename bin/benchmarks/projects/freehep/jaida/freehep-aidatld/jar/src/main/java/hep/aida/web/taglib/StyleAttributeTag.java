package hep.aida.web.taglib;

/**
 * Used to set individual style elements, and must be nested inside
 * <code>&lt;style&gt;</code> tags.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface StyleAttributeTag {

    /**
     * Set the name of the AIDA parameter to set. Legal values depend on the
     * context in which the tag is being used. Consult the AIDA documentation
     * and examples for more details.
     */
    public void setName(String name);

    /**
     * Set the value to associate with the given AIDA name.
     */
    public void setValue(String value);
}