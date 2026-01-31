package com.inventory.inventory_backend.service;

import com.inventory.inventory_backend.model.AuditLog;
import com.inventory.inventory_backend.model.EAction;
import com.inventory.inventory_backend.model.EEntity;
import com.inventory.inventory_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(
            Long userId,
            EAction action,
            EEntity entity,
            Long entityId,
            String description
    ) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
