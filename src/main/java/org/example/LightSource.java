package org.example;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class LightSource {

    private float power;
    private Color3f color;

    private Point3f pos;

    public LightSource() {
    }

    public LightSource(float power, Color3f color, Point3f pos) {
        this.power = power;
        this.color = color;
        this.pos = pos;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public Color3f getColor() {
        return new Color3f(color);
    }

    public void setColor(Color3f color) {
        this.color = color;
    }

    public Point3f getPos() {
        return new Point3f(pos);
    }

    public void setPos(Point3f pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "LightSource{" +
                "power=" + power +
                ", color=" + color +
                ", pos=" + pos +
                '}';
    }
}
