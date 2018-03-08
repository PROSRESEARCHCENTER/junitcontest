package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.Trapezoid;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Trap</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.1.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Trap.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Trap definition</a>
  */
public class G4Trap extends Trapezoid {

  /** Constructor doesn't check for arguments consistency, 
    * it's resposability of a user to provide correct parameters. */
  public G4Trap(String name,
                double pDz,
                double pTheta,
                double pPhi,
                double pDy1,
                double pDx1,
                double pDx2,
                double pAlp1,
                double pDy2,
                double pDx3,
                double pDx4,
                double pAlp2) {
    super(pDx2 * 2,
          pDx1 * 2,
          pDx4 * 2,
          pDx3 * 2,
          pDy1 * 2,
          pDy2 * 2,
          pDz  * 2,
          Math.toDegrees(Math.atan(Math.tan(pTheta) * Math.cos(pPhi))),
          Math.toDegrees(Math.atan(Math.tan(pTheta) * Math.sin(pPhi))),
          Math.toDegrees(pAlp1),
          Math.toDegrees(pAlp2),
          new Appearance());
    }

  }



