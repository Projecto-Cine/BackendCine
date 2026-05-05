package com.cine.demo.model.enums;

public enum PurchaseStatus {
    PENDING, CONFIRMED, CANCELLED, REFUNDED,
    /** @deprecated kept for existing DB records — use CONFIRMED for new ones */
    PAID
}