package org.example;

import mikera.vectorz.Vector3;

import javax.vecmath.Vector3f;
import java.util.Collections;

public class VectorExperiments {
    public static void main(String[] args) {
        Vector3f vector3f = new Vector3f(10,9,8);
        Vector3 vector3 = Vector3.of(10,9,8);
        org.joml.Vector3f vector3f1 = new org.joml.Vector3f(10,9,8);

        var l1 = Collections.nCopies((int) 10e7, vector3f);
        var l2 = Collections.nCopies((int) 10e7, vector3);
        var l3 = Collections.nCopies((int) 10e7, vector3f1);

        System.out.println("normalizing 10 million vectors:");

        long time = System.currentTimeMillis();
        for(var v : l1) {
            v.normalize();
        }
        System.out.println(System.currentTimeMillis() - time + " ms - javax.vecmath.Vector3f");

        time = System.currentTimeMillis();
        for(var v : l2) {
            v.normalise();
        }
        System.out.println(System.currentTimeMillis() - time + " ms - mikera.vectorz.Vector3");

        time = System.currentTimeMillis();
        for(var v : l3) {
            v.normalize();
        }
        System.out.println(System.currentTimeMillis() - time + " ms - org.joml.Vector3f");
    }
}
