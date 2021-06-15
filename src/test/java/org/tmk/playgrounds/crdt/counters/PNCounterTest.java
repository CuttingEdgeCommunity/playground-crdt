package org.tmk.playgrounds.crdt.counters;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PNCounterTest {

    @Test
    public void testIncrementByOne() {
        var counter = new PNCounter("replica1")
                .inc();

        assertThat(counter.value(), is(BigInteger.ONE));
    }

    @Test
    public void testDecrementByOne() {
        var counter = new PNCounter("replica1")
                .dec();

        assertThat(counter.value(), is(BigInteger.valueOf(-1)));
    }

    @Test
    public void testConflictsMerged() {
        var counter = new PNCounter("replica1").inc().inc();

        var outOfSync1 = counter.inc().inc(); //
        var outOfSync2 = counter.dec();

        var merged = outOfSync1.merge(outOfSync2);

        assertThat(merged.value(), is(BigInteger.valueOf(3)));
    }

    @Test
    public void testIdempotency() {
        var c1 = new PNCounter("replica1")
                .inc();
        var c2 = new PNCounter("replica2")
                .inc().inc().inc().dec();

        var result1 = c1.merge(c2);
        var result2 = c1.merge(result1);

        assertThat(result2.value(), is(BigInteger.valueOf(3)));
    }

    @Test
    public void testCommutativity() {
        var c1 = new PNCounter("replica1").inc().inc().inc().dec();
        var c2 = new PNCounter("replica2").inc();

        var result_a = c1.merge(c2);
        var result_b = c2.merge(c1);

        assertThat(result_a.value(), equalTo(result_b.value()));
    }

    @Test
    public void testAssociativity() {
        var c1 = new PNCounter("replica1")
                .inc();
        var c2 = new PNCounter("replica2")
                .inc()
                .inc()
                .dec();
        var c3 = new PNCounter("replica3")
                .inc()
                .inc()
                .inc();

        var result_a = c1.merge(c2.merge(c3));
        var result_b = c2.merge(c1.merge(c3));

        assertThat(result_a.value(), equalTo(result_b.value()));
    }
}
