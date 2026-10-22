package com.cybersecurity.dashboard.web;

import com.cybersecurity.dashboard.model.AlertSeverity;
import com.cybersecurity.dashboard.repository.InMemoryAlertRepository;
import com.cybersecurity.dashboard.repository.InMemoryAssetRepository;
import com.cybersecurity.dashboard.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardApiControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var service = new DashboardService(new InMemoryAlertRepository(), new InMemoryAssetRepository());
        mockMvc = MockMvcBuilders.standaloneSetup(new DashboardApiController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void dashboardReturnsSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openAlerts").value(6))
                .andExpect(jsonPath("$.alertsBySeverity.CRITICAL").value(2));
    }

    @Test
    void alertEndpointAcceptsFilters() throws Exception {
        mockMvc.perform(get("/api/alerts")
                        .param("severity", AlertSeverity.CRITICAL.name())
                        .param("search", "login")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ALT-1042"));
    }

    @Test
    void missingAlertReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/alerts/ALT-9999/acknowledge"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Alert ALT-9999 was not found."));
    }
}
