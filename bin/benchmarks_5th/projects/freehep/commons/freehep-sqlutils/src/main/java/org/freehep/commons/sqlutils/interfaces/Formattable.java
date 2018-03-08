
package org.freehep.commons.sqlutils.interfaces;

import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;

/**
 * Formattable for dumping sql
 * @author bvan
 */
public interface Formattable {
    public String formatted();
    public String formatted(AbstractSQLFormatter formatter);
}
