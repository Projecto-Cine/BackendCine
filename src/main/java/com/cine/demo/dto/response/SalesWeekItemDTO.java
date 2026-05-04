package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesWeekItemDTO {
    private String day;
    private double ventas;
    private int entradas;
}