package com.cine.demo.merchandise;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MerchandiseMapper;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.enums.MerchandiseCategory;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.service.impl.MerchandiseServiceImpl;
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
class MerchandiseServiceTest {

    @Mock private MerchandiseRepository merchandiseRepository;
    @Mock private MerchandiseMapper merchandiseMapper;

    @InjectMocks
    private MerchandiseServiceImpl merchandiseService;

    @Test
    void findAll_returnsAllMerchandiseMappedToDto() {
        Merchandise item = Merchandise.builder()
                .id(1L).name("Camiseta").price(BigDecimal.valueOf(19.99)).stock(10).active(true).build();
        when(merchandiseRepository.findAll()).thenReturn(List.of(item));
        when(merchandiseMapper.toResponseDto(item)).thenReturn(
                MerchandiseResponseDTO.builder().id(1L).name("Camiseta").build());

        List<MerchandiseResponseDTO> result = merchandiseService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Camiseta");
    }

    @Test
    void findActive_usesRepositoryActiveFilter() {
        Merchandise item = Merchandise.builder()
                .id(1L).name("Poster").price(BigDecimal.valueOf(5.0)).stock(20).active(true).build();
        when(merchandiseRepository.findByActiveTrue()).thenReturn(List.of(item));
        when(merchandiseMapper.toResponseDto(item)).thenReturn(
                MerchandiseResponseDTO.builder().id(1L).name("Poster").build());

        List<MerchandiseResponseDTO> result = merchandiseService.findActive();

        assertThat(result).hasSize(1);
        verify(merchandiseRepository).findByActiveTrue();
    }

    @Test
    void findById_returnsMerchandise_whenExists() {
        Merchandise item = Merchandise.builder()
                .id(7L).name("Taza").category(MerchandiseCategory.OTHER)
                .price(BigDecimal.valueOf(8.5)).stock(3).active(true).build();
        when(merchandiseRepository.findById(7L)).thenReturn(Optional.of(item));
        when(merchandiseMapper.toResponseDto(item)).thenReturn(
                MerchandiseResponseDTO.builder().id(7L).name("Taza").price(BigDecimal.valueOf(8.5)).build());

        MerchandiseResponseDTO result = merchandiseService.findById(7L);

        assertThat(result.getName()).isEqualTo("Taza");
    }

    @Test
    void findById_throwsResourceNotFound_whenNotFound() {
        when(merchandiseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> merchandiseService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artículo no encontrado");
    }

    @Test
    void save_createsAndReturnsMerchandise() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Llavero").description("Llavero peli").category(MerchandiseCategory.ACCESSORIES)
                .price(BigDecimal.valueOf(2.5)).stock(50).build();
        Merchandise entity = Merchandise.builder().name("Llavero").build();
        Merchandise saved = Merchandise.builder().id(20L).name("Llavero").active(true).build();
        when(merchandiseMapper.toEntity(dto)).thenReturn(entity);
        when(merchandiseRepository.save(entity)).thenReturn(saved);
        when(merchandiseMapper.toResponseDto(saved)).thenReturn(
                MerchandiseResponseDTO.builder().id(20L).name("Llavero").active(true).build());

        MerchandiseResponseDTO result = merchandiseService.save(dto);

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void update_updatesAllFieldsAndPersists() {
        Merchandise existing = Merchandise.builder()
                .id(1L).name("Antiguo").price(BigDecimal.ONE).stock(1).active(true).build();
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Nuevo").description("desc").category(MerchandiseCategory.POSTERS)
                .price(BigDecimal.valueOf(15.0)).stock(100).imageUrl("http://img").build();
        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(merchandiseRepository.save(existing)).thenReturn(existing);
        when(merchandiseMapper.toResponseDto(existing)).thenReturn(
                MerchandiseResponseDTO.builder().id(1L).name("Nuevo").build());

        MerchandiseResponseDTO result = merchandiseService.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Nuevo");
    }

    @Test
    void update_throwsResourceNotFound_whenNotFound() {
        when(merchandiseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> merchandiseService.update(99L,
                MerchandiseRequestDTO.builder().name("x").build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artículo no encontrado");
    }

    @Test
    void delete_removesMerchandise_whenExists() {
        when(merchandiseRepository.existsById(1L)).thenReturn(true);

        merchandiseService.delete(1L);

        verify(merchandiseRepository).deleteById(1L);
    }

    @Test
    void delete_throwsResourceNotFound_whenNotFound() {
        when(merchandiseRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> merchandiseService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(merchandiseRepository, never()).deleteById(any());
    }
}
