package org.tmk.playgrounds.crdt.counters;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class GCounter {

    private final String replicaId;
    private final Map<String, BigInteger> partials;

    private GCounter(String replicaId, Map<String, BigInteger> partials) {
        this.replicaId = replicaId;
        this.partials = partials;
    }

    public GCounter(String replicaId) {
        this(replicaId, Collections.emptyMap());
    }

    public BigInteger value() {
        return partials.values().stream().reduce(BigInteger.ZERO, BigInteger::add);
    }

    public GCounter inc() {
        var incrementedPartials = new TreeMap<>(partials);
        incrementedPartials.merge(replicaId, BigInteger.ONE, BigInteger::add);
        return new GCounter(replicaId, incrementedPartials);
    }

    public GCounter merge(GCounter other) {
        var mergedPartials = new TreeMap<>(partials);

        other.partials.forEach((replicaId, count) -> {
            mergedPartials.merge(replicaId, count, BigInteger::max);
        });

        return new GCounter(replicaId, mergedPartials);
    }
}
