package com.cine.demo.util;

import com.cine.demo.model.enums.SeatType;
import com.cine.demo.model.enums.TicketType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

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
    void calculateUnitPrice_adultStandardReturnsBase() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(10), SeatType.STANDARD, TicketType.ADULT))
                .isEqualByComparingTo("10.00");
    }

    @Test
    void calculateUnitPrice_adultVipReturnsBaseMultiplied() {
        assertThat(PriceCalculator.calculateUnitPrice(BigDecimal.valueOf(10), SeatType.VIP, TicketType.ADULT))
                .isEqualByComparingTo("15.00");
    }

    @Test
    void applyFidelityDiscount_appliesOnlyOnAdultSubtotalWhenVisitsOver10() {
        BigDecimal adultSubtotal = BigDecimal.valueOf(20);
        List<TicketType> types = List.of(TicketType.ADULT, TicketType.ADULT, TicketType.CHILD);

        BigDecimal discount = PriceCalculator.applyFidelityDiscount(adultSubtotal, 11, types);

        assertThat(discount).isEqualByComparingTo("2.00");
    }

    @Test
    void applyFidelityDiscount_returnsZeroWhenVisitsLessOrEqualTo10() {
        BigDecimal adultSubtotal = BigDecimal.valueOf(30);
        List<TicketType> types = List.of(TicketType.ADULT, TicketType.ADULT, TicketType.ADULT);

        assertThat(PriceCalculator.applyFidelityDiscount(adultSubtotal, 10, types))
                .isEqualByComparingTo("0.00");
        assertThat(PriceCalculator.applyFidelityDiscount(adultSubtotal, 5, types))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void applyFidelityDiscount_doesNotApplyToChildStudentSenior() {
        BigDecimal subtotal = BigDecimal.valueOf(14);
        List<TicketType> types = List.of(TicketType.CHILD, TicketType.STUDENT, TicketType.SENIOR);

        assertThat(PriceCalculator.applyFidelityDiscount(subtotal, 15, types))
                .isEqualByComparingTo("0.00");
    }
}
