package com.cine.demo.service;

import com.cine.demo.dto.response.AuditLogResponseDTO;
import java.util.List;

public interface AuditLogService {
    List<AuditLogResponseDTO> getAll(String severity, String user, String action, String from, String to);
    void log(String user, String action, String resource, String detail, String ip, String severity);
}