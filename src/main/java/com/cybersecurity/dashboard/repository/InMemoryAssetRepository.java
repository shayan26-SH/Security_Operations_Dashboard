package com.cybersecurity.dashboard.repository;

import com.cybersecurity.dashboard.model.SecurityAsset;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public class InMemoryAssetRepository implements AssetRepository {
    private final List<SecurityAsset> assets = List.of(
            new SecurityAsset("AST-001", "Identity Gateway", "Identity", "Production", 82, true, "Current", "Platform"),
            new SecurityAsset("AST-002", "Design Workstation", "Endpoint", "Corporate", 67, false, "Needs review", "Design"),
            new SecurityAsset("AST-003", "Customer API", "Application", "Production", 54, true, "Current", "Engineering"),
            new SecurityAsset("AST-004", "Archive Storage", "Storage", "Production", 91, true, "Needs review", "Data"),
            new SecurityAsset("AST-005", "Analytics Database", "Database", "Production", 46, false, "Pending patch", "Data")
    );

    @Override
    public List<SecurityAsset> findAll() {
        return assets.stream()
                .sorted(Comparator.comparingInt(SecurityAsset::riskScore).reversed())
                .toList();
    }
}
