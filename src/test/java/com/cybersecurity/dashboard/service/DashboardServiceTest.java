package com.cybersecurity.dashboard.service;

import com.cybersecurity.dashboard.model.AlertSeverity;
import com.cybersecurity.dashboard.model.AlertStatus;
import com.cybersecurity.dashboard.repository.InMemoryAlertRepository;
import com.cybersecurity.dashboard.repository.InMemoryAssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashboardServiceTest {
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(new InMemoryAlertRepository(), new InMemoryAssetRepository());
    }

    @Test
    void summaryCountsOpenAlertsAndExposedAssets() {
        var summary = dashboardService.summary();

        assertThat(summary.openAlerts()).isEqualTo(6);
        assertThat(summary.criticalAlerts()).isEqualTo(1);
        assertThat(summary.monitoredAssets()).isEqualTo(5);
        assertThat(summary.exposedAssets()).isEqualTo(3);
        assertThat(summary.alertsBySeverity()).containsEntry(AlertSeverity.CRITICAL, 2L);
    }

    @Test
    void filtersAlertsBySeverityStatusAndSearch() {
        var alerts = dashboardService.alerts(AlertSeverity.HIGH, AlertStatus.OPEN, "storage");

        assertThat(alerts).extracting("id").containsExactly("ALT-1041");
    }

    @Test
    void acknowledgesAnOpenAlert() {
        var acknowledged = dashboardService.acknowledge("ALT-1042");

        assertThat(acknowledged.status()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    void rejectsAcknowledgingAnAlertTwice() {
        dashboardService.acknowledge("ALT-1042");

        assertThatThrownBy(() -> dashboardService.acknowledge("ALT-1042"))
                .isInstanceOf(InvalidAlertStateException.class);
    }

    @Test
    void reportsMissingAlert() {
        assertThatThrownBy(() -> dashboardService.acknowledge("ALT-9999"))
                .isInstanceOf(AlertNotFoundException.class);
    }
}
