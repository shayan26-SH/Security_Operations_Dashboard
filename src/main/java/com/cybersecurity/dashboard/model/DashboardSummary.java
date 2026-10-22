package com.cybersecurity.dashboard.model;

import java.util.Map;

public record DashboardSummary(
        long openAlerts,
        long criticalAlerts,
        long monitoredAssets,
        long exposedAssets,
        double averageRiskScore,
        Map<AlertSeverity, Long> alertsBySeverity
) {
}
