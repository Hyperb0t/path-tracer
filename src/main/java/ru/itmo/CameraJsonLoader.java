package ru.itmo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class CameraJsonLoader {
    public static Camera loadFromScene(String sceneFileName) throws IOException {
        String cameraJsonFilename = sceneFileName.substring(0, sceneFileName.lastIndexOf('.')) + ".cam.json";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(cameraJsonFilename), Camera.class);
    }

}
