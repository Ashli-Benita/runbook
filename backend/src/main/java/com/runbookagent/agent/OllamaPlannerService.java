package com.runbookagent.agent;

import com.runbookagent.dto.RunbookStepDto;
import com.runbookagent.security.ActionMetadata;
import com.runbookagent.security.ActionRegistry;
import com.runbookagent.security.ActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class OllamaPlannerService {

    private static final Logger log = LoggerFactory.getLogger(OllamaPlannerService.class);

    private final ActionRegistry actionRegistry;
    private final ChatModel chatModel;

    @Autowired
    public OllamaPlannerService(ActionRegistry actionRegistry, @Autowired(required = false) ChatModel chatModel) {
        this.actionRegistry = actionRegistry;
        this.chatModel = chatModel;
    }

    public ActionMetadata planStepAction(RunbookStepDto step) {
        String desc = step.getDescription().toLowerCase(Locale.ROOT);

        // 1. Direct Rule-based / Keyword Security Mapping
        ActionType mappedType = mapKeywordToAction(desc);

        // 2. If AI ChatModel is available and keyword mapping was ambiguous, query Ollama LLM
        if (mappedType == null && chatModel != null) {
            try {
                String prompt = String.format(
                        "You are an SRE agent. Map this runbook step description to EXACTLY ONE of these action names: %s. Description: '%s'. Return ONLY the action name.",
                        actionRegistry.getAllowedActions().toString(),
                        step.getDescription()
                );
                String llmResponse = chatModel.call(prompt).trim().toUpperCase();
                if (actionRegistry.isAllowlisted(llmResponse)) {
                    mappedType = ActionType.valueOf(llmResponse);
                }
            } catch (Exception e) {
                log.warn("Ollama LLM call failed or unavailable, defaulting to safe keyword mapping: {}", e.getMessage());
            }
        }

        // Fallback default safe action if no match
        if (mappedType == null) {
            mappedType = ActionType.CHECK_APPLICATION_STATUS;
        }

        return actionRegistry.getMetadata(mappedType);
    }

    private ActionType mapKeywordToAction(String desc) {
        if (desc.contains("verify") || desc.contains("health check")) {
            return ActionType.VERIFY_APPLICATION;
        } else if (desc.contains("date") || desc.contains("time")) {
            return ActionType.CHECK_DATE;
        } else if (desc.contains("uptime")) {
            return ActionType.CHECK_UPTIME;
        } else if (desc.contains("disk") || desc.contains("space") || desc.contains("volume")) {
            return ActionType.CHECK_DISK_USAGE;
        } else if (desc.contains("memory") || desc.contains("ram")) {
            return ActionType.CHECK_MEMORY;
        } else if (desc.contains("port") || desc.contains("socket")) {
            return ActionType.CHECK_PORT;
        } else if (desc.contains("restart") || desc.contains("reboot service")) {
            return ActionType.RESTART_APPLICATION;
        } else if (desc.contains("stop application") || desc.contains("shutdown service")) {
            return ActionType.STOP_APPLICATION;
        } else if (desc.contains("start application") || desc.contains("launch service")) {
            return ActionType.START_APPLICATION;
        } else if (desc.contains("report") || desc.contains("summary")) {
            return ActionType.GENERATE_REPORT;
        } else if (desc.contains("non-existent") || desc.contains("simulate failure") || desc.contains("simulated")) {
            return ActionType.SIMULATED_FAILURE;
        } else if (desc.contains("status") || desc.contains("application") || desc.contains("service")) {
            return ActionType.CHECK_APPLICATION_STATUS;
        }
        return null;
    }

    public String generateFailureRecommendation(String stepDescription, String errorMessage) {
        if (chatModel != null) {
            try {
                String prompt = String.format(
                        "Step '%s' failed with error: '%s'. Provide a 1-sentence safe SRE recommendation.",
                        stepDescription, errorMessage
                );
                return chatModel.call(prompt).trim();
            } catch (Exception e) {
                log.warn("Ollama failure analysis call skipped: {}", e.getMessage());
            }
        }
        return "Possible cause: Target service endpoint unavailable or port unreachable. Recommended action: Check network connectivity or retry step after verification.";
    }
}
