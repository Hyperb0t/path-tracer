package ru.itmo.ptopfd;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class RectangularLight implements Light {

    private Color3f color;
    private float intensity;
    private Point3f position;
    private Vector3f direction; // Направление света (может быть использовано для определения ориентации прямоугольника)
    private float length;
    private float width;

    public RectangularLight(Color3f color, float intensity, Point3f position, Vector3f direction, float length, float width) {
        this.color = color;
        this.intensity = intensity;
        this.position = position;
        this.direction = direction;
        this.length = length;
        this.width = width;
    }

    @Override
    public Color3f illuminate(Point3f point, Vector3f normal) {
        // Логика вычисления освещенности для прямоугольного света
        // ...
        return new Color3f(); // Вернуть цвет освещенности
    }

    // Другие геттеры и сеттеры

    @Override
    public Color3f getColor() {
        return null;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }

    @Override
    public Point3f getPosition() {
        return null;
    }

    @Override
    public Vector3f getDirection() {
        return direction;
    }

    @Override
    public float getRange() {
        // Прямоугольные источники света могут иметь бесконечный дальний свет,
        // поэтому возвращаем 0, что может интерпретироваться как бесконечность.
        return 0;
    }

    @Override
    public float getConeAngle() {
        // У прямоугольных источников света нет конуса света, поэтому возвращаем 0.
        return 0;
    }

    @Override
    public float getFalloff() {
        // У прямоугольных источников света нет коэффициента рассеивания.
        return 0;
    }

    @Override
    public boolean isShadows() {
        // Прямоугольные источники света могут создавать мягкие тени,
        // поэтому можно возвращать true, если требуется учет теней.
        return false;
    }
}

