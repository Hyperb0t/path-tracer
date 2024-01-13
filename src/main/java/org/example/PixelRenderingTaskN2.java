package org.example;

import com.github.ivelate.JavaHDR.HDRImageRGB;

import javax.vecmath.Color3f;

import static org.example.Renderer.getPixelLuminance;
import static org.example.Renderer.getPixelLuminanceN;

public class PixelRenderingTaskN2 implements Runnable {

    private int wMin;
    private int wMax;
    private int hMin;
    private int hMax;
    private Camera camera;
    private Scene scene;
    private HDRImageRGB result;

    private TaskStatUtil taskStatUtil;

    public PixelRenderingTaskN2(int wMin, int wMax, int hMin, int hMax, Camera camera, Scene scene, HDRImageRGB result, TaskStatUtil taskStatUtil) {
        this.wMin = wMin;
        this.wMax = wMax;
        this.hMin = hMin;
        this.hMax = hMax;
        this.camera = camera;
        this.scene = scene;
        this.result = result;
        this.taskStatUtil = taskStatUtil;
    }

    @Override
    public void run() {
        Ray[] rays = camera.getRays2(wMin, wMax, hMin, hMax);
        IntersectionContext[] ic = scene.intersectAll2(rays);
        Color3f[] lum = getPixelLuminanceN(ic, scene);
        int totalH = (hMax - hMin);
        int totalW = (wMax - wMin);
        for(int h = hMin; h < hMax; h++) {
            for (int w = wMin; w < wMax; w++) {
                int index = (h - hMin) * totalW + (w - wMin);
                    result.setPixelValue(w, h, 0, lum[index].x);
                    result.setPixelValue(w, h, 1, lum[index].y);
                    result.setPixelValue(w, h, 2, lum[index].z);
            }
        }

    }
}
