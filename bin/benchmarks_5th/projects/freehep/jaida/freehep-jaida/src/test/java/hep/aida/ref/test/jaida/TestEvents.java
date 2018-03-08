package hep.aida.ref.test.jaida;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.tree.Tree;

import java.util.EventObject;
import java.util.Random;

import junit.framework.TestCase;


/**
 *
 * @author tonyj
 * @version $Id: TestEvents.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TestEvents extends TestCase
{
   public TestEvents(String testName)
   {
      super(testName);
   }
   
   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testCloudEvents()
   {
        Random r = new Random();
        IAnalysisFactory af = IAnalysisFactory.create();
        IHistogramFactory histogramFactory = af.createHistogramFactory(af.createTreeFactory().create());
        
        ICloud1D c1 = histogramFactory.createCloud1D("test");
        IsObservable obs = (IsObservable) c1;
        
        Counter counter1 = new Counter();
        Counter counter2 = new Counter();
        obs.addListener(counter1);
        
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(1);
        counter2.test(0);
        obs.addListener(counter2);
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(1);
        counter2.test(1);
        obs.setValid(counter1);
        c1.fill(r.nextDouble());
        counter1.test(2);
        counter2.test(1);
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(2);
        counter2.test(1);
        obs.removeListener(counter1);
        obs.setValid(counter2);
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(2);
        counter2.test(2);
        obs.removeListener(counter2);
   }
   public void testHistogramEvents()
   {
        Random r = new Random();
        IAnalysisFactory af = IAnalysisFactory.create();
        IHistogramFactory histogramFactory = af.createHistogramFactory(af.createTreeFactory().create());
        
        IHistogram1D c1 = histogramFactory.createHistogram1D("test",50,0,1);
        IsObservable obs = (IsObservable) c1;
        
        Counter counter1 = new Counter();
        Counter counter2 = new Counter();
        obs.addListener(counter1);
        
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(1);
        counter2.test(0);
        obs.addListener(counter2);
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(1);
        counter2.test(1);
        obs.setValid(counter1);
        c1.fill(r.nextDouble());
        counter1.test(2);
        counter2.test(1);
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(2);
        counter2.test(1);
        obs.removeListener(counter1);
        obs.setValid(counter2);
        for (int i=0; i<10; i++) c1.fill(r.nextDouble());
        counter1.test(2);
        counter2.test(2);
        obs.removeListener(counter2);
   }
   public void testTreeEvents()
   {
        Random r = new Random();
        IAnalysisFactory af = IAnalysisFactory.create();
        ITree tree = af.createTreeFactory().create();
        IHistogramFactory histogramFactory = af.createHistogramFactory(tree);
        
        IsObservable obs = (IsObservable) tree;
        Counter counter1 = new Counter();
        obs.addListener(counter1);  
        
        IHistogram1D h1 = histogramFactory.createHistogram1D("test1",50,0,1);
        ICloud1D c1 = histogramFactory.createCloud1D("test2");
        counter1.test(0);
        ((Tree) tree).checkForChildren("/");
        counter1.test(2);
        IHistogram1D h2 = histogramFactory.createHistogram1D("test3",50,0,1);
        ICloud1D c2 = histogramFactory.createCloud1D("test4");
        counter1.test(4);
        ICloud1D c3 = histogramFactory.createCloud1D("test4");
        counter1.test(6);
        tree.rm("test1");
        tree.rm("test2");
        counter1.test(8);
   }
  
   protected void setUp() throws Exception
   {
      super.setUp();
   }
   

   private class Counter implements AIDAListener
   {
      private int count = 0;
      public void stateChanged(EventObject e)
      {
         count++;
      }
      void test(int expectedCount)
      {
         assertEquals(expectedCount,count);
      }
   }
}
