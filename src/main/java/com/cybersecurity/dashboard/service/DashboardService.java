package com.cybersecurity.dashboard.service;

import com.cybersecurity.dashboard.model.AlertSeverity;
import com.cybersecurity.dashboard.model.AlertStatus;
import com.cybersecurity.dashboard.model.DashboardSummary;
import com.cybersecurity.dashboard.model.SecurityAlert;
import com.cybersecurity.dashboard.model.SecurityAsset;
import com.cybersecurity.dashboard.repository.AlertRepository;
import com.cybersecurity.dashboard.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final AlertRepository alertRepository;
    private final AssetRepository assetRepository;

    public DashboardService(AlertRepository alertRepository, AssetRepository assetRepository) {
        this.alertRepository = alertRepository;
        this.assetRepository = assetRepository;
    }

    public DashboardSummary summary() {
        var alerts = alertRepository.findAll();
        var assets = assetRepository.findAll();
        var counts = Arrays.stream(AlertSeverity.values())
                .collect(Collectors.toMap(
                        severity -> severity,
                        severity -> alerts.stream().filter(alert -> alert.severity() == severity).count()
                ));
        var openCount = alerts.stream().filter(alert -> alert.status() == AlertStatus.OPEN).count();
        var criticalOpenCount = alerts.stream()
                .filter(alert -> alert.status() == AlertStatus.OPEN && alert.severity() == AlertSeverity.CRITICAL)
                .count();
        var exposedCount = assets.stream().filter(SecurityAsset::internetExposed).count();
        var averageRisk = assets.stream().mapToInt(SecurityAsset::riskScore).average().orElse(0);

        return new DashboardSummary(
                openCount,
                criticalOpenCount,
                assets.size(),
                exposedCount,
                Math.round(averageRisk * 10.0) / 10.0,
                Map.copyOf(counts)
        );
    }

    public java.util.List<SecurityAlert> alerts(
            AlertSeverity severity,
            AlertStatus status,
            String search
    ) {
        return alertRepository.find(severity, status, search);
    }

    public java.util.List<SecurityAsset> assets() {
        return assetRepository.findAll();
    }

    public SecurityAlert acknowledge(String id) {
        var alert = alertRepository.findById(id).orElseThrow(() -> new AlertNotFoundException(id));
        if (alert.status() != AlertStatus.OPEN) {
            throw new InvalidAlertStateException(id);
        }
        return alertRepository.save(alert.withStatus(AlertStatus.ACKNOWLEDGED));
    }
}
