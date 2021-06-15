package org.tmk.playgrounds.crdt.counters;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class GCounterTest {

    @Test
    public void testZeroAfterInit() {
        assertThat(new GCounter("replica1").value(), is(BigInteger.ZERO));
    }

    @Test
    public void testIncrementsSingleReplica() {
        var g1 = new GCounter("replica1");

        var result = g1.inc();

        assertThat(result.value(), is(BigInteger.ONE));
    }

    @Test
    public void testMergingTwoReplicas() {
        var g1 = new GCounter("replica1")
                .inc();
        var g2 = new GCounter("replica2")
                .inc();

        var result = g1.merge(g2);

        assertThat(result.value(), is(BigInteger.TWO));
    }

    @Test
    public void testConvergence() {
        var outOfSyncCounter = new GCounter("replica1")
                .inc()
                .merge(new GCounter("replica2").inc());


        var otherNode = new GCounter("replica2")
                .inc()
                .inc()
                .inc();

        // outOfSyncCounter has already data from replica2 but it is not up-to-date data
        // so after merging it should pick latest data
        var result = outOfSyncCounter.merge(otherNode);

        assertThat(result.value(), is(BigInteger.valueOf(4)));
    }

    @Test
    public void testIdempotency() {
        var g1 = new GCounter("replica1")
                .inc();
        var g2 = new GCounter("replica2")
                .inc();

        var result1 = g1.merge(g2);
        var result2 = g1.merge(result1);

        assertThat(result2.value(), is(BigInteger.TWO));
    }

    @Test
    public void testCommutativity() {
        var g1 = new GCounter("replica1").inc().inc();
        var g2 = new GCounter("replica2").inc();

        var result_a = g1.merge(g2);
        var result_b = g2.merge(g1);

        assertThat(result_a.value(), equalTo(result_b.value()));
    }

    @Test
    public void testAssociativity() {
        var g1 = new GCounter("replica1")
                .inc();
        var g2 = new GCounter("replica2")
                .inc()
                .inc();
        var g3 = new GCounter("replica3")
                .inc()
                .inc()
                .inc();

        var result_a = g1.merge(g2.merge(g3));
        var result_b = g2.merge(g1.merge(g3));

        assertThat(result_a.value(), equalTo(result_b.value()));
    }
}
