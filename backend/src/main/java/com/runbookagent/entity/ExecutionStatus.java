package com.runbookagent.entity;

public enum ExecutionStatus {
    CREATED,
    PLANNING,
    PLAN_READY,
    EXECUTING,
    WAITING_FOR_APPROVAL,
    VERIFYING,
    COMPLETED,
    FAILED,
    REJECTED,
    CANCELLED
}
