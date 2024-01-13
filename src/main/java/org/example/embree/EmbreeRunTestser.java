package org.example.embree;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public class EmbreeRunTestser {

    public static void main(String[] args) {

        Pointer device = EmbreeNatives.rtcNewDevice(Pointer.NULL);
        Pointer scene = EmbreeNatives.rtcNewScene(device);
        Pointer geom = EmbreeNatives.rtcNewGeometry(device, Embree.RTC_GEOMETRY_TYPE_TRIANGLE);

        Pointer vertexBuffer = EmbreeNatives.rtcSetNewGeometryBuffer(geom, Embree.RTC_BUFFER_TYPE_VERTEX,
                0, Embree.RTC_FORMAT_FLOAT3, 3 * 4, 3);
        float[] vertices = {0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f};

        vertexBuffer.write(0, vertices, 0, vertices.length);

        Pointer faceIndexBuffer = EmbreeNatives.rtcSetNewGeometryBuffer(geom, Embree.RTC_BUFFER_TYPE_INDEX,
                0, Embree.RTC_FORMAT_UINT3, 3 * 4, 1);

        int[] faceIndexes = {0, 1, 2};
        faceIndexBuffer.write(0, faceIndexes, 0, faceIndexes.length);

        EmbreeNatives.rtcCommitGeometry(geom);
        System.out.println("geomId " + EmbreeNatives.rtcAttachGeometry(scene, geom));
        EmbreeNatives.rtcReleaseGeometry(geom);
        EmbreeNatives.rtcCommitScene(scene);

        RTCRayHit rtcRayHit = new RTCRayHit();
        long time = System.currentTimeMillis();
//        for(int i = 0; i < 10000; i++) {
        rtcRayHit = new RTCRayHit();
        rtcRayHit.setOrg(0, 1, -2);
        rtcRayHit.setDir(0, 0, 1);
        rtcRayHit.setTnear(0);
        rtcRayHit.setTfar(Float.POSITIVE_INFINITY);
        rtcRayHit.setGeomID(EmbreeNatives.RTC_INVALID_GEOMETRY_ID);
        rtcRayHit.write();

        System.out.println(rtcRayHit);
        System.out.println(RTCRayHit.fromBytes(rtcRayHit.toBytes()));
        System.out.println(rtcRayHit.equals(RTCRayHit.fromBytes(rtcRayHit.toBytes())));

        byte[] rtcRayHitBytes = rtcRayHit.toBytes();
        System.out.println(rtcRayHitBytes.length);
        Memory rtcRayHitBytesPointer = new Memory(RTCRayHit.STRUCT_BYTE_SIZE);
        rtcRayHitBytesPointer.write(0, rtcRayHitBytes, 0, rtcRayHitBytes.length);

        System.out.println(rtcRayHit.getPointer().dump(0, 80));
        System.out.println(rtcRayHitBytesPointer.dump(0, 80));

        RTCIntersectContext rtcIntersectContext = new RTCIntersectContext();

        EmbreeNatives.rtcIntersect1(scene, rtcIntersectContext.getPointer(), rtcRayHit.getPointer());
        EmbreeNatives.rtcIntersect1M(scene, rtcIntersectContext.getPointer(), rtcRayHitBytesPointer, 1, 16);

        rtcRayHit.read();
        rtcRayHitBytesPointer.read(0, rtcRayHitBytes, 0, rtcRayHitBytes.length);
        RTCRayHit rb = RTCRayHit.fromBytes(rtcRayHitBytes);
//        }
        System.out.println("time " + (System.currentTimeMillis() - time));
        if (rtcRayHit.geomID != EmbreeNatives.RTC_INVALID_GEOMETRY_ID) {
            System.out.println("t = " + rtcRayHit.getTfar());
            System.out.println("geomID " + rtcRayHit.geomID);
            System.out.println("Ng " + rtcRayHit.Ng_x + " " + rtcRayHit.Ng_y + " " + rtcRayHit.Ng_z);
        } else {
            System.out.println("no intersection");
        }
        System.out.println(rtcRayHit);
        System.out.println("-----------2-------------");
        if (rb.geomID != EmbreeNatives.RTC_INVALID_GEOMETRY_ID) {
            System.out.println("t = " + rb.getTfar());
            System.out.println("geomID " + rb.geomID);
            System.out.println("Ng " + rb.Ng_x + " " + rb.Ng_y + " " + rb.Ng_z);
        } else {
            System.out.println("no intersection");
        }
        System.out.println(rb);

        System.out.println("------occluded test----------");
        RTCRay rtcRay = new RTCRay();
        rtcRay.setOrg(0, 1, -3);
        rtcRay.setDir(0,0,1);
        rtcRay.setTnear(0.f);
        rtcRay.setTfar(Float.POSITIVE_INFINITY);
        rtcRay.write();

        EmbreeNatives.rtcOccluded1(scene, rtcIntersectContext.getPointer(), rtcRay.getPointer());
        System.out.println((float)rtcRay.readField("tfar"));
    }
}
