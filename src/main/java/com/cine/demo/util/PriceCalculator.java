package com.cine.demo.util;

import com.cine.demo.model.enums.SeatType;
import com.cine.demo.model.enums.TicketType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceCalculator {

    private static final BigDecimal FIXED_CHILD = new BigDecimal("6.00");
    private static final BigDecimal FIXED_STUDENT = new BigDecimal("6.00");
    private static final BigDecimal FIXED_ADULT = new BigDecimal("9.00");
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
                    ? FIXED_ADULT.multiply(VIP_MULTIPLIER).setScale(2, RoundingMode.HALF_UP)
                    : FIXED_ADULT;
        };
    }

    public static BigDecimal applyFidelityDiscount(BigDecimal adultSubtotal, boolean discountActive, List<TicketType> types) {
        if (!discountActive) return BigDecimal.ZERO;
        boolean hasAdult = types.stream().anyMatch(t -> t == TicketType.ADULT);
        if (!hasAdult) return BigDecimal.ZERO;
        return adultSubtotal.multiply(FIDELITY_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
