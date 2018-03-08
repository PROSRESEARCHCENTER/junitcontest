package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.SphereSegment;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Sphere</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Sphere.hh&FileDir=geometry/solids/CSG/include">Geant4 G4Sphere definition</a>
  */
public class G4Sphere extends SphereSegment {

  public G4Sphere(String pName,
                  double pRmin,
                  double pRmax,
                  double pSPhi,
                  double pDPhi,
                  double pSTheta,
                  double pDTheta) {
    super(pRmin,
          pRmax,
          Math.toDegrees(pSPhi),
          Math.toDegrees(pSPhi + pDPhi),
          Math.toDegrees(pSTheta),
          Math.toDegrees(pSTheta + pDTheta),
          20,
          new Appearance());
    }

  }



