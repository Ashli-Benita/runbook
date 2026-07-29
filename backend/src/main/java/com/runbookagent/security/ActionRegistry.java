package com.runbookagent.security;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Service
public class ActionRegistry {

    private final Map<ActionType, ActionMetadata> registry;

    public ActionRegistry() {
        Map<ActionType, ActionMetadata> map = new EnumMap<>(ActionType.class);

        // Safe Actions
        register(map, ActionType.CHECK_DATE, RiskLevel.SAFE, "Query system date and time");
        register(map, ActionType.CHECK_UPTIME, RiskLevel.SAFE, "Query system uptime");
        register(map, ActionType.CHECK_DISK_USAGE, RiskLevel.SAFE, "Query disk space usage");
        register(map, ActionType.CHECK_MEMORY, RiskLevel.SAFE, "Query memory usage");
        register(map, ActionType.CHECK_PORT, RiskLevel.SAFE, "Query network port status");
        register(map, ActionType.CHECK_APPLICATION_STATUS, RiskLevel.SAFE, "Query application process status");
        register(map, ActionType.VERIFY_APPLICATION, RiskLevel.SAFE, "Verify application health endpoint");
        register(map, ActionType.GENERATE_REPORT, RiskLevel.SAFE, "Generate execution summary report");
        register(map, ActionType.SIMULATED_FAILURE, RiskLevel.SAFE, "Simulate controlled diagnostic failure");

        // High Risk Actions - Require explicit human approval
        register(map, ActionType.RESTART_APPLICATION, RiskLevel.HIGH, "Restart application service");
        register(map, ActionType.STOP_APPLICATION, RiskLevel.HIGH, "Stop application service");
        register(map, ActionType.START_APPLICATION, RiskLevel.HIGH, "Start application service");

        this.registry = Collections.unmodifiableMap(map);
    }

    private void register(Map<ActionType, ActionMetadata> map, ActionType action, RiskLevel risk, String desc) {
        map.put(action, new ActionMetadata(action, risk, desc));
    }

    public ActionMetadata getMetadata(ActionType actionType) {
        if (actionType == null) {
            throw new IllegalArgumentException("ActionType cannot be null");
        }
        ActionMetadata metadata = registry.get(actionType);
        if (metadata == null) {
            throw new SecurityException("ActionType '" + actionType + "' is not registered in the security allowlist");
        }
        return metadata;
    }

    public ActionMetadata validateAndGet(String actionName) {
        ActionType type = ActionType.fromString(actionName);
        return getMetadata(type);
    }

    public boolean isAllowlisted(String actionName) {
        try {
            ActionType type = ActionType.valueOf(actionName.trim().toUpperCase());
            return registry.containsKey(type);
        } catch (Exception e) {
            return false;
        }
    }

    public Set<ActionType> getAllowedActions() {
        return registry.keySet();
    }
}
