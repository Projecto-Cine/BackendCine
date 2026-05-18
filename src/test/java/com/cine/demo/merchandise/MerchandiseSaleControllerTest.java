package com.cine.demo.merchandise;

import com.cine.demo.controller.MerchandiseSaleController;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.MerchandiseSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MerchandiseSaleController.class)
@Import(GlobalExceptionHandler.class)
class MerchandiseSaleControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private MerchandiseSaleService merchandiseSaleService;

    @Test
    void getAll_returns200() throws Exception {
        given(merchandiseSaleService.findAll()).willReturn(List.of(sale()));

        mockMvc.perform(get("/api/merchandisesales"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_returnsEnvelope() throws Exception {
        given(merchandiseSaleService.findAll()).willReturn(List.of(sale()));

        mockMvc.perform(get("/api/merchandisesales"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sales retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAll_returnsSaleList() throws Exception {
        given(merchandiseSaleService.findAll()).willReturn(List.of(sale()));

        mockMvc.perform(get("/api/merchandisesales"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].merchandiseName").value("Popcorn"))
                .andExpect(jsonPath("$.data[0].quantity").value(2));
    }

    @Test
    void getAll_delegatesToService() throws Exception {
        given(merchandiseSaleService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/merchandisesales"));

        verify(merchandiseSaleService).findAll();
    }

    @Test
    void getById_returns200_whenFound() throws Exception {
        given(merchandiseSaleService.findById(1L)).willReturn(sale());

        mockMvc.perform(get("/api/merchandisesales/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_returnsEnvelope() throws Exception {
        given(merchandiseSaleService.findById(1L)).willReturn(sale());

        mockMvc.perform(get("/api/merchandisesales/1"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sale retrieved successfully"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getById_returnsAllFields() throws Exception {
        given(merchandiseSaleService.findById(1L)).willReturn(sale());

        mockMvc.perform(get("/api/merchandisesales/1"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.userId").value(5))
                .andExpect(jsonPath("$.data.merchandiseId").value(10))
                .andExpect(jsonPath("$.data.merchandiseName").value("Popcorn"))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.data.total").isNumber());
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        given(merchandiseSaleService.findById(99L))
                .willThrow(new ResourceNotFoundException("MerchandiseSale not found with id: 99"));

        mockMvc.perform(get("/api/merchandisesales/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("MerchandiseSale not found with id: 99"));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        given(merchandiseSaleService.save(any())).willReturn(sale());

        String body = "{\"userId\":5,\"merchandiseId\":10,\"quantity\":2}";
        mockMvc.perform(post("/api/merchandisesales")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void create_returnsEnvelope() throws Exception {
        given(merchandiseSaleService.save(any())).willReturn(sale());

        String body = "{\"userId\":5,\"merchandiseId\":10,\"quantity\":2}";
        mockMvc.perform(post("/api/merchandisesales")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sale registered successfully"))
                .andExpect(jsonPath("$.data.merchandiseName").value("Popcorn"));
    }

    @Test
    void create_returns400_whenQuantityIsZero() throws Exception {
        String body = "{\"userId\":5,\"merchandiseId\":10,\"quantity\":0}";
        mockMvc.perform(post("/api/merchandisesales")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantity").isNotEmpty());
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        MerchandiseSaleResponseDTO updated = MerchandiseSaleResponseDTO.builder()
                .id(1L).userId(5L).merchandiseId(10L).merchandiseName("Popcorn")
                .quantity(5).total(new BigDecimal("15"))
                .saleDate(LocalDateTime.of(2026, 5, 17, 16, 0)).build();
        given(merchandiseSaleService.update(eq(1L), any())).willReturn(updated);

        String body = "{\"userId\":5,\"merchandiseId\":10,\"quantity\":5}";
        mockMvc.perform(put("/api/merchandisesales/1")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sale updated successfully"))
                .andExpect(jsonPath("$.data.quantity").value(5));
    }

    @Test
    void update_returns404_whenNotFound() throws Exception {
        given(merchandiseSaleService.update(eq(99L), any()))
                .willThrow(new ResourceNotFoundException("MerchandiseSale not found with id: 99"));

        String body = "{\"userId\":5,\"merchandiseId\":10,\"quantity\":1}";
        mockMvc.perform(put("/api/merchandisesales/99")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("MerchandiseSale not found with id: 99"));
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/merchandisesales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sale deleted successfully"));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("MerchandiseSale not found with id: 99"))
                .when(merchandiseSaleService).delete(99L);

        mockMvc.perform(delete("/api/merchandisesales/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("MerchandiseSale not found with id: 99"));
    }

    private MerchandiseSaleResponseDTO sale() {
        return MerchandiseSaleResponseDTO.builder()
                .id(1L)
                .userId(5L)
                .merchandiseId(10L)
                .merchandiseName("Popcorn")
                .quantity(2)
                .total(new BigDecimal("6"))
                .saleDate(LocalDateTime.of(2026, 5, 17, 16, 0))
                .build();
    }
}
