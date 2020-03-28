package com.github.tehArchitecht.springbankingapp.logic;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Provides a single static method convert to convert between different
 * currencies used by the application.
 * @implNote Uses hard coded values for conversion rates.
 */
public class CurrencyConverter {
    private static final BigDecimal EUR_TO_RUB = new BigDecimal("80.93");
    private static final BigDecimal USD_TO_RUB = new BigDecimal("72.62");

    private static final int PRECISION = 3;
    private static final RoundingMode MODE = RoundingMode.HALF_UP;

    public static BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        BigDecimal result = amount;

        if (from == Currency.EUR) result = result.multiply(EUR_TO_RUB);
        else if (from == Currency.USD) result = result.multiply(USD_TO_RUB);

        if (to == Currency.EUR) result = result.divide(EUR_TO_RUB, PRECISION, MODE);
        else if (to == Currency.USD) result = result.divide(USD_TO_RUB, PRECISION, MODE);

        return result;
    }
}
