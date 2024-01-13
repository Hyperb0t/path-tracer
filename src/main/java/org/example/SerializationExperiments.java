//package org.example;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.NativeBytes;
import net.openhft.chronicle.bytes.UncheckedNativeBytes;
import org.example.embree.RTCRayHitNonStruct;
import org.example.embree.RTCRayNS;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

//@State(Scope.Benchmark)
//public class SerializationExperiments {
//    public static void main(String[] args) throws IOException {
//        RTCRayNS rtcRayNS = new RTCRayNS(1,1,1, 5, 2,2,2, 10);
//        RTCRayNS[] rays = Collections.nCopies((int) 10e6, rtcRayNS).toArray(new RTCRayNS[]{});
//
//        long time = System.currentTimeMillis();
//        for (var r: rays) {
//            r.toBytes();
//        }
//        System.out.println(System.currentTimeMillis() - time);
//
//        time = System.currentTimeMillis();
//        ByteBuffer b = ByteBuffer.allocate(RTCRayNS.STRUCT_BYTE_SIZE);
//        b.order(ByteOrder.LITTLE_ENDIAN);
//        for(var r: rays) {
//            r.toBytes(b);
//            b.clear();
//        }
//        System.out.println(System.currentTimeMillis() - time);
//
//        var bytes = Bytes.allocateDirect(RTCRayNS.STRUCT_BYTE_SIZE);
//
//        time = System.currentTimeMillis();
//        for(var r : rays) {
//            bytes.writeFloat(r.org_x);
//            bytes.writeFloat(r.org_y);
//            bytes.writeFloat(r.org_z);
//            bytes.writeFloat(r.tnear);
//            bytes.writeFloat(r.dir_x);
//            bytes.writeFloat(r.dir_y);
//            bytes.writeFloat(r.dir_z);
//            bytes.writeFloat(r.time);
//            bytes.writeFloat(r.tfar);
//            bytes.writeFloat(r.mask);
//            bytes.writeFloat(r.id);
//            bytes.writeFloat(r.flags);
//            bytes.clear();
//        }
//        System.out.println(System.currentTimeMillis() - time);
//
//        time = System.currentTimeMillis();
//        for(var r : rays) {
//            bytes.unsafeWriteObject(r, RTCRayNS.STRUCT_BYTE_SIZE);
//            bytes.clear();
//        }
//        System.out.println(System.currentTimeMillis() - time);
//
//
//        System.out.println(Arrays.toString(rtcRayNS.toBytes()));
//        System.out.println(Arrays.toString(bytes.unsafeWriteObject(rtcRayNS, RTCRayNS.STRUCT_BYTE_SIZE).toByteArray()));
//        var tt = rtcRayNS.toBytes();
//        System.out.println(RTCRayNS.fromBytes(tt));
//        RTCRayNS t2 = new RTCRayNS();
//        bytes.unsafeReadObject(t2,RTCRayNS.STRUCT_BYTE_SIZE);
//        System.out.println(t2);
//
//
//        var bytes2 = Bytes.allocateDirect(RTCRayNS.STRUCT_BYTE_SIZE * 2);
//        bytes2.unsafeWriteObject(rtcRayNS, RTCRayNS.STRUCT_BYTE_SIZE);
//        bytes2.unsafeWriteObject(rtcRayNS, RTCRayNS.STRUCT_BYTE_SIZE);
//        System.out.println(Arrays.toString(bytes2.toByteArray()));
//    }
//
//    public RTCRayHitNonStruct r = new RTCRayHitNonStruct();;
//    public ByteBuffer b = ByteBuffer.allocate(RTCRayHitNonStruct.STRUCT_BYTE_SIZE);
//
//    public SerializationExperiments() {
//        r.setId(87);
//        r.setDir_x(1.5f);
//        r.setOrg_y(7.77f);
//        r.setTfar(0.3f);
//        r.setMask(9);
//    }
//
//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @Fork(value = 1, warmups = 1)
//    public void deserialize() {
//        RTCRayHitNonStruct.fromBytes3(b);
//        b.rewind();
//    }
//
//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @Fork(value = 1, warmups = 1)
//    public void deserializeOld() {
//        RTCRayHitNonStruct.fromBytes(b);
//        b.rewind();
//    }
//
//    public static void main(String[] args) throws IOException {
//        org.openjdk.jmh.Main.main(args);
//    }
//}
