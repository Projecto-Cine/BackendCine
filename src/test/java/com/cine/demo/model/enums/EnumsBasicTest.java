package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnumsBasicTest {

    @Test
    void role_hasAdminAndClient() {
        assertThat(Role.values()).containsExactlyInAnyOrder(Role.ADMIN, Role.CLIENTE);
        assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
        assertThat(Role.valueOf("CLIENTE")).isEqualTo(Role.CLIENTE);
    }

    @Test
    void userType_hasThreeValues() {
        assertThat(UserType.values()).containsExactlyInAnyOrder(
                UserType.ADULT, UserType.STUDENT, UserType.SENIOR);
        assertThat(UserType.valueOf("ADULT")).isEqualTo(UserType.ADULT);
        assertThat(UserType.valueOf("STUDENT")).isEqualTo(UserType.STUDENT);
        assertThat(UserType.valueOf("SENIOR")).isEqualTo(UserType.SENIOR);
    }

    @Test
    void seatType_hasStandardAndVip() {
        assertThat(SeatType.values()).contains(SeatType.STANDARD, SeatType.VIP);
        assertThat(SeatType.valueOf("STANDARD")).isEqualTo(SeatType.STANDARD);
    }

    @Test
    void ticketType_hasAllExpectedValues() {
        assertThat(TicketType.values()).contains(TicketType.ADULT, TicketType.CHILD);
        assertThat(TicketType.valueOf("ADULT")).isEqualTo(TicketType.ADULT);
    }

    @Test
    void purchaseStatus_hasPendingPaidCancelled() {
        assertThat(PurchaseStatus.values()).contains(
                PurchaseStatus.PENDING, PurchaseStatus.PAID, PurchaseStatus.CANCELLED);
        assertThat(PurchaseStatus.valueOf("PAID")).isEqualTo(PurchaseStatus.PAID);
    }

    @Test
    void merchandiseCategory_includesAllKnownCategories() {
        assertThat(MerchandiseCategory.values()).contains(
                MerchandiseCategory.CLOTHING,
                MerchandiseCategory.ACCESSORIES,
                MerchandiseCategory.POSTERS,
                MerchandiseCategory.COLLECTIBLES,
                MerchandiseCategory.OTHER,
                MerchandiseCategory.FOOD,
                MerchandiseCategory.DRINK,
                MerchandiseCategory.MERCHANDISE);
    }
}
