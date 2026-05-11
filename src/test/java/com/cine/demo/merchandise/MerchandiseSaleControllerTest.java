package com.cine.demo.merchandise;

import com.cine.demo.controller.MerchandiseSaleController;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.service.MerchandiseSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchandiseSaleController.class)
@Import(GlobalExceptionHandler.class)
class MerchandiseSaleControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private MerchandiseSaleService merchandiseSaleService;

    /**
     * MerchandiseSaleController es un esqueleto. Estos tests cubren
     * todas las rutas para garantizar que ESTÁN registradas (sin 404)
     * y devuelven 200 con cuerpo vacío en su estado actual.
     */
    @Test
    void getAll_returns200_inSkeleton() throws Exception {
        mockMvc.perform(get("/api/merchandisesales")).andExpect(status().isOk());
    }

    @Test
    void getById_returns200_inSkeleton() throws Exception {
        mockMvc.perform(get("/api/merchandisesales/1")).andExpect(status().isOk());
    }

    @Test
    void create_returns200_inSkeleton() throws Exception {
        mockMvc.perform(post("/api/merchandisesales")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    void update_returns200_inSkeleton() throws Exception {
        mockMvc.perform(put("/api/merchandisesales/1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns200_inSkeleton() throws Exception {
        mockMvc.perform(delete("/api/merchandisesales/1")).andExpect(status().isOk());
    }
}
