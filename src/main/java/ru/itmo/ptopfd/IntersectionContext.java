package ru.itmo.ptopfd;

import ru.itmo.Ray;
import ru.itmo.embree.RTCRayHit;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class IntersectionContext {
    private boolean hit;
    private float t;
    private Material material;
    private Vector3f normal;
    private RTCRayHit rtcRayHit;

    private Color3f color;
    private Ray ray;

    public IntersectionContext() {
    }

    public IntersectionContext(boolean hit, float t, Material material, Vector3f normal, RTCRayHit rtcRayHit, Color3f color) {
        this.hit = hit;
        this.t = t;
        this.material = material;
        this.normal = normal;
        this.rtcRayHit = rtcRayHit;
        this.color = color;
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

    public Vector3f getPoint(Point3f origin, Vector3f direction) {
        Vector3f intersectionPoint = new Vector3f();
// Вычисление точки пересечения
     //   System.out.println(t);
        intersectionPoint.scaleAdd(t, direction, origin);
        return intersectionPoint;
    }

    public Color3f getColor() {
        return this.color;
    }

    // Метод для проверки, является ли материал зеркальным
    public boolean isMirror() {
        // Пример условия: материал считается зеркальным, если его спекулярный цвет достаточно яркий
        // Здесь 0.8 - это примерное пороговое значение для каждого компонента цвета
        Color3f specularColor = new Color3f();
        this.material.getSpecularColor(specularColor);
        return specularColor.x > 0.8 && specularColor.y > 0.8 && specularColor.z > 0.8;
    }

    public void setColor(Color3f color) {
         this.color = color;
    }
}