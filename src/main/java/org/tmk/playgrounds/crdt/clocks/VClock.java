package org.tmk.playgrounds.crdt.clocks;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class VClock /* implements Comparable<VClock> */ {

    private final String replicaId;
    private final Map<String, BigInteger> partials;


    private VClock(String replicaId, Map<String, BigInteger> partials) {
        this.replicaId = replicaId;
        this.partials = partials;
    }

    public VClock(String replicaId) {
        this(replicaId, Collections.emptyMap());
    }

    public VClock inc() {
        var incrementedPartials = new TreeMap<>(partials);
        incrementedPartials.merge(replicaId, BigInteger.ONE, BigInteger::add);
        return new VClock(replicaId, incrementedPartials);
    }

    public VClock merge(VClock other) {
        var mergedPartials = new TreeMap<>(partials);

        other.partials.forEach((replicaId, count) -> {
            mergedPartials.merge(replicaId, count, BigInteger::max);
        });

        return new VClock(replicaId, mergedPartials);
    }

    /*
    @Override
    public int compareTo(VClock other) {
        return 0;
    }
    */

    public ClockOrder compareTo(VClock other) {
        var allKeys = new TreeSet<>(partials.keySet());
        allKeys.addAll(other.partials.keySet());

        var result = 0;

        for (var key: allKeys) {
            var leftVal = partials.getOrDefault(key, BigInteger.ZERO);
            var rightVal = other.partials.getOrDefault(key, BigInteger.ZERO);
            var partialOrd = leftVal.compareTo(rightVal);

            if (result * partialOrd < 0) // detect different signs
                return ClockOrder.Concurrent;
            result += partialOrd;
        }

        return ClockOrder.from(result);
    }

}
