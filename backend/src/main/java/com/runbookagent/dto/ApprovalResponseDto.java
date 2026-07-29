package com.runbookagent.dto;

import com.runbookagent.entity.ApprovalStatus;
import com.runbookagent.security.ActionType;
import com.runbookagent.security.RiskLevel;

import java.time.LocalDateTime;

public class ApprovalResponseDto {
    private Long id;
    private Long executionId;
    private Long stepExecutionId;
    private ActionType action;
    private String reason;
    private RiskLevel riskLevel;
    private ApprovalStatus status;
    private LocalDateTime requestedAt;

    public ApprovalResponseDto() {
    }

    public ApprovalResponseDto(Long id, Long executionId, Long stepExecutionId, ActionType action, String reason, RiskLevel riskLevel, ApprovalStatus status, LocalDateTime requestedAt) {
        this.id = id;
        this.executionId = executionId;
        this.stepExecutionId = stepExecutionId;
        this.action = action;
        this.reason = reason;
        this.riskLevel = riskLevel;
        this.status = status;
        this.requestedAt = requestedAt;
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

    public Long getStepExecutionId() {
        return stepExecutionId;
    }

    public void setStepExecutionId(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
