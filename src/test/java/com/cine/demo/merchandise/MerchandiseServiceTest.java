package com.cine.demo.merchandise;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.enums.MerchandiseCategory;
import com.cine.demo.repository.MerchandiseRepository;
import com.cine.demo.service.impl.MerchandiseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchandiseServiceTest {

    @Mock private MerchandiseRepository merchandiseRepository;

    @InjectMocks
    private MerchandiseServiceImpl merchandiseService;

    /**
     * Verifica que findAll() devuelve TODOS los productos (activos e inactivos)
     * tal y como los devuelve el repositorio. Comprobamos que se mantiene el
     * número de elementos y que el mapeo a DTO conserva el nombre.
     */
    @Test
    void findAll_returnsAllMerchandiseMappedToDto() {
        Merchandise item = Merchandise.builder()
                .id(1L).name("Camiseta").price(19.99).stock(10).active(true).build();
        when(merchandiseRepository.findAll()).thenReturn(List.of(item));

        List<MerchandiseResponseDTO> result = merchandiseService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Camiseta");
    }

    /**
     * Comprueba que findActive() utiliza específicamente el método
     * findByActiveTrue() del repositorio para filtrar y NO findAll().
     * Así garantizamos que el endpoint público sólo expone productos activos.
     */
    @Test
    void findActive_usesRepositoryActiveFilter() {
        Merchandise item = Merchandise.builder()
                .id(1L).name("Poster").price(5.0).stock(20).active(true).build();
        when(merchandiseRepository.findByActiveTrue()).thenReturn(List.of(item));

        List<MerchandiseResponseDTO> result = merchandiseService.findActive();

        assertThat(result).hasSize(1);
        verify(merchandiseRepository).findByActiveTrue();
    }

    /**
     * Caso feliz de findById: cuando el id existe, debemos devolver el DTO
     * con los datos correctos. Útil para confirmar que el mapeo interno
     * (toDTO) propaga price, stock y category.
     */
    @Test
    void findById_returnsMerchandise_whenExists() {
        Merchandise item = Merchandise.builder()
                .id(7L).name("Taza").category(MerchandiseCategory.OTHER)
                .price(8.5).stock(3).active(true).build();
        when(merchandiseRepository.findById(7L)).thenReturn(Optional.of(item));

        MerchandiseResponseDTO result = merchandiseService.findById(7L);

        assertThat(result.getName()).isEqualTo("Taza");
        assertThat(result.getPrice()).isEqualTo(8.5);
        assertThat(result.getCategory()).isEqualTo(MerchandiseCategory.OTHER);
    }

    /**
     * Caso de error de findById: si el id NO existe debe lanzar RuntimeException
     * con el mensaje "Merchandise not found", tal y como hace el código actual.
     */
    @Test
    void findById_throwsRuntime_whenNotFound() {
        when(merchandiseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> merchandiseService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Merchandise not found");
    }

    /**
     * Verifica el método save: al crear un producto nuevo
     *  - se construye una entidad Merchandise con active = true por defecto
     *  - se guarda llamando a merchandiseRepository.save(...)
     *  - se devuelve un DTO con el id que asigna la base de datos.
     */
    @Test
    void save_createsAndReturnsMerchandiseWithActiveTrue() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Llavero").description("Llavero peli").category(MerchandiseCategory.ACCESSORIES)
                .price(2.5).stock(50).build();
        Merchandise saved = Merchandise.builder()
                .id(20L).name("Llavero").price(2.5).stock(50).active(true).build();
        when(merchandiseRepository.save(any(Merchandise.class))).thenReturn(saved);

        MerchandiseResponseDTO result = merchandiseService.save(dto);

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getActive()).isTrue();
        verify(merchandiseRepository).save(argThat(m -> Boolean.TRUE.equals(m.getActive())));
    }

    /**
     * Caso feliz de update: cuando el producto existe se actualizan
     * todos los campos del DTO sobre la entidad y se guarda.
     */
    @Test
    void update_updatesAllFieldsAndPersists() {
        Merchandise existing = Merchandise.builder()
                .id(1L).name("Antiguo").price(1.0).stock(1).active(true).build();
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Nuevo").description("desc").category(MerchandiseCategory.POSTERS)
                .price(15.0).stock(100).imageUrl("http://img").build();
        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(merchandiseRepository.save(any(Merchandise.class))).thenAnswer(inv -> inv.getArgument(0));

        MerchandiseResponseDTO result = merchandiseService.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Nuevo");
        assertThat(result.getPrice()).isEqualTo(15.0);
        assertThat(result.getStock()).isEqualTo(100);
        assertThat(result.getImageUrl()).isEqualTo("http://img");
    }

    /**
     * Caso de error de update: si el id no existe lanza RuntimeException.
     */
    @Test
    void update_throwsRuntime_whenNotFound() {
        when(merchandiseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> merchandiseService.update(99L,
                MerchandiseRequestDTO.builder().name("x").build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Merchandise not found");
    }

    /**
     * Verifica que el delete es un SOFT delete: no se borra de la BD,
     * se marca active = false y se guarda. Esto permite mantener historial
     * en ventas previas que apunten al producto.
     */
    @Test
    void delete_softDeletesByMarkingActiveFalse() {
        Merchandise existing = Merchandise.builder()
                .id(1L).name("Adios").active(true).build();
        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(existing));

        merchandiseService.delete(1L);

        assertThat(existing.getActive()).isFalse();
        verify(merchandiseRepository).save(existing);
    }

    /**
     * Caso de error de delete: si el id no existe se lanza RuntimeException
     * y NUNCA se invoca save() (no debemos persistir nada).
     */
    @Test
    void delete_throwsRuntime_whenNotFound() {
        when(merchandiseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> merchandiseService.delete(99L))
                .isInstanceOf(RuntimeException.class);
        verify(merchandiseRepository, never()).save(any());
    }
}
