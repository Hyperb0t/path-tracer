package ru.itmo;

import com.github.ivelate.JavaHDR.HDRImageRGB;

import javax.vecmath.Color3f;

import static ru.itmo.Renderer.getPixelLuminance;

public class PixelRenderingTaskN implements Runnable {

    private int wMin;
    private int wMax;
    private int hMin;
    private int hMax;
    private Camera camera;
    private Scene scene;
    private HDRImageRGB result;

    private TaskStatUtil taskStatUtil;

    public PixelRenderingTaskN(int wMin, int wMax, int hMin, int hMax, Camera camera, Scene scene, HDRImageRGB result, TaskStatUtil taskStatUtil) {
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
        Ray[] rays = camera.getRays(wMin, wMax, hMin, hMax);
        IntersectionContext[] ic = scene.intersectAll(rays);
        int totalH = (hMax - hMin);
        int totalW = (wMax - wMin);
        for(int h = hMin; h < hMax; h++) {
            for(int w = wMin; w < wMax; w++) {
                int index = (h-hMin) * totalW + (w-wMin);
                if (ic[index].isHit()) {
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
                taskStatUtil.incrementAndInfo();
            }
        }

    }
}
