package hep.io.stdhep;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class StdhepTest extends TestCase
{
   public StdhepTest(String testName)
   {
      super(testName);
   }
   
   /**
    * Test of writeRecord method, of class hep.io.stdhep.StdhepWriter.
    */
   public void testReadWrite() throws Exception
   {
      int nEvents = 10000;
      File file = File.createTempFile("test","stdhep");
      file.deleteOnExit();
      
      long t0 = System.currentTimeMillis();
      
      StdhepWriter writer = new StdhepWriter(file.getAbsolutePath(), "TestTitle", "TestComment", nEvents);
      StdhepBeginRun begin = new StdhepBeginRun(nEvents, nEvents, nEvents, 0, 0, 0, 0);
      writer.writeRecord(begin);
      for (int i=0; i<nEvents; i++)
      {
         int[] isthep = new int[10];
         int[] idhep = new int[10];
         int[] jmohep = new int[10*2];
         int[] jdahep = new int[10*2];
         double[] phep = new double[10*5];
         double[] vhep = new double[10*4];
         StdhepEvent event = new StdhepEvent(i, 10, isthep, idhep, jmohep, jdahep, phep, vhep);
         writer.writeRecord(event);
      }
      StdhepEndRun end = new StdhepEndRun(nEvents, nEvents, nEvents, 0, 0, 0, 0);
      writer.writeRecord(end);
      writer.close();
      
      long t1 = System.currentTimeMillis();
      
      StdhepReader reader = new StdhepReader(file.getAbsolutePath());
      testReadAndRewind(reader,nEvents);
      reader.rewind();
      testReadAndRewind(reader,nEvents);
      reader.close();
      long t2 = System.currentTimeMillis();
      
      reader = new StdhepReader(new FileInputStream(file));
      testReadAndRewind(reader,nEvents);
      reader.close();
      long t3 = System.currentTimeMillis();
      
      reader = new StdhepReader(file.getAbsolutePath());
      reader.nextRecord();
      StdhepEvent event = (StdhepEvent) reader.nextRecord();
      assertEquals(0,event.getNEVHEP());
      reader.skip(0);
      event = (StdhepEvent) reader.nextRecord();
      assertEquals(1,event.getNEVHEP());
      reader.skip(5);
      event = (StdhepEvent) reader.nextRecord();
      assertEquals(7,event.getNEVHEP());      
      reader.skip(5000);
      event = (StdhepEvent) reader.nextRecord();
      assertEquals(5008,event.getNEVHEP());
      try
      {
         reader.skip(5000);
         fail("Should have thrown EOFException");
      }
      catch (EOFException x)
      {
         // OK, exception expected
      }
      reader.close();
      
      long t4 = System.currentTimeMillis();
      
      reader = new StdhepReader(file.getAbsolutePath());
      event = (StdhepEvent) reader.goToRecord(0,5000);
      assertEquals(5000,event.getNEVHEP()); 
      event = (StdhepEvent) reader.goToRecord(0,4000);
      assertEquals(4000,event.getNEVHEP());     
      try
      {
         reader.goToRecord(0,nEvents+10);
         fail("Should have thrown EOFException");
      }
      catch (EOFException x)
      {
         // OK, exception expected
      }
      reader.close();
      
      long t5 = System.currentTimeMillis();
      
      System.out.println("Write time "+(t1-t0)+"ms");
      System.out.println("Read time "+(t2-t1)+"ms");
      System.out.println("Sequential read time "+(t3-t2)+"ms");
      System.out.println("Skip time "+(t4-t3)+"ms");
      System.out.println("Goto time "+(t5-t4)+"ms");
   }
   private void testReadAndRewind(StdhepReader reader, int nEvents) throws IOException
   {
      assertEquals("TestTitle",reader.getTitle());
      assertEquals("TestComment",reader.getComment());
      assertEquals(nEvents+2,reader.getNumberOfEvents());
      assertEquals(nEvents,reader.getNumberOfEventsExpected());
      
      StdhepRecord record = reader.nextRecord();
      assertTrue(record instanceof StdhepBeginRun);
      
      int i = 0;
      try
      {
         for (;;)
         {
            record = reader.nextRecord();
            if (record instanceof StdhepEvent)
            {
               StdhepEvent event = (StdhepEvent) record;
               assertEquals(i,event.getNEVHEP());
               assertEquals(10,event.getNHEP());
               i++;
            }
            else
            {
               assertTrue(record instanceof StdhepEndRun);
            }
         }
      }
      catch (EOFException x)
      {
         // No problem
      }
      assertEquals(nEvents,i);
   }
   
}
