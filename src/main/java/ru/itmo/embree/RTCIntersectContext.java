package ru.itmo.embree;


import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"RTCIntersectContextFlags", "RTCFilterFunctionN", "instID"})
public class RTCIntersectContext extends Structure {

    public int RTCIntersectContextFlags = 0;
    public Pointer RTCFilterFunctionN = Pointer.NULL;
    public int instID = 0;
}
