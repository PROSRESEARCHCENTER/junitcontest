package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.Trapezoid;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Trd</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Trd.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Trd definition</a>
  */
public class G4Trd extends Trapezoid {

  public G4Trd(String Name,
               double dx1,
               double dx2,
               double dy1,
               double dy2,
               double dz) {
    super(dx1 * 2,
          dx2 * 2,
          dy1 * 2,
          dy2 * 2,
          dz  * 2,
          new Appearance());
    }

  }



