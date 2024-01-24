package ru.itmo.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import java.io.IOException;

public class MaterialSerializer extends StdSerializer<Material> {

    public MaterialSerializer() {
        super(Material.class);
    }


    @Override
    public void serialize(Material material, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        Color3f diffuseColor = new Color3f();
        material.getDiffuseColor(diffuseColor);
        gen.writeObjectField("diffuseColor", diffuseColor);
        gen.writeEndObject();
    }
}
