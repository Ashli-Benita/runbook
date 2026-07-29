# AI Runbook-Following Agent

> Production-style, secure, hackathon-ready SRE automation system powered by Java 25, Spring Boot 3.4.2, Spring AI Ollama (`llama3.2`), Model Context Protocol (MCP) execution control, PostgreSQL/H2 persistence, and React dashboard.

---

## 1. Problem Statement
At 2 AM, an on-call DevOps/SRE engineer receives an incident alert and must manually open a Markdown runbook, read numbered instructions, run commands one-by-one, check results, handle errors, and restart or recover services.

The **AI Runbook-Following Agent** automates runbook execution safely by:
1. Parsing Markdown runbook steps.
2. Using local LLMs (Ollama `llama3.2`) to understand steps and generate structured plans.
3. Classifying action risks (`SAFE` vs `HIGH`).
4. Automatically executing safe diagnostic steps through controlled MCP tools.
5. Pausing at high-risk steps to request explicit human approval.
6. Executing approved actions through controlled MCP wrappers (zero arbitrary shell execution).
7. Verifying service operational status post-execution.
8. Generating comprehensive execution reports and audit trails.

---

## 2. Security Architecture

```
User → React Dashboard → REST API → Agent Engine → Risk Classifier → MCP Tool Executor → OS Command Map
```

- **Zero Unrestricted Shell Execution**: Arbitrary command strings from LLM output or user input are rejected.
- **Strict Allowlist Enums**: Actions are locked to `CHECK_DATE`, `CHECK_UPTIME`, `CHECK_DISK_USAGE`, `CHECK_MEMORY`, `CHECK_APPLICATION_STATUS`, `RESTART_APPLICATION`, `STOP_APPLICATION`, `START_APPLICATION`, `VERIFY_APPLICATION`, `GENERATE_REPORT`.
- **Human-in-the-Loop Approval**: High-risk actions require explicit DB-logged approval.
- **MCP Authorization**: `McpToolExecutor` checks database approval state independently before executing any platform command.

---

## 3. Technology Stack

- **Backend**: Java 25 / 21, Spring Boot 3.4.2, Spring Data JPA, Spring AI Ollama
- **Database**: PostgreSQL / H2 in-memory mode
- **AI Engine**: Local Ollama (`llama3.2`)
- **Frontend**: React 18, Vite 6, Lucide Icons, Vanilla CSS Dark Theme
- **Build & Containers**: Maven 3.9, npm 11, Docker, Docker Compose

---

## 4. How to Run

### Prerequisites
1. Installed Java 21/25 (`$env:JAVA_HOME="C:\Program Files\Java\jdk-25"`)
2. Installed Maven 3.9+ and Node.js 20+
3. Running Ollama locally (`ollama serve` and `ollama pull llama3.2`)

### 1. Start Backend
```powershell
cd backend
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
mvn spring-boot:run
```
*Backend runs on `http://localhost:8080`.*

### 2. Start Frontend
```powershell
cd frontend
npm run dev
```
*Frontend runs on `http://localhost:5173`.*

---

## 5. Hackathon Demo Workflow

1. Open `http://localhost:5173`.
2. Select or upload `application-recovery.md`.
3. Click **Execute**. Watch safe steps (Check status, Check disk, Check memory) run automatically.
4. When Step 4 (`RESTART_APPLICATION`) is reached, observe the **Human Approval Required** modal popup.
5. Click **Approve & Execute**. Controlled MCP tool executes service restart.
6. Step 5 verifies application status and finishes with status `COMPLETED`.
7. Click **View Final Execution Report** to view summary, duration, and audit logs.
