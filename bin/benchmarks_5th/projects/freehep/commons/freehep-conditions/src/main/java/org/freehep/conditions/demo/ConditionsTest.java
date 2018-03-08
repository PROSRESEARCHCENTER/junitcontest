package org.freehep.conditions.demo;

import java.util.*;
import org.freehep.conditions.Conditions;
import org.freehep.conditions.ConditionsEvent;
import org.freehep.conditions.ConditionsListener;
import org.freehep.conditions.ConditionsManager;
import org.freehep.conditions.ConditionsUpdateException;
import org.freehep.conditions.ConditionsSet;
import org.freehep.conditions.util.DatabaseConnector;
import org.w3c.dom.Document;

/**
 * Test case for use with {@link DemoConditionsManager}.
 * <p>
 * Retrieves the following conditions :
 * <p>
 * From a detector-dependent archive ("alpha" - local zip, "beta" - remote zip):
 * <ul>
 *   <li>"compact.xml" - RawConditions xml file.
 *   <li>"Tracker/tracker.properties" - ConditionsSet from properties file.
 * </ul>
 * From "location" dependent archive ("SLAC" - local directory, "JLAB" - resource zip):
 * <ul>
 *   <li>"LOCATION-DEPENDENT:vtx-data" - ConditionsSet from properties file.
 *   <li>"LOCATION-DEPENDENT:ECal Calibration" - CachedConditions EcalCalib object.
 * </ul>
 * From absolute URL:
 * <ul>
 *   <li>"TestImage" - CachedConditions image object.
 * </ul>
 * From HPS test database, ecal_channels table, "crate" dependent:
 * <ul>
 *   <li>"HPS-DB:Ecal Channels for crate?slot=10" - ConditionsSet
 * </ul>
 * From HPS test database, using SpecialDatabaseConditionsReader, run-dependent:
 * <ul>
 *   <li>"HPS-SPEC:calibration" - ConditionsSet
 *   <li>"HPS-SPEC:?completeon=OK" - ConditionsSet
 *   <li>"HPS-SPEC:calibration" - CachedConditions Calibrator object.
 *   <li>"HPS-DB:SVTBadChannels" - ConditionsSet
 * </ul>
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class ConditionsTest implements ConditionsListener {

// -- Private parts : ----------------------------------------------------------
  
  private int _iEvent = -1;
  private final ArrayList<Conditions> _conditions = new  ArrayList<>();
  
  String _detector;
  Integer _run;
  Date _timestamp;
  
  private final DemoConditionsManager _manager;

// -- Construction and initialization : ----------------------------------------
  
  public ConditionsTest() {
    
    _manager = new DemoConditionsManager();
    
    DatabaseConnector dbConnector = _manager.getConnector();
    dbConnector.setUserName("rd_hps_cond_ro");
    dbConnector.setPassword("2jumpinphotons.");
  }
  
// -- Listening : --------------------------------------------------------------
  
  @Override
  public void conditionsChanged(ConditionsEvent event) {
    System.out.println("- "+ event.getSource().getName() +" may have changed.");
  }
  
// -- Getters : ----------------------------------------------------------------
  
  public ConditionsManager getConditionsManager() {
    return _manager;
  }
  
// -- Updating conditions : ----------------------------------------------------
  
  public void update(String detector, Integer run, Date timestamp, String... extraParameters) {
    _detector = detector;
    _run = run;
    _timestamp = timestamp;
    StringBuilder sb = new StringBuilder();
    sb.append("\nTriggering update with: detector=").append(detector).append("; run=").append(run).append("; timestamp=").append(timestamp);
    ConditionsEvent event = new ConditionsEvent(detector, run, timestamp);
    for (int i = 0; i < extraParameters.length; i += 2) {
      event.put(extraParameters[i], extraParameters[i + 1]);
      sb.append("; ").append(extraParameters[i]).append("=").append(extraParameters[i + 1]);
    }
    System.out.println(sb.toString());
    try {
      _manager.update(event);
    } catch (ConditionsUpdateException x) {
      System.out.println("Error updating conditions");
      System.err.println("Error updating conditions\n "+ x);
    }
  }
  
  public void update(String... extraParameters) {
    update(_detector, _run, _timestamp, extraParameters);
  }
  
  private void trackConditions(Conditions conditions) {
    _conditions.add(conditions);
    conditions.addConditionsListener(this);
    System.out.println("Creating "+ conditions.getName());
  }
  
  private void removeConditions(String name) {
    Iterator<Conditions> it = _conditions.iterator();
    while (it.hasNext()) {
      Conditions con = it.next();
      if (con.getName().equals(name)) {
        System.out.println(" Removing "+ con.getName());
        it.remove();
        break;
      }
    }
  }
  
// -- Testing : ----------------------------------------------------------------
  
  ConditionsSet set1, set2, set3, set4;
  
  public boolean hasNext() {
    return _iEvent < 3;
  }
  
  public void next() {
    switch (++_iEvent) {
      case 0:
        trackConditions( _manager.getConditions("Tracker/tracker.properties") );
        trackConditions( _manager.getCachedConditions(Document.class, "compact.xml") );
        trackConditions( _manager.getConditions("LOCATION-DEPENDENT:vtx-data") );
        trackConditions( _manager.getConditions("HPS-DB:Ecal Channels for crate?slot=10") );
        trackConditions( _manager.getConditions("HPS-SPEC:calibration") );
        trackConditions( _manager.getConditions("HPS-SPEC:?completion=OK") );
        trackConditions( _manager.getConditions("HPS-DB:SVTBadChannels") );
        break;
      case 1:
        update("alpha", 200, new Date(),
               "crate", "1",
               "location", "SLAC");
        trackConditions( _manager.getCachedConditions(JrbCalibrator.class, "HPS-SPEC:calibration") );
        break;
      case 2:
        update(_detector, 350, _timestamp,
                "crate", "2",
                "set", "1");
        removeConditions("HPS-DB:Ecal Channels for crate?slot=10");
        break;
      case 3:
        update("beta", 351, _timestamp,
                "set", "1",
                "location", "JLAB");
        break;
      default:
        throw new IllegalArgumentException("No more test steps");
    }
  }
  
  static public void main(String... args) {
    ConditionsTest test = new ConditionsTest();
    try {
      while (true) test.next();
    } catch (IllegalArgumentException x) {
      System.out.println("End of test");
    }
 }

}
