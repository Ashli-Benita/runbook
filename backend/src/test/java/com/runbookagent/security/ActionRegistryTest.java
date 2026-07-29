package com.runbookagent.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionRegistryTest {

    private ActionRegistry actionRegistry;

    @BeforeEach
    void setUp() {
        actionRegistry = new ActionRegistry();
    }

    @Test
    void getMetadata_SafeAction_ReturnsSafeAndNoApprovalRequired() {
        ActionMetadata metadata = actionRegistry.getMetadata(ActionType.CHECK_DISK_USAGE);
        assertNotNull(metadata);
        assertEquals(RiskLevel.SAFE, metadata.getRiskLevel());
        assertFalse(metadata.isRequiresApproval());
    }

    @Test
    void getMetadata_HighRiskAction_ReturnsHighAndRequiresApproval() {
        ActionMetadata metadata = actionRegistry.getMetadata(ActionType.RESTART_APPLICATION);
        assertNotNull(metadata);
        assertEquals(RiskLevel.HIGH, metadata.getRiskLevel());
        assertTrue(metadata.isRequiresApproval());
    }

    @Test
    void validateAndGet_UnknownAction_ThrowsSecurityException() {
        assertThrows(SecurityException.class, () -> actionRegistry.validateAndGet("RM_-RF_/"));
        assertThrows(SecurityException.class, () -> actionRegistry.validateAndGet("EXECUTE_ARBITRARY_SHELL"));
    }

    @Test
    void isAllowlisted_ChecksValidityCorrectly() {
        assertTrue(actionRegistry.isAllowlisted("CHECK_MEMORY"));
        assertTrue(actionRegistry.isAllowlisted("restart_application"));
        assertFalse(actionRegistry.isAllowlisted("sudo_reboot"));
    }
}
