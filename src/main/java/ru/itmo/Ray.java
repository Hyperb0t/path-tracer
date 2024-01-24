package ru.itmo;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Objects;

public class Ray {

    private Point3f origin;
    private Vector3f dir;
    private Color3f color;

    int id;

    public Ray() {
    }

    public Ray(Point3f origin, Vector3f dir, Color3f color) {
        this.origin = origin;
        this.dir = dir;
        this.color = color;
    }

    public Point3f getOrigin() {
        return new Point3f(origin);
    }

    public void setOrigin(Point3f origin) {
        this.origin = origin;
    }

    public Vector3f getDir() {
        return new Vector3f(dir);
    }

    public void setDir(Vector3f dir) {
        this.dir = dir;
    }

    public Color3f getColor() {
        return new Color3f(color);
    }

    public void setColor(Color3f color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getOrgX() {
        return origin.x;
    }

    public float getOrgY() {
        return origin.y;
    }

    public float getOrgZ() {
        return origin.z;
    }

    public float getDirX() {
        return dir.x;
    }

    public float getDirY() {
        return dir.y;
    }

    public float getDirZ() {
        return dir.z;
    }

    @Override
    public String toString() {
        return "Ray{" +
                "origin=" + origin +
                ", dir=" + dir +
                ", color=" + color +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ray ray = (Ray) o;
        return id == ray.id && Objects.equals(origin, ray.origin) && Objects.equals(dir, ray.dir) && Objects.equals(color, ray.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, dir, color, id);
    }
}
