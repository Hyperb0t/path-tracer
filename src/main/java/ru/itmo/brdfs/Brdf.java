package ru.itmo.brdfs;

import ru.itmo.Ray;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public interface Brdf {
    public Ray reflectRay(Ray previousRay, Vector3f surfaceNormal, Color3f surfaceColor);
}
