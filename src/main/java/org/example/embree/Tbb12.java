package org.example.embree;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Tbb12 extends Library {
    public static final Tbb12 INSTANCE = Native.load("tbb12", Tbb12.class);
}
