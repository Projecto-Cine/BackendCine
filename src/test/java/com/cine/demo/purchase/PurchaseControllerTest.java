package com.cine.demo.purchase;

import com.cine.demo.controller.PurchaseController;
import com.cine.demo.dto.request.PurchaseRequestDTO;
import com.cine.demo.dto.request.TicketRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.exception.*;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.model.enums.TicketType;
import com.cine.demo.service.PurchaseService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
@Import(GlobalExceptionHandler.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseService purchaseService;

    @Autowired
    private ObjectMapper objectMapper;

    private PurchaseRequestDTO validRequest() {
        return PurchaseRequestDTO.builder()
                .userId(1L).screeningId(1L)
                .tickets(List.of(TicketRequestDTO.builder().seatId(1L).ticketType(TicketType.ADULT).build()))
                .build();
    }

    private PurchaseResponseDTO sampleResponse() {
        return PurchaseResponseDTO.builder()
                .id(1L).userId(1L).screeningId(1L)
                .movieTitulo("Inception").theaterNombre("Sala 1")
                .fechaHora(LocalDateTime.now().plusDays(1))
                .totalAmount(BigDecimal.TEN)
                .discountApplied(false).discountAmount(BigDecimal.ZERO)
                .status(PurchaseStatus.PENDING)
                .tickets(List.of())
                .build();
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        when(purchaseService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void create_returns422_whenMinorWithoutAdult() throws Exception {
        when(purchaseService.create(any()))
                .thenThrow(new MinorWithoutAdultException("Un menor debe ir acompañado de un adulto"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        PurchaseRequestDTO invalid = PurchaseRequestDTO.builder().build();

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void confirm_returns200_whenSuccessful() throws Exception {
        PurchaseResponseDTO paid = PurchaseResponseDTO.builder()
                .id(1L).status(PurchaseStatus.PAID).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseService.confirm(1L)).thenReturn(paid);

        mockMvc.perform(post("/api/purchases/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    @Test
    void cancel_returns200_whenSuccessful() throws Exception {
        PurchaseResponseDTO cancelled = PurchaseResponseDTO.builder()
                .id(1L).status(PurchaseStatus.CANCELLED).totalAmount(BigDecimal.TEN)
                .tickets(List.of()).build();
        when(purchaseService.cancel(1L)).thenReturn(cancelled);

        mockMvc.perform(post("/api/purchases/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(purchaseService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Compra no encontrada con id: 99"));

        mockMvc.perform(get("/api/purchases/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * GET /api/purchases/{id}: caso feliz devuelve 200 con la compra.
     */
    @Test
    void getById_returns200_whenFound() throws Exception {
        when(purchaseService.getById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/purchases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Compra obtenida correctamente"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /**
     * GET /api/purchases/user/{userId}: historial de compras del usuario.
     */
    @Test
    void getByUser_returns200WithPurchaseHistory() throws Exception {
        when(purchaseService.getByUser(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/purchases/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Historial de compras obtenido correctamente"))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    /**
     * GET /api/purchases/screening/{screeningId}: compras hechas para una sesión.
     */
    @Test
    void getByScreening_returns200WithPurchasesForScreening() throws Exception {
        when(purchaseService.getByScreening(5L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/purchases/screening/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Compras de la proyección obtenidas correctamente"));
    }

    /**
     * confirm: si la compra ya está pagada, el servicio lanza
     * InvalidPurchaseStatusException → 422 Unprocessable Entity.
     */
    @Test
    void confirm_returns422_whenStatusIsNotPending() throws Exception {
        when(purchaseService.confirm(1L))
                .thenThrow(new InvalidPurchaseStatusException("Solo se pueden confirmar compras en estado PENDING"));

        mockMvc.perform(post("/api/purchases/1/confirm"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * cancel: si la compra ya estaba cancelada, lanzamos
     * PurchaseAlreadyCancelledException → 409 Conflict.
     */
    @Test
    void cancel_returns409_whenAlreadyCancelled() throws Exception {
        when(purchaseService.cancel(1L))
                .thenThrow(new PurchaseAlreadyCancelledException("Ya cancelada"));

        mockMvc.perform(post("/api/purchases/1/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * create: si el asiento ya está ocupado por otra compra,
     * SeatAlreadyTakenException → 409 Conflict.
     */
    @Test
    void create_returns409_whenSeatAlreadyTaken() throws Exception {
        when(purchaseService.create(any()))
                .thenThrow(new SeatAlreadyTakenException("El asiento A1 ya está ocupado"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * create: si la película es +18 y el usuario no cumple la edad,
     * AgeRestrictionException → 403 Forbidden.
     */
    @Test
    void create_returns403_whenAgeRestriction() throws Exception {
        when(purchaseService.create(any()))
                .thenThrow(new AgeRestrictionException("El usuario no cumple la edad mínima"));

        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}
