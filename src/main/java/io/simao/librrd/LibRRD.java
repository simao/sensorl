package io.simao.librrd;

public class LibRRD {
    static { System.loadLibrary("sensorl"); }
    public static native int rrdcreate();
}
