package com.cine.demo.ticket;

import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.mapper.PurchaseMapper;
import com.cine.demo.model.Ticket;
import com.cine.demo.model.enums.TicketType;
import com.cine.demo.repository.TicketRepository;
import com.cine.demo.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private PurchaseMapper purchaseMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    /**
     * getByPurchase debe filtrar entradas (tickets) por id de compra.
     * Verificamos que se delega al repositorio y que cada Ticket
     * se mapea individualmente a TicketResponseDTO usando PurchaseMapper.
     */
    @Test
    void getByPurchase_returnsMappedTicketsForPurchase() {
        Ticket ticket = Ticket.builder()
                .id(1L).ticketType(TicketType.ADULT).unitPrice(BigDecimal.TEN).build();
        TicketResponseDTO dto = TicketResponseDTO.builder().id(1L).ticketType(TicketType.ADULT).build();
        when(ticketRepository.findByPurchaseId(99L)).thenReturn(List.of(ticket));
        when(purchaseMapper.toTicketResponseDto(ticket)).thenReturn(dto);

        List<TicketResponseDTO> result = ticketService.getByPurchase(99L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(ticketRepository).findByPurchaseId(99L);
    }

    /**
     * getByScreening debe filtrar entradas por id de proyección.
     * Caso típico: "qué entradas se han vendido para esta sesión".
     */
    @Test
    void getByScreening_returnsMappedTicketsForScreening() {
        Ticket ticket = Ticket.builder()
                .id(2L).ticketType(TicketType.CHILD).unitPrice(BigDecimal.valueOf(5)).build();
        when(ticketRepository.findByScreeningId(7L)).thenReturn(List.of(ticket));
        when(purchaseMapper.toTicketResponseDto(ticket))
                .thenReturn(TicketResponseDTO.builder().id(2L).build());

        List<TicketResponseDTO> result = ticketService.getByScreening(7L);

        assertThat(result).hasSize(1);
        verify(ticketRepository).findByScreeningId(7L);
    }

    /**
     * Si no hay entradas para la compra, devolvemos lista vacía
     * (no null), evitando NullPointerException en el frontend.
     */
    @Test
    void getByPurchase_returnsEmptyList_whenNoTickets() {
        when(ticketRepository.findByPurchaseId(99L)).thenReturn(List.of());

        List<TicketResponseDTO> result = ticketService.getByPurchase(99L);

        assertThat(result).isEmpty();
    }
}
