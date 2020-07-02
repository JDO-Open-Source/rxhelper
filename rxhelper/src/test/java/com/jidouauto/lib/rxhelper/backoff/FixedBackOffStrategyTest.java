package com.jidouauto.lib.rxhelper.backoff;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FixedBackOffStrategyTest {

    @Test
    public void getDelay() {

        FixedBackOffStrategy fixedBackOffStrategy = new FixedBackOffStrategy(100);

        for (int i = 1; i <= 100; i++) {
            Assert.assertEquals(100, fixedBackOffStrategy.getDelay(i));
        }

        FixedBackOffStrategy fixedBackOffStrategy2 = new FixedBackOffStrategy(1000, 0.2);
        for (int i = 1; i <= 100; i++) {
            System.out.println("FixedBackOffStrategyTest.getDelay:" + fixedBackOffStrategy2.getDelay(i));
            Assert.assertTrue(fixedBackOffStrategy2.getDelay(i) >= 1000 - 1000 * 0.2);
            Assert.assertTrue(fixedBackOffStrategy2.getDelay(i) <= 1000 + 1000 * 0.2);
        }

    }
}