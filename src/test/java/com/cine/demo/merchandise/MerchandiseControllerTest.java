package com.cine.demo.merchandise;

import com.cine.demo.controller.MerchandiseController;
import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.model.enums.MerchandiseCategory;
import com.cine.demo.service.MerchandiseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MerchandiseController.class)
@Import(GlobalExceptionHandler.class)
class MerchandiseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private MerchandiseService merchandiseService;

    @Test
    void getAll_returns200WithMerchandiseList() throws Exception {
        MerchandiseResponseDTO item = MerchandiseResponseDTO.builder()
                .id(1L).name("Camiseta").price(BigDecimal.valueOf(19.99)).build();
        when(merchandiseService.findAll()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/merchandise"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Camiseta"))
                .andExpect(jsonPath("$.data[0].price").value(19.99));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        when(merchandiseService.findById(7L)).thenReturn(
                MerchandiseResponseDTO.builder().id(7L).name("Llavero").build());

        mockMvc.perform(get("/api/merchandise/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.name").value("Llavero"));
    }

    @Test
    void create_returns201WithSavedMerchandise() throws Exception {
        MerchandiseRequestDTO request = MerchandiseRequestDTO.builder()
                .name("Nuevo").category(MerchandiseCategory.POSTERS).price(BigDecimal.valueOf(12.0)).stock(50).build();
        MerchandiseResponseDTO response = MerchandiseResponseDTO.builder()
                .id(20L).name("Nuevo").price(BigDecimal.valueOf(12.0)).build();
        when(merchandiseService.save(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/merchandise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(20));
    }

    @Test
    void update_returns200WithUpdatedMerchandise() throws Exception {
        MerchandiseRequestDTO request = MerchandiseRequestDTO.builder()
                .name("Renombrado").category(MerchandiseCategory.POSTERS).price(BigDecimal.valueOf(9.99)).stock(10).build();
        MerchandiseResponseDTO response = MerchandiseResponseDTO.builder()
                .id(1L).name("Renombrado").price(BigDecimal.valueOf(9.99)).build();
        when(merchandiseService.update(any(), any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/merchandise/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Renombrado"));
    }

    @Test
    void delete_returns200() throws Exception {
        mockMvc.perform(delete("/api/merchandise/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
