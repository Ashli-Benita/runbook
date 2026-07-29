package com.runbookagent.controller;

import com.runbookagent.agent.RunbookExecutionAgentService;
import com.runbookagent.dto.*;
import com.runbookagent.entity.*;
import com.runbookagent.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/executions")
@CrossOrigin(origins = "*")
public class ExecutionController {

    private final RunbookExecutionAgentService agentService;
    private final RunbookRepository runbookRepository;
    private final RunbookExecutionRepository executionRepository;
    private final StepExecutionRepository stepExecutionRepository;
    private final ApprovalRequestRepository approvalRepository;
    private final AuditLogRepository auditLogRepository;

    public ExecutionController(
            RunbookExecutionAgentService agentService,
            RunbookRepository runbookRepository,
            RunbookExecutionRepository executionRepository,
            StepExecutionRepository stepExecutionRepository,
            ApprovalRequestRepository approvalRepository,
            AuditLogRepository auditLogRepository) {
        this.agentService = agentService;
        this.runbookRepository = runbookRepository;
        this.executionRepository = executionRepository;
        this.stepExecutionRepository = stepExecutionRepository;
        this.approvalRepository = approvalRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @PostMapping
    public ResponseEntity<ExecutionResponseDto> startExecution(@RequestBody Map<String, Long> payload) {
        Long runbookId = payload.get("runbookId");
        if (runbookId == null) {
            throw new IllegalArgumentException("Field 'runbookId' is required");
        }
        RunbookExecutionEntity entity = agentService.startExecution(runbookId);
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @GetMapping
    public ResponseEntity<List<ExecutionResponseDto>> getAllExecutions() {
        List<ExecutionResponseDto> list = executionRepository.findAll().stream()
                .map(this::mapExecutionToDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExecutionResponseDto> getExecutionById(@PathVariable Long id) {
        RunbookExecutionEntity entity = executionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + id));
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @GetMapping("/{id}/steps")
    public ResponseEntity<List<StepExecutionResponseDto>> getExecutionSteps(@PathVariable Long id) {
        List<StepExecutionResponseDto> steps = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(id).stream()
                .map(this::mapStepToDto)
                .toList();
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<ApprovalResponseDto>> getPendingApprovals() {
        List<ApprovalResponseDto> pending = approvalRepository.findByStatus(ApprovalStatus.PENDING).stream()
                .map(this::mapApprovalToDto)
                .toList();
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ExecutionResponseDto> approveStep(@PathVariable Long id) {
        RunbookExecutionEntity entity = agentService.approveStep(id);
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ExecutionResponseDto> rejectStep(
            @PathVariable Long id,
            @RequestBody(required = false) RejectRequestDto request) {
        String choice = request != null && request.getUserChoice() != null ? request.getUserChoice() : "STOP";
        RunbookExecutionEntity entity = agentService.rejectStep(id, choice);
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ExecutionResponseDto> cancelExecution(@PathVariable Long id) {
        RunbookExecutionEntity entity = agentService.cancelExecution(id);
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @PostMapping("/{id}/steps/{stepId}/retry")
    public ResponseEntity<ExecutionResponseDto> retryStep(@PathVariable Long id, @PathVariable Long stepId) {
        RunbookExecutionEntity entity = agentService.retryStep(id, stepId);
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @PostMapping("/{id}/steps/{stepId}/skip")
    public ResponseEntity<ExecutionResponseDto> skipStep(@PathVariable Long id, @PathVariable Long stepId) {
        RunbookExecutionEntity entity = agentService.skipStep(id, stepId);
        return ResponseEntity.ok(mapExecutionToDto(entity));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<ExecutionReportDto> getExecutionReport(@PathVariable Long id) {
        RunbookExecutionEntity execution = executionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + id));

        RunbookEntity runbook = runbookRepository.findById(execution.getRunbookId()).orElse(null);
        String runbookName = runbook != null ? runbook.getName() : "Unknown Runbook";

        List<StepExecutionEntity> stepEntities = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(id);
        List<StepExecutionResponseDto> stepDtos = stepEntities.stream().map(this::mapStepToDto).toList();

        int totalSteps = stepEntities.size();
        int successful = (int) stepEntities.stream().filter(s -> s.getStatus() == StepStatus.COMPLETED).count();
        int failed = (int) stepEntities.stream().filter(s -> s.getStatus() == StepStatus.FAILED).count();
        int skipped = (int) stepEntities.stream().filter(s -> s.getStatus() == StepStatus.SKIPPED).count();

        List<ApprovalRequestEntity> approvals = approvalRepository.findByExecutionId(id);
        int approvalCount = (int) approvals.stream().filter(a -> a.getStatus() == ApprovalStatus.APPROVED).count();

        long durationSeconds = 0;
        if (execution.getStartedAt() != null) {
            java.time.LocalDateTime endTime = execution.getCompletedAt() != null ? execution.getCompletedAt() : java.time.LocalDateTime.now();
            durationSeconds = Duration.between(execution.getStartedAt(), endTime).getSeconds();
        }

        List<String> auditLogs = auditLogRepository.findByExecutionIdOrderByTimestampAsc(id).stream()
                .map(a -> String.format("[%s] %s - %s", a.getTimestamp(), a.getEventType(), a.getMessage()))
                .toList();

        ExecutionReportDto report = new ExecutionReportDto(
                execution.getId(),
                runbookName,
                execution.getStatus(),
                totalSteps,
                successful,
                failed,
                skipped,
                approvalCount,
                durationSeconds,
                execution.getStartedAt(),
                execution.getCompletedAt(),
                stepDtos,
                auditLogs
        );

        return ResponseEntity.ok(report);
    }

    private ExecutionResponseDto mapExecutionToDto(RunbookExecutionEntity entity) {
        RunbookEntity runbook = runbookRepository.findById(entity.getRunbookId()).orElse(null);
        String runbookName = runbook != null ? runbook.getName() : "Unknown";

        return new ExecutionResponseDto(
                entity.getId(),
                entity.getRunbookId(),
                runbookName,
                entity.getStatus(),
                entity.getCurrentStepNumber(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getCreatedBy()
        );
    }

    private StepExecutionResponseDto mapStepToDto(StepExecutionEntity entity) {
        return new StepExecutionResponseDto(
                entity.getId(),
                entity.getExecutionId(),
                entity.getStepNumber(),
                entity.getDescription(),
                entity.getAction(),
                entity.getRiskLevel(),
                entity.getStatus(),
                entity.getOutput(),
                entity.getError(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }

    private ApprovalResponseDto mapApprovalToDto(ApprovalRequestEntity entity) {
        return new ApprovalResponseDto(
                entity.getId(),
                entity.getExecutionId(),
                entity.getStepExecutionId(),
                entity.getAction(),
                entity.getReason(),
                entity.getRiskLevel(),
                entity.getStatus(),
                entity.getRequestedAt()
        );
    }
}
