package org.tmk.playgrounds.crdt.clocks;

public enum ClockOrder {
    Less,
    Equals,
    Greater,
    Concurrent;

    public static ClockOrder from(int compareResult) {
        if (compareResult == 0)
            return Equals;
        if (compareResult < 0)
            return Less;

        return Greater;
    }

}
