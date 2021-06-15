package org.tmk.playgrounds.crdt;

public class LWWReg {

    private final String data;
    private final long timestamp;

    public LWWReg(String data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LWWReg merge(LWWReg other) {
        if (timestamp > other.timestamp)
            return this;
        return other;
    }

}
