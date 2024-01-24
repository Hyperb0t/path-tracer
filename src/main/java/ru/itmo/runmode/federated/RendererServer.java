package ru.itmo.runmode.federated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.ivelate.JavaHDR.HDREncoder;
import com.github.ivelate.JavaHDR.HDRImageRGB;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import ru.itmo.Camera;
import ru.itmo.HdrReply;
import ru.itmo.Renderer;
import ru.itmo.Scene;
import ru.itmo.SceneRequest;
import ru.itmo.StreamingHdrGrpc;
import ru.itmo.jcommander.Algorithm;
import ru.itmo.jcommander.ProgramArguments;
import ru.itmo.serialization.MaterialDeserializer;
import ru.itmo.serialization.MaterialSerializer;

import javax.media.j3d.Material;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class RendererServer {

    private static final Logger logger =
            Logger.getLogger(RendererServer.class.getName());

    public static void main(String[] args) throws Exception {
        run(new ProgramArguments());
    }

    public static void run(ProgramArguments programArguments) throws Exception{
        StreamingHdrGrpc.StreamingHdrImplBase svc = new StreamingHdrGrpc.StreamingHdrImplBase() {
            @Override
            public StreamObserver<SceneRequest> sayHdrStreaming(final StreamObserver<HdrReply> responseObserver) {
                final ServerCallStreamObserver<HdrReply> serverCallStreamObserver =
                        (ServerCallStreamObserver<HdrReply>) responseObserver;
                serverCallStreamObserver.disableAutoRequest();

                class OnReadyHandler implements Runnable {
                    private boolean wasReady = false;

                    @Override
                    public void run() {
                        if (serverCallStreamObserver.isReady() && !wasReady) {
                            wasReady = true;
                            logger.info("READY");
                            serverCallStreamObserver.request(1);
                        }
                    }
                }
                final OnReadyHandler onReadyHandler = new OnReadyHandler();
                serverCallStreamObserver.setOnReadyHandler(onReadyHandler);

                // Give gRPC a StreamObserver that can observe and process incoming requests.
                return new StreamObserver<SceneRequest>() {
                    @Override
                    public void onNext(SceneRequest request) {
                        // Process the request and send a response or an error.
                        try {
                            // Accept and enqueue the request.
                            String scene_json = request.getScene();
                            String camera_json = request.getCamera();
                            // logger.info("--> " + scene_json);
                            // logger.info("--> " + camera_json);

                            // Simulate server "work"

                            // Calculating md5sum to compare with client.
                            MessageDigest md = MessageDigest.getInstance("MD5");

                            byte[] bytesOfMessage = scene_json.getBytes("UTF-8");
                            String message = "Scene MD5 sum is " + new String(md.digest(bytesOfMessage), "UTF-8");
                            logger.info("<-- " + message);

                            bytesOfMessage = camera_json.getBytes("UTF-8");
                            message = "Camera MD5 sum is " + new String(md.digest(bytesOfMessage), "UTF-8");
                            logger.info("<-- " + message);

                            ObjectMapper om = new ObjectMapper();
                            SimpleModule simpleModule = new SimpleModule();
                            simpleModule.addSerializer(Material.class, new MaterialSerializer());
                            simpleModule.addDeserializer(Material.class, new MaterialDeserializer());
                            om.registerModule(simpleModule);
                            ObjectWriter ow = om.writer();
                            ObjectReader or = om.reader();
                            Scene scene = or.readValue(scene_json, Scene.class);
                            scene.initEmbree();
                            Camera camera = or.readValue(camera_json, Camera.class);

                            // Криво, но уже не стал разбираться как в string форматнуть HDRImageRGB
                            // На первый взгляд никак
                            long time = System.currentTimeMillis();
                            HDRImageRGB image = null;
                            if(programArguments.getAlgorithm().equals(Algorithm.PT)) {
                                time = System.currentTimeMillis();

//         image = Renderer.render(scene, camera);
//         image = Renderer.renderParallel(scene, camera, 6);
//         image = Renderer.renderN(scene, camera);
                                image = Renderer.renderParallelN(scene, camera, 6, 200);
//         image = Renderer.renderN2(scene, camera);
                            }
                            else if(programArguments.getAlgorithm().equals(Algorithm.PTOPFD)) {
                                for(int i = 1; i < scene.getLightSources().size(); i++) {
                                    scene.getLightSources().remove(i);
                                }
                                ru.itmo.ptopfd.Scene ptopfdScene = new ru.itmo.ptopfd.Scene(scene);
                                ptopfdScene.initEmbree();
                                time = System.currentTimeMillis();
                                image = ru.itmo.ptopfd.Renderer.render(ptopfdScene, camera);
                            }

                            System.out.println("spent " + time + "ms");
                            HDREncoder.writeHDR(image, new File(programArguments.getOutput()));

		      /*
		      BufferedReader image_reader = new BufferedReader(new FileReader("output.hdr"));
		      HdrReply reply = HdrReply.newBuilder().setImage(image_reader.lines().collect(Collectors.joining())).build();
		      */

                            Path content_path = Paths.get(programArguments.getOutput());
                            byte[] output_content = Files.readAllBytes(content_path);
                            HdrReply reply = HdrReply.newBuilder().setImage(ByteString.copyFrom(output_content)).build();
                            responseObserver.onNext(reply);

                            // Check the provided ServerCallStreamObserver to see if it is still ready to accept more messages.
                            if (serverCallStreamObserver.isReady()) {
                                serverCallStreamObserver.request(1);
                            } else {
                                // If not, note that back-pressure has begun.
                                onReadyHandler.wasReady = false;
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            responseObserver.onError(
                                    Status.UNKNOWN.withDescription("Error handling request").withCause(throwable).asException());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        // End the response stream if the client presents an error.
                        t.printStackTrace();
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        // Signal the end of work when the client ends the request stream.
                        logger.info("COMPLETED");
                        responseObserver.onCompleted();
                    }
                };
            }
        };

        final Server server = ServerBuilder
                .forPort(programArguments.getPort())
                .addService(svc)
                .build()
                .start();

        logger.info("Listening on " + server.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("Shutting down");
                try {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
        server.awaitTermination();
    }
}

/*
public class App {
    public static void main(String[] args) throws Exception {
//        new Scanner(System.in).nextLine();
//        System.out.println("application started");
        NativeLibsManager.prepareNativeLibs();
        System.setProperty("jna.debug_load", "true");
//        Scene scene = SceneLoader.loadOBJ("models/cornell_box.obj");
//        Scene scene = SceneLoader.loadOBJ("models/teapot.obj");
        Scene scene = SceneLoader.loadOBJ("models/casa/casa.obj");
//        Scene scene = SceneLoader.loadOBJ("models/relax_tea_table/cup_of_tea.obj");
//        System.out.println(scene.getSceneObjects());
        scene.initEmbree();

        scene.getLightSources().add(new LightSource(
                1e1f,
                new Color3f(0.1f,0.1f,0.1f),
                new Point3f(213, 300, 280)
        ));
        scene.getLightSources().add(new LightSource(
                1e1f,
                new Color3f(1,1,1),
                new Point3f(278, 273, -500)
        ));

//        scene.getLightSources().add(new LightSource(
//                1e1f,
//                new Color3f(1,1,1),
//                new Point3f(0, 300, 0)
//        ));
        scene.getLightSources().add(new LightSource(
                1e1f,
                new Color3f(0.1f,0.1f,0.1f),
                new Point3f(213, 300, 330)
        ));

//        (159.61807, 453.01334, 559.19995) (-0.16988958, 0.48696953, 0.8567369) true
//        (159.61807, 453.01334, 559.19995) (-0.10952262, 0.16654174, 0.97993296) true
//        (423.6675, 325.23984, 249.1659) (0.9750067, 0.11681446, -0.18898751) false
//        (423.6675, 325.23984, 249.1659) (0.19041951, 0.068288974, 0.9793248) true
//        System.out.println(Arrays.toString(scene.areOccludedMultipleByAllLS(new Point3f[]{new Point3f(159.61807f, 453.01334f, 559.19995f), new Point3f(423.6675f, 325.23984f, 249.1659f)})));
//        System.exit(0);
//        Point3f cameraPos = new Point3f(278, 273, -800); // for cornell box
//        Point3f cameraLookAtPos = new Point3f(300, 253, 0); // for cornell box
//        Point3f cameraPos = new Point3f(300, 300, 300); // for teapot model
//        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for teapot model

        Point3f cameraPos = new Point3f(10, 10, 10); // for casa model
        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for casa model

//        Point3f cameraPos = new Point3f(20, 20, 20); // for tea model
//        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for tea model

        Vector3f cameraDirection = new Vector3f();
        cameraDirection.sub(cameraLookAtPos, cameraPos);
        Camera camera = new Camera(
                cameraPos,
                new Vector3f(0, 1, 0),
                cameraDirection,
                40
        );
        camera.setWidth(1000);
        camera.setHeight(1000);

        long time = System.currentTimeMillis();

//        HDRImageRGB image = Renderer.render(scene, camera);
//        HDRImageRGB image = Renderer.renderParallel(scene, camera, 6);
//        HDRImageRGB image = Renderer.renderN(scene, camera);
        HDRImageRGB image = Renderer.renderParallelN(scene, camera, 6, 200);
//        HDRImageRGB image = Renderer.renderN2(scene, camera);

        time = System.currentTimeMillis() - time;
        System.out.println("spent " + time + "ms");

        HDREncoder.writeHDR(image, new File("output.hdr"));

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File("output.hdr"));
            }
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Can't open output result hdr file with default app.");
        }
    }
}
*/
