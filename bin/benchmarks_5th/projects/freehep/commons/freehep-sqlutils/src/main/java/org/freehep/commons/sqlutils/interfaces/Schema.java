
package org.freehep.commons.sqlutils.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author bvan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Schema {
    public String name() default "";
    public String alias() default "";
}
