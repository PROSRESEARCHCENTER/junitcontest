package hep.graphics.heprep.test;

import hep.aida.IAnalysisFactory;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import hep.graphics.heprep.HasHepRep;
import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import java.util.*;
import org.freehep.record.loop.RecordEvent;
import org.freehep.record.loop.RecordListener;

/**
 * Analyzes an HepRep file for occurrences of HepRep objects.
 * 
 * @author duns
 * @version $Id: HepRepAnalyze.java 15005 2013-05-21 19:00:45Z onoprien $
 */
public class HepRepAnalyze implements RecordListener {
    
    private ITuple tuple;

    /**
     * Creates the analyzer
     */
    public HepRepAnalyze() {  
        IAnalysisFactory af = IAnalysisFactory.create();
        ITree tree = af.createTreeFactory().create();
        ITupleFactory tf = af.createTupleFactory(tree);
        String[] columnNames  = { "TypeTrees", "InstanceTrees", 
                                  "Types", "Instances", "Points", 
                                  "AttDefs", 
                                  "AttValuesOnTypes", "AttValuesOnInstances", "AttValuesOnPoints"};
        Class[] columnClasses = { Integer.TYPE, Integer.TYPE, 
                                  Integer.TYPE, Integer.TYPE, Integer.TYPE, 
                                  Integer.TYPE, 
                                  Integer.TYPE, Integer.TYPE, Integer.TYPE};
      
        tuple = tf.create( "HepRep", "HepRep Analysis", columnNames, columnClasses);
    }
    
    @Override
    public void recordSupplied(RecordEvent event) {
        Object record = event.getRecord();

        HepRep heprep;
        if (record instanceof HepRep) {
            heprep = (HepRep)record;
        } else if (record instanceof HasHepRep) {
            heprep = ((HasHepRep)record).getHepRep();
        } else if (record == null) {
            heprep = null;
        } else {
            System.out.println("Cannot handle record '"+record+"' of class: "+record.getClass());
            return;
        }

        Collection typeTrees = heprep.getTypeTreeList();
        tuple.fill(0, typeTrees.size());
        
        int noOfTypes = 0;
        for (Iterator i=typeTrees.iterator(); i.hasNext(); ) {
            HepRepTypeTree typeTree = (HepRepTypeTree)i.next();
            noOfTypes += countTypes(typeTree.getTypeList());
        }
        tuple.fill(2, noOfTypes);
        
        int noOfAttDefs = 0;
        for (Iterator i=typeTrees.iterator(); i.hasNext(); ) {
            HepRepTypeTree typeTree = (HepRepTypeTree)i.next();
            noOfAttDefs += countAttDefs(typeTree.getTypeList());
        }
        tuple.fill(5, noOfAttDefs);        
        
        Collection instanceTrees = heprep.getInstanceTreeList();
        tuple.fill(1, instanceTrees.size());

        int noOfInstances = 0;
        for (Iterator i=instanceTrees.iterator(); i.hasNext(); ) {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)i.next();
            noOfInstances += countInstances(instanceTree.getInstances());
        }
        tuple.fill(3, noOfInstances);
        
        int noOfPoints = 0;
        for (Iterator i=instanceTrees.iterator(); i.hasNext(); ) {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)i.next();
            noOfPoints += countPoints(instanceTree.getInstances());
        }
        tuple.fill(4, noOfPoints);
        
        int noOfAttValuesOnTypes = 0;
        for (Iterator i=typeTrees.iterator(); i.hasNext(); ) {
            HepRepTypeTree typeTree = (HepRepTypeTree)i.next();
            noOfAttValuesOnTypes += countAttValuesOnTypes(typeTree.getTypeList());
        }
        tuple.fill(6, noOfAttValuesOnTypes);        
        
        int noOfAttValuesOnInstances = 0;
        for (Iterator i=instanceTrees.iterator(); i.hasNext(); ) {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)i.next();
            noOfAttValuesOnInstances += countAttValuesOnInstances(instanceTree.getInstances());
        }
        tuple.fill(7, noOfAttValuesOnInstances);        
        
        int noOfAttValuesOnPoints = 0;
        for (Iterator i=instanceTrees.iterator(); i.hasNext(); ) {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)i.next();
            noOfAttValuesOnPoints += countAttValuesOnPoints(instanceTree.getInstances());
        }
        tuple.fill(8, noOfAttValuesOnPoints);        
        
        tuple.addRow();
    }
    
    private int countTypes(Collection types) {
        int count = types.size();
        for (Iterator i=types.iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            count += countTypes(type.getTypeList());
        }
        return count;
    }

    private int countAttDefs(Collection types) {
        int count = 0;
        for (Iterator i=types.iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            count += type.getAttDefsFromNode().size();
            count += countAttDefs(type.getTypeList());
        }
        return count;
    }

    private int countAttValuesOnTypes(Collection types) {
        int count = 0;
        for (Iterator i=types.iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            count += type.getAttValuesFromNode().size();
            count += countAttValuesOnTypes(type.getTypeList());
        }
        return count;
    }

    private int countInstances(List instances) {
        int count = instances.size();
        for (Iterator i=instances.iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            count += countInstances(instance.getInstances());
        }
        return count;
    }

    private int countPoints(List instances) {
        int count = 0;
        for (Iterator i=instances.iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            count += instance.getPoints().size();
            count += countPoints(instance.getInstances());
        }
        return count;
    }

    private int countAttValuesOnInstances(List instances) {
        int count = 0;
        for (Iterator i=instances.iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            count += instance.getAttValuesFromNode().size();
            count += countAttValuesOnInstances(instance.getInstances());
        }
        return count;
    }

    private int countAttValuesOnPoints(List instances) {
        int count = 0;
        for (Iterator i=instances.iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            for (Iterator p=instance.getPoints().iterator(); p.hasNext(); ) {
                HepRepPoint point = (HepRepPoint)p.next();
                count += point.getAttValuesFromNode().size();
            }
            count += countAttValuesOnPoints(instance.getInstances());
        }
        return count;
    }
}
