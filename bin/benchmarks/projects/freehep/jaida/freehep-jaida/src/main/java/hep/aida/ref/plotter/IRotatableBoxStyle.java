package hep.aida.ref.plotter;

import hep.aida.IBoxStyle;

public interface IRotatableBoxStyle extends IBoxStyle {
    
    static double VERTICAL  = 90.0;
    static double HORIZONTAL = 0.0; // Default
    
    // Box is rotated around its bottom-left corner (default)
    //
    // Maybe can use IBoxStyle constants to specify position of
    // center of rotation within the BOX (CENTER, TOP_CENTER, etc.):
    //
    // int centerOfRotation();
    // void setCenterOfRotation(int position);
    
    // Angle is calculated from the horizontal, counter-clockwise direction is positive
    // Angle can be positive or negative
    double orientation();
    void setOrientation(double angle);
}
