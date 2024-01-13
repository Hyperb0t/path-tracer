package org.example;

import net.openhft.chronicle.bytes.Bytes;
import org.example.embree.RTCRayHitNonStruct;
import org.example.embree.RTCRayNS;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SerializationUtil {

    public static byte[] toBytesAll(RTCRayNS[] arr) {
        var b = Bytes.allocateDirect((long) arr.length * RTCRayNS.STRUCT_BYTE_SIZE);
        for(var r : arr) {
            b.unsafeWriteObject(r, RTCRayNS.STRUCT_BYTE_SIZE);
        }
        return b.toByteArray();
    }

    public static byte[] toBytesAll(RTCRayHitNonStruct[] arr) {
        var b = Bytes.allocateDirect((long) arr.length * RTCRayHitNonStruct.STRUCT_BYTE_SIZE);
        for(var r : arr) {
            b.unsafeWriteObject(r, RTCRayHitNonStruct.STRUCT_BYTE_SIZE);
        }
        return b.toByteArray();
    }

    public static RTCRayNS[] fromBytesAll1(byte[] bytes) {
        var b = Bytes.allocateDirect(bytes);
        RTCRayNS[] result = new RTCRayNS[bytes.length / RTCRayNS.STRUCT_BYTE_SIZE];
        for(int i = 0; i < result.length; i++) {
            RTCRayNS t = new RTCRayNS();
            b.unsafeReadObject(t, RTCRayNS.STRUCT_BYTE_SIZE);
            result[i] = t;
        }
        return result;
    }

    public static RTCRayHitNonStruct[] fromBytesAll2(byte[] bytes) {
        var b = Bytes.allocateDirect(bytes);
        RTCRayHitNonStruct[] result = new RTCRayHitNonStruct[bytes.length / RTCRayHitNonStruct.STRUCT_BYTE_SIZE];
        for(int i = 0; i < result.length; i++) {
            RTCRayHitNonStruct t = new RTCRayHitNonStruct();
            b.unsafeReadObject(t, RTCRayHitNonStruct.STRUCT_BYTE_SIZE);
            result[i] = t;
        }
        return result;
    }

    public static RTCRayHitNonStruct[] fromBytesAll3(byte[] bytes) {
        ByteBuffer b = ByteBuffer.wrap(bytes);
        b.order(ByteOrder.LITTLE_ENDIAN);
        int raynum = bytes.length / RTCRayHitNonStruct.STRUCT_BYTE_SIZE;
        RTCRayHitNonStruct[] result = new RTCRayHitNonStruct[raynum];
        float[] farr = new float[RTCRayHitNonStruct.STRUCT_BYTE_SIZE / 4 * raynum];
        b.asFloatBuffer().get(farr);
        int[] iarr = new int[RTCRayHitNonStruct.STRUCT_BYTE_SIZE / 4 * raynum];
        b.asIntBuffer().get(iarr);
        for(int i = 0; i < raynum; i++) {
            int offset = RTCRayHitNonStruct.STRUCT_BYTE_SIZE / 4 * i;
            result[i] = new RTCRayHitNonStruct();
            result[i].setOrg_x(farr[offset]);
            result[i].setOrg_y(farr[offset+1]);
            result[i].setOrg_z(farr[offset+2]);
            result[i].setTnear(farr[offset+3]);
            result[i].setDir_x(farr[offset+4]);
            result[i].setDir_y(farr[offset+5]);
            result[i].setDir_z(farr[offset+6]);
            result[i].setTime(farr[offset+7]);
            result[i].setTfar(farr[offset+8]);
            result[i].setMask(iarr[offset+9]);
            result[i].setId(iarr[offset+10]);
            result[i].setFlags(iarr[offset+11]);
            result[i].setNg_x(farr[offset+12]);
            result[i].setNg_y(farr[offset+13]);
            result[i].setNg_z(farr[offset+14]);
            result[i].setU(farr[offset+15]);
            result[i].setV(farr[offset+16]);
            result[i].setPrimID(iarr[offset+17]);
            result[i].setGeomID(iarr[offset+18]);
            result[i].setInstID(iarr[offset+19]);
        }
        return result;
    }

    public static void main(String[] args) {
        RTCRayHitNonStruct r = new RTCRayHitNonStruct();
        r.setId(87);
        r.setDir_x(1.5f);
        r.setOrg_y(7.77f);
        r.setTfar(0.3f);
        r.setMask(9);
        RTCRayHitNonStruct r1 = new RTCRayHitNonStruct();
        r1.setId(34);
        r1.setDir_x(2.3f);
        r1.setOrg_y(66.66f);
        r1.setTfar(0.1f);
        r1.setMask(17);

        RTCRayHitNonStruct[] arr = new RTCRayHitNonStruct[] {r1, r};
        byte[] bytes = SerializationUtil.toBytesAll(arr);
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(fromBytesAll3(bytes)));
    }

}
