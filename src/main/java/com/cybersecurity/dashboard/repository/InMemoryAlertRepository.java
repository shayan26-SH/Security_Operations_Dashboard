package com.cybersecurity.dashboard.repository;

import com.cybersecurity.dashboard.model.AlertSeverity;
import com.cybersecurity.dashboard.model.AlertStatus;
import com.cybersecurity.dashboard.model.SecurityAlert;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAlertRepository implements AlertRepository {
    private final Map<String, SecurityAlert> alerts = new ConcurrentHashMap<>();

    public InMemoryAlertRepository() {
        var now = Instant.now();
        seed(alert("ALT-1042", AlertSeverity.CRITICAL, AlertStatus.OPEN,
                "Repeated privileged login failures", "Multiple failed administrator sign-ins were recorded.",
                "Identity monitor", "AST-001", now.minus(8, ChronoUnit.MINUTES)));
        seed(alert("ALT-1041", AlertSeverity.HIGH, AlertStatus.OPEN,
                "Public storage policy detected", "A storage resource permits anonymous read access.",
                "Cloud posture", "AST-004", now.minus(24, ChronoUnit.MINUTES)));
        seed(alert("ALT-1040", AlertSeverity.HIGH, AlertStatus.ACKNOWLEDGED,
                "Endpoint protection is out of date", "The workstation signature package is behind policy.",
                "Endpoint monitor", "AST-002", now.minus(2, ChronoUnit.HOURS)));
        seed(alert("ALT-1039", AlertSeverity.MEDIUM, AlertStatus.OPEN,
                "Unusual outbound data volume", "Outbound transfer volume exceeded the asset baseline.",
                "Network monitor", "AST-003", now.minus(4, ChronoUnit.HOURS)));
        seed(alert("ALT-1038", AlertSeverity.MEDIUM, AlertStatus.OPEN,
                "Patch compliance below target", "A managed host is missing a recommended security update.",
                "Vulnerability management", "AST-005", now.minus(6, ChronoUnit.HOURS)));
        seed(alert("ALT-1037", AlertSeverity.LOW, AlertStatus.RESOLVED,
                "New service account created", "A new service identity was created through the approved workflow.",
                "Identity monitor", "AST-001", now.minus(1, ChronoUnit.DAYS)));
        seed(alert("ALT-1036", AlertSeverity.LOW, AlertStatus.OPEN,
                "Certificate renewal approaching", "A service certificate expires within the next 30 days.",
                "Certificate monitor", "AST-003", now.minus(1, ChronoUnit.DAYS)));
        seed(alert("ALT-1035", AlertSeverity.CRITICAL, AlertStatus.ACKNOWLEDGED,
                "Endpoint isolation requested", "An endpoint detection rule requested analyst review.",
                "Endpoint monitor", "AST-002", now.minus(2, ChronoUnit.DAYS)));
        seed(alert("ALT-1034", AlertSeverity.MEDIUM, AlertStatus.OPEN,
                "Database access outside normal hours", "A database sign-in occurred outside its usual time window.",
                "Database audit", "AST-005", now.minus(3, ChronoUnit.DAYS)));
        seed(alert("ALT-1033", AlertSeverity.HIGH, AlertStatus.RESOLVED,
                "Suspicious file quarantined", "The endpoint agent quarantined a file for analyst review.",
                "Endpoint monitor", "AST-002", now.minus(4, ChronoUnit.DAYS)));
    }

    @Override
    public List<SecurityAlert> findAll() {
        return sorted(alerts.values().stream().toList());
    }

    @Override
    public Optional<SecurityAlert> findById(String id) {
        return Optional.ofNullable(alerts.get(id));
    }

    @Override
    public List<SecurityAlert> find(AlertSeverity severity, AlertStatus status, String search) {
        var normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return sorted(alerts.values().stream()
                .filter(alert -> severity == null || alert.severity() == severity)
                .filter(alert -> status == null || alert.status() == status)
                .filter(alert -> normalizedSearch.isEmpty()
                        || alert.title().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || alert.description().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || alert.assetId().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .toList());
    }

    @Override
    public SecurityAlert save(SecurityAlert alert) {
        alerts.put(alert.id(), alert);
        return alert;
    }

    private void seed(SecurityAlert alert) {
        alerts.put(alert.id(), alert);
    }

    private SecurityAlert alert(
            String id,
            AlertSeverity severity,
            AlertStatus status,
            String title,
            String description,
            String source,
            String assetId,
            Instant detectedAt
    ) {
        return new SecurityAlert(id, severity, status, title, description, source, assetId, detectedAt);
    }

    private List<SecurityAlert> sorted(List<SecurityAlert> results) {
        return results.stream()
                .sorted(Comparator.comparing(SecurityAlert::detectedAt).reversed())
                .toList();
    }
}
