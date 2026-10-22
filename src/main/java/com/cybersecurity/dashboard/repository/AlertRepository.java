package com.cybersecurity.dashboard.repository;

import com.cybersecurity.dashboard.model.AlertSeverity;
import com.cybersecurity.dashboard.model.AlertStatus;
import com.cybersecurity.dashboard.model.SecurityAlert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    List<SecurityAlert> findAll();

    Optional<SecurityAlert> findById(String id);

    List<SecurityAlert> find(AlertSeverity severity, AlertStatus status, String search);

    SecurityAlert save(SecurityAlert alert);
}
