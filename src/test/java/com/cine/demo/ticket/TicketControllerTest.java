package com.cine.demo.ticket;

import com.cine.demo.controller.TicketController;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.enums.TicketType;
import com.cine.demo.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@Import(GlobalExceptionHandler.class)
class TicketControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TicketService ticketService;

    // ── GET /api/tickets (no params) ──────────────────────────────────────

    @Test
    void getAll_withNoParams_returns200() throws Exception {
        given(ticketService.findAll()).willReturn(List.of(ticket()));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_withNoParams_callsFindAll() throws Exception {
        given(ticketService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/tickets"));

        verify(ticketService).findAll();
    }

    @Test
    void getAll_withNoParams_returnsEnvelope() throws Exception {
        given(ticketService.findAll()).willReturn(List.of(ticket()));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tickets retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAll_withNoParams_returnsTicketList() throws Exception {
        given(ticketService.findAll()).willReturn(List.of(ticket()));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].purchaseId").value(10))
                .andExpect(jsonPath("$.data[0].row").value("C"))
                .andExpect(jsonPath("$.data[0].number").value(5))
                .andExpect(jsonPath("$.data[0].ticketType").value("ADULT"));
    }

    // ── GET /api/tickets?purchaseId ───────────────────────────────────────

    @Test
    void getAll_withPurchaseId_callsGetByPurchase() throws Exception {
        given(ticketService.getByPurchase(10L)).willReturn(List.of(ticket()));

        mockMvc.perform(get("/api/tickets").param("purchaseId", "10"))
                .andExpect(status().isOk());

        verify(ticketService).getByPurchase(10L);
    }

    @Test
    void getAll_withPurchaseId_returnsFilteredTickets() throws Exception {
        given(ticketService.getByPurchase(10L)).willReturn(List.of(ticket()));

        mockMvc.perform(get("/api/tickets").param("purchaseId", "10"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].purchaseId").value(10));
    }

    // ── GET /api/tickets?screeningId ──────────────────────────────────────

    @Test
    void getAll_withScreeningId_callsGetByScreening() throws Exception {
        given(ticketService.getByScreening(7L)).willReturn(List.of(ticket()));

        mockMvc.perform(get("/api/tickets").param("screeningId", "7"))
                .andExpect(status().isOk());

        verify(ticketService).getByScreening(7L);
    }

    // ── GET /api/tickets/{id} ─────────────────────────────────────────────

    @Test
    void getById_returns200_whenFound() throws Exception {
        given(ticketService.findById(1L)).willReturn(ticket());

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_returnsEnvelope() throws Exception {
        given(ticketService.findById(1L)).willReturn(ticket());

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ticket retrieved successfully"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getById_returnsAllFields() throws Exception {
        given(ticketService.findById(1L)).willReturn(ticket());

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.purchaseId").value(10))
                .andExpect(jsonPath("$.data.seatId").value(20))
                .andExpect(jsonPath("$.data.row").value("C"))
                .andExpect(jsonPath("$.data.number").value(5))
                .andExpect(jsonPath("$.data.seatType").value("STANDARD"))
                .andExpect(jsonPath("$.data.ticketType").value("ADULT"))
                .andExpect(jsonPath("$.data.unitPrice").isNumber());
    }

    @Test
    void getById_delegatesToService() throws Exception {
        given(ticketService.findById(1L)).willReturn(ticket());

        mockMvc.perform(get("/api/tickets/1"));

        verify(ticketService).findById(1L);
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        given(ticketService.findById(99L))
                .willThrow(new ResourceNotFoundException("Ticket not found with id: 99"));

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found with id: 99"));
    }

    // ── Fixture ───────────────────────────────────────────────────────────

    private TicketResponseDTO ticket() {
        return TicketResponseDTO.builder()
                .id(1L)
                .purchaseId(10L)
                .seatId(20L)
                .row("C")
                .number(5)
                .seatType("STANDARD")
                .ticketType(TicketType.ADULT)
                .unitPrice(new BigDecimal("9"))
                .build();
    }
}
