package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.PolyConeSegment;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Polycone</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Polycone.hh&FileDir=geometry/solids/specific/include">Geant4 G4Polycone definition</a>
  */
public class G4Polycone extends PolyConeSegment {

  public G4Polycone(String   name,
                    double   phiStart,
                    double   phiTotal,
                    int      numZPlanes,
                    double[] zPlane ,
                    double[] rInner,
                    double[] rOuter) {
    super(rInner,
          rOuter,
          zPlane,
          Math.toDegrees(phiStart),
          Math.toDegrees(phiStart + phiTotal),
          20,
          new Appearance());
    }

  }



