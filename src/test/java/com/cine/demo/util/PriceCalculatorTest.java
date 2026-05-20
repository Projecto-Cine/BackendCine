package com.cine.demo.util;

import com.cine.demo.model.enums.SeatType;
import com.cine.demo.model.enums.TicketType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceCalculatorTest {

    @Test
    void calculateUnitPrice_childAlwaysReturns6_regardlessOfSeatType() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(15), SeatType.STANDARD, TicketType.CHILD))
                .isEqualByComparingTo("6.00");
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(15), SeatType.VIP, TicketType.CHILD))
                .isEqualByComparingTo("6.00");
    }

    @Test
    void calculateUnitPrice_seniorAlwaysReturns2() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(10), SeatType.STANDARD, TicketType.SENIOR))
                .isEqualByComparingTo("2.00");
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(10), SeatType.VIP, TicketType.SENIOR))
                .isEqualByComparingTo("2.00");
    }

    @Test
    void calculateUnitPrice_studentAlwaysReturns6() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(12), SeatType.STANDARD, TicketType.STUDENT))
                .isEqualByComparingTo("6.00");
    }

    @Test
    void calculateUnitPrice_adultStandardReturns9() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(10), SeatType.STANDARD, TicketType.ADULT))
                .isEqualByComparingTo("9.00");
    }

    @Test
    void calculateUnitPrice_adultVipReturns13dot5() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(10), SeatType.VIP, TicketType.ADULT))
                .isEqualByComparingTo("13.50");
    }

    @Test
    void applyFidelityDiscount_appliesWhenDiscountActive() {
        BigDecimal subtotal = BigDecimal.valueOf(20);

        assertThat(PriceCalculator.applyFidelityDiscount(subtotal, true))
                .isEqualByComparingTo("2.00");
    }

    @Test
    void applyFidelityDiscount_returnsZeroWhenDiscountInactive() {
        BigDecimal subtotal = BigDecimal.valueOf(30);

        assertThat(PriceCalculator.applyFidelityDiscount(subtotal, false))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void calculateFidelityDiscount_returns10PercentOfTotal() {
        assertThat(PriceCalculator.calculateFidelityDiscount(BigDecimal.valueOf(50)))
                .isEqualByComparingTo("5.00");
    }

    @Test
    void isEligibleForDiscount_returnsTrueWhenVisitsAtThreshold() {
        assertThat(PriceCalculator.isEligibleForDiscount(10)).isTrue();
    }

    @Test
    void isEligibleForDiscount_returnsTrueWhenVisitsAboveThreshold() {
        assertThat(PriceCalculator.isEligibleForDiscount(11)).isTrue();
    }

    @Test
    void isEligibleForDiscount_returnsFalseWhenVisitsBelowThreshold() {
        assertThat(PriceCalculator.isEligibleForDiscount(9)).isFalse();
    }
}
