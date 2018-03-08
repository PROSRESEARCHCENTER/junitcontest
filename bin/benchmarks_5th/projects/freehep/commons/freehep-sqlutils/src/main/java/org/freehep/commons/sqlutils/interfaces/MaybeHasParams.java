
package org.freehep.commons.sqlutils.interfaces;

import java.util.List;
import org.freehep.commons.sqlutils.Param;

/**
 *
 * @author bvan
 */
public interface MaybeHasParams {
    public boolean hasParams();
    public List<Param> getParams();
}
