package com.jidouauto.lib.rxhelper.backoff;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExponentialBackOffStrategyTest {

    @Test
    public void getDelay() {

        ExponentialBackOffStrategy e1 = new ExponentialBackOffStrategy(500, 1.5, 60 * 1000);

        for (int i = 1; i <= 100; i++) {
            if (Math.pow(1.5, i - 1) * 500 > 60_000) {
                Assert.assertEquals(e1.getDelay(i), 60_000);
            } else {
                Assert.assertEquals(e1.getDelay(i), (long) (500 * Math.pow(1.5, i - 1)), 0.001);
            }
        }

    }
}