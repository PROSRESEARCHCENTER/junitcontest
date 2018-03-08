package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.TorusSegment;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Torus</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Torus.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Torus definition</a>
  */
public class G4Torus extends TorusSegment {

  public G4Torus(String pName,
                 double pRmin,
                 double pRmax,
                 double pRtor,
                 double pSPhi,
                 double pDPhi) {
    super(pRmin,
          pRmax,
          pRtor,
          Math.toDegrees(pSPhi),
          Math.toDegrees(pSPhi + pDPhi),
          20,
          new Appearance());
    }

  }



