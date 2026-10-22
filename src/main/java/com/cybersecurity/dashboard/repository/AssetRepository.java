package com.cybersecurity.dashboard.repository;

import com.cybersecurity.dashboard.model.SecurityAsset;

import java.util.List;

public interface AssetRepository {
    List<SecurityAsset> findAll();
}
