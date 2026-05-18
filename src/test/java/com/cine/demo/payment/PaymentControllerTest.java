package com.cine.demo.payment;

import com.cine.demo.controller.PaymentController;
import com.cine.demo.dto.request.CreatePaymentIntentRequest;
import com.cine.demo.dto.request.RefundRequest;
import com.cine.demo.dto.response.PaymentHistoryResponse;
import com.cine.demo.dto.response.PaymentIntentResponse;
import com.cine.demo.dto.response.RefundResponse;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private PaymentService paymentService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void createPaymentIntent_returns201_whenValid() throws Exception {
        CreatePaymentIntentRequest request = CreatePaymentIntentRequest.builder()
                .purchaseId(1L).amount(BigDecimal.TEN).currency("EUR").build();
        PaymentIntentResponse response = PaymentIntentResponse.builder()
                .clientSecret("cs_test_abc").paymentIntentId("pi_123").publishableKey("pk_test").build();
        when(paymentService.createPaymentIntent(any())).thenReturn(response);

        mockMvc.perform(post("/api/payments/intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentIntentId").value("pi_123"))
                .andExpect(jsonPath("$.data.clientSecret").value("cs_test_abc"));
    }

    @Test
    void createPaymentIntent_alsoAcceptsCreateIntentAlias() throws Exception {
        CreatePaymentIntentRequest request = CreatePaymentIntentRequest.builder()
                .purchaseId(1L).amount(BigDecimal.TEN).currency("EUR").build();
        when(paymentService.createPaymentIntent(any()))
                .thenReturn(PaymentIntentResponse.builder().paymentIntentId("pi_456").build());

        mockMvc.perform(post("/api/payments/create-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.paymentIntentId").value("pi_456"));
    }

    @Test
    void createPaymentIntent_returns400_whenValidationFails() throws Exception {
        CreatePaymentIntentRequest invalid = CreatePaymentIntentRequest.builder().build();

        mockMvc.perform(post("/api/payments/intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentIntent_returns404_whenPurchaseNotFound() throws Exception {
        CreatePaymentIntentRequest request = CreatePaymentIntentRequest.builder()
                .purchaseId(99L).amount(BigDecimal.TEN).currency("EUR").build();
        when(paymentService.createPaymentIntent(any()))
                .thenThrow(new ResourceNotFoundException("Purchase not found with id: 99"));

        mockMvc.perform(post("/api/payments/intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Purchase not found with id: 99"));
    }

    @Test
    void webhook_returns200_whenProcessed() throws Exception {
        mockMvc.perform(post("/api/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=123,v1=abc")
                        .content("{\"type\":\"payment_intent.succeeded\",\"data\":{}}"))
                .andExpect(status().isOk());
    }

    @Test
    void refund_returns200_whenValid() throws Exception {
        RefundRequest request = RefundRequest.builder().purchaseId(1L).reason("Customer request").build();
        RefundResponse response = RefundResponse.builder()
                .refundId("re_123").amount(BigDecimal.TEN).status("succeeded").build();
        when(paymentService.refund(any())).thenReturn(response);

        mockMvc.perform(post("/api/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.refundId").value("re_123"))
                .andExpect(jsonPath("$.data.status").value("succeeded"));
    }

    @Test
    void refund_returns400_whenPurchaseIdMissing() throws Exception {
        RefundRequest invalid = RefundRequest.builder().build();

        mockMvc.perform(post("/api/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHistory_returns200_withEmptyList() throws Exception {
        when(paymentService.getHistory(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/payments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getHistory_returns200_withResults() throws Exception {
        PaymentHistoryResponse entry = PaymentHistoryResponse.builder()
                .purchaseId(1L).paymentIntentId("pi_1").amount(BigDecimal.TEN)
                .status(PurchaseStatus.PAID).type("purchase")
                .createdAt(LocalDateTime.now()).userId(1L).userName("Ana").build();
        when(paymentService.getHistory(any(), any(), any())).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/payments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].purchaseId").value(1))
                .andExpect(jsonPath("$.data[0].userName").value("Ana"));
    }

    @Test
    void getHistory_returns200_withDateAndStatusFilters() throws Exception {
        when(paymentService.getHistory(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/payments/history")
                        .param("from", "2025-01-01")
                        .param("to", "2025-12-31")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment history retrieved successfully"));
    }
}
