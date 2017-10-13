package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.Trapezoid;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Box</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Box.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Box definition</a>
  */
public class G4Box extends Trapezoid {

  public G4Box(String pName,
               double pX,
               double pY,
               double pZ) {
    super(pX * 2,
          pY * 2,
          pZ * 2,
          new Appearance());
    }

  }



