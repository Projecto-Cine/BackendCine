package com.cine.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TaquillaResponseDTO {

    @JsonProperty("sale_id")
    private Long saleId;

    @JsonProperty("qr_codes")
    private List<String> qrCodes;
}