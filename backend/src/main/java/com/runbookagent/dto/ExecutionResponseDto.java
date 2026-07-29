package com.runbookagent.dto;

import com.runbookagent.entity.ExecutionStatus;
import java.time.LocalDateTime;

public class ExecutionResponseDto {
    private Long id;
    private Long runbookId;
    private String runbookName;
    private ExecutionStatus status;
    private Integer currentStepNumber;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;

    public ExecutionResponseDto() {
    }

    public ExecutionResponseDto(Long id, Long runbookId, String runbookName, ExecutionStatus status, Integer currentStepNumber, LocalDateTime startedAt, LocalDateTime completedAt, String createdBy) {
        this.id = id;
        this.runbookId = runbookId;
        this.runbookName = runbookName;
        this.status = status;
        this.currentStepNumber = currentStepNumber;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRunbookId() {
        return runbookId;
    }

    public void setRunbookId(Long runbookId) {
        this.runbookId = runbookId;
    }

    public String getRunbookName() {
        return runbookName;
    }

    public void setRunbookName(String runbookName) {
        this.runbookName = runbookName;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public Integer getCurrentStepNumber() {
        return currentStepNumber;
    }

    public void setCurrentStepNumber(Integer currentStepNumber) {
        this.currentStepNumber = currentStepNumber;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
