package ru.itmo.ptopfd;

import com.github.ivelate.JavaHDR.HDRImageRGB;
import ru.itmo.Camera;
import ru.itmo.LightSource;
import ru.itmo.PixelRenderingTask;
import ru.itmo.PixelRenderingTaskN2;
import ru.itmo.Ray;
import ru.itmo.TaskStatUtil;

import javax.media.j3d.Material;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//renderer in PTOPFD package
//PTOPFD - PATH TRACING ONE PASS FIXED DEPTH
public class Renderer {

    //uses intersect1
    public static HDRImageRGB render(Scene scene, Camera camera) {

        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
                for (int h = 0; h < camera.getHeight(); h++) {
                    for (int w = 0; w < camera.getWidth(); w++) {
                        Ray primaryRay = camera.getRay(w, h);
                        Color3f pixelColor = traceRay(primaryRay, scene, camera, 3,4);
                        result.setPixelValue(w, h, 0, pixelColor.x);
                        result.setPixelValue(w, h, 1, pixelColor.y);
                        result.setPixelValue(w, h, 2, pixelColor.z);
                    }
                    if(h % (camera.getHeight() / 10) == 0) {
                    System.out.printf("rendering %.1f%%\n", + (float)h/camera.getHeight() * 100);
                    }
                }
       return result;
    }

    private static Color3f calculateTotalLighting(IntersectionContext intersectionContext, List<Light> lights, Ray r, Scene scene) {
        Color3f totalLighting = new Color3f();

        for (Light light : lights) {
            // Для каждого источника света, вычислить его вклад в общую яркость
            Color3f lightContribution = calculateDiffuseLighting(intersectionContext, light, r, scene);
            totalLighting.add(lightContribution);
            //System.out.println(lightContribution);
        }

        return totalLighting;
    }
    private static Color3f calculateDiffuseLighting(IntersectionContext intersectionContext, Light light, Ray r, Scene scene) {
        Vector3f normal = intersectionContext.getNormal();  // Нормаль к поверхности
        Vector3f pointPos = intersectionContext.getPoint(r.getOrigin(), r.getDir());
        Vector3f lightDirection = new Vector3f();
        lightDirection.sub(pointPos, new Vector3f(light.getPosition()));
        lightDirection.normalize();
        //System.out.println(r.getColor());
        Ray secondRay = new Ray(light.getPosition(), lightDirection, light.getColor());
        Point3f point3f = new Point3f(pointPos);

        float cosTheta = Math.max(0, normal.dot(lightDirection));
        if (cosTheta == 0) {
            return new Color3f(0, 0, 0);  // Свет не достигает поверхности
        }

        Color3f diffuseColor = new Color3f();
        intersectionContext.getMaterial().getDiffuseColor(diffuseColor);
        float lightIntensity = light.getIntensity();  // Нормализованная интенсивность света
        //float lightIntensity = light.getIntensity();  // Нормализованная интенсивность света

        Color3f result = new Color3f(diffuseColor);
        result.scale(cosTheta);
        result.scale(lightIntensity);

        if (intersectionContext.isMirror()) {
            // Если это зеркало, игнорируем тени
            return result;
        }
        if (!scene.isOccluded(secondRay, point3f)) {
                Vector3f coef = new Vector3f(0.0f, 0.0f, 0.0f);
                result.x = coef.x * result.x;
                result.y = coef.y * result.y;
                result.z = coef.z * result.z;
                return result;  // Точка находится в тени

        }

        return result;
    }




    private static Color3f traceRay(Ray ray, Scene scene, Camera camera, int depth, int numSamples) {
        if (depth <= 0) {
            return new Color3f(0, 0, 0);  // Цвет в случае достижения максимальной глубины трассировки
        }

        IntersectionContext intersectionContext = scene.intersect(ray);
        if (intersectionContext.isHit()) {
            Color3f directLighting = calculateTotalLighting(intersectionContext, scene.getLight(), ray, scene);

            // Добавляем зеркальное отражение
            Color3f mirrorReflection = new Color3f();
            Color3f specularColor = new Color3f();
            intersectionContext.getMaterial().getSpecularColor(specularColor);

            if (specularColor.x != 0 || specularColor.y != 0 || specularColor.z != 0) {
                mirrorReflection = traceMirrorRay(ray, scene, depth - 1, camera, numSamples);
                mirrorReflection.x *=specularColor.x;
                mirrorReflection.y *=specularColor.y;
                mirrorReflection.z *=specularColor.z;
                //System.out.println(mirrorReflection);
            }
            // Добавляем индиректное освещение
            Color3f indirectLighting = traceIndirectRay(ray, scene, depth, camera, numSamples);

     //       System.out.println(directLighting);
    //         Комбинируем прямое и индиректное освещение
            directLighting.add(indirectLighting);
            directLighting.add(mirrorReflection);
            return directLighting;
        } else {
            return new Color3f(0, 0, 0);  // Фоновый цвет в случае отсутствия пересечения
        }
    }


    private static Color3f traceMirrorRay(Ray ray, Scene scene, int depth, Camera camera, int numSamples) {
        if (depth <= 0) {
            return new Color3f(0, 0, 0);  // Достигнута максимальная глубина трассировки
        }

        IntersectionContext intersectionContext = scene.intersect(ray);
        if (intersectionContext.isHit()) {
            Color3f mirrorColor = new Color3f();
            intersectionContext.getMaterial().getSpecularColor(mirrorColor);

            // Генерация луча отражения
            Vector3f reflectedDirection = calculateReflectedDirection(intersectionContext.getNormal(), ray.getDir());
            Vector3f pointRay = new Vector3f(intersectionContext.getPoint(ray.getOrigin(), ray.getDir()));
            Point3f pointR = new Point3f(pointRay);
            Ray reflectedRay = new Ray(pointR, reflectedDirection, ray.getColor());
            if (scene.isOccluded(reflectedRay, pointR) && intersectionContext.isMirror()) {
                Color3f reflectedColor = traceRay(reflectedRay, scene, camera, depth - 1, numSamples);
                mirrorColor.x *= reflectedColor.x;
                mirrorColor.y *= reflectedColor.y;
                mirrorColor.z *= reflectedColor.z;
                //System.out.println(mirrorColor);
                return mirrorColor;
            } else {
                return new Color3f(0, 0, 0);  // В случае тени
            }


        }
        return new Color3f(0, 0, 0);  // В случае тени
    }
    private static Vector3f calculateReflectedDirection(Vector3f normal, Vector3f incidentDirection) {
        // Используем закон отражения: угол падения равен углу отражения
        // reflected = incident - 2 * dot(incident, normal) * normal
        Vector3f reflectedDirection = new Vector3f(incidentDirection);
        reflectedDirection.scaleAdd(-2.0f * incidentDirection.dot(normal), normal, reflectedDirection);
        reflectedDirection.normalize();

        return reflectedDirection;
    }


    private static Color3f traceIndirectRay(Ray ray, Scene scene, int depth, Camera camera, int numSamples) {
        if (depth <= 0) {
            return new Color3f(0, 0, 0);  // Достигнута максимальная глубина трассировки
        }

        // ... другие проверки и расчеты ...

        IntersectionContext intersectionContext = scene.intersect(ray);
        if (intersectionContext.isHit()) {
            Color3f indirectColor = new Color3f();
            intersectionContext.getMaterial().getDiffuseColor(indirectColor);

            // Генерация отраженных лучей и их трассировка
            int numNonZeroSamples = 0;
            for (int i = 0; i < numSamples; i++) {
                Vector3f reflectedDirection = generateRandomReflectionVector(intersectionContext.getNormal()
                        ,camera.getDir()
                        ,intersectionContext.getMaterial().getShininess());

                // Вывод отладочной информации
                //System.out.println("Reflected Direction: " + reflectedDirection);

                // Проверка на нулевой вектор
                if (reflectedDirection.lengthSquared() > 0) {
                    numNonZeroSamples++;
                    Vector3f pointRay = new Vector3f(intersectionContext.getPoint(ray.getOrigin(), ray.getDir()));
                    Point3f pointR = new Point3f(pointRay);
                    Ray reflectedRay = new Ray(pointR, reflectedDirection, ray.getColor());

                    // Добавим отладочный вывод для точки отраженного луча


                    Color3f reflectedColor = traceIndirectRay(reflectedRay, scene, depth - 1, camera, numSamples);
                    //System.out.println(intersectionContext);
                    indirectColor.add(reflectedColor);
                }
            }

            // Усреднение вкладов отраженных лучей только если есть ненулевые векторы
            if (numNonZeroSamples > 0) {
                indirectColor.scale(1.0f / numNonZeroSamples);

                return indirectColor; // Возвращаем усредненный цвет отраженного света
            } else {
                return new Color3f(0, 0, 0); // В случае отсутствия ненулевых векторов возвращаем черный цвет
            }
        }
        else {
            return new Color3f(0, 0, 0);  // В случае отсутствия пересечения
        }
    }

//    private static Vector3f generateRandomReflectionVector(Vector3f normal) {
//        // Генерация случайного вектора в полусфере над нормалью
//        float theta = (float) Math.acos(Math.sqrt(Math.random()));
//        float phi = (float) (2 * Math.PI * Math.random());
//
//        // Переводим сферические координаты в декартовы
//        float x = (float) (Math.sin(theta) * Math.cos(phi));
//        float y = (float) (Math.sin(theta) * Math.sin(phi));
//        float z = (float) Math.cos(theta);
//
//        // Поворачиваем вектор относительно нормали
//        Matrix3f rotationMatrix = new Matrix3f();
//        rotationMatrix.setIdentity();
//
//        Vector3f axis = new Vector3f(-normal.y, normal.x, 0);
//        float angle = (float) Math.acos(normal.z);
//        rotationMatrix.set(new AxisAngle4f(axis, angle));
//
//        Vector3f result = new Vector3f(x, y, z);
//        rotationMatrix.transform(result);
//
//        return result;
//    }

    private static Vector3f generateRandomReflectionVector(Vector3f normal, Vector3f viewDirection, float shininess) {
        // Вычисляем полувектор (half vector)
        Vector3f h = new Vector3f();
        h.add(viewDirection, new Vector3f(normal));
        h.normalize();

        // Генерация случайного угла для Фонга
        float theta = (float) Math.acos(Math.pow(Math.random(), 1.0 / (shininess + 1.0)));
        float phi = (float) (2 * Math.PI * Math.random());

        // Вычисляем координаты по сферическим координатам
        float x = (float) (Math.sin(theta) * Math.cos(phi));
        float y = (float) (Math.sin(theta) * Math.sin(phi));
        float z = (float) Math.cos(theta);

        // Поворачиваем вектор относительно полувектора
        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.setIdentity();

        Vector3f axis = new Vector3f(-h.y, h.x, 0);
        float angle = (float) Math.acos(h.z);
        rotationMatrix.set(new AxisAngle4f(axis, angle));

        Vector3f result = new Vector3f(x, y, z);
        rotationMatrix.transform(result);

        return result;
    }

    private static Color3f calculateEnvironmentReflection(IntersectionContext context, Scene scene, Camera camera, Ray ray, int depth, int numSamples) {
        if (depth <= 0) {
            return new Color3f(0, 0, 0); // Возвращаем черный цвет при достижении максимальной глубины рекурсии
        }

        Vector3f reflectionDirection = generateRandomReflectionVector(context.getNormal(),ray.getDir(),context.getMaterial().getShininess());
        Vector3f intersectionPoint = context.getPoint(ray.getOrigin(), ray.getDir());
        Color3f reflectedColor = new Color3f(0, 0, 0);

        for (int i = 0; i < numSamples; i++) {
            // Выбираем случайное направление отражения в пределах некоторого конуса
            Vector3f randomDirection = generateRandomReflectionVector(context.getNormal(),reflectionDirection, context.getMaterial().getShininess());

            // Создаем новый луч для трассировки отражения
            Color3f color = new Color3f();
            context.getMaterial().getDiffuseColor(color);
            Ray reflectedRay = new Ray(new Point3f(intersectionPoint), randomDirection, color);

            // Суммируем цвета, возвращаемые отраженными лучами
            Color3f sampleColor = traceRay(reflectedRay, scene, camera, depth - 1, 4);
            reflectedColor.add(sampleColor);
        }

        // Усредняем цвет, полученный из всех образцов
        reflectedColor.scale(1.0f / numSamples);
        return reflectedColor;
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
//    public static HDRImageRGB renderN(Scene scene, Camera camera) {
//
//        HDRImageRGB result = new HDRImageRGB(camera.getWidth(), camera.getHeight());
//        Ray[] rays = camera.getRays(0, camera.getWidth(), 0, camera.getHeight());
//        IntersectionContext[] ic = scene.intersectAll(rays);
//        for(int h = 0; h < camera.getHeight(); h++) {
//            for (int w = 0; w < camera.getWidth(); w++) {
//                int index = h * camera.getWidth() + w;
//                if(ic[index].isHit()) {
//                    Color3f color = getPixelLuminance(ic[index], scene);
//                    result.setPixelValue(w, h, 0, color.x);
//                    result.setPixelValue(w, h, 1, color.y);
//                    result.setPixelValue(w, h, 2, color.z);
//                }
//                else {
//                    for (int c = 0; c < 3; c++) {
//                        result.setPixelValue(w, h, c, 0);
//                    }
//                }
//            }
//        }
//        return result;
//    }

    //uses intersect1
    public static HDRImageRGB renderParallel(ru.itmo.Scene scene, Camera camera, int threads) {

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

    public static HDRImageRGB renderParallelN(ru.itmo.Scene scene, Camera camera, int threads, int tileSize) {

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
