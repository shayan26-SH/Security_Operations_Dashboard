package com.cybersecurity.dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.security.username=test-analyst",
        "app.security.password=test-password-for-integration-tests"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/dashboard").accept("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acknowledgeRequiresCsrfToken() throws Exception {
        mockMvc.perform(post("/api/alerts/ALT-1039/acknowledge")
                        .with(httpBasic("test-analyst", "test-password-for-integration-tests")))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedRequestWithCsrfCanAcknowledgeAlert() throws Exception {
        mockMvc.perform(post("/api/alerts/ALT-1039/acknowledge")
                        .with(httpBasic("test-analyst", "test-password-for-integration-tests"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
