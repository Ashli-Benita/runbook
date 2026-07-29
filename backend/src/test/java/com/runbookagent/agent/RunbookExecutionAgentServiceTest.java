package com.runbookagent.agent;

import com.runbookagent.entity.*;
import com.runbookagent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RunbookExecutionAgentServiceTest {

    @Autowired
    private RunbookRepository runbookRepository;

    @Autowired
    private RunbookExecutionRepository executionRepository;

    @Autowired
    private StepExecutionRepository stepExecutionRepository;

    @Autowired
    private ApprovalRequestRepository approvalRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private RunbookExecutionAgentService agentService;

    private RunbookEntity serverHealthRunbook;
    private RunbookEntity appRecoveryRunbook;
    private RunbookEntity failureRunbook;

    @BeforeEach
    void setUp() {
        serverHealthRunbook = runbookRepository.save(new RunbookEntity(
                "Server Health Check",
                "Routine diagnostics",
                "server-health.md",
                """
                # Server Health Check Runbook

                ## Steps
                1. Check current date and system time.
                2. Check system uptime.
                3. Check disk usage across primary volumes.
                4. Check available system memory.
                5. Generate server health summary report.
                """
        ));

        appRecoveryRunbook = runbookRepository.save(new RunbookEntity(
                "Application Recovery",
                "Recover application service",
                "application-recovery.md",
                """
                # Application Recovery Runbook

                ## Steps
                1. Check target application status.
                2. Check disk usage.
                3. Check system memory usage.
                4. Restart the application service.
                5. Verify target application status post-restart.
                """
        ));

        failureRunbook = runbookRepository.save(new RunbookEntity(
                "Failure Simulation",
                "Simulated diagnostic failure",
                "failure-simulation.md",
                """
                # Failure Simulation Runbook

                ## Steps
                1. Check current system memory.
                2. Simulate failure for non-existent database.
                3. Verify application status.
                """
        ));
    }

    @Test
    void execute_ServerHealthRunbook_CompletesAllSafeStepsAutomatically() {
        RunbookExecutionEntity execution = agentService.startExecution(serverHealthRunbook.getId());

        assertNotNull(execution);
        assertEquals(ExecutionStatus.COMPLETED, execution.getStatus());

        List<StepExecutionEntity> steps = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(execution.getId());
        assertEquals(5, steps.size());
        assertTrue(steps.stream().allMatch(s -> s.getStatus() == StepStatus.COMPLETED));
    }

    @Test
    void execute_ApplicationRecovery_PausesOnRiskyStepAndResumesOnApproval() {
        // 1. Initial Start -> Should pause on Step 4 (RESTART_APPLICATION)
        RunbookExecutionEntity execution = agentService.startExecution(appRecoveryRunbook.getId());

        assertEquals(ExecutionStatus.WAITING_FOR_APPROVAL, execution.getStatus());

        List<StepExecutionEntity> steps = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(execution.getId());
        assertEquals(5, steps.size());
        assertEquals(StepStatus.COMPLETED, steps.get(0).getStatus());
        assertEquals(StepStatus.COMPLETED, steps.get(1).getStatus());
        assertEquals(StepStatus.COMPLETED, steps.get(2).getStatus());
        assertEquals(StepStatus.WAITING_FOR_APPROVAL, steps.get(3).getStatus());
        assertEquals(StepStatus.PENDING, steps.get(4).getStatus());

        // Verify Pending Approval Entity
        List<ApprovalRequestEntity> approvals = approvalRepository.findByExecutionId(execution.getId());
        assertEquals(1, approvals.size());
        assertEquals(ApprovalStatus.PENDING, approvals.get(0).getStatus());

        // 2. Approve Step -> Execution should complete
        RunbookExecutionEntity resumedExecution = agentService.approveStep(execution.getId());
        assertEquals(ExecutionStatus.COMPLETED, resumedExecution.getStatus());

        List<StepExecutionEntity> updatedSteps = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(execution.getId());
        assertEquals(StepStatus.COMPLETED, updatedSteps.get(3).getStatus());
        assertEquals(StepStatus.COMPLETED, updatedSteps.get(4).getStatus());
    }

    @Test
    void execute_ApplicationRecovery_StopsOnRejection() {
        RunbookExecutionEntity execution = agentService.startExecution(appRecoveryRunbook.getId());
        assertEquals(ExecutionStatus.WAITING_FOR_APPROVAL, execution.getStatus());

        RunbookExecutionEntity rejectedExecution = agentService.rejectStep(execution.getId(), "STOP");
        assertEquals(ExecutionStatus.REJECTED, rejectedExecution.getStatus());
    }

    @Test
    void execute_FailureSimulation_HandlesFailureAndSkipOption() {
        RunbookExecutionEntity execution = agentService.startExecution(failureRunbook.getId());
        assertEquals(ExecutionStatus.FAILED, execution.getStatus());

        List<StepExecutionEntity> steps = stepExecutionRepository.findByExecutionIdOrderByStepNumberAsc(execution.getId());
        assertEquals(StepStatus.FAILED, steps.get(1).getStatus());

        // Skip failed step and resume
        RunbookExecutionEntity resumed = agentService.skipStep(execution.getId(), steps.get(1).getId());
        assertEquals(ExecutionStatus.COMPLETED, resumed.getStatus());
    }
}
