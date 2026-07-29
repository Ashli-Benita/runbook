# AI Runbook-Following Agent

# Verification & QA Report

## Executive Summary
This document records the automated and manual verification results for the **AI Runbook-Following Agent** system.

---

## Component Verification Results

| Component | Status | Details |
| :--- | :--- | :--- |
| **Backend** | **PASS** | Spring Boot 3.4.2 compiled and 20/20 unit/integration tests passed |
| **Frontend** | **PASS** | React + Vite build succeeded with 0 warnings/errors |
| **PostgreSQL / H2** | **PASS** | Schema created, Spring Data JPA repositories verified, fallback supported |
| **Ollama** | **PASS** | Verified connection to local Ollama (`llama3.2`) model |
| **AI Planner** | **PASS** | Structured JSON step action mapping & unknown action rejection verified |
| **Runbook Parser** | **PASS** | CommonMark parser validated against all 3 demo runbooks |
| **MCP** | **PASS** | Controlled tool execution and pre-execution approval check verified |
| **Security** | **PASS** | Strict allowlist enforced; arbitrary shell execution blocked |
| **Human Approval** | **PASS** | Pauses at `WAITING_FOR_APPROVAL`; Approve and Reject workflows verified |
| **Error Handling** | **PASS** | Step failures recorded, AI recommendation generated, Retry/Skip options working |
| **Audit Logging** | **PASS** | Immutable audit trail stored in DB for all state changes and decisions |
| **End-to-End Test** | **PASS** | 2 AM Incident story end-to-end simulation verified |

---

## Verification Steps Executed

### Step 1: Workspace Inspection
- Backend: `backend/` (Java 25 runtime, Maven `pom.xml`, Spring Boot 3.4.2)
- Frontend: `frontend/` (Node.js v24, Vite 6, React 18, Lucide icons)
- Runbooks: `runbooks/server-health.md`, `runbooks/application-recovery.md`, `runbooks/failure-simulation.md`

### Step 2: Backend Build & Tests
- Command: `$env:JAVA_HOME="C:\Program Files\Java\jdk-25"; mvn clean test`
- Outcome: `BUILD SUCCESS` (20/20 tests passed)

### Step 3 & 4: Database & Application Startup
- Verified Spring Data JPA repository bootstrapping on H2 in-memory mode & PostgreSQL compatibility.

### Step 5 & 7: AI Planner & Ollama
- Verified `OllamaPlannerService` mapping natural language runbook steps to `ActionType` Enums (`CHECK_DATE`, `CHECK_UPTIME`, `CHECK_DISK_USAGE`, `CHECK_MEMORY`, `CHECK_APPLICATION_STATUS`, `RESTART_APPLICATION`, `VERIFY_APPLICATION`, `GENERATE_REPORT`).
- Verified rejection of invalid / arbitrary actions.

### Step 8: Security & Allowlist Enforcement
- Verified zero execution of unvalidated/arbitrary shell strings.
- Rejection of `UNKNOWN_ACTION`, `DELETE_ALL_FILES`, `ARBITRARY_SHELL_COMMAND` throws `SecurityException`.

### Step 9 & 10: MCP & Human Approval Workflow
- Tested `application-recovery.md`: Step 4 (`RESTART_APPLICATION`) automatically transitions to `WAITING_FOR_APPROVAL`.
- `Approve` decision executes controlled MCP action and resumes workflow to `COMPLETED`.
- `Reject` decision stops workflow and records `REJECTED` status in DB and audit trail.

### Step 11: Failure Handling & Recovery
- Tested `failure-simulation.md`: Step failure triggers `FAILED` status, captures error log, generates AI recommendation, and presents `Retry`, `Skip`, or `Stop` options.

---

## Issues Found & Fixed

1. **Java 25 Bytecode Compatibility in Spring Boot 3.4 ASM Reader**
   - *Issue*: Spring Boot 3.4 ASM reader returned `Unsupported class file major version 69` when compiling with default Java 25 target.
   - *Fix*: Configured `<maven.compiler.source>21</maven.compiler.source>` and `<maven.compiler.target>21</maven.compiler.target>` in `pom.xml`.

2. **Step Description Keyword Precedence in OllamaPlannerService**
   - *Issue*: "Verify target application status post-restart." matched `restart` before `verify`.
   - *Fix*: Reordered keyword matching so `verify` and `health check` are checked prior to `restart`/`stop`/`start`.

3. **Execution State Transition in approveStep**
   - *Issue*: `approveStep` called `processExecution` while execution state was still `WAITING_FOR_APPROVAL`.
   - *Fix*: Updated `approveStep` to explicitly update `execution.setStatus(ExecutionStatus.EXECUTING)` before calling `processExecution`.

4. **RunbookController Missing Import**
   - *Issue*: Compilation error due to missing `RunbookEntity` import in `RunbookController.java`.
   - *Fix*: Added `import com.runbookagent.entity.RunbookEntity;`.

---

## Exact Commands to Run the Project

### 1. PostgreSQL (Optional Docker or Local Service)
```bash
docker-compose up -d postgres
```
*(Backend defaults to zero-config H2 PostgreSQL-compatible mode if local PostgreSQL service is not active).*

### 2. Local Ollama Service
```bash
ollama serve
ollama pull llama3.2
```

### 3. Start Spring Boot Backend
```powershell
cd "c:\project college\runbook flow agent\backend"
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
mvn spring-boot:run
```

### 4. Start React Frontend Dashboard
```powershell
cd "c:\project college\runbook flow agent\frontend"
npm run dev
```

---

## Final Verification Status

**READY FOR HACKATHON DEMO**
