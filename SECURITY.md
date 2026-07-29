# Security Specification & Verification - AI Runbook-Following Agent

## Security Model & Principles

### 1. No Arbitrary Shell Execution
- The agent NEVER executes raw strings returned by LLMs or inputted via REST APIs.
- Execution is strictly bounded to `ActionType` Enums (`CHECK_DATE`, `CHECK_UPTIME`, `CHECK_DISK_USAGE`, `CHECK_MEMORY`, `CHECK_APPLICATION_STATUS`, `RESTART_APPLICATION`, `STOP_APPLICATION`, `START_APPLICATION`, `VERIFY_APPLICATION`, `GENERATE_REPORT`).

### 2. Risk Classification Matrix
- **SAFE Actions**: Read-only diagnostic queries (`CHECK_DATE`, `CHECK_UPTIME`, `CHECK_DISK_USAGE`, `CHECK_MEMORY`, `CHECK_APPLICATION_STATUS`, `VERIFY_APPLICATION`, `GENERATE_REPORT`). Automatically executed via MCP tool executor.
- **HIGH Risk Actions**: Service modifications (`RESTART_APPLICATION`, `STOP_APPLICATION`, `START_APPLICATION`). Require explicit human operator approval.

### 3. MCP Pre-Execution Authorization Check
- `McpToolExecutor` queries the database for an `APPROVED` status record before calling any OS process builder.
- Unapproved or rejected actions are blocked at the MCP boundary.

### 4. Input Validation & Exception Handling
- Malformed Markdown, unknown actions, and unauthorized commands throw `SecurityException` and return HTTP 403 Forbidden.
