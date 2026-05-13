package com.cine.demo.service.impl;

import com.cine.demo.dto.request.CreatePaymentIntentRequest;
import com.cine.demo.dto.request.RefundRequest;
import com.cine.demo.dto.response.PaymentHistoryResponse;
import com.cine.demo.dto.response.PaymentIntentResponse;
import com.cine.demo.dto.response.RefundResponse;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.MerchandiseSale;
import com.cine.demo.model.Purchase;
import com.cine.demo.model.Refund;
import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.RefundRepository;
import com.cine.demo.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PurchaseRepository purchaseRepository;
    private final RefundRepository refundRepository;
    private final MerchandiseSaleRepository merchandiseSaleRepository;
    private final MerchandiseRepository merchandiseRepository;

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + request.getPurchaseId()));

        long amountInCents = request.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .putMetadata("purchaseId", String.valueOf(purchase.getId()))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            purchase.setPaymentIntentId(intent.getId());
            purchase.setPaymentMethod(PaymentMethod.CARD);
            purchaseRepository.save(purchase);

            return PaymentIntentResponse.builder()
                    .clientSecret(intent.getClientSecret())
                    .paymentIntentId(intent.getId())
                    .publishableKey(publishableKey)
                    .build();

        } catch (StripeException e) {
            throw new RuntimeException("Error al crear el PaymentIntent: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Firma del webhook inválida", e);
        }

        Optional<StripeObject> stripeObjectOpt = event.getDataObjectDeserializer().getObject();
        if (stripeObjectOpt.isEmpty()) return;

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent intent = (PaymentIntent) stripeObjectOpt.get();
                purchaseRepository.findByPaymentIntentId(intent.getId()).ifPresent(purchase -> {
                    if (purchase.getStatus() == PurchaseStatus.PAID || purchase.getStatus() == PurchaseStatus.CONFIRMED) {
                        return;
                    }
                    fulfillConcessionSales(purchase);
                    purchase.setStatus(PurchaseStatus.PAID);
                    purchase.setPaidAt(LocalDateTime.now());
                    purchaseRepository.save(purchase);
                });
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = (PaymentIntent) stripeObjectOpt.get();
                purchaseRepository.findByPaymentIntentId(intent.getId()).ifPresent(purchase -> {
                    purchase.setStatus(PurchaseStatus.CANCELLED);
                    purchaseRepository.save(purchase);
                });
            }
            default -> { /* evento no gestionado */ }
        }
    }

    private void fulfillConcessionSales(Purchase purchase) {
        List<MerchandiseSale> sales = merchandiseSaleRepository.findByPurchaseId(purchase.getId());
        for (MerchandiseSale sale : sales) {
            Merchandise merchandise = sale.getMerchandise();
            if (merchandise.getStock() < sale.getQuantity()) {
                throw new IllegalStateException("Stock insuficiente para " + merchandise.getName());
            }
            merchandise.setStock(merchandise.getStock() - sale.getQuantity());
            merchandiseRepository.save(merchandise);
        }
    }

    @Override
    @Transactional
    public RefundResponse refund(RefundRequest request) {
        Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + request.getPurchaseId()));

        if (purchase.getPaymentIntentId() == null) {
            throw new IllegalStateException("Esta compra no tiene un PaymentIntent asociado");
        }
        if (purchase.getStatus() == PurchaseStatus.REFUNDED) {
            throw new IllegalStateException("Esta compra ya fue reembolsada");
        }

        try {
            PaymentIntent intent = PaymentIntent.retrieve(purchase.getPaymentIntentId());
            String chargeId = intent.getLatestCharge();

            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(chargeId)
                    .build();

            com.stripe.model.Refund stripeRefund = com.stripe.model.Refund.create(params);

            Refund refund = Refund.builder()
                    .purchaseId(purchase.getId())
                    .stripeRefundId(stripeRefund.getId())
                    .amount(BigDecimal.valueOf(stripeRefund.getAmount()).divide(BigDecimal.valueOf(100)))
                    .reason(request.getReason())
                    .status(stripeRefund.getStatus())
                    .build();
            refundRepository.save(refund);

            purchase.setStatus(PurchaseStatus.REFUNDED);
            purchaseRepository.save(purchase);

            return RefundResponse.builder()
                    .refundId(stripeRefund.getId())
                    .amount(refund.getAmount())
                    .status(stripeRefund.getStatus())
                    .build();

        } catch (StripeException e) {
            throw new RuntimeException("Error al procesar el reembolso: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryResponse> getHistory(LocalDate from, LocalDate to, String status) {
        PurchaseStatus purchaseStatus = null;
        if (status != null && !status.isBlank()) {
            purchaseStatus = PurchaseStatus.valueOf(status.toUpperCase());
        }

        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt = (to != null) ? to.atTime(23, 59, 59) : null;

        return purchaseRepository.findByStatusAndDateRange(purchaseStatus, fromDt, toDt).stream()
                .map(p -> PaymentHistoryResponse.builder()
                        .purchaseId(p.getId())
                        .paymentIntentId(p.getPaymentIntentId())
                        .amount(p.getTotalAmount())
                        .status(p.getStatus())
                        .paymentMethod(p.getPaymentMethod())
                        .type("purchase")
                        .createdAt(p.getCreatedAt())
                        .userId(p.getUser().getId())
                        .userName(p.getUser().getName())
                        .build())
                .toList();
    }
}
