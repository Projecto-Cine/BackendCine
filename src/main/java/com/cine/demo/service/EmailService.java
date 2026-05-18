package com.cine.demo.service;

import com.cine.demo.model.Purchase;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendPurchaseConfirmation(Purchase purchase);
}
