package com.runbookagent.dto;

import java.util.ArrayList;
import java.util.List;

public class RunbookDto {
    private String title;
    private String description;
    private List<RunbookStepDto> steps = new ArrayList<>();

    public RunbookDto() {
    }

    public RunbookDto(String title, String description, List<RunbookStepDto> steps) {
        this.title = title;
        this.description = description;
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RunbookStepDto> getSteps() {
        return steps;
    }

    public void setSteps(List<RunbookStepDto> steps) {
        this.steps = steps;
    }

    public static class Builder {
        private String title;
        private String description;
        private List<RunbookStepDto> steps = new ArrayList<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder steps(List<RunbookStepDto> steps) {
            this.steps = steps;
            return this;
        }

        public RunbookDto build() {
            return new RunbookDto(title, description, steps);
        }
    }
}
