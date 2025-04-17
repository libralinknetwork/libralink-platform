package io.libralink.platform.wallet.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BalanceUtils {

    private final static int SCALE = 2;
    private final static RoundingMode ROUNDING_MODE = RoundingMode.UP;


    private BalanceUtils() {}

    public static BigDecimal getTotalAmount(BigDecimal amount, BigDecimal feeAmount, String feeType) {
        BigDecimal fee = BigDecimal.ZERO;

        if ("percent".equals(feeType)) {
            fee = amount.divide(BigDecimal.valueOf(100), SCALE, ROUNDING_MODE)
                 .multiply(feeAmount);
        } else {
            fee = feeAmount;
        }

        return amount.add(fee);
    }
}
