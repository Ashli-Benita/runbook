# AI Runbook-Following Agent Architecture

## Overview
An enterprise AI agent system that parses Markdown runbooks, plans step execution via Ollama (`llama3.2`), and executes allowlisted commands through a custom Model Context Protocol (MCP) tool layer with mandatory human approval for high-risk actions.

## Core Layers
1. **Frontend**: React Dashboard with step execution timeline, approval modal, and execution reports.
2. **REST API**: Spring Boot controllers exposing endpoints for runbook upload, execution control, and audit trail.
3. **Agent State Engine**: State machine tracking `CREATED`, `PLANNING`, `EXECUTING`, `WAITING_FOR_APPROVAL`, `VERIFYING`, `COMPLETED`, `FAILED`, `REJECTED`, `CANCELLED`.
4. **Security & MCP Layer**: Maps actions to strict `ActionType` Enums, classifies risk, verifies database approvals, and executes fixed safe OS commands.
5. **Persistence**: PostgreSQL / H2 recording runbooks, executions, step statuses, human approvals, and audit logs.
