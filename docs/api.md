# REST API Documentation

## Runbooks API
- `POST /api/runbooks/upload` - Upload a Markdown runbook file (.md)
- `GET /api/runbooks` - List all uploaded runbooks
- `GET /api/runbooks/{id}` - Get runbook details and parsed steps

## Executions API
- `POST /api/executions` - Start a runbook execution (`runbookId`)
- `GET /api/executions` - List all executions
- `GET /api/executions/{id}` - Get execution details and overall status
- `GET /api/executions/{id}/steps` - Get step-by-step execution details
- `POST /api/executions/{id}/approve` - Approve pending risky action step
- `POST /api/executions/{id}/reject` - Reject pending risky action step (options: STOP or SKIP)
- `POST /api/executions/{id}/cancel` - Cancel execution
- `POST /api/executions/{id}/steps/{stepId}/retry` - Retry failed execution step
- `POST /api/executions/{id}/steps/{stepId}/skip` - Skip failed execution step
- `GET /api/executions/{id}/report` - Generate and retrieve final execution summary report

## Audit API
- `GET /api/audit/execution/{executionId}` - Retrieve complete audit trail logs for an execution
