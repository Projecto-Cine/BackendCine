package com.cine.demo.dto;

import com.cine.demo.dto.response.ApiError;
import com.cine.demo.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseDtoTest {

    @Test
    void apiResponse_ok_withData_setsSuccessTrueAndData() {
        ApiResponse<String> response = ApiResponse.ok("Retrieved", "payload");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Retrieved");
        assertThat(response.data()).isEqualTo("payload");
    }

    @Test
    void apiResponse_ok_withoutData_setsNullData() {
        ApiResponse<Void> response = ApiResponse.ok("Done");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Done");
        assertThat(response.data()).isNull();
    }

    @Test
    void apiResponse_directConstruction_setsAllFields() {
        ApiResponse<Integer> response = new ApiResponse<>(false, "Error", null);

        assertThat(response.success()).isFalse();
        assertThat(response.message()).isEqualTo("Error");
        assertThat(response.data()).isNull();
    }

    @Test
    void apiError_withMessage_setsMessageAndTimestamp() {
        ApiError error = new ApiError("Something went wrong");

        assertThat(error.message()).isEqualTo("Something went wrong");
        assertThat(error.timestamp()).isNotNull();
        assertThat(error.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void apiError_withMessageAndTimestamp_setsExactTimestamp() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 15, 12, 0, 0);

        ApiError error = new ApiError("Conflict", now);

        assertThat(error.message()).isEqualTo("Conflict");
        assertThat(error.timestamp()).isEqualTo(now);
    }
}
