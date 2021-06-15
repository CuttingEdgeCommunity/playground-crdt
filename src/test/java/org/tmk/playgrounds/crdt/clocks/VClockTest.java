package org.tmk.playgrounds.crdt.clocks;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class VClockTest {

    @Test
    public void case1() {
        var c1 = new VClock("replica1").inc().merge(new VClock("replica2").inc());
        var c3 = new VClock("Replica3").inc().inc().inc();

        assertThat(c1.compareTo(c3), is(ClockOrder.Concurrent));
    }

    @Test
    public void testMergeAndUpdate() {
        var c1 = new VClock("replica1").inc().merge(new VClock("replica2").inc());
        var c3 = new VClock("Replica3").inc().inc().inc();

        var updated = c3.merge(c1);

        assertThat(updated.compareTo(c3), is(ClockOrder.Greater));
        assertThat(updated.compareTo(c1), is(ClockOrder.Greater));
    }
}
