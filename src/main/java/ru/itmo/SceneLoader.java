package ru.itmo;

import com.mokiat.data.front.parser.*;

import javax.media.j3d.Material;
import javax.vecmath.Point3f;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class SceneLoader {

    public static Scene loadOBJ(String filepath) throws IOException {
        IOBJParser parser = new OBJParser();
        OBJModel objModel = parser.parse(new FileInputStream(filepath));
        MTLParser mtlParser;
        MTLLibrary mtlLibrary = null;
        if(!objModel.getMaterialLibraries().isEmpty()) {
//            System.out.println("MTLS:");
//            System.out.println(objModel.getMaterialLibraries());
            mtlParser = new MTLParser();
            mtlLibrary = mtlParser.parse(new FileInputStream(Path.of(filepath).getParent().toString() + "/" + objModel.getMaterialLibraries().get(0)));
        }
        Scene result = new Scene();
        result.setVertices(objModel.getVertices().stream()
                .map(ov -> new Point3f(ov.x, ov.y, ov.z)).collect(Collectors.toList()));

        for (OBJObject obj : objModel.getObjects()) {
            SceneObject sceneObject = new SceneObject();
            sceneObject.setName(obj.getName());
            for (OBJMesh mesh : obj.getMeshes()) {
                if(mtlLibrary != null) {
                    MTLMaterial mtlMaterial = mtlLibrary.getMaterial(mesh.getMaterialName());
                    if (mtlMaterial != null) {
                        Material material = new Material();
                        material.setDiffuseColor(mtlMaterial.getDiffuseColor().r, mtlMaterial.getDiffuseColor().g, mtlMaterial.getDiffuseColor().b);
                        sceneObject.setMaterial(material);
                    }
                }
                for (OBJFace objFace : mesh.getFaces()) {
                    sceneObject.getFaces().add(
                        objFace.getReferences().stream().map(r -> r.vertexIndex).collect(Collectors.toList())
                    );

                }
            }
            result.getSceneObjects().add(sceneObject);
        }
        return result;
    }

    public static Scene loadShp(String filepath) {

        return new Scene();
    }
}
