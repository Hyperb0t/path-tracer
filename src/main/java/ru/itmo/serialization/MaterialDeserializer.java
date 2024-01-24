package ru.itmo.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import java.io.IOException;

//not needed for minimal federated demo
//but can be useful in the future if serialization and data transfer in json format becomes complicated
public class MaterialDeserializer extends StdDeserializer<Material> {
    public MaterialDeserializer() {
        super(Material.class);
    }

    @Override
    public Material deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = parser.getCodec().readTree(parser);
        JsonNode diffuseColorNode = node.get("diffuseColor");
        ObjectMapper objectMapper = new ObjectMapper();
        Color3f diffuseColor = objectMapper.treeToValue(diffuseColorNode, Color3f.class);
        Material material = new Material();
        material.setDiffuseColor(diffuseColor);
        return material;
    }
}
