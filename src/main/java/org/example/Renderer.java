package org.example;

import com.github.ivelate.JavaHDR.HDRImageRGB;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Renderer {

    //uses intersect1
    public static HDRImageRGB render(Scene scene, Camera camera) {

        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
        for(int h = 0; h < camera.getHeight(); h++) {
            for(int w = 0; w < camera.getWidth(); w++) {
                Ray r = camera.getRay(w, h);
                IntersectionContext intersectionContext = scene.intersect(r);
                if(intersectionContext.isHit()) {
                    Color3f color = getPixelLuminance(intersectionContext, scene);
                    result.setPixelValue(w, h, 0, color.x);
                    result.setPixelValue(w, h, 1, color.y);
                    result.setPixelValue(w, h, 2, color.z);
                }
                else {
                    for (int c = 0; c < 3; c++) {
                        result.setPixelValue(w, h, c, 0);
                    }
                }
            }

            if(h % (camera.getHeight() / 10) == 0) {
                System.out.println("rendering " + (float)h/camera.getHeight() * 100 + "%");
            }
        }
        return result;
    }

    //uses rtcIntersectM and rtcOccludedM
    public static HDRImageRGB renderN2(Scene scene, Camera camera) {

        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
        Ray[] rays = camera.getRays2(0, camera.getWidth(), 0, camera.getHeight());
        IntersectionContext[] ic = scene.intersectAll(rays);
        Color3f[] lum = getPixelLuminanceN(ic, scene);
        for(int h = 0; h < camera.getHeight(); h++) {
            for (int w = 0; w < camera.getWidth(); w++) {
                int index = h * camera.getWidth() + w;
                    result.setPixelValue(w, h, 0, lum[index].x);
                    result.setPixelValue(w, h, 1, lum[index].y);
                    result.setPixelValue(w, h, 2, lum[index].z);
            }
        }
        return result;
    }

    //uses intersectN
    public static HDRImageRGB renderN(Scene scene, Camera camera) {

        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
        Ray[] rays = camera.getRays(0, camera.getWidth(), 0, camera.getHeight());
        IntersectionContext[] ic = scene.intersectAll(rays);
        for(int h = 0; h < camera.getHeight(); h++) {
            for (int w = 0; w < camera.getWidth(); w++) {
                int index = h * camera.getWidth() + w;
                if(ic[index].isHit()) {
                    Color3f color = getPixelLuminance(ic[index], scene);
                    result.setPixelValue(w, h, 0, color.x);
                    result.setPixelValue(w, h, 1, color.y);
                    result.setPixelValue(w, h, 2, color.z);
                }
                else {
                    for (int c = 0; c < 3; c++) {
                        result.setPixelValue(w, h, c, 0);
                    }
                }
            }
        }
        return result;
    }

    //uses intersect1
    public static HDRImageRGB renderParallel(Scene scene, Camera camera, int threads) {

        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
//        ExecutorService executor = Executors.newFixedThreadPool(threads);
        ThreadPoolExecutor executor = new ThreadPoolExecutor( threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
        TaskStatUtil util = new TaskStatUtil(camera.getHeight() * camera.getWidth());
        for(int h = 0; h < camera.getHeight(); h++) {
            for(int w = 0; w < camera.getWidth(); w++) {
                Runnable task = new PixelRenderingTask(w,h,camera,scene,result, util);
                executor.submit(task);
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static HDRImageRGB renderParallelN(Scene scene, Camera camera, int threads, int tileSize) {

        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
//        ExecutorService executor = Executors.newFixedThreadPool(threads);
        ThreadPoolExecutor executor = new ThreadPoolExecutor( threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
        TaskStatUtil util = new TaskStatUtil(camera.getHeight() * camera.getWidth());
        for(int h = 0; h <= camera.getHeight() - tileSize; h+= tileSize) {
            for(int w = 0; w <= camera.getWidth() - tileSize; w+= tileSize) {
                int wMax = Math.min(camera.getWidth(), w + tileSize);
                int hMax = Math.min(camera.getHeight(), h + tileSize);
                Runnable task = new PixelRenderingTaskN2(w, wMax, h, hMax, camera,scene,result, util);
                executor.submit(task);
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }



    public static Color3f getPixelLuminance(IntersectionContext intersectionContext, Scene scene) {
        Color3f illum = getIntersPointIlluminance(intersectionContext, scene);
        Color3f materialDiffColor = new Color3f();
        intersectionContext.getMaterial().getDiffuseColor(materialDiffColor);

        Color3f result = new Color3f();

        result.x = illum.x * materialDiffColor.x / (float) Math.PI;
        result.y = illum.y * materialDiffColor.y / (float) Math.PI;
        result.z = illum.z * materialDiffColor.z / (float) Math.PI;

        return result;
    }

    public static Color3f[] getPixelLuminanceN(IntersectionContext[] intersectionContext, Scene scene) {
        Point3f[] points = new Point3f[intersectionContext.length];
        for(int i = 0; i < intersectionContext.length; i++) {
            Point3f point = intersectionContext[i].getRay().getOrigin();
            Vector3f rayTVector = intersectionContext[i].getRay().getDir();
            rayTVector.normalize();
            rayTVector.scale(intersectionContext[i].getT());
            point.add(rayTVector);
            points[i] = point;
        }
        boolean[] occluded = scene.areOccludedMultipleByAllLS(points);


        Color3f[] result = new Color3f[points.length];
        Point3f[] LSPos = scene.getLightSources().stream().map(LightSource::getPos).collect(Collectors.toList()).toArray(new Point3f[]{});
        for(int p = 0; p < points.length; p++) {
            Color3f pointIllum = new Color3f(0,0,0);
            if(!intersectionContext[p].isHit()) {
                result[p] = pointIllum;
                continue;
            }
            for(int l = 0; l < scene.getLightSources().size(); l++) {
                if(occluded[p * scene.getLightSources().size() + l]) {
                    Vector3f vecFromLSToPoint = new Vector3f();
                    vecFromLSToPoint.sub(points[p], LSPos[l]);
                    Vector3f dirFromLSToPoint = new Vector3f(vecFromLSToPoint);
                    dirFromLSToPoint.normalize();

                    float incidenceAngleCos = intersectionContext[p].getNormal().dot(dirFromLSToPoint);
                    if(incidenceAngleCos < 0) continue;

                    Color3f illumToAdd = scene.getLightSources().get(l).getColor();
                    illumToAdd.scale(scene.getLightSources().get(l).getPower());

                    illumToAdd.x = illumToAdd.x / vecFromLSToPoint.lengthSquared() * incidenceAngleCos;
                    illumToAdd.y = illumToAdd.y / vecFromLSToPoint.lengthSquared() * incidenceAngleCos;
                    illumToAdd.z = illumToAdd.z / vecFromLSToPoint.lengthSquared() * incidenceAngleCos;

                    pointIllum.add(illumToAdd);
                }
            }
            result[p] = calcLumByLambertBRDF(pointIllum, intersectionContext[p].getMaterial());
        }
        return result;
    }

    private static Color3f calcLumByLambertBRDF(Color3f illum, Material material) {
        Color3f materialDiffColor = new Color3f();
        material.getDiffuseColor(materialDiffColor);

        Color3f result = new Color3f();

        result.x = illum.x * materialDiffColor.x / (float) Math.PI;
        result.y = illum.y * materialDiffColor.y / (float) Math.PI;
        result.z = illum.z * materialDiffColor.z / (float) Math.PI;

        return result;
    }

    private static Color3f getIntersPointIlluminance(IntersectionContext cameraIntersectionContext, Scene scene) {
        Point3f point = cameraIntersectionContext.getRay().getOrigin();
        Vector3f rayTVector = cameraIntersectionContext.getRay().getDir();
        rayTVector.normalize();
        rayTVector.scale(cameraIntersectionContext.getT());
        point.add(rayTVector);

        Color3f illuminance = new Color3f(0, 0, 0);

        for(LightSource ls : scene.getLightSources()) {
            Vector3f vecFromLSToPoint = new Vector3f();
            vecFromLSToPoint.sub(point, ls.getPos());

            Vector3f dirFromLsToPoint = new Vector3f(vecFromLSToPoint);
            dirFromLsToPoint.normalize();

            Ray fromLsToP = new Ray(ls.getPos(), dirFromLsToPoint, ls.getColor());

            boolean occluded = scene.isOccluded(fromLsToP, point);
            if(occluded) {
//                float incidenceAngle = cameraIntersectionContext.getNormal().angle(vecFromLSToPoint);
                float incidenceAngleCos = cameraIntersectionContext.getNormal().dot(dirFromLsToPoint);
//                if(incidenceAngle > (Math.PI / 2 - 0.01f)) continue;
                if(incidenceAngleCos < 0) continue;
                Color3f illumToAdd = ls.getColor();
                illumToAdd.scale(ls.getPower());

//                illumToAdd.x = illumToAdd.x / vecFromLSToPoint.lengthSquared() * (float) Math.cos(incidenceAngle);
//                illumToAdd.y = illumToAdd.y / vecFromLSToPoint.lengthSquared() * (float) Math.cos(incidenceAngle);
//                illumToAdd.z = illumToAdd.z / vecFromLSToPoint.lengthSquared() * (float) Math.cos(incidenceAngle);

                illumToAdd.x = illumToAdd.x / vecFromLSToPoint.lengthSquared() * incidenceAngleCos;
                illumToAdd.y = illumToAdd.y / vecFromLSToPoint.lengthSquared() * incidenceAngleCos;
                illumToAdd.z = illumToAdd.z / vecFromLSToPoint.lengthSquared() * incidenceAngleCos;
                illuminance.add(illumToAdd);
            }
        }
        illuminance.clamp(0, Float.MAX_VALUE);
        return illuminance;
    }

    private static List<LightSource> getLsForHemisphere(IntersectionContext intersectionContext, Scene scene) {

        return scene.getLightSources();
    }

}
