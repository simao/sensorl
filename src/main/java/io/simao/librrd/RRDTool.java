package io.simao.librrd;

public class RRDTool {
    public static boolean create(String fileName, int step, int start, String[] arguments) {
        int res = LibRRD.rrdcreate(fileName, step, start, arguments);
        return res == 0;
    }

    public static boolean update(String fileName, long timestamp, double value) {
        String[] args = new String[]{timestamp + ":" + value};
        int res = LibRRD.rrdupdate(fileName, args);
        return res == 0;
    }

    public static RRDFetchResult fetch(String filename, String cf, long start, long end, long step) {
        return LibRRD.rrdfetch(filename, cf, start, end, step);
    }
}
