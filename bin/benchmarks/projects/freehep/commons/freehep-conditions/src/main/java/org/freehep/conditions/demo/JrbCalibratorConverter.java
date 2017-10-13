package org.freehep.conditions.demo;

import org.freehep.conditions.ConditionsConverter;
import org.freehep.conditions.ConditionsManager;
import org.freehep.conditions.ConditionsSet;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class JrbCalibratorConverter implements ConditionsConverter {

// -- Private parts : ----------------------------------------------------------

// -- Construction and initialization : ----------------------------------------

  @Override
  public Class getType() {
    return JrbCalibrator.class;
  }

  @Override
  public JrbCalibrator getData(ConditionsManager manager, String name) {
    ConditionsSet data = manager.getConditions(name);
    return new JrbCalibrator(data);
  }

}
