package io.simao.librrd;

public class RRDFetchResult {
    private final long start;
    private final long end;
    private final long step;
    private final long ds_cnt;
    private final String[] ds_names;
    private final double[] data;

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getStep() {
        return step;
    }

    public long getDs_cnt() {
        return ds_cnt;
    }

    public String[] getDs_names() {
        return ds_names;
    }

    public double[] getData() {
        return data;
    }

    public RRDFetchResult(long start, long end, long step, long ds_cnt,
                          String[] ds_names, double[] data) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.ds_cnt = ds_cnt;
        this.ds_names = ds_names;
        this.data = data;
    }
}
