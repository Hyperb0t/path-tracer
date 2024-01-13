package org.example.embree;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface Embree extends Library {

    public static final Tbb12 TBB_12 = Tbb12.INSTANCE;
    public static final Embree INSTANCE = Native.load("embree3", Embree.class);

    public static final int RTC_GEOMETRY_TYPE_TRIANGLE = 0;

    public static final int RTC_GEOMETRY_TYPE_QUAD = 1;

    public static final int RTC_BUFFER_TYPE_INDEX = 0;

    public static final int RTC_BUFFER_TYPE_VERTEX = 1;

    public static final int RTC_FORMAT_FLOAT3 = 36867;

    public static final int RTC_FORMAT_UINT3 = 20483;

    public static final int RTC_FORMAT_INT3 = 24577;

    public static final int RTC_FORMAT_UINT4 = 20484;

    public static final int RTC_INVALID_GEOMETRY_ID = -1;

    public Pointer rtcNewDevice(Pointer config);

    public Pointer rtcNewScene(Pointer device);

    public Pointer rtcNewGeometry(Pointer device, int geometryType);

    public Pointer rtcSetNewGeometryBuffer(Pointer geometry, int bufferType, int slot, int format, int sizeByteStride, int itemCount);

    public void rtcCommitGeometry(Pointer geometry);

    public int rtcAttachGeometry(Pointer scene, Pointer geom);

    public void rtcReleaseGeometry(Pointer geom);

    public void rtcCommitScene(Pointer scene);

    public void rtcInitIntersectContext(Pointer rtcIntersectContext);

    public void rtcIntersect1(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRayHit);

    public void rtcIntersect1M(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRayHitArr, int m, int byteStride);

    public void rtcOccluded1(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRay);

    public void rtcOccluded1M(Pointer scene, Pointer rtcIntersectContext, Pointer rtcRayArr, int m, int byteStride);



}
