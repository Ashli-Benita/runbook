# System Architecture - AI Runbook-Following Agent

## Architecture Overview

```
+-----------------------------------------------------------------------+
|                            REACT DASHBOARD                            |
|  - Dashboard Stats  - Runbook Upload  - Timeline  - Human Approval    |
+-----------------------------------+-----------------------------------+
                                    | REST API
+-----------------------------------v-----------------------------------+
|                          SPRING BOOT BACKEND                          |
|                                                                       |
|  +------------------------+          +-----------------------------+  |
|  | Markdown Parser        |          | Ollama Chat Model           |  |
|  | (CommonMark)           |          | (llama3.2)                  |  |
|  +-----------+------------+          +--------------+--------------+  |
|              |                                      |                 |
|              v                                      v                 |
|  +-----------------------------------------------------------------+  |
|  | Agent Execution Engine                                          |  |
|  | State: CREATED -> PLANNING -> EXECUTING -> APPROVAL -> COMPLETED  |  |
|  +--------------------------------+--------------------------------+  |
|                                   |                                   |
|                                   v                                   |
|  +-----------------------------------------------------------------+  |
|  | Security & Risk Classifier Layer                                |  |
|  | Enforces ActionType Enums (SAFE vs HIGH)                        |  |
|  +--------------------------------+--------------------------------+  |
|                                   |                                   |
|                                   v                                   |
|  +-----------------------------------------------------------------+  |
|  | MCP Tool Executor Layer                                         |  |
|  | Pre-execution Approval Verification & Fixed Platform Commands     |  |
|  +--------------------------------+--------------------------------+  |
+-----------------------------------|-----------------------------------+
                                    |
                    +---------------+---------------+
                    |                               |
                    v                               v
          +-------------------+           +-------------------+
          | PostgreSQL / H2   |           | Host Operating    |
          | (Executions &     |           | System Execution  |
          | Audit Trail)      |           | (Allowlisted)     |
          +-------------------+           +-------------------+
```

## Workflow Lifecycle States

1. `CREATED`: Execution entity generated from selected runbook.
2. `PLANNING`: Runbook steps parsed and mapped to structured `ActionType` Enums by AI planner.
3. `PLAN_READY`: Structured execution plan validated against security registry.
4. `EXECUTING`: Sequential processing of safe allowlisted steps.
5. `WAITING_FOR_APPROVAL`: Paused at a high-risk action pending human confirmation.
6. `VERIFYING`: Post-execution health check verification.
7. `COMPLETED`: Incident resolved successfully.
8. `FAILED` / `REJECTED` / `CANCELLED`: Execution stopped due to failure, human rejection, or user cancellation.
