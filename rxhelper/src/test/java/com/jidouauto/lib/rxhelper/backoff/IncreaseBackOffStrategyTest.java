package com.jidouauto.lib.rxhelper.backoff;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class IncreaseBackOffStrategyTest {

    @Test
    public void getDelay() {

        IncreaseBackOffStrategy i1 = new IncreaseBackOffStrategy(0, 500, 60_000);

        for (int i = 1; i <= 100; i++) {
            if (0 + (i - 1) * 500 > 60_000) {
                Assert.assertEquals(i1.getDelay(i), 60_000);
            } else {
                Assert.assertEquals(i1.getDelay(i), (long) (0 + (i - 1) * 500));
            }
        }

    }
}