package com.cine.demo.ticket;

import com.cine.demo.controller.TicketController;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@Import(GlobalExceptionHandler.class)
class TicketControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TicketService ticketService;

    @Test
    void getAll_returns200WithNoBody_inSkeleton() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_returns200WithNoBody_inSkeleton() throws Exception {
        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk());
    }


}
