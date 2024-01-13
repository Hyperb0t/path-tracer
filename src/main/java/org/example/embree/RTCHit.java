package org.example.embree;

import com.sun.jna.Structure;

@Structure.FieldOrder({"Ng_x", "Ng_y", "Ng_z", "u", "v", "primID", "geomID", "instID"})
public class RTCHit extends Structure {
    float Ng_x;          // x coordinate of geometry normal
    float Ng_y;          // y coordinate of geometry normal
    float Ng_z;          // z coordinate of geometry normal

    float u;             // barycentric u coordinate of hit
    float v;             // barycentric v coordinate of hit

    int primID; // primitive ID
    int geomID; // geometry ID
    int instID;

    public RTCHit() {
        super();
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
}
