package ru.itmo;

import javax.media.j3d.Material;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SceneObject that = (SceneObject) o;
        return Objects.equals(name, that.name) && Objects.equals(faces, that.faces) && Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, faces, material);
    }
}
