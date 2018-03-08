package org.freehep.j3d;

// Java3D
import javax.media.j3d.Appearance;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

/** Outlined Shape3D is factory for creating wireframe
  * <code>Shape3D</code> from normal <code>Shape3D</code>. 
  * @version 1.2.0
  * @author <a href="mailto:Julius.Hrivnac@cern.ch">J.Hrivnac</a> */
public class OutlinedShape3D {
        
  /** Create wireframe <code>Shape3D</code> clone. 
    * There are special values for <code>color</code> defined:
    * <ul>
    * <li><code>BLACK</code>
    * <li><code>WHITE</code>
    * <li><code>DARK</code> - darker version of the <code>shape</code>'s color
    * <li><code>BRIGHT</code> - brighter version of the <code>shape</code>'s color
    * </ul>
    */ 
  // TBD: reuse Geometry
  public static Shape3D create(Shape3D shape, Color3f color) {
    if (color == null) {
      return null;
      }
    Shape3D oShape = (Shape3D)(shape.cloneNode(false));
    Appearance appearance = (Appearance)(shape.getAppearance().cloneNodeComponent(false));
    appearance.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_LINE,
                                                          PolygonAttributes.CULL_NONE,
                                                          -1,
                                                          false));
    if (color == BRIGHT) {
      Material m = oShape.getAppearance().getMaterial();
      m.getDiffuseColor(color);
      color.x = color.x * 1.5f;
      color.y = color.y * 1.5f;
      color.z = color.z * 1.5f;
      }
    else if (color == DARK) {
      Material m = oShape.getAppearance().getMaterial();
      m.getDiffuseColor(color);
      color.x = color.x / 3;
      color.y = color.y / 3;
      color.z = color.z / 3;
      }
    Material material = new Material();
    material.setAmbientColor(color);
    material.setDiffuseColor(color);
    material.setEmissiveColor(color);
    material.setShininess(20);
    appearance.setMaterial(material);
    oShape.setAppearance(appearance);
    return oShape;
    }
        
  /** Use outlined shapes Color (null means no outline). 
    * DARK is default. */  
  public static void setOutlineColor(Color3f oc) {
    _outlineColor = oc;
    }
    
  /** Return outline Color. */  
  public static Color3f outlineColor() {
    return _outlineColor;
    }
  
  public static final Color3f WHITE  = new Color3f(1f, 1f, 1f);

  public static final Color3f BLACK  = new Color3f(0f, 0f, 0f);
  
  public static final Color3f BRIGHT = new Color3f(1f, 1f, 1f);
  
  public static final Color3f DARK   = new Color3f(0f, 0f, 0f);
  
  private static Color3f _outlineColor = DARK;
    
  }
