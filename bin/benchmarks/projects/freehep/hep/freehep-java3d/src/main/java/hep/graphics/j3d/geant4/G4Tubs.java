package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.ConeSegment;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Tubs</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Tubs.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Tubs definition</a>
  */
public class G4Tubs extends ConeSegment {

  public G4Tubs(String pName,
                double pRMin,
                double pRMax,
                double pDz,
                double pSPhi,
                double pDPhi) {
    super(pRMin,
          pRMax,
          pDz * 2,
          pSPhi,
          Math.toDegrees(pSPhi + pDPhi),
          20,
          new Appearance());
    }

  }



