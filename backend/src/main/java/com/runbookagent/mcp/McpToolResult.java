package com.runbookagent.mcp;

public class McpToolResult {

    public enum Status {
        SUCCESS,
        FAILURE,
        APPROVAL_REQUIRED,
        REJECTED,
        UNSUPPORTED_PLATFORM
    }

    private Status status;
    private String message;
    private String output;
    private long durationMs;

    public McpToolResult() {
    }

    public McpToolResult(Status status, String message, String output, long durationMs) {
        this.status = status;
        this.message = message;
        this.output = output;
        this.durationMs = durationMs;
    }

    public static McpToolResult success(String output, long durationMs) {
        return new McpToolResult(Status.SUCCESS, "Command executed successfully", output, durationMs);
    }

    public static McpToolResult failure(String errorMsg, String output, long durationMs) {
        return new McpToolResult(Status.FAILURE, errorMsg, output, durationMs);
    }

    public static McpToolResult approvalRequired(String actionName) {
        return new McpToolResult(Status.APPROVAL_REQUIRED, "Human approval required for action: " + actionName, null, 0);
    }

    public static McpToolResult rejected(String reason) {
        return new McpToolResult(Status.REJECTED, "Action rejected: " + reason, null, 0);
    }

    public static McpToolResult unsupportedPlatform(String osName) {
        return new McpToolResult(Status.UNSUPPORTED_PLATFORM, "Unsupported operating system: " + osName, null, 0);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }
}
