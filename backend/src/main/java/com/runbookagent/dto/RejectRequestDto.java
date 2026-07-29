package com.runbookagent.dto;

public class RejectRequestDto {
    private String userChoice; // "STOP" or "SKIP"

    public RejectRequestDto() {
    }

    public RejectRequestDto(String userChoice) {
        this.userChoice = userChoice;
    }

    public String getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }
}
