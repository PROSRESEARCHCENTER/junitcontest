// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep;


/**
 * The HepRep Converter interface.
 *
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepConverter.java 8584 2006-08-10 23:06:37Z duns $
 * @deprecated use hep.graphics.heprep.HepRepProvider instead.
 */
public interface HepRepConverter {

    /**
     * Returns true if the given objectClass can be converted to a HepRep.
     * @param objectClass class to convert
     * @return true if conversion is possible
     * @deprecated use hep.graphics.heprep.HepRepProvider instead.
     */
    public boolean canHandle(Class objectClass);

    /**
     * Returns the converted HepRep from the given object.
     * @param object object to convert
     * @return converted HepRep
     * @deprecated use hep.graphics.heprep.HepRepProvider instead.
     */
    public HepRep convert(Object object);  
} 

