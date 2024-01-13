package org.example;

import com.github.ivelate.JavaHDR.HDRImageRGB;

import javax.vecmath.Color3f;

import static org.example.Renderer.getPixelLuminance;

public class PixelRenderingTask implements Runnable {

    private int w;
    private int h;
    private Camera camera;
    private Scene scene;
    private HDRImageRGB result;

    private TaskStatUtil taskStatUtil;

    public PixelRenderingTask(int w, int h, Camera camera, Scene scene, HDRImageRGB result, TaskStatUtil util) {
        this.w = w;
        this.h = h;
        this.camera = camera;
        this.scene = scene;
        this.result = result;
        this.taskStatUtil = util;
    }

    @Override
    public void run() {
        Ray r = camera.getRay(w, h);
        IntersectionContext intersectionContext = scene.intersect(r);
        if (intersectionContext.isHit()) {
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
        taskStatUtil.incrementAndInfo();
    }
}
