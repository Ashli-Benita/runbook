package com.runbookagent.parser;

import com.runbookagent.dto.RunbookDto;
import com.runbookagent.dto.RunbookStepDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownRunbookParserTest {

    private MarkdownRunbookParser parser;

    @BeforeEach
    void setUp() {
        parser = new MarkdownRunbookParser();
    }

    @Test
    void parse_ServerHealthRunbook_Success() {
        String markdown = """
                # Server Health Check Runbook

                ## Objective
                Perform routine system diagnostics to verify server health and status.

                ## Steps
                1. Check current date and system time.
                2. Check system uptime.
                3. Check disk usage across primary volumes.
                4. Check available system memory.
                5. Generate server health summary report.
                """;

        RunbookDto runbook = parser.parse(markdown);

        assertNotNull(runbook);
        assertEquals("Server Health Check Runbook", runbook.getTitle());
        assertTrue(runbook.getDescription().contains("routine system diagnostics"));
        assertEquals(5, runbook.getSteps().size());

        RunbookStepDto step1 = runbook.getSteps().get(0);
        assertEquals(1, step1.getStepNumber());
        assertEquals("Check current date and system time.", step1.getDescription());

        RunbookStepDto step4 = runbook.getSteps().get(3);
        assertEquals(4, step4.getStepNumber());
        assertEquals("Check available system memory.", step4.getDescription());
    }

    @Test
    void parse_ApplicationRecoveryRunbook_Success() {
        String markdown = """
                # Application Recovery Runbook

                ## Steps
                1. Check target application status.
                2. Check disk usage.
                3. Check system memory usage.
                4. Restart the application service.
                5. Verify target application status post-restart.
                """;

        RunbookDto runbook = parser.parse(markdown);

        assertNotNull(runbook);
        assertEquals("Application Recovery Runbook", runbook.getTitle());
        assertEquals(5, runbook.getSteps().size());
        assertEquals("Restart the application service.", runbook.getSteps().get(3).getDescription());
    }

    @Test
    void parse_EmptyMarkdown_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
    }
}
