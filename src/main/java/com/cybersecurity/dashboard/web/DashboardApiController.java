package com.cybersecurity.dashboard.web;

import com.cybersecurity.dashboard.model.AlertSeverity;
import com.cybersecurity.dashboard.model.AlertStatus;
import com.cybersecurity.dashboard.model.DashboardSummary;
import com.cybersecurity.dashboard.model.SecurityAlert;
import com.cybersecurity.dashboard.model.SecurityAsset;
import com.cybersecurity.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardApiController {
    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public DashboardSummary summary() {
        return dashboardService.summary();
    }

    @GetMapping("/alerts")
    public List<SecurityAlert> alerts(
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) String search
    ) {
        return dashboardService.alerts(severity, status, search);
    }

    @PostMapping("/alerts/{id}/acknowledge")
    public SecurityAlert acknowledge(@PathVariable String id) {
        return dashboardService.acknowledge(id);
    }

    @GetMapping("/assets")
    public List<SecurityAsset> assets() {
        return dashboardService.assets();
    }
}
