package com.runbookagent.dto;

import com.runbookagent.entity.ExecutionStatus;
import java.time.LocalDateTime;
import java.util.List;

public class ExecutionReportDto {
    private Long executionId;
    private String runbookName;
    private ExecutionStatus finalStatus;
    private int totalSteps;
    private int successfulSteps;
    private int failedSteps;
    private int skippedSteps;
    private int humanApprovalsCount;
    private long totalDurationSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<StepExecutionResponseDto> steps;
    private List<String> auditSummary;

    public ExecutionReportDto() {
    }

    public ExecutionReportDto(Long executionId, String runbookName, ExecutionStatus finalStatus, int totalSteps, int successfulSteps, int failedSteps, int skippedSteps, int humanApprovalsCount, long totalDurationSeconds, LocalDateTime startedAt, LocalDateTime completedAt, List<StepExecutionResponseDto> steps, List<String> auditSummary) {
        this.executionId = executionId;
        this.runbookName = runbookName;
        this.finalStatus = finalStatus;
        this.totalSteps = totalSteps;
        this.successfulSteps = successfulSteps;
        this.failedSteps = failedSteps;
        this.skippedSteps = skippedSteps;
        this.humanApprovalsCount = humanApprovalsCount;
        this.totalDurationSeconds = totalDurationSeconds;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.steps = steps;
        this.auditSummary = auditSummary;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getRunbookName() {
        return runbookName;
    }

    public void setRunbookName(String runbookName) {
        this.runbookName = runbookName;
    }

    public ExecutionStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(ExecutionStatus finalStatus) {
        this.finalStatus = finalStatus;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getSuccessfulSteps() {
        return successfulSteps;
    }

    public void setSuccessfulSteps(int successfulSteps) {
        this.successfulSteps = successfulSteps;
    }

    public int getFailedSteps() {
        return failedSteps;
    }

    public void setFailedSteps(int failedSteps) {
        this.failedSteps = failedSteps;
    }

    public int getSkippedSteps() {
        return skippedSteps;
    }

    public void setSkippedSteps(int skippedSteps) {
        this.skippedSteps = skippedSteps;
    }

    public int getHumanApprovalsCount() {
        return humanApprovalsCount;
    }

    public void setHumanApprovalsCount(int humanApprovalsCount) {
        this.humanApprovalsCount = humanApprovalsCount;
    }

    public long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(long totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<StepExecutionResponseDto> getSteps() {
        return steps;
    }

    public void setSteps(List<StepExecutionResponseDto> steps) {
        this.steps = steps;
    }

    public List<String> getAuditSummary() {
        return auditSummary;
    }

    public void setAuditSummary(List<String> auditSummary) {
        this.auditSummary = auditSummary;
    }
}
