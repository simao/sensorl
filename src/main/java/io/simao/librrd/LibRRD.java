package io.simao.librrd;

public class LibRRD {
    // TODO: Use _r versions
    static { System.loadLibrary("sensorl"); }

    public static native int rrdcreate(String[] arguments);

    public static native int rrdupdate(String[] arguments);
}
