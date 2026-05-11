package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeRatingTest {

    @Test
    void getDbValue_returnsShortCode() {
        assertThat(AgeRating.ALL.getDbValue()).isEqualTo("ALL");
        assertThat(AgeRating.SEVEN.getDbValue()).isEqualTo("7");
        assertThat(AgeRating.TWELVE.getDbValue()).isEqualTo("12");
        assertThat(AgeRating.SIXTEEN.getDbValue()).isEqualTo("16");
        assertThat(AgeRating.EIGHTEEN.getDbValue()).isEqualTo("18");
    }

    @Test
    void fromDbValue_parsesShortCodes() {
        assertThat(AgeRating.fromDbValue("ALL")).isEqualTo(AgeRating.ALL);
        assertThat(AgeRating.fromDbValue("7")).isEqualTo(AgeRating.SEVEN);
        assertThat(AgeRating.fromDbValue("12")).isEqualTo(AgeRating.TWELVE);
        assertThat(AgeRating.fromDbValue("16")).isEqualTo(AgeRating.SIXTEEN);
        assertThat(AgeRating.fromDbValue("18")).isEqualTo(AgeRating.EIGHTEEN);
    }

    @Test
    void fromDbValue_throwsIllegalArgument_whenUnknown() {
        assertThatThrownBy(() -> AgeRating.fromDbValue("XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }
}
