package org.example;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Triangulator;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.example.embree.*;

import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

public class Scene {

    private List<Point3f> vertices = new ArrayList<>();

//    private Map<Integer, Integer> sortedVIndexesToVIndexes;

    private Map<Point3f, Integer> verticesToIndices = new HashMap<>();
    private List<SceneObject> sceneObjects = new ArrayList<>();
    private List<LightSource> lightSources = new ArrayList<>();
    private Pointer embreeDevice = EmbreeNatives.rtcNewDevice(Pointer.NULL);
    private Pointer embreeScene = EmbreeNatives.rtcNewScene(embreeDevice);

    private Map<Integer, SceneObject> objectsByGeomId = new HashMap<>();
    public Scene() {
    }

    public Scene(List<SceneObject> sceneObjects, List<LightSource> lightSources) {
        this.sceneObjects = sceneObjects;
        this.lightSources = lightSources;
    }

    public IntersectionContext intersect(Ray ray) {
        RTCRayHit rtcRayHit = new RTCRayHit();
        rtcRayHit.setOrg(ray.getOrigin().x, ray.getOrigin().y, ray.getOrigin().z);
        rtcRayHit.setDir(ray.getDir().x, ray.getDir().y, ray.getDir().z);
        rtcRayHit.setTnear(0f);
        rtcRayHit.setTfar(Float.POSITIVE_INFINITY);
        rtcRayHit.setGeomID(Embree.RTC_INVALID_GEOMETRY_ID);
        rtcRayHit.write();

        RTCIntersectContext rtcIntersectContext = new RTCIntersectContext();

        EmbreeNatives.rtcIntersect1(embreeScene, rtcIntersectContext.getPointer(), rtcRayHit.getPointer());

        rtcRayHit.read();
        IntersectionContext result = new IntersectionContext();
        if(rtcRayHit.geomID != Embree.RTC_INVALID_GEOMETRY_ID) {
//            System.out.println("t = " + rtcRayHit.getTfar());
//            System.out.println("geomID " + rtcRayHit.geomID);
//            System.out.println("Ng " + rtcRayHit.Ng_x + " " + rtcRayHit.Ng_y + " " + rtcRayHit.Ng_z);
            result.setHit(true);
            result.setMaterial(objectsByGeomId.get(rtcRayHit.geomID).getMaterial());
            Vector3f normal = new Vector3f(rtcRayHit.Ng_x, rtcRayHit.Ng_y, rtcRayHit.Ng_z);
            normal.normalize();
            if(normal.angle(ray.getDir()) > Math.PI / 2) {
                normal.negate();
            }
            result.setNormal(normal);
            result.setT(rtcRayHit.getTfar());
            result.setRay(ray);
        }
        else {
//            System.out.println("no intersection");
            result.setHit(false);
        }
        return result;
    }

    public IntersectionContext[] intersectAll(Ray[] rays) {
        byte[] rtcRayHitsBytes = new byte[RTCRayHit.STRUCT_BYTE_SIZE * rays.length];
        ByteBuffer b = ByteBuffer.allocate(RTCRayHit.STRUCT_BYTE_SIZE);
        b.order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < rays.length; i++) {
            RTCRayHitNonStruct rtcRayHit = new RTCRayHitNonStruct();
            rtcRayHit.setOrg(rays[i].getOrgX(), rays[i].getOrgY(), rays[i].getOrgZ());
            rtcRayHit.setDir(rays[i].getDirX(), rays[i].getDirY(), rays[i].getDirZ());
            rtcRayHit.setTnear(0f);
            rtcRayHit.setTfar(Float.POSITIVE_INFINITY);
            rtcRayHit.setGeomID(Embree.RTC_INVALID_GEOMETRY_ID);
            rtcRayHit.setId(i);
            rays[i].setId(i);
            System.arraycopy(rtcRayHit.toBytes(b), 0, rtcRayHitsBytes, i * RTCRayHit.STRUCT_BYTE_SIZE, RTCRayHit.STRUCT_BYTE_SIZE);
            b.rewind();
        }
        Pointer rtcRayHitsPointer = new Memory((long) RTCRayHit.STRUCT_BYTE_SIZE * rays.length);
        rtcRayHitsPointer.write(0, rtcRayHitsBytes, 0, rtcRayHitsBytes.length);
        
        RTCIntersectContext rtcIntersectContext = new RTCIntersectContext();
        
        EmbreeNatives.rtcIntersect1M(embreeScene, rtcIntersectContext.getPointer(), rtcRayHitsPointer, rays.length, 80);
        
        rtcRayHitsPointer.read(0, rtcRayHitsBytes, 0, rtcRayHitsBytes.length);
        
        RTCRayHitNonStruct[] rtcRayHitArr = new RTCRayHitNonStruct[rays.length];
        for(int i = 0; i < rays.length; i++) {
            b.put(Arrays.copyOfRange(rtcRayHitsBytes,
                    i * RTCRayHitNonStruct.STRUCT_BYTE_SIZE, (i+1) * RTCRayHitNonStruct.STRUCT_BYTE_SIZE));
            b.rewind();
            rtcRayHitArr[i] = RTCRayHitNonStruct.fromBytes(b);
            b.rewind();
        }
//        RTCRayHitNonStruct[] rtcRayHitArr = SerializationUtil.fromBytesAll3(rtcRayHitsBytes);

        IntersectionContext[] result = new IntersectionContext[rays.length];
        for(int i =0; i < rays.length; i++) {
            IntersectionContext toAdd = new IntersectionContext();
            if(rtcRayHitArr[i].geomID != Embree.RTC_INVALID_GEOMETRY_ID) {
                toAdd.setHit(true);
                toAdd.setMaterial(objectsByGeomId.get(rtcRayHitArr[i].geomID).getMaterial());
                Vector3f normal = new Vector3f(rtcRayHitArr[i].Ng_x, rtcRayHitArr[i].Ng_y, rtcRayHitArr[i].Ng_z);
                normal.normalize();
//                if(normal.angle(rays[i].getDir()) > Math.PI / 2) {
                if(normal.dot(rays[i].getDir()) < 0) {
                    normal.negate();
                }
                toAdd.setNormal(normal);
                toAdd.setT(rtcRayHitArr[i].getTfar());
            }
            else {
                toAdd.setHit(false);
            }
            Point3f org = new Point3f(rtcRayHitArr[i].org_x, rtcRayHitArr[i].org_y, rtcRayHitArr[i].org_z);
            Vector3f dir = new Vector3f(rtcRayHitArr[i].dir_x, rtcRayHitArr[i].dir_y, rtcRayHitArr[i].dir_z);
            Ray r = new Ray(org, dir, new Color3f(1,1,1));
            r.setId(rtcRayHitArr[i].getId());
            toAdd.setRay(r);
            result[i] = toAdd;
        }
        return result;
    }

    public IntersectionContext[] intersectAll2(Ray[] rays) {
        byte[] rtcRayHitsBytes = new byte[RTCRayHit.STRUCT_BYTE_SIZE * rays.length];
        ByteBuffer b = ByteBuffer.wrap(rtcRayHitsBytes);
        b.order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < rays.length; i++) {
            rays[i].setId(i);
            b.putFloat(rays[i].getOrgX());
            b.putFloat(rays[i].getOrgY());
            b.putFloat(rays[i].getOrgZ());
            b.putFloat(0f); //Tnear
            b.putFloat(rays[i].getDirX());
            b.putFloat(rays[i].getDirY());
            b.putFloat(rays[i].getDirZ());
            b.putFloat(0); //time
            b.putFloat(Float.POSITIVE_INFINITY); //tfar
            b.putInt(0); //mask
            b.putInt(i); //id
            b.putInt(0); //flags
            b.putFloat(0); //ng x
            b.putFloat(0); //ng y
            b.putFloat(0); //ng z
            b.putFloat(0); //u
            b.putFloat(0); //v
            b.putInt(0); //primId
            b.putInt(Embree.RTC_INVALID_GEOMETRY_ID);
            b.putInt(0); //instId
        }

        Pointer rtcRayHitsPointer = new Memory((long) RTCRayHit.STRUCT_BYTE_SIZE * rays.length);
        rtcRayHitsPointer.write(0, rtcRayHitsBytes, 0, rtcRayHitsBytes.length);

        RTCIntersectContext rtcIntersectContext = new RTCIntersectContext();

        EmbreeNatives.rtcIntersect1M(embreeScene, rtcIntersectContext.getPointer(), rtcRayHitsPointer, rays.length, 80);

        rtcRayHitsPointer.read(0, rtcRayHitsBytes, 0, rtcRayHitsBytes.length);


//        for(int i = 0; i < rays.length; i++) {
//            b.read(Arrays.copyOfRange(rtcRayHitsBytes,
//                    i * RTCRayHitNonStruct.STRUCT_BYTE_SIZE, (i+1) * RTCRayHitNonStruct.STRUCT_BYTE_SIZE));
//            rtcRayHitArr[i] = RTCRayHitNonStruct.fromBytes2(b);
//            b.clear();
//        }
//        RTCRayHitNonStruct[] rtcRayHitArr = SerializationUtil.fromBytesAll2(rtcRayHitsBytes);
        IntersectionContext[] result = new IntersectionContext[rays.length];
        for(int i =0; i < rays.length; i++) {
            int iterationOffset = i * RTCRayHitNonStruct.STRUCT_BYTE_SIZE;
            int geomId = b.getInt(iterationOffset + RTCRayHitNonStruct.GEOM_ID_OFFSET);
            IntersectionContext toAdd = new IntersectionContext();
            if(geomId != Embree.RTC_INVALID_GEOMETRY_ID) {
                toAdd.setHit(true);
                toAdd.setMaterial(objectsByGeomId.get(geomId).getMaterial());
                float ngX = b.getFloat(iterationOffset + RTCRayHitNonStruct.NG_X_OFFSET);
                float ngY = b.getFloat(iterationOffset + RTCRayHitNonStruct.NG_Y_OFFSET);
                float ngZ = b.getFloat(iterationOffset + RTCRayHitNonStruct.NG_Z_OFFSET);
                Vector3f normal = new Vector3f(ngX, ngY, ngZ);
                normal.normalize();
                if(normal.dot(rays[i].getDir()) < 0) {
                    normal.negate();
                }
                toAdd.setNormal(normal);
                float tfar = b.getFloat(iterationOffset + RTCRayHitNonStruct.T_FAR_OFFSET);
                toAdd.setT(tfar);
            }
            else {
                toAdd.setHit(false);
            }
            toAdd.setRay(rays[i]);
            result[i] = toAdd;
        }
        return result;
    }

    public boolean isOccluded(Ray rayFromLightSource, Point3f scenePoint) {
        //tnear and tfar are set in such way so to check only line between ray origin and point
        //if in these borders there are no intersection, then rtcRay.tfar will not be updated and set to -inf
        //that's why we check that it is not equal to -inf
        //we could also check that it equals what it used to be
        RTCRay rtcRay = new RTCRay();
        rtcRay.setOrg(rayFromLightSource.getOrigin());
        rtcRay.setDir(rayFromLightSource.getDir());
        rtcRay.setTnear(0.f);

        Vector3f vecFromLsToP = new Vector3f(scenePoint);
        vecFromLsToP.sub(rayFromLightSource.getOrigin());
        rtcRay.setTfar(vecFromLsToP.length() - 0.01f);

        rtcRay.write();

        RTCIntersectContext context = new RTCIntersectContext();

        EmbreeNatives.rtcOccluded1(embreeScene, context.getPointer(), rtcRay.getPointer());
//            rtcRay.read();
//            System.out.println(rtcRay);
        return (float)rtcRay.readField("tfar") != Float.NEGATIVE_INFINITY;
    }

    //returns boolean for each point and light source which specifies if point is occluded by light
    //format: [bool_point1_by_light1, bool_point1_by_light2, bool_point2_by_light1, bool_point2_by_light2]
    public boolean[] areOccludedMultipleByAllLS(Point3f[] points) {
        RTCRayNS[] allLightRaysToPoints = new RTCRayNS[points.length * getLightSources().size()];

        //need this array for speed, because LightSource.getPos() creates new Point3f instance every time
        Point3f[] LSPos = getLightSources().stream().map(LightSource::getPos).collect(Collectors.toList()).toArray(new Point3f[]{});
        for(int p = 0; p < points.length; p++) {
            for(int l = 0; l < getLightSources().size(); l++) {
                Vector3f vecFromLSToPoint = new Vector3f();
                vecFromLSToPoint.sub(points[p], LSPos[l]);
                Vector3f dirFromLSToPoint = new Vector3f(vecFromLSToPoint);
                dirFromLSToPoint.normalize();
                RTCRayNS rtcRayNS = new RTCRayNS();
                rtcRayNS.setOrg(LSPos[l]);
                rtcRayNS.setDir(dirFromLSToPoint);
                rtcRayNS.setTnear(0);
                rtcRayNS.setTfar(vecFromLSToPoint.length() - 0.01f);
                rtcRayNS.setId(p * getLightSources().size() + l);
                allLightRaysToPoints[p * getLightSources().size() + l] = rtcRayNS;
            }
        }

        byte[] rayToPointsBytes = new byte[allLightRaysToPoints.length * RTCRayNS.STRUCT_BYTE_SIZE];
        ByteBuffer b = ByteBuffer.allocate(RTCRayNS.STRUCT_BYTE_SIZE);
        b.order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < allLightRaysToPoints.length; i++) {
            System.arraycopy(allLightRaysToPoints[i].toBytes(b), 0, rayToPointsBytes,
                    i * RTCRayNS.STRUCT_BYTE_SIZE, RTCRayNS.STRUCT_BYTE_SIZE);
            b.clear();
        }
        Pointer raysPointer = new Memory(rayToPointsBytes.length);
        raysPointer.write(0, rayToPointsBytes, 0, rayToPointsBytes.length);

        RTCIntersectContext context = new RTCIntersectContext();
        EmbreeNatives.rtcOccluded1M(embreeScene, context.getPointer(), raysPointer, allLightRaysToPoints.length, RTCRayNS.STRUCT_BYTE_SIZE);

        raysPointer.read(0, rayToPointsBytes, 0, rayToPointsBytes.length);

        boolean[] result = new boolean[allLightRaysToPoints.length];

        for(int i = 0; i < allLightRaysToPoints.length; i++) {
            b.put(Arrays.copyOfRange(rayToPointsBytes,
                    i * RTCRayNS.STRUCT_BYTE_SIZE, (i+1) * RTCRayNS.STRUCT_BYTE_SIZE));
            b.rewind();
//            RTCRayNS rtcRayNS = RTCRayNS.fromBytes(b);
//            allLightRaysToPoints[i] = rtcRayNS;
            float tfar = RTCRayNS.tfarFromBytes(b);
            b.clear();
//            result[i] = allLightRaysToPoints[i].tfar != Float.NEGATIVE_INFINITY;
            result[i] = tfar != Float.NEGATIVE_INFINITY;
//            if(isOccluded(  new Ray(
//                    allLightRaysToPoints[i].getOrg(),
//                    allLightRaysToPoints[i].getDir(),
//                    new Color3f(0,0,0)
//                    ),
//                    points[i / getLightSources().size()]) != result[i]) {
//                int wtf = 11;
//            }
        }
        return result;
    }

    public Color3f[] getLuminanceForAllIntersPoints(IntersectionContext[] intersectionContextsFromCamera) {
        for(var ic : intersectionContextsFromCamera) {
            Point3f point = ic.getRay().getOrigin();
            Vector3f rayTVector = ic.getRay().getDir();
            rayTVector.normalize();
            rayTVector.scale(ic.getT());
            point.add(rayTVector);


        }

        return new Color3f[]{};
    }

    public void initEmbree() {
        for(SceneObject object : sceneObjects) {
            if(object.getFaces().isEmpty()) {
                continue;
            }
            boolean allObjectFacesSize4 = true;
            for(List<Integer> face : object.getFaces()) {
                if(face.size() != 4) {
                    allObjectFacesSize4 = false;
                    break;
                }
            }
            int faceSize = allObjectFacesSize4 ? 4 : 3;
            Pointer geom = null;
            if(faceSize == 3) {
                System.out.println("creating triangle geometry");
                geom = EmbreeNatives.rtcNewGeometry(embreeDevice, Embree.RTC_GEOMETRY_TYPE_TRIANGLE);
            }
            else {
                System.out.println("creating quad geometry");
                geom = EmbreeNatives.rtcNewGeometry(embreeDevice, Embree.RTC_GEOMETRY_TYPE_QUAD);
            }
            Pointer vertexBuffer = EmbreeNatives.rtcSetNewGeometryBuffer(geom, Embree.RTC_BUFFER_TYPE_VERTEX,
                    0, Embree.RTC_FORMAT_FLOAT3, 3 * 4, this.getVertices().size());
            float[] vertices = new float[this.getVertices().size() * 3];
            for(int i = 0; i < this.getVertices().size(); i++) {
                vertices[i * 3] = this.getVertices().get(i).x;
                vertices[i * 3 + 1] = this.getVertices().get(i).y;
                vertices[i * 3 + 2] = this.getVertices().get(i).z;
            }
            vertexBuffer.write(0, vertices, 0, vertices.length);


            if(faceSize == 3) {
                //
                var triangulatedFaces = getTriangulatedFaces(object.getFaces());
                Pointer faceIndexBuffer = EmbreeNatives.rtcSetNewGeometryBuffer(geom, Embree.RTC_BUFFER_TYPE_INDEX,
                        0, Embree.RTC_FORMAT_UINT3, 3 * 4, triangulatedFaces.size());

                int[] faceIndexes = triangulatedFaces.stream()
                        .flatMapToInt(f -> Arrays.stream(new int[]{f.get(0), f.get(1), f.get(2)})).toArray();
                faceIndexBuffer.write(0, faceIndexes, 0, faceIndexes.length);
            }
            else {
                Pointer faceIndexBuffer = EmbreeNatives.rtcSetNewGeometryBuffer(geom, Embree.RTC_BUFFER_TYPE_INDEX,
                        0, Embree.RTC_FORMAT_UINT4, 4 * 4, object.getFaces().size());

                int[] faceIndexes = object.getFaces().stream()
                        .flatMapToInt(f -> Arrays.stream(new int[]{f.get(0), f.get(1), f.get(2), f.get(3)})).toArray();
                faceIndexBuffer.write(0, faceIndexes, 0, faceIndexes.length);
            }
            EmbreeNatives.rtcCommitGeometry(geom);
            int geomId = EmbreeNatives.rtcAttachGeometry(embreeScene, geom);
            objectsByGeomId.put(geomId, object);
        }
        EmbreeNatives.rtcCommitScene(embreeScene);
    }

    private List<List<Integer>> getTriangulatedFaces(List<List<Integer>> faces) {
        List<List<Integer>> result = new LinkedList<>();
        Triangulator triangulator = new Triangulator();
        for(List<Integer> face : faces) {
            if(face.size() == 3) {
                result.add(face);
            }
            else {
                GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
                List<Point3f> polygon = new LinkedList<>();
                for(Integer i : face) {
                    polygon.add(this.getVertices().get(i));
                }
                geometryInfo.setCoordinates(polygon.toArray(new Point3f[]{}));
                geometryInfo.setStripCounts(new int[]{polygon.size()});
                triangulator.triangulate(geometryInfo);
                TriangleArray ta = (TriangleArray) geometryInfo.getGeometryArray();
                for(int i = 0; i < ta.getVertexCount(); i+=3) {
                    Point3f p1 = new Point3f();
                    ta.getCoordinate(i, p1);
                    Point3f p2 = new Point3f();
                    ta.getCoordinate(i+1, p2);
                    Point3f p3 = new Point3f();
                    ta.getCoordinate(i+2, p3);
                    int p1Index = findVertexIndex(p1);
                    if(p1Index == -1) { System.out.println("Can't find point index for: " + p1); }
                    int p2Index = findVertexIndex(p2);
                    if(p2Index == -1) { System.out.println("Can't find point index for: " + p2); }
                    int p3Index = findVertexIndex(p3);
                    if(p3Index == -1) { System.out.println("Can't find point index for: " + p3); }
                    result.add(List.of(p1Index, p2Index, p3Index));
                }
            }
        }
        return result;
    }

//    private int findVertexIndex(Point3f v) {
//        int sortedIndex = Arrays.binarySearch(sortedVertices, v, Comparator.comparing(p -> p.hashCode()));
//        return sortedVIndexesToVIndexes.get(sortedIndex);
//    }

    private int findVertexIndex(Point3f v) {
        return verticesToIndices.get(v);
    }

    private int findVertexIndexSlow(Point3f v) {
        return this.getVertices().indexOf(v);
    }

    public List<Point3f> getVertices() {
        return vertices;
    }

//    public void setVertices(List<Point3f> vertices) {
//        this.vertices = vertices;
//        sortedVertices = vertices.toArray(new Point3f[]{});
//        Arrays.sort(sortedVertices, Comparator.comparing(p -> p.hashCode()));
//        sortedVIndexesToVIndexes = new HashMap<>();
//        for(int i = 0; i < sortedVertices.length; i++) {
//            sortedVIndexesToVIndexes.put(i, vertices.indexOf(sortedVertices[i]));
//        }
//    }

    public void setVertices(List<Point3f> vertices) {
        this.vertices = vertices;
        for(int i = 0 ; i < vertices.size(); i++) {
            verticesToIndices.put(vertices.get(i), i);
        }
    }


    public List<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    public List<LightSource> getLightSources() {
        return lightSources;
    }

}
