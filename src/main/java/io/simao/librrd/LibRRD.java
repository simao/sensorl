package io.simao.librrd;

public class LibRRD {
    // TODO: Use _r versions
    static { System.loadLibrary("sensorl"); }

    // TODO: We need to ensure thread safety on rrdupate calls

    public static native int rrdcreate(String fileName,
                                       int step,
                                       int start,
                                       String[] arguments);

    public static native int rrdupdate(String fileName, String[] arguments);
}
