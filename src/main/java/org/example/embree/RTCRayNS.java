package org.example.embree;

import com.sun.jna.Structure;
import net.openhft.chronicle.bytes.Bytes;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class RTCRayNS implements Serializable {
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

    public static final int STRUCT_BYTE_SIZE = 48;

    public RTCRayNS() {
        super();
    }

    public RTCRayNS(float org_x, float org_y, float org_z, float tnear, float dir_x, float dir_y, float dir_z, float tfar) {
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

    public RTCRayNS(Vector3f org, Vector3f dir, float tnear, float tfar) {
        this(org.x, org.y, org.z, tnear, dir.x, dir.y, dir.z, tfar);
    }

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
        return b.array();
    }

    //needs pre-allocated bytebuffer with little endian byte order
    //but does not spend time for allocation
    public byte[] toBytes(ByteBuffer b) {
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
        return b.array();
    }

    public byte[] toBytes2(Bytes b) {
        b.unsafeWriteObject(this, STRUCT_BYTE_SIZE);
        return b.toByteArray();
    }

    public static RTCRayNS fromBytes(byte[] bytes) {
        if(bytes.length != STRUCT_BYTE_SIZE) {
            throw new IllegalArgumentException("array size should be " + STRUCT_BYTE_SIZE);
        }
        RTCRayNS r = new RTCRayNS();
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
        return r;
    }

    //needs pre-allocated bytebuffer with little endian byte order
    //but does not spend time for allocation
    public static RTCRayNS fromBytes(ByteBuffer b) {
        RTCRayNS r = new RTCRayNS();
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
        return r;
    }

    public static RTCRayNS fromBytes2(Bytes b) {
        RTCRayNS r = new RTCRayNS();
        b.unsafeReadObject(r, STRUCT_BYTE_SIZE);
        return r;
    }

    public static float tfarFromBytes(ByteBuffer b) {
        return b.getFloat(32);
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
        return "RTCRayNS{" +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RTCRayNS rtcRayNS = (RTCRayNS) o;
        return Float.compare(rtcRayNS.org_x, org_x) == 0 && Float.compare(rtcRayNS.org_y, org_y) == 0 && Float.compare(rtcRayNS.org_z, org_z) == 0 && Float.compare(rtcRayNS.tnear, tnear) == 0 && Float.compare(rtcRayNS.dir_x, dir_x) == 0 && Float.compare(rtcRayNS.dir_y, dir_y) == 0 && Float.compare(rtcRayNS.dir_z, dir_z) == 0 && Float.compare(rtcRayNS.time, time) == 0 && Float.compare(rtcRayNS.tfar, tfar) == 0 && mask == rtcRayNS.mask && id == rtcRayNS.id && flags == rtcRayNS.flags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(org_x, org_y, org_z, tnear, dir_x, dir_y, dir_z, time, tfar, mask, id, flags);
    }
}
