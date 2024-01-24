package ru.itmo.ptopfd;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

// Интерфейс для различных типов источников света
// Интерфейс для различных типов источников света
public interface Light {

    // Обязательные свойства
    Color3f getColor();     // Цвет света

    float getIntensity();    // Интенсивность света

    Point3f getPosition();   // Позиция источника света

    // Дополнительные свойства (указываются при возможности)
    Vector3f getDirection();   // Направление света (для направленных источников)

    float getRange();          // Дальность света (для точечных источников)

    float getConeAngle();      // Угол излучения конуса света (для прожекторов)

    float getFalloff();        // Коэффициент рассеивания

    boolean isShadows();      // Наличие теней

    Color3f illuminate(Point3f point, Vector3f normal);
}


