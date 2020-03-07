package eu.ha3.matmos.util;

import net.minecraft.util.Vec3;

/***
 * Backport of Vec3d from 1.12.2.
 */

public class Vec3d extends Vec3 implements Cloneable {
    
    public Vec3d(double x, double y, double z) {
        super(x, y, z);
    }
    
    public double length() {
        return super.lengthVector();
    }
    
    public Vec3d normalize() {
        double l = length();
        return new Vec3d(xCoord / l, yCoord / l, zCoord / l);
    }
    
    public Vec3d scale(double s) {
        return new Vec3d(xCoord * s, yCoord * s, zCoord * s);
    }
    
    public Vec3d add(Vec3 o) {
        return new Vec3d(xCoord + o.xCoord, yCoord + o.yCoord, zCoord + o.zCoord);
    }
    
    public Vec3d clone() {
        return new Vec3d(xCoord, yCoord, zCoord);
    }
}
