package com.cine.demo.mapper;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.enums.MerchandiseCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MerchandiseMapperTest {

    private final MerchandiseMapper mapper = new MerchandiseMapper();

    // ── toEntity ──────────────────────────────────────────────────────────

    @Test
    void toEntity_mapsAllFields() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Popcorn").description("Large bucket").category("FOOD")
                .price(new BigDecimal("5.00")).stock(100).imageUrl("http://img.com/pc.jpg").build();

        Merchandise entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("Popcorn");
        assertThat(entity.getDescription()).isEqualTo("Large bucket");
        assertThat(entity.getCategory()).isEqualTo(MerchandiseCategory.FOOD);
        assertThat(entity.getPrice()).isEqualByComparingTo("5.00");
        assertThat(entity.getStock()).isEqualTo(100);
        assertThat(entity.getImageUrl()).isEqualTo("http://img.com/pc.jpg");
    }

    @Test
    void toEntity_idIsNull() {
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Shirt").category("CLOTHING")
                .price(BigDecimal.TEN).build();

        assertThat(mapper.toEntity(dto).getId()).isNull();
    }

    // ── toResponseDto ─────────────────────────────────────────────────────

    @Test
    void toResponseDto_mapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Merchandise entity = Merchandise.builder()
                .id(1L).name("Popcorn").description("Salty")
                .category(MerchandiseCategory.FOOD).price(new BigDecimal("5.50"))
                .stock(50).imageUrl("http://img.jpg").active(true).createdAt(now).build();

        MerchandiseResponseDTO dto = mapper.toResponseDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Popcorn");
        assertThat(dto.description()).isEqualTo("Salty");
        assertThat(dto.category()).isEqualTo("FOOD");
        assertThat(dto.price()).isEqualByComparingTo("5.50");
        assertThat(dto.stock()).isEqualTo(50);
        assertThat(dto.imageUrl()).isEqualTo("http://img.jpg");
        assertThat(dto.active()).isTrue();
        assertThat(dto.createdAt()).isEqualTo(now);
    }

    @Test
    void toResponseDto_categoryIsNullWhenEntityCategoryIsNull() {
        Merchandise entity = Merchandise.builder()
                .id(2L).name("Item").category(null).price(BigDecimal.ONE).build();

        assertThat(mapper.toResponseDto(entity).category()).isNull();
    }

    // ── updateEntityFromDto ───────────────────────────────────────────────

    @Test
    void updateEntityFromDto_updatesAllNonNullFields() {
        Merchandise entity = Merchandise.builder()
                .name("Old").description("Old desc").category(MerchandiseCategory.OTHER)
                .price(BigDecimal.ONE).stock(5).imageUrl("old.jpg").build();
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("New").description("New desc").category("DRINK")
                .price(new BigDecimal("9.99")).stock(20).imageUrl("new.jpg").build();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("New");
        assertThat(entity.getDescription()).isEqualTo("New desc");
        assertThat(entity.getCategory()).isEqualTo(MerchandiseCategory.DRINK);
        assertThat(entity.getPrice()).isEqualByComparingTo("9.99");
        assertThat(entity.getStock()).isEqualTo(20);
        assertThat(entity.getImageUrl()).isEqualTo("new.jpg");
    }

    @Test
    void updateEntityFromDto_doesNotOverwriteNonStockFieldsWhenNull() {
        Merchandise entity = Merchandise.builder()
                .name("Popcorn").description("Salty").category(MerchandiseCategory.FOOD)
                .price(BigDecimal.TEN).stock(10).imageUrl("img.jpg").build();
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder().stock(99).build();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getName()).isEqualTo("Popcorn");
        assertThat(entity.getDescription()).isEqualTo("Salty");
        assertThat(entity.getCategory()).isEqualTo(MerchandiseCategory.FOOD);
        assertThat(entity.getPrice()).isEqualByComparingTo("10");
        assertThat(entity.getImageUrl()).isEqualTo("img.jpg");
    }

    @Test
    void updateEntityFromDto_stockIsAlwaysUpdated_evenToZero() {
        Merchandise entity = Merchandise.builder()
                .name("Item").category(MerchandiseCategory.OTHER)
                .price(BigDecimal.ONE).stock(50).build();
        MerchandiseRequestDTO dto = MerchandiseRequestDTO.builder()
                .name("Item").category("OTHER")
                .price(BigDecimal.ONE).stock(0).build();

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getStock()).isEqualTo(0);
    }
}
