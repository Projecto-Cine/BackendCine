package com.cine.demo.exception;

import com.cine.demo.security.ForbiddenException;
import com.cine.demo.security.InvalidTokenException;
import com.cine.demo.security.UnauthorizedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void businessRuleException_propagatesMessage() {
        BusinessRuleException ex = new BusinessRuleException("Rule broken");
        assertThat(ex.getMessage()).isEqualTo("Rule broken");
    }

    @Test
    void forbiddenException_propagatesMessage() {
        ForbiddenException ex = new ForbiddenException("Access forbidden");
        assertThat(ex.getMessage()).isEqualTo("Access forbidden");
    }

    @Test
    void unauthorizedException_propagatesMessage() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");
        assertThat(ex.getMessage()).isEqualTo("Unauthorized");
    }

    @Test
    void invalidTokenException_propagatesMessage() {
        InvalidTokenException ex = new InvalidTokenException("Token expired");
        assertThat(ex.getMessage()).isEqualTo("Token expired");
    }

    @Test
    void resourceNotFoundException_propagatesMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertThat(ex.getMessage()).isEqualTo("Not found");
    }

    @Test
    void conflictException_propagatesMessage() {
        ConflictException ex = new ConflictException("Conflict");
        assertThat(ex.getMessage()).isEqualTo("Conflict");
    }

    @Test
    void seatAlreadyTakenException_propagatesMessage() {
        SeatAlreadyTakenException ex = new SeatAlreadyTakenException("Occupied");
        assertThat(ex.getMessage()).isEqualTo("Occupied");
    }

    @Test
    void screeningFullException_propagatesMessage() {
        ScreeningFullException ex = new ScreeningFullException("Full");
        assertThat(ex.getMessage()).isEqualTo("Full");
    }

    @Test
    void screeningAlreadyPassedException_propagatesMessage() {
        ScreeningAlreadyPassedException ex = new ScreeningAlreadyPassedException("Already passed");
        assertThat(ex.getMessage()).isEqualTo("Already passed");
    }

    @Test
    void purchaseAlreadyCancelledException_propagatesMessage() {
        PurchaseAlreadyCancelledException ex = new PurchaseAlreadyCancelledException("Already cancelled");
        assertThat(ex.getMessage()).isEqualTo("Already cancelled");
    }

    @Test
    void invalidPurchaseStatusException_propagatesMessage() {
        InvalidPurchaseStatusException ex = new InvalidPurchaseStatusException("Invalid status");
        assertThat(ex.getMessage()).isEqualTo("Invalid status");
    }

    @Test
    void ageRestrictionException_propagatesMessage() {
        AgeRestrictionException ex = new AgeRestrictionException("Minimum age not met");
        assertThat(ex.getMessage()).isEqualTo("Minimum age not met");
    }

    @Test
    void minorWithoutAdultException_propagatesMessage() {
        MinorWithoutAdultException ex = new MinorWithoutAdultException("Minor without adult");
        assertThat(ex.getMessage()).isEqualTo("Minor without adult");
    }
}
