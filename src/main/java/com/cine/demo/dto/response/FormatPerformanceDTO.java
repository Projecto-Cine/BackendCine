package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormatPerformanceDTO {
    private String format;
    private long sessions;
    private long tickets;
    private double revenue;
    private int occupancy;
}