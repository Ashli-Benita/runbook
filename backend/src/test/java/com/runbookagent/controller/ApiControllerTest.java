package com.runbookagent.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRunbooks_ReturnsSeededDemoRunbooks() throws Exception {
        mockMvc.perform(get("/api/runbooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Server Health Check"))
                .andExpect(jsonPath("$[1].name").value("Application Recovery"));
    }

    @Test
    void startExecution_CreatesExecutionAndSteps() throws Exception {
        mockMvc.perform(post("/api/executions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"runbookId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void startExecution_ApplicationRecovery_TriggersPendingApproval() throws Exception {
        mockMvc.perform(post("/api/executions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"runbookId\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_FOR_APPROVAL"));
    }
}
