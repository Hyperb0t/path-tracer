package org.example;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@State(Scope.Benchmark)
public class IntersectSpeedExperiments {

//    public static void main(String[] args) throws IOException {
//        Scene scene = SceneLoader.loadOBJ("models/cornell_box.obj");
//                Ray r = new Ray(new Point3f(551.8f, 0.0f, 0.0f), new Vector3f(1,0,0), new Color3f(1,1,1));
//        System.out.println(r);
//        long time1 = System.currentTimeMillis();
//        var res = scene.intersectAll(Collections.nCopies((int)1e7, r).toArray(new Ray[]{}));
//        System.out.println("intersectAll 10M rays time: " + (System.currentTimeMillis() - time1));
//    }
    public Scene scene;
    public Ray[] rays;

    public IntersectSpeedExperiments() {
        try {
            scene = SceneLoader.loadOBJ("models/cornell_box.obj");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Ray r = new Ray(new Point3f(551.8f, 0.0f, 0.0f), new Vector3f(1,0,0), new Color3f(1,1,1));
        rays = Collections.nCopies((int)1e7, r).toArray(new Ray[]{});
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @BenchmarkMode(Mode.AverageTime)
    public void intersect10m() throws IOException {
        long time1 = System.currentTimeMillis();
        var res = scene.intersectAll(rays);
        System.out.println("intersectAll 10M rays time: " + (System.currentTimeMillis() - time1));
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @BenchmarkMode(Mode.AverageTime)
    public void intersect10m2() throws IOException {
        long time1 = System.currentTimeMillis();
        var res = scene.intersectAll2(rays);
        System.out.println("intersectAll2 10M rays time: " + (System.currentTimeMillis() - time1));
    }

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
//        IntersectSpeedExperiments ise = new IntersectSpeedExperiments();
//        for(int i = 0; i < 10; i++) {
//            ise.intersect10m();
//        }
//        System.out.println();
//        for(int i = 0; i < 10; i++) {
//            ise.intersect10m2();
//        }
    }


}
