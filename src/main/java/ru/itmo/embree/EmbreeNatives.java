package ru.itmo.embree;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Library;

public class EmbreeNatives {

    public static final int RTC_GEOMETRY_TYPE_TRIANGLE = 0;

    public static final int RTC_GEOMETRY_TYPE_QUAD = 1;

    public static final int RTC_BUFFER_TYPE_INDEX = 0;

    public static final int RTC_BUFFER_TYPE_VERTEX = 1;

    public static final int RTC_FORMAT_FLOAT3 = 36867;

    public static final int RTC_FORMAT_UINT3 = 20483;

    public static final int RTC_FORMAT_INT3 = 24577;

    public static final int RTC_FORMAT_UINT4 = 20484;

    public static final int RTC_INVALID_GEOMETRY_ID = -1;

    public static native Pointer rtcNewDevice(Pointer config);

    public static native Pointer rtcNewScene(Pointer device);

    public static native Pointer rtcNewGeometry(Pointer device, int geometryType);

    public static native Pointer rtcSetNewGeometryBuffer(Pointer geometry, int bufferType, int slot, int format, int sizeByteStride, int itemCount);

    public static native void rtcCommitGeometry(Pointer geometry);

    public static native int rtcAttachGeometry(Pointer scene, Pointer geom);

    public static native void rtcReleaseGeometry(Pointer geom);

    public static native void rtcCommitScene(Pointer scene);

    public static native void rtcIntersect1(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRayHit);

    public static native void rtcIntersect1M(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRayHitArr, int m, int byteStride);

    public static native void rtcOccluded1(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRay);

    public static native void rtcOccluded1M(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRayArr, int m, int byteStride);


    static {
        Native.register("embree3");
    }

}
