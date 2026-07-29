package com.runbookagent.service;

import com.runbookagent.dto.RunbookDto;
import com.runbookagent.dto.RunbookResponseDto;
import com.runbookagent.entity.RunbookEntity;
import com.runbookagent.parser.MarkdownRunbookParser;
import com.runbookagent.repository.RunbookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RunbookService {

    private final RunbookRepository runbookRepository;
    private final MarkdownRunbookParser markdownParser;

    public RunbookService(RunbookRepository runbookRepository, MarkdownRunbookParser markdownParser) {
        this.runbookRepository = runbookRepository;
        this.markdownParser = markdownParser;
    }

    @PostConstruct
    public void seedDemoRunbooks() {
        if (runbookRepository.count() == 0) {
            saveRunbook(
                    "Server Health Check",
                    "server-health.md",
                    """
                    # Server Health Check Runbook

                    ## Objective
                    Perform routine system diagnostics to verify server health and status.

                    ## Steps
                    1. Check current date and system time.
                    2. Check system uptime.
                    3. Check disk usage across primary volumes.
                    4. Check available system memory.
                    5. Generate server health summary report.
                    """
            );

            saveRunbook(
                    "Application Recovery",
                    "application-recovery.md",
                    """
                    # Application Recovery Runbook

                    ## Objective
                    Recover an unavailable application at 2 AM.

                    ## Steps
                    1. Check target application status.
                    2. Check disk usage.
                    3. Check system memory usage.
                    4. Restart the application service.
                    5. Verify target application status post-restart.
                    """
            );

            saveRunbook(
                    "Failure Simulation",
                    "failure-simulation.md",
                    """
                    # Failure Simulation Runbook

                    ## Objective
                    Simulate a controlled diagnostic failure.

                    ## Steps
                    1. Check current system memory.
                    2. Simulate failure for non-existent database.
                    3. Verify application status.
                    """
            );
        }
    }

    public RunbookResponseDto uploadRunbook(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "uploaded-runbook.md";

        RunbookDto parsed = markdownParser.parse(content);
        RunbookEntity entity = saveRunbook(parsed.getTitle(), fileName, content);
        return mapToDto(entity);
    }

    public RunbookEntity saveRunbook(String name, String fileName, String content) {
        RunbookDto parsed = markdownParser.parse(content);
        RunbookEntity entity = new RunbookEntity(
                name != null ? name : parsed.getTitle(),
                parsed.getDescription(),
                fileName,
                content
        );
        return runbookRepository.save(entity);
    }

    public List<RunbookResponseDto> getAllRunbooks() {
        return runbookRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public RunbookResponseDto getRunbookById(Long id) {
        RunbookEntity entity = runbookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Runbook not found with ID: " + id));
        return mapToDto(entity);
    }

    public RunbookDto getParsedRunbook(Long id) {
        RunbookEntity entity = runbookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Runbook not found with ID: " + id));
        return markdownParser.parse(entity.getContent());
    }

    private RunbookResponseDto mapToDto(RunbookEntity entity) {
        return new RunbookResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getFileName(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
