package com.cine.demo.service;

import com.cine.demo.dto.request.CreatePaymentIntentRequest;
import com.cine.demo.dto.request.RefundRequest;
import com.cine.demo.dto.response.PaymentHistoryResponse;
import com.cine.demo.dto.response.PaymentIntentResponse;
import com.cine.demo.dto.response.RefundResponse;
import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request);
    void handleWebhook(String payload, String sigHeader);
    RefundResponse refund(RefundRequest request);
    List<PaymentHistoryResponse> getHistory(LocalDate from, LocalDate to, String status);
}