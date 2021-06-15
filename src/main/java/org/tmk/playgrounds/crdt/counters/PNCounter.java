package org.tmk.playgrounds.crdt.counters;

import java.math.BigInteger;

public class PNCounter {

    private final GCounter positiveCounter;
    private final GCounter negativeCounter;

    private PNCounter(GCounter positiveCounter, GCounter negativeCounter) {
        this.positiveCounter = positiveCounter;
        this.negativeCounter = negativeCounter;
    }

    public PNCounter(String replicaId) {
        this(new GCounter(replicaId), new GCounter(replicaId));
    }

    public BigInteger value() {
        return positiveCounter.value().subtract(negativeCounter.value());
    }

    public PNCounter inc() {
        return new PNCounter(positiveCounter.inc(), negativeCounter);
    }

    public PNCounter dec() {
        return new PNCounter(positiveCounter, negativeCounter.inc());
    }

    public PNCounter merge(PNCounter other) {
        return new PNCounter(
                positiveCounter.merge(other.positiveCounter),
                negativeCounter.merge(other.negativeCounter)
        );
    }
}
