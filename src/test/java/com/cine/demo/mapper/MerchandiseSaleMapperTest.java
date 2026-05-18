package com.cine.demo.mapper;

import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.MerchandiseSale;
import com.cine.demo.model.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MerchandiseSaleMapperTest {

    private final MerchandiseSaleMapper mapper = new MerchandiseSaleMapper();

    // ── toResponseDto ─────────────────────────────────────────────────────

    @Test
    void toResponseDto_mapsAllFields_whenUserAndMerchandisePresent() {
        User user = User.builder().id(5L).build();
        Merchandise merch = Merchandise.builder().id(10L).name("Popcorn").build();
        LocalDateTime saleDate = LocalDateTime.of(2026, 5, 17, 12, 0);
        MerchandiseSale entity = MerchandiseSale.builder()
                .id(1L).user(user).merchandise(merch)
                .quantity(3).total(new BigDecimal("15.00")).saleDate(saleDate).build();

        MerchandiseSaleResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.userId()).isEqualTo(5L);
        assertThat(dto.merchandiseId()).isEqualTo(10L);
        assertThat(dto.merchandiseName()).isEqualTo("Popcorn");
        assertThat(dto.quantity()).isEqualTo(3);
        assertThat(dto.total()).isEqualByComparingTo("15.00");
        assertThat(dto.saleDate()).isEqualTo(saleDate);
    }

    @Test
    void toResponseDto_userIdIsNull_whenUserIsNull() {
        Merchandise merch = Merchandise.builder().id(10L).name("Drink").build();
        MerchandiseSale entity = MerchandiseSale.builder()
                .id(2L).user(null).merchandise(merch)
                .quantity(1).total(BigDecimal.TEN).build();

        MerchandiseSaleResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.userId()).isNull();
        assertThat(dto.merchandiseId()).isEqualTo(10L);
    }

    @Test
    void toResponseDto_merchandiseFieldsAreNull_whenMerchandiseIsNull() {
        User user = User.builder().id(3L).build();
        MerchandiseSale entity = MerchandiseSale.builder()
                .id(3L).user(user).merchandise(null)
                .quantity(2).total(BigDecimal.TEN).build();

        MerchandiseSaleResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.merchandiseId()).isNull();
        assertThat(dto.merchandiseName()).isNull();
        assertThat(dto.userId()).isEqualTo(3L);
    }

    @Test
    void toResponseDto_allNullableFieldsAreNull_whenBothUserAndMerchandiseAreNull() {
        MerchandiseSale entity = MerchandiseSale.builder()
                .id(4L).user(null).merchandise(null)
                .quantity(1).total(BigDecimal.ONE).build();

        MerchandiseSaleResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.userId()).isNull();
        assertThat(dto.merchandiseId()).isNull();
        assertThat(dto.merchandiseName()).isNull();
    }
}
