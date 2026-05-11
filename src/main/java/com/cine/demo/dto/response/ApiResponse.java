package com.cine.demo.dto.response;

import lombok.Builder;
import java.util.Map;

@Builder
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Map<String, String> errors
) {}
