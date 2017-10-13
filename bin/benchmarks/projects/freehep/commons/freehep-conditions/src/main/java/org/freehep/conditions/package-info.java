/**
 * Conditions framework - API and description.
 * 
 * The framework allows retrieval of parameters describing data taking conditions:
 * detector geometry, DAQ settings, calibration constants, etc.
 * No restrictions are placed on the type of conditions data, although additional support
 * is provided for some commonly used data structures. 
 * At the interface level, no assumptions are made about how the data is stored or read.
 * Readers are provided for frequently used types of storage.
 * <p>
 * <h4>The structure of the framework:</h4>
 * <ul>
 * <li>This package defines public API of the framework.</li>
 * <li><tt>org.freehep.conditions.base</tt> package contains default implementation that 
 * can be used directly or as a base for customized implementations of the API.</li>
 * <li><tt>org.freehep.conditions.readers</tt> provides classes that simplify retrieving
 * data from specific types of storage. These readers are designed to work with the default 
 * implementation or its derivatives.</li>
 * <li><tt>org.freehep.conditions.util</tt> and its subpackages contain miscellaneous
 * utilities used by the framework implementation and available to users.</li>
 * </ul>
 * <h4>Note on error handling:</h4>
 * <p>
 * {@link org.freehep.conditions.ConditionsNotFoundException} is thrown by <tt>update(...)</tt> methods in various classes
 * to indicate that conditions data specified by an update triggering event could not be found.
 * The fact that this exception has not been thrown in a call to <tt>update(...)</tt> does not guarantee 
 * that every existing {@link Conditions} object has been successfully updated. It is up to
 * specific framework implementations to decide whether an
 * issue encountered while updating is serious enough to inform the client
 * immediately by throwing <tt>ConditionsNotFoundException</tt>. If a specific
 * {@link Conditions} object has not been successfully updated, it will remain
 * in invalid state until the next update, and any attempt to extract data from
 * it will result in <tt>ConditionsSetNotFoundException</tt>.
 * <p>
 * {@link org.freehep.conditions.ConditionsSetNotFoundException} is thrown by getters of {@link Conditions} objects to
 * indicate that the object has not been successfully updated and is currently invalid. It may also
 * be thrown by {@link ConditionsManager}'s <tt>getXXXConditions(name)</tt> methods if it is
 * immediately known that conditions with the specified name cannot exist.
 * 
 */
package org.freehep.conditions;
