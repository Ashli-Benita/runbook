package com.runbookagent.dto;

import com.runbookagent.entity.StepStatus;
import com.runbookagent.security.ActionType;
import com.runbookagent.security.RiskLevel;
import java.time.LocalDateTime;

public class StepExecutionResponseDto {
    private Long id;
    private Long executionId;
    private Integer stepNumber;
    private String description;
    private ActionType action;
    private RiskLevel riskLevel;
    private StepStatus status;
    private String output;
    private String error;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public StepExecutionResponseDto() {
    }

    public StepExecutionResponseDto(Long id, Long executionId, Integer stepNumber, String description, ActionType action, RiskLevel riskLevel, StepStatus status, String output, String error, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.executionId = executionId;
        this.stepNumber = stepNumber;
        this.description = description;
        this.action = action;
        this.riskLevel = riskLevel;
        this.status = status;
        this.output = output;
        this.error = error;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
}
