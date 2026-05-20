package com.cine.demo.payment;

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
import com.cine.demo.model.User;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.RefundRepository;
import com.cine.demo.service.impl.PaymentServiceImpl;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PurchaseRepository purchaseRepository;
    @Mock private RefundRepository refundRepository;
    @Mock private MerchandiseSaleRepository merchandiseSaleRepository;
    @Mock private MerchandiseRepository merchandiseRepository;

    @InjectMocks
    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "secretKey", "sk_test_dummy");
        ReflectionTestUtils.setField(service, "publishableKey", "pk_test_dummy");
        ReflectionTestUtils.setField(service, "webhookSecret", "whsec_dummy");
    }

    // ── init ──────────────────────────────────────────────────────────────────

    @Test
    void init_setsStripeApiKey() {
        service.init();
        assertThat(Stripe.apiKey).isEqualTo("sk_test_dummy");
    }

    // ── createPaymentIntent ───────────────────────────────────────────────────

    @Test
    void createPaymentIntent_throwsResourceNotFoundException_whenPurchaseNotFound() {
        CreatePaymentIntentRequest req = CreatePaymentIntentRequest.builder()
                .purchaseId(99L).amount(BigDecimal.TEN).currency("EUR").build();
        when(purchaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPaymentIntent(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createPaymentIntent_returnsClientSecret_whenSuccessful() throws StripeException {
        CreatePaymentIntentRequest req = CreatePaymentIntentRequest.builder()
                .purchaseId(1L).amount(BigDecimal.valueOf(20)).currency("EUR").build();
        Purchase purchase = Purchase.builder().id(1L).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(purchaseRepository.save(any())).thenReturn(purchase);

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getId()).thenReturn("pi_test_123");
        when(mockIntent.getClientSecret()).thenReturn("cs_test_abc");

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockIntent);

            PaymentIntentResponse result = service.createPaymentIntent(req);

            assertThat(result.paymentIntentId()).isEqualTo("pi_test_123");
            assertThat(result.clientSecret()).isEqualTo("cs_test_abc");
            assertThat(result.publishableKey()).isEqualTo("pk_test_dummy");
            assertThat(purchase.getPaymentIntentId()).isEqualTo("pi_test_123");
        }
    }

    @Test
    void createPaymentIntent_throwsRuntimeException_whenStripeFails() throws StripeException {
        CreatePaymentIntentRequest req = CreatePaymentIntentRequest.builder()
                .purchaseId(1L).amount(BigDecimal.TEN).currency("EUR").build();
        Purchase purchase = Purchase.builder().id(1L).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        StripeException stripeEx = mock(StripeException.class);
        when(stripeEx.getMessage()).thenReturn("stripe error");

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenThrow(stripeEx);

            assertThatThrownBy(() -> service.createPaymentIntent(req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to create PaymentIntent");
        }
    }

    // ── handleWebhook ─────────────────────────────────────────────────────────

    @Test
    void handleWebhook_throwsRuntimeException_whenSignatureInvalid() throws Exception {
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenThrow(new SignatureVerificationException("Invalid", "header"));

            assertThatThrownBy(() -> service.handleWebhook("payload", "bad_sig"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid webhook signature");
        }
    }

    @Test
    void handleWebhook_doesNothing_whenStripeObjectEmpty() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.empty());

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            verify(purchaseRepository, never()).save(any());
        }
    }

    @Test
    void handleWebhook_marksPurchasePaid_whenPaymentSucceeded() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        when(event.getType()).thenReturn("payment_intent.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("pi_test_123");

        Purchase purchase = Purchase.builder().id(1L).status(PurchaseStatus.PENDING).build();
        when(purchaseRepository.findByPaymentIntentId("pi_test_123")).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(1L)).thenReturn(List.of());
        when(purchaseRepository.save(any())).thenReturn(purchase);

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PAID);
            verify(purchaseRepository).save(purchase);
        }
    }

    @Test
    void handleWebhook_skipsAlreadyPaidPurchase_whenPaymentSucceeded() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        when(event.getType()).thenReturn("payment_intent.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("pi_paid");

        Purchase purchase = Purchase.builder().id(1L).status(PurchaseStatus.PAID).build();
        when(purchaseRepository.findByPaymentIntentId("pi_paid")).thenReturn(Optional.of(purchase));

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            verify(purchaseRepository, never()).save(any());
        }
    }

    @Test
    void handleWebhook_skipsConfirmedPurchase_whenPaymentSucceeded() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        when(event.getType()).thenReturn("payment_intent.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("pi_confirmed");

        Purchase purchase = Purchase.builder().id(2L).status(PurchaseStatus.CONFIRMED).build();
        when(purchaseRepository.findByPaymentIntentId("pi_confirmed")).thenReturn(Optional.of(purchase));

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            verify(purchaseRepository, never()).save(any());
        }
    }

    @Test
    void handleWebhook_fulfillsConcessionSales_whenSufficientStock() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        when(event.getType()).thenReturn("payment_intent.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("pi_concession");

        Purchase purchase = Purchase.builder().id(3L).status(PurchaseStatus.PENDING).build();
        Merchandise merch = Merchandise.builder().id(1L).name("Popcorn").stock(5).build();
        MerchandiseSale sale = MerchandiseSale.builder().id(1L).merchandise(merch).quantity(2).build();

        when(purchaseRepository.findByPaymentIntentId("pi_concession")).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(3L)).thenReturn(List.of(sale));
        when(merchandiseRepository.save(merch)).thenReturn(merch);
        when(purchaseRepository.save(any())).thenReturn(purchase);

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            assertThat(merch.getStock()).isEqualTo(3);
            verify(merchandiseRepository).save(merch);
        }
    }

    @Test
    void handleWebhook_throwsIllegalStateException_whenInsufficientStock() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        when(event.getType()).thenReturn("payment_intent.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("pi_nostock");

        Purchase purchase = Purchase.builder().id(4L).status(PurchaseStatus.PENDING).build();
        Merchandise merch = Merchandise.builder().id(1L).name("Soda").stock(1).build();
        MerchandiseSale sale = MerchandiseSale.builder().id(1L).merchandise(merch).quantity(5).build();

        when(purchaseRepository.findByPaymentIntentId("pi_nostock")).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(4L)).thenReturn(List.of(sale));

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            assertThatThrownBy(() -> service.handleWebhook("payload", "sig"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Insufficient stock");
        }
    }

    @Test
    void handleWebhook_marksPurchaseCancelled_whenPaymentFailed() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        PaymentIntent paymentIntent = mock(PaymentIntent.class);

        when(event.getType()).thenReturn("payment_intent.payment_failed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
        when(paymentIntent.getId()).thenReturn("pi_failed");

        Purchase purchase = Purchase.builder().id(5L).status(PurchaseStatus.PENDING).build();
        when(purchaseRepository.findByPaymentIntentId("pi_failed")).thenReturn(Optional.of(purchase));
        when(purchaseRepository.save(any())).thenReturn(purchase);

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CANCELLED);
        }
    }

    @Test
    void handleWebhook_doesNothing_whenEventTypeUnknown() throws Exception {
        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);

        when(event.getType()).thenReturn("charge.succeeded");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(Optional.of(mock(StripeObject.class)));

        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(event);

            service.handleWebhook("payload", "sig");

            verify(purchaseRepository, never()).save(any());
        }
    }

    // ── refund ────────────────────────────────────────────────────────────────

    @Test
    void refund_throwsResourceNotFoundException_whenPurchaseNotFound() {
        RefundRequest req = RefundRequest.builder().purchaseId(99L).build();
        when(purchaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refund(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void refund_throwsIllegalStateException_whenNoPaymentIntentId() {
        RefundRequest req = RefundRequest.builder().purchaseId(1L).build();
        Purchase purchase = Purchase.builder().id(1L).paymentIntentId(null).status(PurchaseStatus.PAID).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.refund(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no associated PaymentIntent");
    }

    @Test
    void refund_throwsIllegalStateException_whenAlreadyRefunded() {
        RefundRequest req = RefundRequest.builder().purchaseId(1L).build();
        Purchase purchase = Purchase.builder().id(1L).paymentIntentId("pi_test")
                .status(PurchaseStatus.REFUNDED).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.refund(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been refunded");
    }

    @Test
    void refund_processesRefundSuccessfully() throws StripeException {
        RefundRequest req = RefundRequest.builder().purchaseId(1L).reason("duplicate").build();
        Purchase purchase = Purchase.builder().id(1L).paymentIntentId("pi_test")
                .status(PurchaseStatus.PAID).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(purchaseRepository.save(any())).thenReturn(purchase);

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getLatestCharge()).thenReturn("ch_test_123");

        com.stripe.model.Refund mockStripeRefund = mock(com.stripe.model.Refund.class);
        when(mockStripeRefund.getId()).thenReturn("re_test_123");
        when(mockStripeRefund.getAmount()).thenReturn(1000L);
        when(mockStripeRefund.getStatus()).thenReturn("succeeded");

        when(refundRepository.save(any())).thenReturn(Refund.builder().id(1L).build());

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class);
             MockedStatic<com.stripe.model.Refund> refundMock = mockStatic(com.stripe.model.Refund.class)) {

            piMock.when(() -> PaymentIntent.retrieve(anyString())).thenReturn(mockIntent);
            refundMock.when(() -> com.stripe.model.Refund.create(any(RefundCreateParams.class)))
                    .thenReturn(mockStripeRefund);

            RefundResponse result = service.refund(req);

            assertThat(result.refundId()).isEqualTo("re_test_123");
            assertThat(result.status()).isEqualTo("succeeded");
            assertThat(result.amount()).isEqualByComparingTo("10.00");
            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.REFUNDED);
        }
    }

    @Test
    void refund_throwsRuntimeException_whenStripeFails() throws StripeException {
        RefundRequest req = RefundRequest.builder().purchaseId(1L).build();
        Purchase purchase = Purchase.builder().id(1L).paymentIntentId("pi_test")
                .status(PurchaseStatus.PAID).build();
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        StripeException stripeEx = mock(StripeException.class);
        when(stripeEx.getMessage()).thenReturn("stripe refund error");

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            piMock.when(() -> PaymentIntent.retrieve(anyString())).thenThrow(stripeEx);

            assertThatThrownBy(() -> service.refund(req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to process refund");
        }
    }

    // ── getHistory ────────────────────────────────────────────────────────────

    @Test
    void getHistory_returnsEmptyList_whenNoPurchases() {
        when(purchaseRepository.findByStatusAndDateRange(any(), any(), any())).thenReturn(List.of());

        assertThat(service.getHistory(null, null, null)).isEmpty();
    }

    @Test
    void getHistory_filtersWithStatusAndDateRange() {
        User user = User.builder().id(1L).name("Ana").build();
        Purchase purchase = Purchase.builder()
                .id(1L).status(PurchaseStatus.PAID).paymentIntentId("pi_test")
                .totalAmount(BigDecimal.TEN).user(user).build();
        when(purchaseRepository.findByStatusAndDateRange(
                eq(PurchaseStatus.PAID), any(), any()))
                .thenReturn(List.of(purchase));

        List<PaymentHistoryResponse> result = service.getHistory(
                LocalDate.now().minusDays(7), LocalDate.now(), "PAID");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).purchaseId()).isEqualTo(1L);
        assertThat(result.get(0).status()).isEqualTo(PurchaseStatus.PAID);
        assertThat(result.get(0).userName()).isEqualTo("Ana");
    }

    @Test
    void getHistory_returnsAllPurchases_whenNoFilters() {
        User user = User.builder().id(1L).name("Bob").build();
        Purchase purchase = Purchase.builder()
                .id(2L).status(PurchaseStatus.PENDING).totalAmount(BigDecimal.valueOf(20))
                .user(user).build();
        when(purchaseRepository.findByStatusAndDateRange(isNull(), isNull(), isNull()))
                .thenReturn(List.of(purchase));

        List<PaymentHistoryResponse> result = service.getHistory(null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo("purchase");
    }
}