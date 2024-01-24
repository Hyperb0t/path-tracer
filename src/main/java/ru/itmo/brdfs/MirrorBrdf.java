package ru.itmo.brdfs;

import ru.itmo.Ray;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class MirrorBrdf implements Brdf{
    @Override
    public Ray reflectRay(Ray previousRay, Vector3f surfaceNormal, Color3f surfaceColor) {
        System.out.println("normal normalization test: len=" + surfaceNormal.length());
        float dotProduct = previousRay.getDir().dot(surfaceNormal);
        Vector3f doubledNormal = new Vector3f(surfaceNormal);
        doubledNormal.scale(2);
        Vector3f negativeDir = new Vector3f(previousRay.getDir());
        negativeDir.negate();

//        Vector3f reflectDir = surfaceNormal * 2 * viewVec.dot(N) - viewVec;
        return null;
    }
}
