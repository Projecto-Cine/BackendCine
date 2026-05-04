package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponseDTO {
    private Long id;
    private LocalDateTime timestamp;
    private String user;
    private String action;
    private String resource;
    private String detail;
    private String ip;
    private String severity;
}