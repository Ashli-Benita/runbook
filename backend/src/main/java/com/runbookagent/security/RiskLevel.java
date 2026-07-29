package com.runbookagent.security;

public enum RiskLevel {
    SAFE,
    LOW,
    MEDIUM,
    HIGH;

    public boolean requiresApproval() {
        return this == HIGH;
    }
}
