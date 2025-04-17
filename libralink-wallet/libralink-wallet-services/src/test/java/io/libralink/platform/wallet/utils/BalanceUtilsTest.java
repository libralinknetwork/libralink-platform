package io.libralink.platform.wallet.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class BalanceUtilsTest {

    @Test
    public void test_percent_fee_calculation() {
        assertEquals(BigDecimal.valueOf(110).setScale(2), BalanceUtils.getTotalAmount(BigDecimal.valueOf(100), BigDecimal.valueOf(10), "percent"));
    }

    @Test
    public void test_flat_fee_calculation() {
        assertEquals(BigDecimal.valueOf(105), BalanceUtils.getTotalAmount(BigDecimal.valueOf(100), BigDecimal.valueOf(5), "flat"));
    }
}
