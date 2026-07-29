# REST API Documentation - AI Runbook-Following Agent

## Endpoints Summary

### Runbooks
- `POST /api/runbooks/upload` - Upload Markdown runbook file
- `POST /api/runbooks/create` - Create runbook from raw Markdown text
- `GET /api/runbooks` - List all available runbooks
- `GET /api/runbooks/{id}` - Get runbook metadata
- `GET /api/runbooks/{id}/parsed` - Get parsed steps and structured plan

### Executions
- `POST /api/executions` - Start runbook execution (`{ "runbookId": 1 }`)
- `GET /api/executions` - List all executions
- `GET /api/executions/{id}` - Get execution details
- `GET /api/executions/{id}/steps` - List step executions
- `GET /api/executions/pending-approvals` - List pending human approval requests
- `POST /api/executions/{id}/approve` - Approve pending risky action
- `POST /api/executions/{id}/reject` - Reject pending risky action (`{ "userChoice": "STOP" | "SKIP" }`)
- `POST /api/executions/{id}/cancel` - Cancel execution
- `POST /api/executions/{id}/steps/{stepId}/retry` - Retry failed step
- `POST /api/executions/{id}/steps/{stepId}/skip` - Skip failed step
- `GET /api/executions/{id}/report` - Generate final execution summary report

### Audit Logs
- `GET /api/audit/execution/{executionId}` - Audit trail for execution
- `GET /api/audit/recent` - Top 50 recent audit events
