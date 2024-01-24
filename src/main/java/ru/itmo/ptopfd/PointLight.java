package ru.itmo.ptopfd;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
// Класс для точечного источника света
public class PointLight implements Light {

    private Color3f color;
    private float intensity;
    private Point3f position;
    private float constantAttenuation;
    private float linearAttenuation;
    private float quadraticAttenuation;

    public PointLight(Color3f color, float intensity, Point3f position) {
        this.color = color;
        this.intensity = intensity;
        this.position = position;
        this.constantAttenuation = 1.0f;
        this.linearAttenuation = 0.0f;
        this.quadraticAttenuation = 0.0f;
    }

    // Реализация метода интерфейса Light
    @Override
    public Color3f getColor() {
        return color;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }

    @Override
    public Point3f getPosition() {
        return position;
    }

    // Дополнительные геттеры и сеттеры

    @Override
    public Vector3f getDirection() {
        return null; // Замените на нужную логику
    }

    @Override
    public float getRange() {
        return 0; // Замените на нужную логику
    }

    @Override
    public float getConeAngle() {
        return 0; // Замените на нужную логику
    }

    @Override
    public float getFalloff() {
        return 0; // Замените на нужную логику
    }

    @Override
    public boolean isShadows() {
        return false; // Замените на нужную логику
    }

    @Override
    public Color3f illuminate(Point3f point, Vector3f normal) {
        return null;
    }
}