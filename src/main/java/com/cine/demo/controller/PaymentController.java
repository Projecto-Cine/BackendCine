package com.cine.demo.controller;

import com.cine.demo.dto.request.CreatePaymentIntentRequest;
import com.cine.demo.dto.request.RefundRequest;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.PaymentHistoryResponse;
import com.cine.demo.dto.response.PaymentIntentResponse;
import com.cine.demo.dto.response.RefundResponse;
import com.cine.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping({"/intent", "/create-intent"})
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PaymentIntentResponse>builder()
                        .success(true)
                        .message("PaymentIntent creado correctamente")
                        .data(paymentService.createPaymentIntent(request))
                        .build());
    }

    @PostMapping(value = "/webhook", consumes = "application/json")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<RefundResponse>> refund(
            @Valid @RequestBody RefundRequest request) {
        return ResponseEntity.ok(ApiResponse.<RefundResponse>builder()
                .success(true)
                .message("Reembolso procesado correctamente")
                .data(paymentService.refund(request))
                .build());
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentHistoryResponse>>> getHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.<List<PaymentHistoryResponse>>builder()
                .success(true)
                .message("Historial de pagos obtenido correctamente")
                .data(paymentService.getHistory(from, to, status))
                .build());
    }
}
