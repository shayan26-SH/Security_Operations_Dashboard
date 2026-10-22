package com.cybersecurity.dashboard.model;

import java.time.Instant;

public record SecurityAlert(
        String id,
        AlertSeverity severity,
        AlertStatus status,
        String title,
        String description,
        String source,
        String assetId,
        Instant detectedAt
) {
    public SecurityAlert withStatus(AlertStatus newStatus) {
        return new SecurityAlert(id, severity, newStatus, title, description, source, assetId, detectedAt);
    }
}
