package org.example;

import javax.media.j3d.Material;
import java.util.LinkedList;
import java.util.List;

public class SceneObject {

    private String name;
    private List<List<Integer>> faces = new LinkedList<List<Integer>>();

    private javax.media.j3d.Material material;

    public SceneObject() {
    }

    public SceneObject(List<List<Integer>> faces, Material material) {
        this.faces = faces;
        this.material = material;
    }

    public List<List<Integer>> getFaces() {
        return faces;
    }

    public void setFaces(List<List<Integer>> faces) {
        this.faces = faces;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SceneObject{" +
                "name='" + name + '\'' +
                ", faces=" + faces +
                ", material=" + material +
                '}';
    }
}
