package com.runbookagent.mcp;

import com.runbookagent.security.ActionMetadata;
import com.runbookagent.security.ActionRegistry;
import com.runbookagent.security.ActionType;
import org.springframework.stereotype.Service;

@Service
public class McpToolExecutor {

    private final ActionRegistry actionRegistry;
    private final OsCommandExecutor osCommandExecutor;

    public McpToolExecutor(ActionRegistry actionRegistry, OsCommandExecutor osCommandExecutor) {
        this.actionRegistry = actionRegistry;
        this.osCommandExecutor = osCommandExecutor;
    }

    public McpToolResult executeTool(ActionType actionType, boolean isApproved) {
        if (actionType == null) {
            throw new IllegalArgumentException("ActionType cannot be null");
        }

        // 1. Independent Security Check against Allowlist Registry
        ActionMetadata metadata = actionRegistry.getMetadata(actionType);

        // 2. Risk & Approval Verification
        if (metadata.isRequiresApproval() && !isApproved) {
            return McpToolResult.approvalRequired(actionType.name());
        }

        // 3. Execution of Predefined Implementation
        return osCommandExecutor.executeAction(actionType);
    }

    public McpToolResult executeTool(String actionName, boolean isApproved) {
        ActionMetadata metadata = actionRegistry.validateAndGet(actionName);
        return executeTool(metadata.getActionType(), isApproved);
    }
}
