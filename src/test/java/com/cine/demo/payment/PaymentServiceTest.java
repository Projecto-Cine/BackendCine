package com.cine.demo.payment;

import com.cine.demo.dto.request.CreatePaymentIntentRequest;
import com.cine.demo.dto.request.RefundRequest;
import com.cine.demo.dto.response.PaymentHistoryResponse;
import com.cine.demo.dto.response.PaymentIntentResponse;
import com.cine.demo.dto.response.RefundResponse;
import com.cine.demo.exception.BusinessRuleException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.MerchandiseSale;
import com.cine.demo.model.Purchase;
import com.cine.demo.model.Refund;
import com.cine.demo.model.Ticket;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.RefundRepository;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.impl.PaymentServiceImpl;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pagos con Stripe.
 *
 * Los casos marcados como regresion corresponden a fallos concretos que tenia
 * la version anterior. El mas grave: el importe a cobrar llegaba en la peticion
 * y se usaba sin compararlo nunca con el total de la compra.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock private PurchaseRepository purchaseRepository;
    @Mock private RefundRepository refundRepository;
    @Mock private MerchandiseSaleRepository merchandiseSaleRepository;
    @Mock private MerchandiseRepository merchandiseRepository;
    @Mock private PurchaseService purchaseService;

    @InjectMocks
    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "secretKey", "sk_test_dummy");
        ReflectionTestUtils.setField(service, "publishableKey", "pk_test_dummy");
        ReflectionTestUtils.setField(service, "webhookSecret", "whsec_dummy");
        when(merchandiseSaleRepository.findByPurchaseId(any())).thenReturn(List.of());
        when(refundRepository.findByPurchaseId(any())).thenReturn(List.of());
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private static Purchase purchaseWithTickets(Long id, String... prices) {
        List<Ticket> tickets = new ArrayList<>();
        for (String price : prices) {
            tickets.add(Ticket.builder().unitPrice(new BigDecimal(price)).build());
        }
        return Purchase.builder()
                .id(id)
                .status(PurchaseStatus.PENDING)
                .tickets(tickets)
                .discountAmount(BigDecimal.ZERO)
                .build();
    }

    private static CreatePaymentIntentRequest intentFor(Long purchaseId) {
        return CreatePaymentIntentRequest.builder().purchaseId(purchaseId).currency("EUR").build();
    }

    private PaymentIntent stubbedIntent() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_test_123");
        when(intent.getClientSecret()).thenReturn("cs_test_abc");
        return intent;
    }

    // ── init ──────────────────────────────────────────────────────────────────

    @Test
    void init_setsStripeApiKey() {
        service.init();
        assertThat(Stripe.apiKey).isEqualTo("sk_test_dummy");
    }

    // ── createPaymentIntent ───────────────────────────────────────────────────

    @Test
    void createPaymentIntent_throwsNotFound_whenPurchaseDoesNotExist() {
        when(purchaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPaymentIntent(intentFor(99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * REGRESION. El importe llegaba en la peticion, validado solo con
     * @Positive, y se enviaba a Stripe sin compararlo con la compra: se podia
     * pagar un centimo por una compra de cualquier valor y el webhook la
     * marcaba pagada igualmente. Ahora se calcula desde las entradas.
     */
    @Test
    void createPaymentIntent_chargesTheAmountComputedFromTheTickets() throws StripeException {
        Purchase purchase = purchaseWithTickets(1L, "9.00", "9.00", "6.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PaymentIntent createdIntent = stubbedIntent();
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            ArgumentCaptor<PaymentIntentCreateParams> params =
                    ArgumentCaptor.forClass(PaymentIntentCreateParams.class);
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(createdIntent);

            service.createPaymentIntent(intentFor(1L));

            piMock.verify(() -> PaymentIntent.create(params.capture(), any(RequestOptions.class)));
            // 24,00 EUR en centimos. Nada de lo que envie el cliente influye.
            assertThat(params.getValue().getAmount()).isEqualTo(2400L);
        }
    }

    @Test
    void createPaymentIntent_addsConcessionSalesToTheAmount() throws StripeException {
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(1L)).thenReturn(List.of(
                MerchandiseSale.builder().quantity(1).total(new BigDecimal("4.50"))
                        .merchandise(Merchandise.builder().name("Palomitas").stock(10).build()).build()));

        PaymentIntent createdIntent = stubbedIntent();
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            ArgumentCaptor<PaymentIntentCreateParams> params =
                    ArgumentCaptor.forClass(PaymentIntentCreateParams.class);
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(createdIntent);

            service.createPaymentIntent(intentFor(1L));

            piMock.verify(() -> PaymentIntent.create(params.capture(), any(RequestOptions.class)));
            assertThat(params.getValue().getAmount()).isEqualTo(1350L);
        }
    }

    @Test
    void createPaymentIntent_subtractsTheDiscountAlreadyApplied() throws StripeException {
        Purchase purchase = purchaseWithTickets(1L, "20.00");
        purchase.setDiscountAmount(new BigDecimal("2.00"));
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PaymentIntent createdIntent = stubbedIntent();
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            ArgumentCaptor<PaymentIntentCreateParams> params =
                    ArgumentCaptor.forClass(PaymentIntentCreateParams.class);
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(createdIntent);

            service.createPaymentIntent(intentFor(1L));

            piMock.verify(() -> PaymentIntent.create(params.capture(), any(RequestOptions.class)));
            assertThat(params.getValue().getAmount()).isEqualTo(1800L);
        }
    }

    @Test
    void createPaymentIntent_returnsTheClientSecretAndStoresTheIntentId() throws StripeException {
        Purchase purchase = purchaseWithTickets(1L, "20.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PaymentIntent createdIntent = stubbedIntent();
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(createdIntent);

            PaymentIntentResponse result = service.createPaymentIntent(intentFor(1L));

            assertThat(result.paymentIntentId()).isEqualTo("pi_test_123");
            assertThat(result.clientSecret()).isEqualTo("cs_test_abc");
            assertThat(result.publishableKey()).isEqualTo("pk_test_dummy");
            assertThat(purchase.getPaymentIntentId()).isEqualTo("pi_test_123");
        }
    }

    /**
     * REGRESION. Sin clave de idempotencia, pulsar dos veces el boton de pagar
     * creaba dos PaymentIntent para la misma compra.
     */
    @Test
    void createPaymentIntent_sendsAnIdempotencyKeyDerivedFromThePurchase() throws StripeException {
        Purchase purchase = purchaseWithTickets(1L, "20.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PaymentIntent createdIntent = stubbedIntent();
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            ArgumentCaptor<RequestOptions> options = ArgumentCaptor.forClass(RequestOptions.class);
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(createdIntent);

            service.createPaymentIntent(intentFor(1L));

            piMock.verify(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), options.capture()));
            assertThat(options.getValue().getIdempotencyKey()).isEqualTo("purchase-1-2000");
        }
    }

    @Test
    void createPaymentIntent_refusesAPurchaseAlreadyPaid() {
        Purchase purchase = purchaseWithTickets(1L, "20.00");
        purchase.setStatus(PurchaseStatus.PAID);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.createPaymentIntent(intentFor(1L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya esta pagada");
    }

    @Test
    void createPaymentIntent_refusesACancelledPurchase() {
        Purchase purchase = purchaseWithTickets(1L, "20.00");
        purchase.setStatus(PurchaseStatus.CANCELLED);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.createPaymentIntent(intentFor(1L)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void createPaymentIntent_refusesAnEmptyPurchase() {
        Purchase purchase = purchaseWithTickets(1L);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.createPaymentIntent(intentFor(1L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("importe");
    }

    /**
     * REGRESION. El stock solo se comprobaba al confirmar, es decir con el
     * dinero ya cobrado. Ahora se verifica antes de pedir el cobro.
     */
    @Test
    void createPaymentIntent_refusesBeforeChargingWhenStockIsNotEnough() {
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(1L)).thenReturn(List.of(
                MerchandiseSale.builder().quantity(5).total(new BigDecimal("10.00"))
                        .merchandise(Merchandise.builder().name("Palomitas").stock(2).build()).build()));

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            assertThatThrownBy(() -> service.createPaymentIntent(intentFor(1L)))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Palomitas");

            // Lo importante: no se ha pedido ningun cobro.
            piMock.verifyNoInteractions();
        }
    }

    @Test
    void createPaymentIntent_translatesAStripeFailureIntoABusinessError() throws StripeException {
        Purchase purchase = purchaseWithTickets(1L, "10.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        StripeException stripeEx = mock(StripeException.class);
        when(stripeEx.getMessage()).thenReturn("tarjeta rechazada");

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class)) {
            piMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenThrow(stripeEx);

            assertThatThrownBy(() -> service.createPaymentIntent(intentFor(1L)))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("No se pudo iniciar el pago");
        }
    }

    // ── handleWebhook ─────────────────────────────────────────────────────────

    /**
     * REGRESION. Una firma invalida lanzaba RuntimeException, que acababa en
     * 500, y Stripe reintentaba el mismo evento durante dias. Es un error
     * definitivo del emisor: 400.
     */
    @Test
    void handleWebhook_rejectsAnInvalidSignatureAsABadRequest() {
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenThrow(new SignatureVerificationException("firma mala", "sig"));

            assertThatThrownBy(() -> service.handleWebhook("{}", "firma-mala"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Firma de webhook invalida");
        }
    }

    private Event eventOf(String type, StripeObject object) {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn(type);
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(deserializer.getObject()).thenReturn(Optional.ofNullable(object));
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        return event;
    }

    @Test
    void handleWebhook_ignoresAnEventWithoutPayload() {
        Event stripeEvent = eventOf("payment_intent.succeeded", null);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            assertThatCode(() -> service.handleWebhook("{}", "sig")).doesNotThrowAnyException();
            verify(purchaseRepository, never()).save(any());
        }
    }

    @Test
    void handleWebhook_ignoresAnUnhandledEventType() {
        PaymentIntent intent = mock(PaymentIntent.class);
        Event stripeEvent = eventOf("customer.created", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            assertThatCode(() -> service.handleWebhook("{}", "sig")).doesNotThrowAnyException();
            verify(purchaseRepository, never()).save(any());
        }
    }

    @Test
    void handleWebhook_marksThePurchaseAsPaid() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));

        Event stripeEvent = eventOf("payment_intent.succeeded", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            service.handleWebhook("{}", "sig");

            // El cambio de estado, la reserva de butacas, el descuento de socio
            // y el correo los hace el servicio de compras, que es la misma
            // rutina que usa el mostrador. Antes el webhook solo marcaba PAID y
            // una compra online se quedaba sin butacas reservadas.
            verify(purchaseService).completeAfterOnlinePayment(1L);
        }
    }

    @Test
    void handleWebhook_decrementsConcessionStockOnce() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        Merchandise popcorn = Merchandise.builder().name("Palomitas").stock(10).build();
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(1L)).thenReturn(List.of(
                MerchandiseSale.builder().quantity(3).total(new BigDecimal("6.00"))
                        .merchandise(popcorn).build()));

        Event stripeEvent = eventOf("payment_intent.succeeded", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            service.handleWebhook("{}", "sig");

            assertThat(popcorn.getStock()).isEqualTo(7);
        }
    }

    /** Stripe repite eventos: el stock no puede descontarse dos veces. */
    @Test
    void handleWebhook_doesNotProcessAPurchaseThatIsAlreadyPaid() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        purchase.setStatus(PurchaseStatus.PAID);
        Merchandise popcorn = Merchandise.builder().name("Palomitas").stock(10).build();
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(1L)).thenReturn(List.of(
                MerchandiseSale.builder().quantity(3).total(new BigDecimal("6.00"))
                        .merchandise(popcorn).build()));

        Event stripeEvent = eventOf("payment_intent.succeeded", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            service.handleWebhook("{}", "sig");

            assertThat(popcorn.getStock()).isEqualTo(10);
            verify(purchaseService, never()).completeAfterOnlinePayment(any());
        }
    }

    @Test
    void handleWebhook_doesNotProcessAPurchaseThatIsAlreadyConfirmed() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        purchase.setStatus(PurchaseStatus.CONFIRMED);
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));

        Event stripeEvent = eventOf("payment_intent.succeeded", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            service.handleWebhook("{}", "sig");

            verify(purchaseService, never()).completeAfterOnlinePayment(any());
        }
    }

    /**
     * REGRESION. Si al confirmar faltaba stock se lanzaba IllegalStateException
     * con el dinero ya cobrado: la compra se quedaba sin marcar como pagada y
     * Stripe reintentaba el evento en bucle, con el cliente cobrado y sin
     * compra. Ahora se registra el descuadre y la compra se cierra.
     */
    @Test
    void handleWebhook_doesNotFailWhenStockRanOutAfterCharging() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        Merchandise popcorn = Merchandise.builder().name("Palomitas").stock(1).build();
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));
        when(merchandiseSaleRepository.findByPurchaseId(1L)).thenReturn(List.of(
                MerchandiseSale.builder().quantity(5).total(new BigDecimal("10.00"))
                        .merchandise(popcorn).build()));

        Event stripeEvent = eventOf("payment_intent.succeeded", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            assertThatCode(() -> service.handleWebhook("{}", "sig")).doesNotThrowAnyException();

            verify(purchaseService).completeAfterOnlinePayment(1L);
            assertThat(popcorn.getStock()).isZero();
        }
    }

    @Test
    void handleWebhook_cancelsAPendingPurchaseWhenPaymentFails() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));

        Event stripeEvent = eventOf("payment_intent.payment_failed", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            service.handleWebhook("{}", "sig");

            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CANCELLED);
        }
    }

    /**
     * REGRESION. Se cancelaba sin mirar el estado, asi que un evento de fallo
     * recibido despues del de exito — Stripe no garantiza el orden — dejaba
     * cancelada una compra ya cobrada.
     */
    @Test
    void handleWebhook_doesNotCancelAPurchaseThatWasAlreadyPaid() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getId()).thenReturn("pi_1");
        Purchase purchase = purchaseWithTickets(1L, "9.00");
        purchase.setStatus(PurchaseStatus.PAID);
        when(purchaseRepository.findByPaymentIntentId("pi_1")).thenReturn(Optional.of(purchase));

        Event stripeEvent = eventOf("payment_intent.payment_failed", intent);
        try (MockedStatic<Webhook> webhookMock = mockStatic(Webhook.class)) {
            webhookMock.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                    .thenReturn(stripeEvent);

            service.handleWebhook("{}", "sig");

            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PAID);
            verify(purchaseRepository, never()).save(any());
        }
    }

    // ── refund ────────────────────────────────────────────────────────────────

    private Purchase paidPurchase(String total) {
        Purchase purchase = purchaseWithTickets(1L, total);
        purchase.setStatus(PurchaseStatus.PAID);
        purchase.setPaymentIntentId("pi_1");
        purchase.setTotalAmount(new BigDecimal(total));
        return purchase;
    }

    @Test
    void refund_throwsNotFound_whenPurchaseDoesNotExist() {
        when(purchaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refund(RefundRequest.builder().purchaseId(99L).build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void refund_refusesAPurchaseWithoutPayment() {
        Purchase purchase = paidPurchase("20.00");
        purchase.setPaymentIntentId(null);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.refund(RefundRequest.builder().purchaseId(1L).build()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no tiene un pago asociado");
    }

    @Test
    void refund_refusesAPurchaseAlreadyRefunded() {
        Purchase purchase = paidPurchase("20.00");
        purchase.setStatus(PurchaseStatus.REFUNDED);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.refund(RefundRequest.builder().purchaseId(1L).build()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya fue devuelta");
    }

    /** No se puede devolver lo que no se ha cobrado. */
    @Test
    void refund_refusesAPurchaseThatWasNeverCharged() {
        Purchase purchase = paidPurchase("20.00");
        purchase.setStatus(PurchaseStatus.PENDING);
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.refund(RefundRequest.builder().purchaseId(1L).build()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cobrada");
    }

    @Test
    void refund_refusesAnAmountAboveWhatIsRefundable() {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(paidPurchase("20.00")));

        assertThatThrownBy(() -> service.refund(RefundRequest.builder()
                        .purchaseId(1L).amount(new BigDecimal("50.00")).build()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("devolvible");
    }

    @Test
    void refund_accountsForRefundsAlreadyIssued() {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(paidPurchase("20.00")));
        when(refundRepository.findByPurchaseId(1L)).thenReturn(List.of(
                Refund.builder().amount(new BigDecimal("15.00")).build()));

        assertThatThrownBy(() -> service.refund(RefundRequest.builder()
                        .purchaseId(1L).amount(new BigDecimal("10.00")).build()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("5.00");
    }

    private com.stripe.model.Refund stubbedStripeRefund(long cents) {
        com.stripe.model.Refund refund = mock(com.stripe.model.Refund.class);
        when(refund.getId()).thenReturn("re_test_1");
        when(refund.getAmount()).thenReturn(cents);
        when(refund.getStatus()).thenReturn("succeeded");
        return refund;
    }

    @Test
    void refund_issuesTheFullAmountWhenNoneIsGiven() throws StripeException {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(paidPurchase("20.00")));

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getLatestCharge()).thenReturn("ch_1");

        com.stripe.model.Refund stripeRefund = stubbedStripeRefund(2000L);
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class);
             MockedStatic<com.stripe.model.Refund> refundMock = mockStatic(com.stripe.model.Refund.class)) {
            piMock.when(() -> PaymentIntent.retrieve("pi_1")).thenReturn(intent);
            ArgumentCaptor<RefundCreateParams> params = ArgumentCaptor.forClass(RefundCreateParams.class);
            refundMock.when(() -> com.stripe.model.Refund.create(any(RefundCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(stripeRefund);

            RefundResponse response = service.refund(RefundRequest.builder().purchaseId(1L).build());

            refundMock.verify(() -> com.stripe.model.Refund.create(params.capture(), any(RequestOptions.class)));
            assertThat(params.getValue().getAmount()).isEqualTo(2000L);
            assertThat(response.amount()).isEqualByComparingTo("20.00");
        }
    }

    /**
     * REGRESION. La orden se enviaba sin importe, de modo que Stripe devolvia
     * siempre el cargo completo y una devolucion parcial era imposible.
     */
    @Test
    void refund_issuesOnlyThePartialAmountRequested() throws StripeException {
        Purchase purchase = paidPurchase("20.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getLatestCharge()).thenReturn("ch_1");

        com.stripe.model.Refund stripeRefund = stubbedStripeRefund(900L);
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class);
             MockedStatic<com.stripe.model.Refund> refundMock = mockStatic(com.stripe.model.Refund.class)) {
            piMock.when(() -> PaymentIntent.retrieve("pi_1")).thenReturn(intent);
            ArgumentCaptor<RefundCreateParams> params = ArgumentCaptor.forClass(RefundCreateParams.class);
            refundMock.when(() -> com.stripe.model.Refund.create(any(RefundCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(stripeRefund);

            service.refund(RefundRequest.builder().purchaseId(1L).amount(new BigDecimal("9.00")).build());

            refundMock.verify(() -> com.stripe.model.Refund.create(params.capture(), any(RequestOptions.class)));
            assertThat(params.getValue().getAmount()).isEqualTo(900L);
            // Devuelto en parte: la compra sigue cobrada por el resto.
            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PAID);
        }
    }

    @Test
    void refund_marksThePurchaseRefundedOnlyWhenEverythingIsReturned() throws StripeException {
        Purchase purchase = paidPurchase("20.00");
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getLatestCharge()).thenReturn("ch_1");

        com.stripe.model.Refund stripeRefund = stubbedStripeRefund(2000L);
        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class);
             MockedStatic<com.stripe.model.Refund> refundMock = mockStatic(com.stripe.model.Refund.class)) {
            piMock.when(() -> PaymentIntent.retrieve("pi_1")).thenReturn(intent);
            refundMock.when(() -> com.stripe.model.Refund.create(any(RefundCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(stripeRefund);

            service.refund(RefundRequest.builder().purchaseId(1L).amount(new BigDecimal("20.00")).build());

            assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.REFUNDED);
        }
    }

    @Test
    void refund_translatesAStripeFailureIntoABusinessError() throws StripeException {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(paidPurchase("20.00")));

        StripeException stripeEx = mock(StripeException.class);
        when(stripeEx.getMessage()).thenReturn("cargo no reembolsable");
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getLatestCharge()).thenReturn("ch_1");

        try (MockedStatic<PaymentIntent> piMock = mockStatic(PaymentIntent.class);
             MockedStatic<com.stripe.model.Refund> refundMock = mockStatic(com.stripe.model.Refund.class)) {
            piMock.when(() -> PaymentIntent.retrieve("pi_1")).thenReturn(intent);
            refundMock.when(() -> com.stripe.model.Refund.create(any(RefundCreateParams.class), any(RequestOptions.class)))
                    .thenThrow(stripeEx);

            assertThatThrownBy(() -> service.refund(RefundRequest.builder().purchaseId(1L).build()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("No se pudo procesar la devolucion");
        }
    }

    // ── getHistory ────────────────────────────────────────────────────────────

    @Test
    void getHistory_returnsEmptyWhenThereIsNothing() {
        when(purchaseRepository.findByStatusAndDateRange(any(), any(), any())).thenReturn(List.of());

        assertThat(service.getHistory(null, null, null)).isEmpty();
    }

    @Test
    void getHistory_mapsThePurchaseData() {
        Purchase purchase = paidPurchase("20.00");
        purchase.setUser(User.builder().id(5L).name("Ana").build());
        when(purchaseRepository.findByStatusAndDateRange(any(), any(), any())).thenReturn(List.of(purchase));

        List<PaymentHistoryResponse> history = service.getHistory(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "PAID");

        assertThat(history).hasSize(1);
        assertThat(history.get(0).userName()).isEqualTo("Ana");
        assertThat(history.get(0).amount()).isEqualByComparingTo("20.00");
    }

    /** Antes un filtro mal escrito provocaba un 500 con traza. */
    @Test
    void getHistory_rejectsAnUnknownStatusAsABadRequest() {
        assertThatThrownBy(() -> service.getHistory(null, null, "INVENTADO"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("INVENTADO");
    }

    @Test
    void getHistory_toleratesAPurchaseWithoutUser() {
        Purchase purchase = paidPurchase("20.00");
        purchase.setUser(null);
        when(purchaseRepository.findByStatusAndDateRange(any(), any(), any())).thenReturn(List.of(purchase));

        List<PaymentHistoryResponse> history = service.getHistory(null, null, null);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).userId()).isNull();
    }
}
