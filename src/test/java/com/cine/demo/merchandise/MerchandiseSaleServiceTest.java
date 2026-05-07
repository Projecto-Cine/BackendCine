package com.cine.demo.merchandise;

import com.cine.demo.dto.request.MerchandiseSaleRequestDTO;
import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.service.impl.MerchandiseSaleServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MerchandiseSaleServiceTest {

    private final MerchandiseSaleServiceImpl service = new MerchandiseSaleServiceImpl();

    /**
     * El servicio MerchandiseSale es un esqueleto pendiente de implementar.
     * Por ahora todos los métodos de lectura devuelven null sin lanzar
     * excepción. Este test fija ese contrato: si alguien implementa el
     * método de verdad, este test fallará y obligará a actualizar la API.
     */
    @Test
    void findAll_returnsNull_inCurrentSkeleton() {
        assertThat(service.findAll()).isNull();
    }

    @Test
    void findById_returnsNull_inCurrentSkeleton() {
        assertThat(service.findById(1L)).isNull();
    }

    @Test
    void save_returnsNull_inCurrentSkeleton() {
        MerchandiseSaleRequestDTO dto = new MerchandiseSaleRequestDTO();
        assertThat(service.save(dto)).isNull();
    }

    @Test
    void update_returnsNull_inCurrentSkeleton() {
        MerchandiseSaleResponseDTO result = service.update(1L, new MerchandiseSaleRequestDTO());

        assertThat(result).isNull();
    }

    /**
     * delete no debe lanzar excepción aunque sea un esqueleto.
     */
    @Test
    void delete_doesNothing_inCurrentSkeleton() {
        service.delete(1L); // simplemente debe no lanzar
    }
}
