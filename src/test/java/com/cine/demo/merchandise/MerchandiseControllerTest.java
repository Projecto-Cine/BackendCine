package com.cine.demo.merchandise;

import com.cine.demo.controller.MerchandiseController;
import com.cine.demo.dto.request.MerchandiseRequestDTO;
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

    /**
     * GET /api/merchandise debe devolver el listado completo (activos + inactivos)
     * sirviendo el JSON tal cual viene del servicio.
     */
    @Test
    void getAll_returns200WithMerchandiseList() throws Exception {
        MerchandiseResponseDTO item = MerchandiseResponseDTO.builder()
                .id(1L).name("Camiseta").price(19.99).build();
        when(merchandiseService.findAll()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/merchandise"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Camiseta"))
                .andExpect(jsonPath("$[0].price").value(19.99));
    }

    /**
     * GET /api/merchandise/active devuelve sólo los productos activos.
     */
    @Test
    void getActive_returns200WithActiveItemsOnly() throws Exception {
        MerchandiseResponseDTO active = MerchandiseResponseDTO.builder()
                .id(1L).name("Activo").active(true).build();
        when(merchandiseService.findActive()).thenReturn(List.of(active));

        mockMvc.perform(get("/api/merchandise/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    /**
     * GET /api/merchandise/{id}: devuelve el producto pedido.
     */
    @Test
    void getById_returns200_whenExists() throws Exception {
        when(merchandiseService.findById(7L)).thenReturn(
                MerchandiseResponseDTO.builder().id(7L).name("Llavero").build());

        mockMvc.perform(get("/api/merchandise/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Llavero"));
    }

    /**
     * POST /api/merchandise crea un producto. El controller delega al servicio
     * y devuelve 200 con el DTO resultante (incluyendo id asignado por la BD).
     */
    @Test
    void create_returns200WithSavedMerchandise() throws Exception {
        MerchandiseRequestDTO request = MerchandiseRequestDTO.builder()
                .name("Nuevo").category(MerchandiseCategory.POSTERS).price(12.0).stock(50).build();
        MerchandiseResponseDTO response = MerchandiseResponseDTO.builder()
                .id(20L).name("Nuevo").price(12.0).build();
        when(merchandiseService.save(any())).thenReturn(response);

        mockMvc.perform(post("/api/merchandise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20));
    }

    /**
     * PUT /api/merchandise/{id}: actualiza y devuelve el producto modificado.
     */
    @Test
    void update_returns200WithUpdatedMerchandise() throws Exception {
        MerchandiseRequestDTO request = MerchandiseRequestDTO.builder()
                .name("Renombrado").price(9.99).stock(10).build();
        MerchandiseResponseDTO response = MerchandiseResponseDTO.builder()
                .id(1L).name("Renombrado").price(9.99).build();
        when(merchandiseService.update(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/merchandise/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renombrado"));
    }

    /**
     * DELETE /api/merchandise/{id}: devuelve 204 No Content (soft delete).
     */
    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/merchandise/1"))
                .andExpect(status().isNoContent());
    }
}
