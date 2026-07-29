package com.runbookagent.agent;

import com.runbookagent.dto.RunbookDto;
import com.runbookagent.dto.RunbookStepDto;
import com.runbookagent.entity.*;
import com.runbookagent.mcp.McpToolExecutor;
import com.runbookagent.mcp.McpToolResult;
import com.runbookagent.parser.MarkdownRunbookParser;
import com.runbookagent.repository.*;
import com.runbookagent.security.ActionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RunbookExecutionAgentService {

    private static final Logger log = LoggerFactory.getLogger(RunbookExecutionAgentService.class);

    private final RunbookRepository runbookRepository;
    private final RunbookExecutionRepository executionRepository;
    private final StepExecutionRepository stepExecutionRepository;
    private final ApprovalRequestRepository approvalRepository;
    private final AuditLogRepository auditLogRepository;

    private final MarkdownRunbookParser markdownParser;
    private final OllamaPlannerService plannerService;
    private final McpToolExecutor mcpToolExecutor;

    public RunbookExecutionAgentService(
            RunbookRepository runbookRepository,
            RunbookExecutionRepository executionRepository,
            StepExecutionRepository stepExecutionRepository,
            ApprovalRequestRepository approvalRepository,
            AuditLogRepository auditLogRepository,
            MarkdownRunbookParser markdownParser,
            OllamaPlannerService plannerService,
            McpToolExecutor mcpToolExecutor) {
        this.runbookRepository = runbookRepository;
        this.executionRepository = executionRepository;
        this.stepExecutionRepository = stepExecutionRepository;
        this.approvalRepository = approvalRepository;
        this.auditLogRepository = auditLogRepository;
        this.markdownParser = markdownParser;
        this.plannerService = plannerService;
        this.mcpToolExecutor = mcpToolExecutor;
    }

    @Transactional
    public RunbookExecutionEntity startExecution(Long runbookId) {
        RunbookEntity runbook = runbookRepository.findById(runbookId)
                .orElseThrow(() -> new IllegalArgumentException("Runbook not found with ID: " + runbookId));

        // 1. Create Execution Entity
        RunbookExecutionEntity execution = new RunbookExecutionEntity(runbookId);
        execution.setStatus(ExecutionStatus.PLANNING);
        execution = executionRepository.save(execution);

        auditLog(execution.getId(), "EXECUTION_STARTED", "Execution started for runbook: " + runbook.getName());

        // 2. Parse Runbook & Generate Execution Plan
        RunbookDto parsedRunbook = markdownParser.parse(runbook.getContent());

        for (RunbookStepDto stepDto : parsedRunbook.getSteps()) {
            ActionMetadata actionMetadata = plannerService.planStepAction(stepDto);

            StepExecutionEntity stepExecution = new StepExecutionEntity(
                    execution.getId(),
                    stepDto.getStepNumber(),
                    stepDto.getDescription(),
                    actionMetadata.getActionType(),
                    actionMetadata.getRiskLevel()
            );
            stepExecutionRepository.save(stepExecution);
        }

        execution.setStatus(ExecutionStatus.PLAN_READY);
        executionRepository.save(execution);

        auditLog(execution.getId(), "PLAN_GENERATED", "Structured execution plan generated with " + parsedRunbook.getSteps().size() + " steps.");

        // 3. Trigger Step Execution Loop
        return processExecution(execution.getId());
    }

    @Transactional
    public RunbookExecutionEntity processExecution(Long executionId) {
        RunbookExecutionEntity execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        if (execution.getStatus() == ExecutionStatus.WAITING_FOR_APPROVAL ||
            execution.getStatus() == ExecutionStatus.COMPLETED ||
            execution.getStatus() == ExecutionStatus.FAILED ||
            execution.getStatus() == ExecutionStatus.REJECTED ||
            execution.getStatus() == ExecutionStatus.CANCELLED) {
            return execution;
        }

        execution.setStatus(ExecutionStatus.EXECUTING);
        executionRepository.save(execution);

        List<StepExecutionEntity> steps = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(executionId);

        for (StepExecutionEntity step : steps) {
            if (step.getStatus() == StepStatus.COMPLETED || step.getStatus() == StepStatus.SKIPPED) {
                continue; // Skip already finished steps
            }

            execution.setCurrentStepNumber(step.getStepNumber());
            executionRepository.save(execution);

            // Risk Gating & Human Approval Check
            if (step.getRiskLevel().requiresApproval()) {
                ApprovalRequestEntity approval = approvalRepository.findByStepExecutionId(step.getId()).orElse(null);

                if (approval == null) {
                    // Request Approval
                    approval = new ApprovalRequestEntity(
                            executionId,
                            step.getId(),
                            step.getAction(),
                            "Action '" + step.getAction() + "' requires human confirmation: " + step.getDescription(),
                            step.getRiskLevel()
                    );
                    approvalRepository.save(approval);

                    step.setStatus(StepStatus.WAITING_FOR_APPROVAL);
                    stepExecutionRepository.save(step);

                    execution.setStatus(ExecutionStatus.WAITING_FOR_APPROVAL);
                    executionRepository.save(execution);

                    auditLog(executionId, "APPROVAL_REQUESTED", "Step " + step.getStepNumber() + " requires human approval: " + step.getAction());
                    return execution; // Pause execution
                } else if (approval.getStatus() == ApprovalStatus.PENDING) {
                    step.setStatus(StepStatus.WAITING_FOR_APPROVAL);
                    stepExecutionRepository.save(step);

                    execution.setStatus(ExecutionStatus.WAITING_FOR_APPROVAL);
                    executionRepository.save(execution);
                    return execution;
                } else if (approval.getStatus() == ApprovalStatus.REJECTED) {
                    step.setStatus(StepStatus.REJECTED);
                    stepExecutionRepository.save(step);

                    execution.setStatus(ExecutionStatus.REJECTED);
                    execution.setCompletedAt(LocalDateTime.now());
                    executionRepository.save(execution);

                    auditLog(executionId, "ACTION_REJECTED", "Step " + step.getStepNumber() + " rejected by user.");
                    return execution;
                }
                // ApprovalStatus.APPROVED -> Continue to execution
            }

            // Execute Safe or Approved Step via MCP
            step.setStatus(StepStatus.EXECUTING);
            step.setStartedAt(LocalDateTime.now());
            stepExecutionRepository.save(step);

            auditLog(executionId, "STEP_EXECUTING", "Executing Step " + step.getStepNumber() + " [" + step.getAction() + "]");

            McpToolResult result = mcpToolExecutor.executeTool(step.getAction(), true);

            step.setCompletedAt(LocalDateTime.now());
            step.setOutput(result.getOutput());

            if (result.getStatus() == McpToolResult.Status.SUCCESS) {
                step.setStatus(StepStatus.COMPLETED);
                stepExecutionRepository.save(step);

                auditLog(executionId, "STEP_COMPLETED", "Step " + step.getStepNumber() + " completed successfully.");
            } else {
                step.setStatus(StepStatus.FAILED);
                step.setError(result.getMessage());
                stepExecutionRepository.save(step);

                execution.setStatus(ExecutionStatus.FAILED);
                executionRepository.save(execution);

                String recommendation = plannerService.generateFailureRecommendation(step.getDescription(), result.getMessage());
                auditLog(executionId, "STEP_FAILED", "Step " + step.getStepNumber() + " failed: " + result.getMessage() + ". AI Rec: " + recommendation);

                return execution; // Stop loop on failure
            }
        }

        // All steps processed successfully
        execution.setStatus(ExecutionStatus.COMPLETED);
        execution.setCompletedAt(LocalDateTime.now());
        executionRepository.save(execution);

        auditLog(executionId, "EXECUTION_COMPLETED", "Runbook execution completed successfully. Incident resolved.");

        return execution;
    }

    @Transactional
    public RunbookExecutionEntity approveStep(Long executionId) {
        ApprovalRequestEntity approval = approvalRepository.findByExecutionId(executionId).stream()
                .filter(a -> a.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No pending approval request for execution: " + executionId));

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setDecidedAt(LocalDateTime.now());
        approvalRepository.save(approval);

        auditLog(executionId, "APPROVAL_GRANTED", "Human operator APPROVED action: " + approval.getAction());

        RunbookExecutionEntity execution = executionRepository.findById(executionId).orElseThrow();
        execution.setStatus(ExecutionStatus.EXECUTING);
        executionRepository.save(execution);

        return processExecution(executionId);
    }

    @Transactional
    public RunbookExecutionEntity rejectStep(Long executionId, String userChoice) {
        ApprovalRequestEntity approval = approvalRepository.findByExecutionId(executionId).stream()
                .filter(a -> a.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No pending approval request for execution: " + executionId));

        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setDecidedAt(LocalDateTime.now());
        approvalRepository.save(approval);

        StepExecutionEntity step = stepExecutionRepository.findById(approval.getStepExecutionId()).orElseThrow();

        if ("SKIP".equalsIgnoreCase(userChoice)) {
            step.setStatus(StepStatus.SKIPPED);
            stepExecutionRepository.save(step);

            auditLog(executionId, "STEP_SKIPPED", "User elected to SKIP risky step " + step.getStepNumber());

            RunbookExecutionEntity execution = executionRepository.findById(executionId).orElseThrow();
            execution.setStatus(ExecutionStatus.EXECUTING);
            executionRepository.save(execution);

            return processExecution(executionId);
        } else {
            step.setStatus(StepStatus.REJECTED);
            stepExecutionRepository.save(step);

            RunbookExecutionEntity execution = executionRepository.findById(executionId).orElseThrow();
            execution.setStatus(ExecutionStatus.REJECTED);
            execution.setCompletedAt(LocalDateTime.now());
            executionRepository.save(execution);

            auditLog(executionId, "EXECUTION_REJECTED", "Execution stopped due to human rejection of step " + step.getStepNumber());
            return execution;
        }
    }

    @Transactional
    public RunbookExecutionEntity retryStep(Long executionId, Long stepId) {
        StepExecutionEntity step = stepExecutionRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Step not found: " + stepId));

        step.setStatus(StepStatus.PENDING);
        step.setError(null);
        step.setOutput(null);
        stepExecutionRepository.save(step);

        auditLog(executionId, "STEP_RETRY", "User requested RETRY for step " + step.getStepNumber());

        RunbookExecutionEntity execution = executionRepository.findById(executionId).orElseThrow();
        execution.setStatus(ExecutionStatus.EXECUTING);
        executionRepository.save(execution);

        return processExecution(executionId);
    }

    @Transactional
    public RunbookExecutionEntity skipStep(Long executionId, Long stepId) {
        StepExecutionEntity step = stepExecutionRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Step not found: " + stepId));

        step.setStatus(StepStatus.SKIPPED);
        stepExecutionRepository.save(step);

        auditLog(executionId, "STEP_SKIPPED", "User requested SKIP for step " + step.getStepNumber());

        RunbookExecutionEntity execution = executionRepository.findById(executionId).orElseThrow();
        execution.setStatus(ExecutionStatus.EXECUTING);
        executionRepository.save(execution);

        return processExecution(executionId);
    }

    @Transactional
    public RunbookExecutionEntity cancelExecution(Long executionId) {
        RunbookExecutionEntity execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        execution.setStatus(ExecutionStatus.CANCELLED);
        execution.setCompletedAt(LocalDateTime.now());
        executionRepository.save(execution);

        auditLog(executionId, "EXECUTION_CANCELLED", "Execution cancelled by user.");
        return execution;
    }

    private void auditLog(Long executionId, String eventType, String message) {
        auditLogRepository.save(new AuditLogEntity(executionId, eventType, message));
    }
}
