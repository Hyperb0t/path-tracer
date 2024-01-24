package ru.itmo.runmode.federated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import ru.itmo.Camera;
import ru.itmo.CameraJsonLoader;
import ru.itmo.HdrReply;
import ru.itmo.LightSource;
import ru.itmo.Scene;
import ru.itmo.SceneLoader;
import ru.itmo.SceneRequest;
import ru.itmo.StreamingHdrGrpc;
import ru.itmo.embree.NativeLibsManager;
import ru.itmo.jcommander.ProgramArguments;
import ru.itmo.serialization.MaterialDeserializer;
import ru.itmo.serialization.MaterialSerializer;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class SceneClient {
    private static final Logger logger =
            Logger.getLogger(SceneClient.class.getName());

    public static void main(String[] args) throws Exception {
        run(new ProgramArguments());
    }

    public static String getScene(String filename) throws Exception {
        NativeLibsManager.prepareNativeLibs();
        System.setProperty("jna.debug_load", "true");
        Scene scene = SceneLoader.loadOBJ(filename);
        scene.initEmbree();

        scene.getLightSources().add(new LightSource(
                1e1f,
                new Color3f(0.1f, 0.1f, 0.1f),
                new Point3f(213, 300, 280)
        ));
        scene.getLightSources().add(new LightSource(
                1e1f,
                new Color3f(1, 1, 1),
                new Point3f(278, 273, -500)
        ));
        scene.getLightSources().add(new LightSource(
                1e1f,
                new Color3f(0.1f, 0.1f, 0.1f),
                new Point3f(213, 300, 330)
        ));

        ObjectMapper om = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Material.class, new MaterialSerializer());
        simpleModule.addDeserializer(Material.class, new MaterialDeserializer());
        om.registerModule(simpleModule);
        ObjectWriter ow = om.writer();
        return ow.writeValueAsString(scene);

    }

    public static String getCamera(String sceneFileName) throws Exception {
        Camera camera = CameraJsonLoader.loadFromScene(sceneFileName);
//        Point3f cameraPos = new Point3f(10, 10, 10); // for casa model
//        Point3f cameraLookAtPos = new Point3f(0, 0, 0); // for casa model
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

        ObjectWriter ow = new ObjectMapper().writer();
        return ow.writeValueAsString(camera);
    }

    public static void run(ProgramArguments programArguments) throws Exception{
        NameResolverRegistry.getDefaultRegistry().register(new DnsNameResolverProvider());
        final CountDownLatch done = new CountDownLatch(1);

        // Create a channel and a stub
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(programArguments.getIp(), programArguments.getPort())
                .usePlaintext()
                .maxInboundMessageSize(Integer.MAX_VALUE)
                .build();

        StreamingHdrGrpc.StreamingHdrStub stub = StreamingHdrGrpc.newStub(channel);

        ClientResponseObserver<SceneRequest, HdrReply> clientResponseObserver =
                new ClientResponseObserver<SceneRequest, HdrReply>() {

                    ClientCallStreamObserver<SceneRequest> requestStream;

                    @Override
                    public void beforeStart(final ClientCallStreamObserver<SceneRequest> requestStream) {
                        this.requestStream = requestStream;
                        requestStream.disableAutoRequestWithInitial(1);
                        requestStream.setOnReadyHandler(new Runnable() {

                            @Override
                            public void run() {
                                while (requestStream.isReady()) {

                                    String scene_json = "";
                                    String camera_json = "";

                                    try {
                                        scene_json = SceneClient.getScene(programArguments.getScene());
                                        camera_json = SceneClient.getCamera(programArguments.getScene());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    SceneRequest request = SceneRequest.newBuilder().setScene(scene_json).setCamera(camera_json).build();
                                    requestStream.onNext(request);
                                    requestStream.onCompleted();
                                }
                            }
                        });
                    }

                    @Override
                    public void onNext(HdrReply value) {
                        ObjectReader or = new ObjectMapper().reader();
                        try {
                            ByteString content_proto = value.getImage();
                            byte[] content = content_proto.toByteArray();

                            Path content_path = Paths.get(programArguments.getOutput());
                            Files.write(content_path, content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        requestStream.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        done.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        logger.info("All Done");
                        try {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(new File(programArguments.getOutput()));
                            }
                        } catch (UnsatisfiedLinkError | IOException e) {
                            System.out.println("Can't open output result hdr file with default app.");
                        }
                        done.countDown();
                    }

                };

        stub.sayHdrStreaming(clientResponseObserver);

        done.await();

        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
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

	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

	BufferedWriter scene_writer = new BufferedWriter(new FileWriter("scene.json"));
	String scene_json = ow.writeValueAsString(scene);
	scene_writer.write(scene_json);
	scene_writer.close();

	String camera_json = ow.writeValueAsString(camera);
	BufferedWriter camera_writer = new BufferedWriter(new FileWriter("camera.json"));
	camera_writer.write(camera_json);
	camera_writer.close();

//        HDRImageRGB image = Renderer.render(scene, camera);
//        HDRImageRGB image = Renderer.renderParallel(scene, camera, 6);
//        HDRImageRGB image = Renderer.renderN(scene, camera);
//        HDRImageRGB image = Renderer.renderParallelN(scene, camera, 6, 200);
//        HDRImageRGB image = Renderer.renderN2(scene, camera);

        time = System.currentTimeMillis() - time;
        System.out.println("spent " + time + "ms");

	/*
        HDREncoder.writeHDR(image, new File("output.hdr"));

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File("output.hdr"));
            }
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Can't open output result hdr file with default app.");
        }
	* /
    }
}
*/
