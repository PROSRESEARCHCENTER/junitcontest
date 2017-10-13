package hep.graphics.j3d.geant4;

// FreeHEP
import org.freehep.j3d.PolyGoneSegment;

// Java3D
import javax.media.j3d.Appearance;

/** Geant4 solid <code>G4Polyhedra</code>.
  * Constructor corresponds exactly to Geant4 constructor.
  * @version 1.0.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> 
  * @see <a href="http://wwwinfo.cern.ch/asdcgi/geant4/SRM/G4GenDoc.exe.pl?flag=2&FileName=G4Polyhedra.hh&FileDir=geometry/solids/specific/include">Geant4 G4Polyhedra definition</a>
  */
public class G4Polyhedra extends PolyGoneSegment {

  public G4Polyhedra(String   name,
                     double   phiStart,
                     double   phiTotal,
                     int      numSides,
                     int      numZPlanes,
                     double[] zPlane ,
                     double[] rInner,
                     double[] rOuter) {
    super(rInner,
          rOuter,
          zPlane,
          Math.toDegrees(phiStart),
          Math.toDegrees(phiStart + phiTotal),
          numSides,
          new Appearance());
    }

  }



