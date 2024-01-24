package ru.itmo;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Camera {
    private Point3f pos;
    private Vector3f up;
    private Vector3f dir;
    private float fov; //horizontal, degrees

    private int width;
    private int height;

    public Camera() {
    }

    public Camera(Point3f pos, Vector3f up, Vector3f dir, float fov) {
        this.pos = pos;
        this.up = up;
        this.up.normalize();
        this.dir = dir;
        this.dir.normalize();
        this.fov = fov;
    }

    public Camera(Point3f pos, Vector3f up, Vector3f dir, float fov, int width, int height) {
        this.pos = pos;
        this.up = up;
        this.dir = dir;
        this.fov = fov;
        this.width = width;
        this.height = height;
    }

    public Ray getRay(int w, int h) {
        if(w >= width || w < 0) {
            throw new IllegalArgumentException("index w outside of border: " + w);
        }
        if(h >= width || h < 0) {
            throw new IllegalArgumentException("index h outside of border: " + h);
        }

        Vector3f hxDirection = new Vector3f();
        hxDirection.cross(dir, up);
        hxDirection.normalize();

        Vector3f hyDirection = new Vector3f(up);
        hyDirection.normalize();

        float aspectRatio = (float) width / (float) height;
        float verticalFov = fov / aspectRatio;
        float pixelWidthDeg = fov / width;
        float pixelHeightDeg = verticalFov / height;

        double hxModule = -Math.tan(Math.toRadians(fov / 2) - Math.toRadians(w * pixelWidthDeg) + Math.toRadians(pixelWidthDeg / 2));
        double hyModule = Math.tan(Math.toRadians(verticalFov / 2) - Math.toRadians(h * pixelHeightDeg) + Math.toRadians(pixelHeightDeg / 2));

        Vector3f hx = new Vector3f();
        hx.scale((float) hxModule, hxDirection);

        Vector3f hy = new Vector3f();
        hy.scale((float) hyModule, hyDirection);

        Point3f raySecondPoint = new Point3f(pos);
        raySecondPoint.add(dir);
        raySecondPoint.add(hx);
        raySecondPoint.add(hy);

        Vector3f rayDirection = new Vector3f();
        rayDirection.sub(raySecondPoint, pos);
        rayDirection.normalize();

        return new Ray(new Point3f(pos), rayDirection, null);
    }

    //min index is included; max index is not included
    public Ray[] getRays(int wMin, int wMax, int hMin, int hMax) {
        int totalH = (hMax - hMin);
        int totalW = (wMax - wMin);
        Ray[] result = new Ray[totalW * totalH];
        for(int h = hMin; h < hMax; h++) {
            for(int w = wMin; w < wMax; w++) {
                int index = (h-hMin) * totalW + (w-wMin);
                result[index] = getRay(w,h);
            }
        }
        return result;
    }

    public Ray[] getRays2(int wMin, int wMax, int hMin, int hMax) {
        int totalH = (hMax - hMin);
        int totalW = (wMax - wMin);
        Ray[] result = new Ray[totalW * totalH];

        Vector3f hxDirection = new Vector3f();
        hxDirection.cross(dir, up);
        hxDirection.normalize();

        Vector3f hyDirection = new Vector3f(up);
        hyDirection.normalize();

        float aspectRatio = (float) width / (float) height;
        float verticalFov = fov / aspectRatio;
        float pixelWidthDeg = fov / width;
        float pixelHeightDeg = verticalFov / height;

        double hxModuleMax = Math.tan(Math.toRadians(fov / 2));
        double hyModuleMax = Math.tan(Math.toRadians(verticalFov / 2));

        float hxModuleStep = (float) (2 * hxModuleMax / width);
        float hyModuleStep = (float) (2* hyModuleMax / height);

        for(int h = hMin; h < hMax; h++) {
            for(int w = wMin; w < wMax; w++) {
                int index = (h-hMin) * totalW + (w-wMin);

                float hxModule = (float) (w * hxModuleStep - hxModuleMax);
                float hyModule = (float) (hyModuleMax - h * hyModuleStep);

                Vector3f hx = new Vector3f();
                hx.scale(hxModule, hxDirection);

                Vector3f hy = new Vector3f();
                hy.scale(hyModule, hyDirection);

//                Point3f raySecondPoint = new Point3f(pos);
//                raySecondPoint.add(dir);
//                raySecondPoint.add(hx);
//                raySecondPoint.add(hy);
//
                Vector3f rayDirection = new Vector3f();
                rayDirection.add(dir);
                rayDirection.add(hx);
                rayDirection.add(hy);
//                rayDirection.sub(raySecondPoint, pos);
                rayDirection.normalize();

                result[index] = new Ray(new Point3f(pos), rayDirection, new Color3f(1.0f, 1.0f, 1.0f));
            }
        }
        return result;
    }

    public Point3f getPos() {
        return pos;
    }

    public void setPos(Point3f pos) {
        this.pos = pos;
    }

    public Vector3f getUp() {
        return up;
    }

    public void setUp(Vector3f up) {
        this.up = up;
        this.up.normalize();
    }

    public Vector3f getDir() {
        return new Vector3f(dir);
    }

    public void setDir(Vector3f dir) {
        this.dir = dir;
        this.dir.normalize();
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "pos=" + pos +
                ", up=" + up +
                ", dir=" + dir +
                ", fov=" + fov +
                '}';
    }
}
