package com.runbookagent.dto;

import java.time.LocalDateTime;

public class RunbookResponseDto {
    private Long id;
    private String name;
    private String description;
    private String fileName;
    private String content;
    private LocalDateTime createdAt;

    public RunbookResponseDto() {
    }

    public RunbookResponseDto(Long id, String name, String description, String fileName, String content, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fileName = fileName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
