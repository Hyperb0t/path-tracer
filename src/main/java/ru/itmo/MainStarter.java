package ru.itmo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import ru.itmo.jcommander.ProgramArguments;
import ru.itmo.runmode.Standalone;
import ru.itmo.runmode.federated.RendererServer;
import ru.itmo.runmode.federated.SceneClient;

public class MainStarter {

    public static void main(String[] args) throws Exception {
        ProgramArguments appArgs = new ProgramArguments();
        JCommander jCommander = JCommander.newBuilder().addObject(appArgs).build();
        jCommander.parse(args);
        if(appArgs.isHelp()) {
            for(ParameterDescription pd :  jCommander.getParameters()) {
                System.out.println(pd.getNames() + " \t default " + pd.getDefault());
                System.out.println(pd.getDescription());
            }
            System.exit(0);
        }
        switch (appArgs.getRunMode()){
            case Scene:
                SceneClient.run(appArgs);
                break;

            case Renderer:
                RendererServer.run(appArgs);
                break;

            default:
                Standalone.run(appArgs);
                break;
        }
    }

//    public static void main(String[] args) throws Exception {
////        new Scanner(System.in).nextLine();
////        System.out.println("application started");
////        LogConfig.configureOff();
//        LogConfig.configureToStdOut();
//        NativeLibsManager.prepareNativeLibs();
////        System.setProperty("jna.debug_load", "true");
////        Scene scene = SceneLoader.loadOBJ("models/cornell_box.obj");
////        Scene scene = SceneLoader.loadOBJ("models/teapot.obj");
//        Scene scene = SceneLoader.loadOBJ("models/casa/casa.obj");
////        Scene scene = SceneLoader.loadOBJ("models/relax_tea_table/cup_of_tea.obj");
////        System.out.println(scene.getSceneObjects());
//        scene.initEmbree();
//
//        scene.getLightSources().add(new LightSource(
//                1e1f,
//                new Color3f(0.1f,0.1f,0.1f),
//                new Point3f(213, 300, 280)
//        ));
//        scene.getLightSources().add(new LightSource(
//                1e1f,
//                new Color3f(1,1,1),
//                new Point3f(278, 273, -500)
//        ));
//
////        scene.getLightSources().add(new LightSource(
////                1e1f,
////                new Color3f(1,1,1),
////                new Point3f(0, 300, 0)
////        ));
//        scene.getLightSources().add(new LightSource(
//                1e1f,
//                new Color3f(0.1f,0.1f,0.1f),
//                new Point3f(213, 300, 330)
//        ));
//
////        (159.61807, 453.01334, 559.19995) (-0.16988958, 0.48696953, 0.8567369) true
////        (159.61807, 453.01334, 559.19995) (-0.10952262, 0.16654174, 0.97993296) true
////        (423.6675, 325.23984, 249.1659) (0.9750067, 0.11681446, -0.18898751) false
////        (423.6675, 325.23984, 249.1659) (0.19041951, 0.068288974, 0.9793248) true
////        System.out.println(Arrays.toString(scene.areOccludedMultipleByAllLS(new Point3f[]{new Point3f(159.61807f, 453.01334f, 559.19995f), new Point3f(423.6675f, 325.23984f, 249.1659f)})));
////        System.exit(0);
//
////        Point3f cameraPos = new Point3f(278, 273, -800); // for cornell box
////        Point3f cameraLookAtPos = new Point3f(300, 253, 0); // for cornell box
//
////        Point3f cameraPos = new Point3f(300, 300, 300); // for teapot model
////        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for teapot model
//
//        Point3f cameraPos = new Point3f(10, 10, 10); // for casa model
//        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for casa model
//
////        Point3f cameraPos = new Point3f(20, 20, 20); // for tea model
////        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for tea model
//
//        Vector3f cameraDirection = new Vector3f();
//        cameraDirection.sub(cameraLookAtPos, cameraPos);
//        Camera camera = new Camera(
//                cameraPos,
//                new Vector3f(0, 1, 0),
//                cameraDirection,
//                40
//        );
//        camera.setWidth(1000);
//        camera.setHeight(1000);
//
//        long time = System.currentTimeMillis();
//
////        HDRImageRGB image = Renderer.render(scene, camera);
////        HDRImageRGB image = Renderer.renderParallel(scene, camera, 6);
////        HDRImageRGB image = Renderer.renderN(scene, camera);
//        HDRImageRGB image = Renderer.renderParallelN(scene, camera, 6, 200);
////        HDRImageRGB image = Renderer.renderN2(scene, camera);
//
//        time = System.currentTimeMillis() - time;
//        System.out.println("spent " + time + "ms");
//
//        HDREncoder.writeHDR(image, new File("output.hdr"));
//
//        try {
//            if (Desktop.isDesktopSupported()) {
//                Desktop.getDesktop().open(new File("output.hdr"));
//            }
//        } catch (UnsatisfiedLinkError e) {
//            System.out.println("Can't open output result hdr file with default app.");
//        }
//    }
}