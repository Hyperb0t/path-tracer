package ru.itmo;

import com.github.ivelate.JavaHDR.HDREncoder;
import com.github.ivelate.JavaHDR.HDRImageRGB;

import javax.imageio.ImageIO;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class HdrExperiments {
    public static void main(String[] args) throws IOException {
        float[] imageData =
                {
                        1f, 0, 0,
                        0, 1f, 0,
                        0, 0, 1f
                };

        HDRImageRGB hdrImageRGB = new HDRImageRGB(3, 1, imageData);
        HDREncoder.writeHDR(hdrImageRGB, new File("test.hdr"));
        BufferedImage bufferedImage = ImageIO.read(new File("jpg.jpg"));
        Raster raster = bufferedImage.getRaster();
        float[] data = new float[raster.getHeight() * raster.getWidth() * 3];
        raster.getPixels(0,0,bufferedImage.getWidth(), bufferedImage.getHeight(), data);
        HDREncoder.writeHDR(new HDRImageRGB(raster.getWidth(), raster.getHeight(), data), new File("test2.hdr"));

        System.out.println(new Color3f(10, 20, 30));
    }
}
