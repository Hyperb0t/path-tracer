package org.example.embree;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.JarFile;

import com.sun.jna.Platform;

public class NativeLibsManager {
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    private static final String TEMP_DIR = System.getProperty(JAVA_IO_TMPDIR);
    private static final String JNA_LIBRARY_PATH = "jna.library.path";

    public static void prepareNativeLibs() throws Exception {
        // ...
        // path management here maybe suboptimal ... feel free to improve
        // from https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
        URL current_jar_dir = NativeLibsManager.class.getProtectionDomain().getCodeSource().getLocation();
        Path jar_path = Paths.get(current_jar_dir.toURI());
        String folderContainingJar = jar_path.getParent().toString();

        ResourceCopy r = new ResourceCopy(); // class from https://stackoverflow.com/a/58318009/7237062 
        Optional<JarFile> jar = r.jar(NativeLibsManager.class);
        if (jar.isPresent()) {
            try {
                System.out.println("JAR detected");
                File target_dir = new File(TEMP_DIR);
                System.out.println(String.format("Trying copy from %s %s to %s", jar.get().getName(), Platform.RESOURCE_PREFIX, target_dir));
                // perform dir copy
                r.copyResourceDirectory(jar.get(), Platform.RESOURCE_PREFIX, target_dir);
                // add created folders to JNA lib loading path
                System.setProperty(JNA_LIBRARY_PATH, target_dir.getCanonicalPath().toString());
            } catch (Exception e) {
                e.printStackTrace(); // TODO: handle exception ?
            }
        } else {
            System.out.println("NO JAR");
        }
    }
}