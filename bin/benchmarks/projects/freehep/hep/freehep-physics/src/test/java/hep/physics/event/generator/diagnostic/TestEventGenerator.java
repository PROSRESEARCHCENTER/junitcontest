package hep.physics.event.generator.diagnostic;

import hep.io.stdhep.StdhepEvent;
import hep.io.stdhep.StdhepReader;
import hep.io.stdhep.StdhepRecord;
import hep.io.stdhep.StdhepWriter;
import hep.physics.event.generator.MCEvent;
import hep.physics.particle.Particle;
import hep.physics.stdhep.convert.StdhepConverter;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import junit.framework.*;

/**
 *
 * @author tonyj
 */
public class TestEventGenerator extends TestCase
{
    
    public TestEventGenerator(String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        return new TestSuite(TestEventGenerator.class);
    }
    public void testStdhepGenerator() throws IOException
    {
        int nEvents = 100;
        
        DiagnosticEventGenerator gen = new DiagnosticEventGenerator();
        gen.setCosthRange(0,0);
        gen.setMomentumRange(5,5);
        gen.setTwoParticleRes(Math.PI/90);
        gen.setParticleType(gen.getParticlePropertyProvider().get(22));
        
        StdhepConverter converter = new StdhepConverter();
        
        File temp = File.createTempFile("test","stdhep");
        temp.deleteOnExit();
        StdhepWriter writer = new StdhepWriter(temp.getAbsolutePath(),"title","comment",nEvents);
        
        for (int i=0; i<nEvents; i++)
        {
            MCEvent event = gen.generate();
            writer.writeRecord(converter.convert(event));
        }
        writer.close();
        
        // Now see if we can read it!
        
        StdhepReader reader = new StdhepReader(temp.getAbsolutePath());
        assertEquals("comment",reader.getComment());
        assertEquals("title",reader.getTitle());
        assertEquals(nEvents,reader.getNumberOfEventsExpected());
        assertEquals(nEvents,reader.getNumberOfEvents());
        
        int n = 0;
        try
        {
            for (;;)
            {
                StdhepRecord record = reader.nextRecord();
                if (record instanceof StdhepEvent)
                {
                    n++;
                    MCEvent event = converter.convert((StdhepEvent) record);
                    List particles = event.getMCParticles();
                    assertEquals(2,particles.size());
                    for (Iterator i = particles.iterator(); i.hasNext(); )
                    {
                        Particle p = (Particle) i.next();
                        assertEquals(22,p.getPDGID());
                    }
                }
            }
        }
        catch (EOFException x)
        {
            // End of file
        }
        assertEquals(nEvents,n);
    }
    
}
