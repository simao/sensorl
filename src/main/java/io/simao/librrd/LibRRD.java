package io.simao.librrd;

public class LibRRD {
    static { System.loadLibrary("sensorl"); }

    // TODO: We need to ensure thread safety on rrd* calls
    public static native int rrdcreate(String fileName,
                                       int step,
                                       int start,
                                       String[] arguments);

    public static native int rrdupdate(String fileName, String[] arguments);

    public static native RRDFetchResult rrdfetch(String filename,
                                                  String cf,
                                                  long start,
                                                  long end,
                                                  long step);
}
