package org.freehep.conditions.demo;

import java.util.*;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.ConditionsInvalidException;

/**
 * Custom class that holds data from a specific calibration set.
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class JrbCalibrator {

// -- Private parts : ----------------------------------------------------------
  
  private double[] _data = new double[10];
  Date _entered;
  Date _date;

// -- Construction and initialization : ----------------------------------------
  
  JrbCalibrator(ConditionsSet data) {
    int id = 0;
    try {
      for (int i=0; i<_data.length; i++) {
        _data[i] = data.getDouble(i, "aValue", Double.NaN);
      }
      _entered = data.getDate("enter_time", null);
    } catch (ConditionsInvalidException x) {
      Arrays.fill(_data, Double.NaN);
    }
    _date = new Date();
  }
  
// -- Getters : ----------------------------------------------------------------
  
  public double getValue(int channelID) {
    return _data[channelID];
  }
  
  public Date getEnterTime() {
    return _entered;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("JRB Calibrator, data enter");
    if (_entered == null) {
      sb.append(" time unknown");
    } else {
      sb.append("ed ").append(_entered);
    }
    sb.append(", object created ").append(_date).append("\n");
    sb.append("Content:\n");
    for (int i=0; i<_data.length; i++) {
      sb.append(_data[i]).append("  ");
    }
    return sb.toString();
  }
}
