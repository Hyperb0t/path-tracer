package org.example.embree;

import com.sun.jna.Structure;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

@Structure.FieldOrder({"org_x", "org_y", "org_z", "tnear", "dir_x", "dir_y", "dir_z", "time", "tfar", "mask", "id", "flags"})
public class RTCRay extends Structure {
    public float org_x = 0;        // x coordinate of ray origin
    public float org_y = 0;        // y coordinate of ray origin
    public float org_z = 0;        // z coordinate of ray origin
    public float tnear = 0;        // start of ray segment

    public float dir_x = 0;        // x coordinate of ray direction
    public float dir_y = 0;        // y coordinate of ray direction
    public float dir_z = 0;        // z coordinate of ray direction
    public float time = 0;         // time of this ray for motion blur

    public float tfar = 0;         // end of ray segment (set to hit distance)
    public int mask = 0;  // ray mask
    public int id = 0;    // ray ID
    public int flags = 0;

    public RTCRay() {
        super();
    }

    public RTCRay(float org_x, float org_y, float org_z, float tnear, float dir_x, float dir_y, float dir_z, float tfar) {
        super();
        this.org_x = org_x;
        this.org_y = org_y;
        this.org_z = org_z;
        this.tnear = tnear;
        this.dir_x = dir_x;
        this.dir_y = dir_y;
        this.dir_z = dir_z;
        this.tfar = tfar;
    }

    public RTCRay(Vector3f org, Vector3f dir, float tnear, float tfar) {
        this(org.x, org.y, org.z, tnear, dir.x, dir.y, dir.z, tfar);
    }

    public void setOrg(float x, float y, float z) {
        this.org_x = x;
        this.org_y = y;
        this.org_z = z;
    }

    public void setOrg(Point3f org) {
        this.org_x = org.x;
        this.org_y = org.y;
        this.org_z = org.z;
    }

    public void setDir(float x, float y, float z) {
        this.dir_x = x;
        this.dir_y = y;
        this.dir_z = z;
    }

    public void setDir(Vector3f dir) {
        this.dir_x = dir.x;
        this.dir_y = dir.y;
        this.dir_z = dir.z;
    }

    public float getOrg_x() {
        return org_x;
    }

    public void setOrg_x(float org_x) {
        this.org_x = org_x;
    }

    public float getOrg_y() {
        return org_y;
    }

    public void setOrg_y(float org_y) {
        this.org_y = org_y;
    }

    public float getOrg_z() {
        return org_z;
    }

    public void setOrg_z(float org_z) {
        this.org_z = org_z;
    }

    public float getTnear() {
        return tnear;
    }

    public void setTnear(float tnear) {
        this.tnear = tnear;
    }

    public float getDir_x() {
        return dir_x;
    }

    public void setDir_x(float dir_x) {
        this.dir_x = dir_x;
    }

    public float getDir_y() {
        return dir_y;
    }

    public void setDir_y(float dir_y) {
        this.dir_y = dir_y;
    }

    public float getDir_z() {
        return dir_z;
    }

    public void setDir_z(float dir_z) {
        this.dir_z = dir_z;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getTfar() {
        return tfar;
    }

    public void setTfar(float tfar) {
        this.tfar = tfar;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Point3f getOrg() {
        return new Point3f(org_x, org_y, org_z);
    }

    public Vector3f getDir() {
        return new Vector3f(dir_x, dir_y, dir_z);
    }

    @Override
    public String toString() {
        return "RTCRay{" +
                "org_x=" + org_x +
                ", org_y=" + org_y +
                ", org_z=" + org_z +
                ", tnear=" + tnear +
                ", dir_x=" + dir_x +
                ", dir_y=" + dir_y +
                ", dir_z=" + dir_z +
                ", time=" + time +
                ", tfar=" + tfar +
                ", mask=" + mask +
                ", id=" + id +
                ", flags=" + flags +
                '}';
    }
}
