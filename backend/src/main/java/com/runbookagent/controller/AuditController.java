package com.runbookagent.controller;

import com.runbookagent.entity.AuditLogEntity;
import com.runbookagent.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/execution/{executionId}")
    public ResponseEntity<List<AuditLogEntity>> getAuditLogsByExecution(@PathVariable Long executionId) {
        List<AuditLogEntity> logs = auditLogRepository.findByExecutionIdOrderByTimestampAsc(executionId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLogEntity>> getRecentAuditLogs() {
        List<AuditLogEntity> logs = auditLogRepository.findTop50ByOrderByTimestampDesc();
        return ResponseEntity.ok(logs);
    }
}
