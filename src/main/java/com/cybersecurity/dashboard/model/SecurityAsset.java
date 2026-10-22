package com.cybersecurity.dashboard.model;

public record SecurityAsset(
        String id,
        String name,
        String type,
        String environment,
        int riskScore,
        boolean internetExposed,
        String patchStatus,
        String owner
) {
}
