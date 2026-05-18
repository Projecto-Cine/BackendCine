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

import com.cine.demo.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private PurchaseMapper purchaseMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void findAll_returnsMappedList() {
        Ticket ticket = Ticket.builder()
                .id(1L).ticketType(TicketType.ADULT).unitPrice(BigDecimal.TEN).build();
        TicketResponseDTO dto = TicketResponseDTO.builder().id(1L).ticketType(TicketType.ADULT).build();
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
        when(purchaseMapper.toTicketResponseDto(ticket)).thenReturn(dto);

        List<TicketResponseDTO> result = ticketService.findAll();

        assertThat(result).hasSize(1).extracting(TicketResponseDTO::getId).containsExactly(1L);
        verify(ticketRepository).findAll();
    }

    @Test
    void findAll_returnsEmptyList_whenNoTickets() {
        when(ticketRepository.findAll()).thenReturn(List.of());

        assertThat(ticketService.findAll()).isEmpty();
    }

    @Test
    void findById_returnsMappedDto_whenExists() {
        Ticket ticket = Ticket.builder()
                .id(5L).ticketType(TicketType.STUDENT).unitPrice(BigDecimal.valueOf(7)).build();
        TicketResponseDTO dto = TicketResponseDTO.builder().id(5L).ticketType(TicketType.STUDENT).build();
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(ticket));
        when(purchaseMapper.toTicketResponseDto(ticket)).thenReturn(dto);

        TicketResponseDTO result = ticketService.findById(5L);

        assertThat(result.id()).isEqualTo(5L);
        verify(ticketRepository).findById(5L);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByPurchase_returnsMappedTicketsForPurchase() {
        Ticket ticket = Ticket.builder()
                .id(1L).ticketType(TicketType.ADULT).unitPrice(BigDecimal.TEN).build();
        TicketResponseDTO dto = TicketResponseDTO.builder().id(1L).ticketType(TicketType.ADULT).build();
        when(ticketRepository.findByPurchaseId(99L)).thenReturn(List.of(ticket));
        when(purchaseMapper.toTicketResponseDto(ticket)).thenReturn(dto);

        List<TicketResponseDTO> result = ticketService.getByPurchase(99L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        verify(ticketRepository).findByPurchaseId(99L);
    }

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

    @Test
    void getByPurchase_returnsEmptyList_whenNoTickets() {
        when(ticketRepository.findByPurchaseId(99L)).thenReturn(List.of());

        List<TicketResponseDTO> result = ticketService.getByPurchase(99L);

        assertThat(result).isEmpty();
    }
}
