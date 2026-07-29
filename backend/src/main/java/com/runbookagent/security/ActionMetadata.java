package com.runbookagent.security;

public class ActionMetadata {
    private final ActionType actionType;
    private final RiskLevel riskLevel;
    private final String description;

    public ActionMetadata(ActionType actionType, RiskLevel riskLevel, String description) {
        this.actionType = actionType;
        this.riskLevel = riskLevel;
        this.description = description;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequiresApproval() {
        return riskLevel.requiresApproval();
    }
}
