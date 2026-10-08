package com.cine.demo.service.impl;

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
import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.RefundRepository;
import com.cine.demo.service.PaymentService;
import com.cine.demo.service.PurchaseService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal CENTS = BigDecimal.valueOf(100);

    private final PurchaseRepository purchaseRepository;
    private final RefundRepository refundRepository;
    private final MerchandiseSaleRepository merchandiseSaleRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final PurchaseService purchaseService;

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

    // -----------------------------------------------------------------------
    // Inicio del pago
    // -----------------------------------------------------------------------

    /**
     * Crea el PaymentIntent de una compra.
     *
     * El importe se calcula aqui a partir de los datos de la compra y nunca se
     * acepta del cliente. Antes llegaba en la peticion y se usaba tal cual, de
     * modo que bastaba enviar un centimo para que el webhook marcase pagada una
     * compra de cualquier valor.
     */
    @Override
    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        Purchase purchase = purchaseRepository.findById(request.purchaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase not found with id: " + request.purchaseId()));

        if (purchase.getStatus() == PurchaseStatus.PAID
                || purchase.getStatus() == PurchaseStatus.CONFIRMED) {
            throw new BusinessRuleException("Esta compra ya esta pagada");
        }
        if (purchase.getStatus() == PurchaseStatus.CANCELLED
                || purchase.getStatus() == PurchaseStatus.REFUNDED) {
            throw new BusinessRuleException("Esta compra no admite pago: " + purchase.getStatus());
        }

        BigDecimal amount = authoritativeAmountFor(purchase);
        if (amount.signum() <= 0) {
            throw new BusinessRuleException("La compra no tiene importe que cobrar");
        }

        // El stock se comprueba antes de cobrar. Antes solo se miraba al
        // confirmar, es decir con el dinero ya cobrado, y entonces era tarde.
        assertConcessionStockAvailable(purchase);

        long amountInCents = amount.multiply(CENTS).setScale(0, RoundingMode.HALF_UP).longValueExact();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.currencyOrDefault())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .putMetadata("purchaseId", String.valueOf(purchase.getId()))
                    .build();

            // Clave de idempotencia: sin ella, pulsar dos veces el boton de
            // pagar creaba dos PaymentIntent para la misma compra, con riesgo
            // de cobro doble e intents huerfanos. Incluye el importe para que
            // un cambio legitimo del pedido si genere un intent nuevo.
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey("purchase-" + purchase.getId() + "-" + amountInCents)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params, options);

            purchase.setPaymentIntentId(intent.getId());
            purchase.setPaymentMethod(PaymentMethod.CARD);
            purchase.setTotalAmount(amount);
            purchaseRepository.save(purchase);

            return PaymentIntentResponse.builder()
                    .clientSecret(intent.getClientSecret())
                    .paymentIntentId(intent.getId())
                    .publishableKey(publishableKey)
                    .build();

        } catch (StripeException e) {
            log.error("Stripe rechazo la creacion del PaymentIntent de la compra {}: {}",
                    purchase.getId(), e.getMessage());
            throw new BusinessRuleException("No se pudo iniciar el pago: " + e.getMessage());
        }
    }

    /**
     * Importe real de la compra, recalculado desde la base de datos.
     *
     * Suma las entradas y los productos de tienda asociados y resta el
     * descuento aplicado. Se recalcula en lugar de confiar en totalAmount
     * porque ese campo tambien podia venir del cliente al crear la compra
     * cuando no habia entradas.
     */
    private BigDecimal authoritativeAmountFor(Purchase purchase) {
        BigDecimal tickets = purchase.getTickets().stream()
                .map(Ticket::getUnitPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal concessions = merchandiseSaleRepository.findByPurchaseId(purchase.getId()).stream()
                .map(MerchandiseSale::getTotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = purchase.getDiscountAmount() == null
                ? BigDecimal.ZERO
                : purchase.getDiscountAmount();

        return tickets.add(concessions).subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private void assertConcessionStockAvailable(Purchase purchase) {
        for (MerchandiseSale sale : merchandiseSaleRepository.findByPurchaseId(purchase.getId())) {
            Merchandise merchandise = sale.getMerchandise();
            if (merchandise != null && merchandise.getStock() < sale.getQuantity()) {
                throw new BusinessRuleException(
                        "No hay stock suficiente de " + merchandise.getName()
                        + ": quedan " + merchandise.getStock() + " y se piden " + sale.getQuantity());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Webhook
    // -----------------------------------------------------------------------

    /**
     * Procesa un evento de Stripe.
     *
     * Una firma invalida es un error definitivo del emisor, no un fallo
     * recuperable: antes se lanzaba RuntimeException, que acababa en 500, y
     * Stripe reintentaba el mismo evento durante dias. Ahora se traduce a 400.
     */
    @Override
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook de Stripe con firma invalida rechazado");
            throw new BusinessRuleException("Firma de webhook invalida");
        }

        Optional<StripeObject> stripeObject = event.getDataObjectDeserializer().getObject();
        if (stripeObject.isEmpty()) {
            log.warn("Evento {} de Stripe sin objeto deserializable; se ignora", event.getType());
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> onPaymentSucceeded((PaymentIntent) stripeObject.get());
            case "payment_intent.payment_failed" -> onPaymentFailed((PaymentIntent) stripeObject.get());
            default -> log.debug("Evento {} de Stripe no manejado", event.getType());
        }
    }

    private void onPaymentSucceeded(PaymentIntent intent) {
        purchaseRepository.findByPaymentIntentId(intent.getId()).ifPresent(purchase -> {
            // Stripe reintenta los eventos y puede repetirlos: si ya se
            // proceso, no se vuelve a descontar stock.
            if (purchase.getStatus() == PurchaseStatus.PAID
                    || purchase.getStatus() == PurchaseStatus.CONFIRMED) {
                log.debug("Compra {} ya estaba pagada; evento repetido ignorado", purchase.getId());
                return;
            }

            fulfillConcessionSales(purchase);

            // Delega en el servicio de compras en lugar de limitarse a marcar
            // PAID. Antes solo cambiaba el estado, de modo que una compra
            // pagada por internet no reservaba sus butacas, no aplicaba el
            // descuento de socio y no enviaba la confirmacion: todo eso solo
            // ocurria por la via del mostrador.
            purchaseService.completeAfterOnlinePayment(purchase.getId());
            log.info("Compra {} completada tras el cobro online", purchase.getId());
        });
    }

    /**
     * Un pago fallido solo cancela una compra que siga pendiente.
     *
     * Antes se cancelaba sin mirar el estado, de modo que un evento de fallo
     * que llegase despues del de exito — Stripe no garantiza el orden — dejaba
     * cancelada una compra ya cobrada.
     */
    private void onPaymentFailed(PaymentIntent intent) {
        purchaseRepository.findByPaymentIntentId(intent.getId()).ifPresent(purchase -> {
            if (purchase.getStatus() != PurchaseStatus.PENDING) {
                log.warn("Evento de pago fallido para la compra {} en estado {}; no se cancela",
                        purchase.getId(), purchase.getStatus());
                return;
            }
            purchase.setStatus(PurchaseStatus.CANCELLED);
            purchaseRepository.save(purchase);
            log.info("Compra {} cancelada por pago fallido", purchase.getId());
        });
    }

    /**
     * Descuenta el stock de los productos de una compra ya cobrada.
     *
     * No lanza si el stock no alcanza. En este punto el dinero esta cobrado, y
     * antes se lanzaba IllegalStateException: la compra se quedaba sin marcar
     * como pagada y Stripe reintentaba el evento en bucle, con el cliente
     * cobrado y sin compra. La disponibilidad se comprueba al crear el
     * PaymentIntent, que es cuando todavia se puede rechazar; si pese a ello
     * falta stock por una venta simultanea, se deja el stock en cero y se
     * registra para que el personal lo resuelva.
     */
    private void fulfillConcessionSales(Purchase purchase) {
        for (MerchandiseSale sale : merchandiseSaleRepository.findByPurchaseId(purchase.getId())) {
            Merchandise merchandise = sale.getMerchandise();
            if (merchandise == null) {
                continue;
            }
            int remaining = merchandise.getStock() - sale.getQuantity();
            if (remaining < 0) {
                log.error("Stock insuficiente de {} en la compra {} ya cobrada: "
                          + "quedaban {} y se vendieron {}. Requiere revision manual.",
                        merchandise.getName(), purchase.getId(),
                        merchandise.getStock(), sale.getQuantity());
                remaining = 0;
            }
            merchandise.setStock(remaining);
            merchandiseRepository.save(merchandise);
        }
    }

    // -----------------------------------------------------------------------
    // Devoluciones
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    public RefundResponse refund(RefundRequest request) {
        Purchase purchase = purchaseRepository.findById(request.purchaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase not found with id: " + request.purchaseId()));

        if (purchase.getPaymentIntentId() == null) {
            throw new BusinessRuleException("Esta compra no tiene un pago asociado");
        }
        if (purchase.getStatus() == PurchaseStatus.REFUNDED) {
            throw new BusinessRuleException("Esta compra ya fue devuelta");
        }
        if (purchase.getStatus() != PurchaseStatus.PAID
                && purchase.getStatus() != PurchaseStatus.CONFIRMED) {
            throw new BusinessRuleException(
                    "Solo se puede devolver una compra cobrada. Estado actual: " + purchase.getStatus());
        }

        BigDecimal alreadyRefunded = refundRepository.findByPurchaseId(purchase.getId()).stream()
                .map(Refund::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refundable = purchase.getTotalAmount().subtract(alreadyRefunded);
        BigDecimal amount = request.amount() == null ? refundable : request.amount();

        if (amount.signum() <= 0) {
            throw new BusinessRuleException("El importe a devolver debe ser positivo");
        }
        if (amount.compareTo(refundable) > 0) {
            throw new BusinessRuleException(
                    "No se puede devolver " + amount + ": el importe devolvible es " + refundable);
        }

        long amountInCents = amount.multiply(CENTS).setScale(0, RoundingMode.HALF_UP).longValueExact();

        try {
            PaymentIntent intent = PaymentIntent.retrieve(purchase.getPaymentIntentId());
            String chargeId = intent.getLatestCharge();
            if (chargeId == null) {
                throw new BusinessRuleException("El pago de esta compra no tiene cargo asociado");
            }

            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(chargeId)
                    .setAmount(amountInCents)
                    .build();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey("refund-" + purchase.getId() + "-" + amountInCents)
                    .build();

            com.stripe.model.Refund stripeRefund = com.stripe.model.Refund.create(params, options);

            Refund refund = Refund.builder()
                    .purchaseId(purchase.getId())
                    .stripeRefundId(stripeRefund.getId())
                    .amount(BigDecimal.valueOf(stripeRefund.getAmount())
                            .divide(CENTS, 2, RoundingMode.HALF_UP))
                    .reason(request.reason())
                    .status(stripeRefund.getStatus())
                    .build();
            refundRepository.save(refund);

            // Solo es REFUNDED si se ha devuelto todo. Una devolucion parcial
            // deja la compra cobrada por el resto.
            if (alreadyRefunded.add(amount).compareTo(purchase.getTotalAmount()) >= 0) {
                purchase.setStatus(PurchaseStatus.REFUNDED);
                purchaseRepository.save(purchase);
            }

            return RefundResponse.builder()
                    .refundId(stripeRefund.getId())
                    .amount(refund.getAmount())
                    .status(stripeRefund.getStatus())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe rechazo la devolucion de la compra {}: {}", purchase.getId(), e.getMessage());
            throw new BusinessRuleException("No se pudo procesar la devolucion: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Historico
    // -----------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryResponse> getHistory(LocalDate from, LocalDate to, String status) {
        PurchaseStatus purchaseStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                purchaseStatus = PurchaseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Antes un filtro mal escrito provocaba un 500 con traza.
                throw new BusinessRuleException("Estado de compra desconocido: " + status);
            }
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
                        .userId(p.getUser() != null ? p.getUser().getId() : null)
                        .userName(p.getUser() != null ? p.getUser().getName() : null)
                        .build())
                .toList();
    }
}
