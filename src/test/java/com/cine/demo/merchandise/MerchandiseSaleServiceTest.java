package com.cine.demo.merchandise;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.exception.BusinessRuleException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MerchandiseSaleMapper;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.MerchandiseSale;
import com.cine.demo.model.User;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.repository.MerchandiseSaleRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.impl.MerchandiseSaleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchandiseSaleServiceTest {

    @Mock private MerchandiseSaleRepository merchandiseSaleRepository;
    @Mock private MerchandiseRepository merchandiseRepository;
    @Mock private UserRepository userRepository;
    @Mock private MerchandiseSaleMapper merchandiseSaleMapper;

    @InjectMocks
    private MerchandiseSaleServiceImpl service;

    @Test
    void findAll_returnsEmptyList_whenNoSales() {
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void findAll_returnsMappedList_whenSalesExist() {
        MerchandiseSale sale = MerchandiseSale.builder().id(1L).build();
        MerchandiseSaleResponseDTO dto = MerchandiseSaleResponseDTO.builder().id(1L).build();
        when(merchandiseSaleRepository.findAll()).thenReturn(List.of(sale));
        when(merchandiseSaleMapper.toResponseDto(sale)).thenReturn(dto);

        List<MerchandiseSaleResponseDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findById_returnsMappedDto_whenFound() {
        MerchandiseSale sale = MerchandiseSale.builder().id(5L).build();
        MerchandiseSaleResponseDTO dto = MerchandiseSaleResponseDTO.builder().id(5L).build();
        when(merchandiseSaleRepository.findById(5L)).thenReturn(Optional.of(sale));
        when(merchandiseSaleMapper.toResponseDto(sale)).thenReturn(dto);

        MerchandiseSaleResponseDTO result = service.findById(5L);

        assertThat(result.id()).isEqualTo(5L);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(merchandiseSaleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_allowsNullUserId_forGuestPurchase() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder()
                .merchandiseId(1L).quantity(2).build();
        Merchandise merchandise = Merchandise.builder().id(1L).stock(10).price(BigDecimal.TEN).build();
        MerchandiseSale sale = MerchandiseSale.builder().id(1L).build();
        MerchandiseSaleResponseDTO expected = MerchandiseSaleResponseDTO.builder().id(1L).build();
        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(merchandise));
        when(merchandiseSaleRepository.save(any())).thenReturn(sale);
        when(merchandiseSaleMapper.toResponseDto(sale)).thenReturn(expected);

        MerchandiseSaleResponseDTO result = service.save(dto);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void save_throwsBusinessRuleException_whenMerchandiseIdNull() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder()
                .userId(1L).quantity(2).build();

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Item");
    }

    @Test
    void save_throwsBusinessRuleException_whenInsufficientStock() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder()
                .userId(1L).merchandiseId(1L).quantity(10).build();

        User user = User.builder().id(1L).build();
        Merchandise merchandise = Merchandise.builder().id(1L).stock(3).price(BigDecimal.TEN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(merchandise));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void save_calculatesTotalAndDecrementsStock() {
        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder()
                .userId(1L).merchandiseId(1L).quantity(3).build();

        User user = User.builder().id(1L).build();
        Merchandise merchandise = Merchandise.builder().id(1L).stock(10).price(BigDecimal.valueOf(5)).build();
        MerchandiseSale savedSale = MerchandiseSale.builder().id(1L).build();
        MerchandiseSaleResponseDTO responseDTO = MerchandiseSaleResponseDTO.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(merchandise));
        when(merchandiseSaleRepository.save(any())).thenReturn(savedSale);
        when(merchandiseSaleMapper.toResponseDto(savedSale)).thenReturn(responseDTO);

        service.save(dto);

        assertThat(merchandise.getStock()).isEqualTo(7);
        verify(merchandiseRepository).save(merchandise);
        verify(merchandiseSaleRepository).save(any(MerchandiseSale.class));
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(merchandiseSaleRepository.findById(99L)).thenReturn(Optional.empty());

        MerchandiseSaleRequestDTO dto = MerchandiseSaleRequestDTO.builder().quantity(2).build();

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(merchandiseSaleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_callsDeleteById_whenFound() {
        when(merchandiseSaleRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(merchandiseSaleRepository).deleteById(1L);
    }
}
