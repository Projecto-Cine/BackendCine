package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;
}
