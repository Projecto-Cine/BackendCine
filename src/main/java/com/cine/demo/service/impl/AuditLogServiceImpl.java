package com.cine.demo.service.impl;

import com.cine.demo.dto.response.AuditLogResponseDTO;
import com.cine.demo.model.AuditLog;
import com.cine.demo.repository.AuditLogRepository;
import com.cine.demo.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getAll(String severity, String user, String action, String from, String to) {
        LocalDateTime fromDt = from != null ? LocalDate.parse(from).atStartOfDay() : null;
        LocalDateTime toDt = to != null ? LocalDate.parse(to).atTime(23, 59, 59) : null;

        return auditLogRepository.findWithFilters(severity, user, action, fromDt, toDt)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void log(String user, String action, String resource, String detail, String ip, String severity) {
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(action)
                .resource(resource)
                .detail(detail)
                .ip(ip)
                .severity(severity)
                .build());
    }

    private AuditLogResponseDTO toDto(AuditLog log) {
        return AuditLogResponseDTO.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .user(log.getUser())
                .action(log.getAction())
                .resource(log.getResource())
                .detail(log.getDetail())
                .ip(log.getIp())
                .severity(log.getSeverity())
                .build();
    }
}