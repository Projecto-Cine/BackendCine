package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryReportDTO {
    private String category;
    private long count;
}