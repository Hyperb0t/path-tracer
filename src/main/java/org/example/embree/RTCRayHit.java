package org.example.embree;

import com.sun.jna.Structure;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

//@Structure.FieldOrder({"rtcRay", "rtcHit"})
@Structure.FieldOrder({"org_x", "org_y", "org_z", "tnear", "dir_x", "dir_y", "dir_z", "time", "tfar", "mask", "id", "flags",
        "Ng_x", "Ng_y", "Ng_z", "u", "v", "primID", "geomID", "instID"})
public class RTCRayHit extends Structure {

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

    public float Ng_x = 0;          // x coordinate of geometry normal
    public float Ng_y = 0;          // y coordinate of geometry normal
    public float Ng_z = 0;          // z coordinate of geometry normal

    public float u = 0;             // barycentric u coordinate of hit
    public float v = 0;             // barycentric v coordinate of hit

    public int primID = 0; // primitive ID
    public int geomID = Embree.RTC_INVALID_GEOMETRY_ID; // geometry ID
    public int instID = 0;
    
    public static final int STRUCT_BYTE_SIZE = 80;

    public byte[] toBytes() {
        ByteBuffer b = ByteBuffer.allocate(STRUCT_BYTE_SIZE);
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putFloat(org_x);
        b.putFloat(org_y);
        b.putFloat(org_z);
        b.putFloat(tnear);
        b.putFloat(dir_x);
        b.putFloat(dir_y);
        b.putFloat(dir_z);
        b.putFloat(time);
        b.putFloat(tfar);
        b.putInt(mask);
        b.putInt(id);
        b.putInt(flags);
        b.putFloat(Ng_x);
        b.putFloat(Ng_y);
        b.putFloat(Ng_z);
        b.putFloat(u);
        b.putFloat(v);
        b.putInt(primID);
        b.putInt(geomID);
        b.putInt(instID);
        return b.array();
    }
    
    public static RTCRayHit fromBytes(byte[] bytes) {
        if(bytes.length != STRUCT_BYTE_SIZE) {
            throw new IllegalArgumentException("array size should be " + STRUCT_BYTE_SIZE);
        }
        RTCRayHit r = new RTCRayHit();
        ByteBuffer b = ByteBuffer.wrap(bytes);
        b.order(ByteOrder.LITTLE_ENDIAN);
        r.setOrg_x(b.getFloat());
        r.setOrg_y(b.getFloat());
        r.setOrg_z(b.getFloat());
        r.setTnear(b.getFloat());
        r.setDir_x(b.getFloat());
        r.setDir_y(b.getFloat());
        r.setDir_z(b.getFloat());
        r.setTime(b.getFloat());
        r.setTfar(b.getFloat());
        r.setMask(b.getInt());
        r.setId(b.getInt());
        r.setFlags(b.getInt());
        r.setNg_x(b.getFloat());
        r.setNg_y(b.getFloat());
        r.setNg_z(b.getFloat());
        r.setU(b.getFloat());
        r.setV(b.getFloat());
        r.setPrimID(b.getInt());
        r.setGeomID(b.getInt());
        r.setInstID(b.getInt());
        return r;
    }

    public void setOrg(float x, float y, float z) {
        this.org_x = x;
        this.org_y = y;
        this.org_z = z;
    }

    public void setDir(float x, float y, float z) {
        this.dir_x = x;
        this.dir_y = y;
        this.dir_z = z;
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

    public float getNg_x() {
        return Ng_x;
    }

    public void setNg_x(float ng_x) {
        Ng_x = ng_x;
    }

    public float getNg_y() {
        return Ng_y;
    }

    public void setNg_y(float ng_y) {
        Ng_y = ng_y;
    }

    public float getNg_z() {
        return Ng_z;
    }

    public void setNg_z(float ng_z) {
        Ng_z = ng_z;
    }

    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public int getPrimID() {
        return primID;
    }

    public void setPrimID(int primID) {
        this.primID = primID;
    }

    public int getGeomID() {
        return geomID;
    }

    public void setGeomID(int geomID) {
        this.geomID = geomID;
    }

    public int getInstID() {
        return instID;
    }

    public void setInstID(int instID) {
        this.instID = instID;
    }

    @Override
    public String toString() {
        return "RTCRayHit{" +
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
                ", Ng_x=" + Ng_x +
                ", Ng_y=" + Ng_y +
                ", Ng_z=" + Ng_z +
                ", u=" + u +
                ", v=" + v +
                ", primID=" + primID +
                ", geomID=" + geomID +
                ", instID=" + instID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
        RTCRayHit rtcRayHit = (RTCRayHit) o;
        return Float.compare(rtcRayHit.org_x, org_x) == 0 && Float.compare(rtcRayHit.org_y, org_y) == 0 && Float.compare(rtcRayHit.org_z, org_z) == 0 && Float.compare(rtcRayHit.tnear, tnear) == 0 && Float.compare(rtcRayHit.dir_x, dir_x) == 0 && Float.compare(rtcRayHit.dir_y, dir_y) == 0 && Float.compare(rtcRayHit.dir_z, dir_z) == 0 && Float.compare(rtcRayHit.time, time) == 0 && Float.compare(rtcRayHit.tfar, tfar) == 0 && mask == rtcRayHit.mask && id == rtcRayHit.id && flags == rtcRayHit.flags && Float.compare(rtcRayHit.Ng_x, Ng_x) == 0 && Float.compare(rtcRayHit.Ng_y, Ng_y) == 0 && Float.compare(rtcRayHit.Ng_z, Ng_z) == 0 && Float.compare(rtcRayHit.u, u) == 0 && Float.compare(rtcRayHit.v, v) == 0 && primID == rtcRayHit.primID && geomID == rtcRayHit.geomID && instID == rtcRayHit.instID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), org_x, org_y, org_z, tnear, dir_x, dir_y, dir_z, time, tfar, mask, id, flags, Ng_x, Ng_y, Ng_z, u, v, primID, geomID, instID);
    }
}

//    public RTCRay rtcRay;
//    public RTCHit rtcHit;
//
//    public RTCRayHit() {
//        super();
//    }
//
//    public RTCRay getRtcRay() {
//        return rtcRay;
//    }
//
//    public void setRtcRay(RTCRay rtcRay) {
//        this.rtcRay = rtcRay;
//    }
//
//    public RTCHit getRtcHit() {
//        return rtcHit;
//    }
//
//    public void setRtcHit(RTCHit rtcHit) {
//        this.rtcHit = rtcHit;
//    }
//}
