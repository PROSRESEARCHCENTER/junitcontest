package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.Trapezoid;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Para</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Para.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Para definition</a>
  */
public class G4Para extends Trapezoid {

  public G4Para(String pName,
                double pDx,
                double pDy,
                double pDz,
                double pAlpha,
                double pTheta,
                double pPhi) {
    super(pDx * 2,
          pDx * 2,
          pDx * 2,
          pDx * 2,
          pDy * 2,
          pDy * 2,
          pDz * 2,
          Math.toDegrees(Math.atan(Math.tan(pTheta) * Math.cos(pPhi))),
          Math.toDegrees(Math.atan(Math.tan(pTheta) * Math.sin(pPhi))),
          Math.toDegrees(pAlpha),
          Math.toDegrees(pAlpha),
          new Appearance());
    }

  }



