package com.cine.demo.concession;

import com.cine.demo.controller.ConcessionSalesController;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConcessionSalesController.class)
@Import(GlobalExceptionHandler.class)
class ConcessionSalesControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private MerchandiseSaleService merchandiseSaleService;

    @Test
    void create_returns201_whenValid() throws Exception {
        MerchandiseSaleResponseDTO response = MerchandiseSaleResponseDTO.builder()
                .id(1L).quantity(2).total(new BigDecimal("10.00")).build();
        when(merchandiseSaleService.save(any())).thenReturn(response);

        String body = "{\"merchandiseId\":1,\"userId\":1,\"quantity\":2}";
        mockMvc.perform(post("/api/merchandise/sales")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Concession sale registered successfully"))
                .andExpect(jsonPath("$.data.quantity").value(2));
    }

    @Test
    void create_returns404_whenMerchandiseNotFound() throws Exception {
        when(merchandiseSaleService.save(any())).thenThrow(new ResourceNotFoundException("Merchandise not found with id: 99"));

        String body = "{\"merchandiseId\":99,\"userId\":1,\"quantity\":1}";
        mockMvc.perform(post("/api/merchandise/sales")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Merchandise not found with id: 99"));
    }
}
