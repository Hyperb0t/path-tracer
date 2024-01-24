package ru.itmo;

import ru.itmo.embree.RTCRayHit;

import javax.media.j3d.Material;
import javax.vecmath.Vector3f;

public class IntersectionContext {
    private boolean hit;
    private float t;
    private Material material;
    private Vector3f normal;
    private RTCRayHit rtcRayHit;

    private Ray ray;

    public IntersectionContext() {
    }

    public IntersectionContext(boolean hit, float t, Material material, Vector3f normal, RTCRayHit rtcRayHit) {
        this.hit = hit;
        this.t = t;
        this.material = material;
        this.normal = normal;
        this.rtcRayHit = rtcRayHit;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Vector3f getNormal() {
        return new Vector3f(normal);
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public RTCRayHit getRtcRayHit() {
        return rtcRayHit;
    }

    public void setRtcRayHit(RTCRayHit rtcRayHit) {
        this.rtcRayHit = rtcRayHit;
    }

    public Ray getRay() {
        return ray;
    }

    public void setRay(Ray ray) {
        this.ray = ray;
    }

    @Override
    public String toString() {
        return "IntersectionContext{" +
                "hit=" + hit +
                ", t=" + t +
                ", material=" + material +
                ", normal=" + normal +
                ", rtcRayHit=" + rtcRayHit +
                '}';
    }
}