// Copyright 2004-2005, FreeHEP.
package hep.graphics.heprep;


/**
 * HepRep Provider interface, allowing HepReps to be converted from other objects.
 *
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepProvider.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface HepRepProvider {

    /**
     * Returns true if the object can be converted into an HepRep.
     * @param object object to convert
     * @return true if conversion is possible
     */
    public boolean canConvert(Object object);

    /**
     * Returns a HepRep converted from object.
     * Throws HepRepConversionException if the conversion was not possible.
     * @param object object to convert
     * @return converted HepRep
     * @throws HepRepConversionException if conversion failed
     */
    public HepRep convert(Object object) throws HepRepConversionException;  
} 

