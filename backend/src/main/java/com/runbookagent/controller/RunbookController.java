package com.runbookagent.controller;

import com.runbookagent.dto.RunbookDto;
import com.runbookagent.dto.RunbookResponseDto;
import com.runbookagent.entity.RunbookEntity;
import com.runbookagent.service.RunbookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/runbooks")
@CrossOrigin(origins = "*")
public class RunbookController {

    private final RunbookService runbookService;

    public RunbookController(RunbookService runbookService) {
        this.runbookService = runbookService;
    }

    @PostMapping("/upload")
    public ResponseEntity<RunbookResponseDto> uploadRunbook(@RequestParam("file") MultipartFile file) throws IOException {
        RunbookResponseDto dto = runbookService.uploadRunbook(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/create")
    public ResponseEntity<RunbookResponseDto> createRunbook(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String fileName = payload.getOrDefault("fileName", "custom-runbook.md");
        String content = payload.get("content");

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Runbook content cannot be empty");
        }

        RunbookEntity entity = runbookService.saveRunbook(name, fileName, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(runbookService.getRunbookById(entity.getId()));
    }

    @GetMapping
    public ResponseEntity<List<RunbookResponseDto>> getAllRunbooks() {
        return ResponseEntity.ok(runbookService.getAllRunbooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RunbookResponseDto> getRunbookById(@PathVariable Long id) {
        return ResponseEntity.ok(runbookService.getRunbookById(id));
    }

    @GetMapping("/{id}/parsed")
    public ResponseEntity<RunbookDto> getParsedRunbook(@PathVariable Long id) {
        return ResponseEntity.ok(runbookService.getParsedRunbook(id));
    }
}
