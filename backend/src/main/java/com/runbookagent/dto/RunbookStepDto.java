package com.runbookagent.dto;

public class RunbookStepDto {
    private Integer stepNumber;
    private String description;
    private String expectedResult;
    private String sectionName;

    public RunbookStepDto() {
    }

    public RunbookStepDto(Integer stepNumber, String description, String expectedResult, String sectionName) {
        this.stepNumber = stepNumber;
        this.description = description;
        this.expectedResult = expectedResult;
        this.sectionName = sectionName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public static class Builder {
        private Integer stepNumber;
        private String description;
        private String expectedResult;
        private String sectionName;

        public Builder stepNumber(Integer stepNumber) {
            this.stepNumber = stepNumber;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder expectedResult(String expectedResult) {
            this.expectedResult = expectedResult;
            return this;
        }

        public Builder sectionName(String sectionName) {
            this.sectionName = sectionName;
            return this;
        }

        public RunbookStepDto build() {
            return new RunbookStepDto(stepNumber, description, expectedResult, sectionName);
        }
    }
}
