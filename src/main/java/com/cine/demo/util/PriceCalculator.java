package com.cine.demo.util;

import com.cine.demo.model.enums.SeatType;
import com.cine.demo.model.enums.TicketType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceCalculator {

    private static final BigDecimal FIXED_CHILD = new BigDecimal("6.00");
    private static final BigDecimal FIXED_STUDENT = new BigDecimal("6.00");
    private static final BigDecimal FIXED_SENIOR = new BigDecimal("2.00");
    private static final BigDecimal VIP_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal FIDELITY_DISCOUNT_RATE = new BigDecimal("0.10");
    private static final int FIDELITY_THRESHOLD = 10;

    private PriceCalculator() {}

    public static BigDecimal calculateUnitPrice(BigDecimal basePrice, SeatType seatType, TicketType ticketType) {
        return switch (ticketType) {
            case CHILD -> FIXED_CHILD;
            case STUDENT -> FIXED_STUDENT;
            case SENIOR -> FIXED_SENIOR;
            case ADULT -> seatType == SeatType.VIP
                    ? basePrice.multiply(VIP_MULTIPLIER).setScale(2, RoundingMode.HALF_UP)
                    : basePrice.setScale(2, RoundingMode.HALF_UP);
        };
    }

    /**
     * Calculates the fidelity discount.
     * @param adultSubtotal sum of prices for ADULT tickets
     * @param yearlyVisits number of user visits in the year
     * @param types list of all ticket types in the purchase
     * @return discount amount (0.00 if not applicable)
     */
    public static BigDecimal applyFidelityDiscount(BigDecimal adultSubtotal, int yearlyVisits, List<TicketType> types) {
        if (yearlyVisits <= FIDELITY_THRESHOLD) return BigDecimal.ZERO;
        boolean hasAdult = types.stream().anyMatch(t -> t == TicketType.ADULT);
        if (!hasAdult) return BigDecimal.ZERO;
        return adultSubtotal.multiply(FIDELITY_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
