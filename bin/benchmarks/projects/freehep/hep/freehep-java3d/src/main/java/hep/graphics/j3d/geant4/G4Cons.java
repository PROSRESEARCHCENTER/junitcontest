package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.ConeSegment;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Cons</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Cons.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Cons definition</a>
  */
public class G4Cons extends ConeSegment {

  public G4Cons(String pName,
                double pRmin1,
                double pRmin2,
                double pRmax1,
                double pRmax2,
                double pDz,
                double pSPhi,
                double pDPhi) {
    super(pRmin1,
          pRmin2,
          pRmax1,
          pRmax2,
          pDz * 2,
          Math.toDegrees(pSPhi),
          Math.toDegrees(pSPhi + pDPhi),
          20,
          new Appearance());
    }

  }



