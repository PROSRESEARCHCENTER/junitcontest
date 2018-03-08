package org.freehep.conditions.demo;

import java.util.*;
import org.freehep.conditions.base.DefaultConditionsManager;
import org.freehep.conditions.readers.DatabaseConditionsReader;
import org.freehep.conditions.readers.DefaultContextValidator;
import org.freehep.conditions.readers.SpecialDatabaseConditionsReader;
import org.freehep.conditions.readers.URLConditionsReader;
import org.freehep.conditions.util.DatabaseConnector;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class DemoConditionsManager extends DefaultConditionsManager {

// -- Private parts : ----------------------------------------------------------
  
  final private DatabaseConnector _dbConnector;

// -- Construction and initialization : ----------------------------------------
  
  public DemoConditionsManager() {
    
    URLConditionsReader urlReader1 = new URLConditionsReader(this,
      "jar:file:///d:/Physics/HPS/conditions/${detector}.zip!/",
      "jar:http://jas.freehep.org/jas3/temp/${detector}.zip!/"
    );
    urlReader1.setContextValidator(new DefaultContextValidator(true));
    urlReader1.setConfiguration(urlReader1.getConfiguration(), true, true);
    addConditionReader(urlReader1, "");
    addConditionsConverter(new DocumentConverter());
    
    URLConditionsReader urlReader2 = new URLConditionsReader(this,
      "file:///d:/Physics/HPS/conditions/${location}/",
      "classpath:org/freehep/conditions/demo/${location}/",
      "http://jas.freehep.org/jas3/temp/defaultLocation/"
    );
    urlReader2.setContextValidator(new DefaultContextValidator(true));
    addConditionReader(urlReader2, "LOCATION-DEPENDENT");
    
    _dbConnector = new DatabaseConnector("jdbc:mysql://mysql-node03.slac.stanford.edu:3306/rd_hps_cond");
    
    Map<String,String> queries = new HashMap<>();
    queries.put("Ecal Channels for crate", "SELECT channel, x, y FROM ecal_channels WHERE crate=${crate} AND slot=${slot};");
    queries.put("SVTBadChannels", "SELECT * FROM svt_bad_channels WHERE set_id=${set};");
    DatabaseConditionsReader dbReader = new DatabaseConditionsReader(this, _dbConnector, queries);
    addConditionReader(dbReader, "HPS-DB");
    
    queries = new HashMap<>();
    queries.put("", "SELECT * FROM conditions_test WHERE run_start<=${run} AND run_end>=${run};");
    SpecialDatabaseConditionsReader specReader = new SpecialDatabaseConditionsReader(this, _dbConnector, queries);
    addConditionReader(specReader, "HPS-SPEC");
    
    addConditionsConverter(new JrbCalibratorConverter()); 
  }
  
  DatabaseConnector getConnector() {
    return _dbConnector;
  }
  


}
