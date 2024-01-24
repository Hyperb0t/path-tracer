package ru.itmo.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.media.j3d.Material;
import java.io.IOException;

public class SerializationExperiments {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Material.class, new MaterialSerializer());
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(simpleModule);
//        Material material = new Material();
//        material.setDiffuseColor(0.1f,0.2f,0.3f);
//        System.out.println(material);
//        System.out.println(objectMapper.writeValueAsString(material));
//        System.out.println(objectMapper.readValue(objectMapper.writeValueAsString(material), Material.class));


    }
}
