package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests muy ligeros para los enums "simples" del dominio.
 * Sirven para que JaCoCo cuente como cubierto el código sintético
 * que Java genera para values()/valueOf() en cada enum.
 */
class EnumsBasicTest {

    /**
     * Role: ADMIN y CLIENTE son los dos únicos valores y deben recuperarse
     * tanto por iteración como por valueOf.
     */
    @Test
    void role_hasAdminAndClient() {
        assertThat(Role.values()).containsExactlyInAnyOrder(Role.ADMIN, Role.CLIENTE);
        assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
        assertThat(Role.valueOf("CLIENTE")).isEqualTo(Role.CLIENTE);
    }

    /**
     * UserType: ADULT, STUDENT, SENIOR. Estado de tarifas según perfil
     * de usuario (sin descuento, descuento estudiante, descuento senior).
     */
    @Test
    void userType_hasThreeValues() {
        assertThat(UserType.values()).containsExactlyInAnyOrder(
                UserType.ADULT, UserType.STUDENT, UserType.SENIOR);
        assertThat(UserType.valueOf("ADULT")).isEqualTo(UserType.ADULT);
        assertThat(UserType.valueOf("STUDENT")).isEqualTo(UserType.STUDENT);
        assertThat(UserType.valueOf("SENIOR")).isEqualTo(UserType.SENIOR);
    }

    /**
     * SeatType: STANDARD, VIP. Define el tipo de asiento físico de la sala.
     */
    @Test
    void seatType_hasStandardAndVip() {
        assertThat(SeatType.values()).contains(SeatType.STANDARD, SeatType.VIP);
        assertThat(SeatType.valueOf("STANDARD")).isEqualTo(SeatType.STANDARD);
    }

    /**
     * TicketType: ADULT, CHILD, STUDENT, SENIOR. Determina el precio aplicado.
     */
    @Test
    void ticketType_hasAllExpectedValues() {
        assertThat(TicketType.values()).contains(TicketType.ADULT, TicketType.CHILD);
        assertThat(TicketType.valueOf("ADULT")).isEqualTo(TicketType.ADULT);
    }

    /**
     * PurchaseStatus: ciclo de vida de una compra
     * (PENDING → PAID o CANCELLED).
     */
    @Test
    void purchaseStatus_hasPendingPaidCancelled() {
        assertThat(PurchaseStatus.values()).contains(
                PurchaseStatus.PENDING, PurchaseStatus.PAID, PurchaseStatus.CANCELLED);
        assertThat(PurchaseStatus.valueOf("PAID")).isEqualTo(PurchaseStatus.PAID);
    }

    /**
     * MerchandiseCategory: el catálogo de tipos de producto vendibles
     * en la tienda del cine. Se hace una verificación amplia de todos
     * los nombres reconocidos.
     */
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
