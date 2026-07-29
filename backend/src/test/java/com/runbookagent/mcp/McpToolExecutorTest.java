package com.runbookagent.mcp;

import com.runbookagent.security.ActionRegistry;
import com.runbookagent.security.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McpToolExecutorTest {

    private McpToolExecutor mcpToolExecutor;

    @BeforeEach
    void setUp() {
        ActionRegistry registry = new ActionRegistry();
        OsCommandExecutor osExecutor = new OsCommandExecutor();
        mcpToolExecutor = new McpToolExecutor(registry, osExecutor);
    }

    @Test
    void executeTool_SafeAction_ReturnsSuccess() {
        McpToolResult result = mcpToolExecutor.executeTool(ActionType.CHECK_DATE, false);
        assertNotNull(result);
        assertEquals(McpToolResult.Status.SUCCESS, result.getStatus());
        assertNotNull(result.getOutput());
        assertFalse(result.getOutput().isBlank());
    }

    @Test
    void executeTool_HighRiskActionWithoutApproval_ReturnsApprovalRequired() {
        McpToolResult result = mcpToolExecutor.executeTool(ActionType.RESTART_APPLICATION, false);
        assertNotNull(result);
        assertEquals(McpToolResult.Status.APPROVAL_REQUIRED, result.getStatus());
        assertTrue(result.getMessage().contains("Human approval required"));
    }

    @Test
    void executeTool_HighRiskActionWithApproval_ExecutesSuccessfully() {
        McpToolResult result = mcpToolExecutor.executeTool(ActionType.RESTART_APPLICATION, true);
        assertNotNull(result);
        assertEquals(McpToolResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getOutput().contains("Restart Completed"));
    }

    @Test
    void executeTool_SimulatedFailureAction_ReturnsFailureStatus() {
        McpToolResult result = mcpToolExecutor.executeTool(ActionType.SIMULATED_FAILURE, true);
        assertNotNull(result);
        assertEquals(McpToolResult.Status.FAILURE, result.getStatus());
        assertTrue(result.getMessage().contains("Simulated Diagnostic Failure"));
    }

    @Test
    void executeTool_UnknownAction_ThrowsSecurityException() {
        assertThrows(SecurityException.class, () -> mcpToolExecutor.executeTool("EXECUTE_ARBITRARY_COMMAND", false));
    }
}
