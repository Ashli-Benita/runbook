package com.runbookagent.security;

public enum ActionType {
    CHECK_DATE,
    CHECK_UPTIME,
    CHECK_DISK_USAGE,
    CHECK_MEMORY,
    CHECK_PORT,
    CHECK_APPLICATION_STATUS,
    RESTART_APPLICATION,
    STOP_APPLICATION,
    START_APPLICATION,
    VERIFY_APPLICATION,
    GENERATE_REPORT,
    SIMULATED_FAILURE;

    public static ActionType fromString(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Action name cannot be empty");
        }
        try {
            return ActionType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Security violation: Action '" + name + "' is not in the allowlist!");
        }
    }
}
