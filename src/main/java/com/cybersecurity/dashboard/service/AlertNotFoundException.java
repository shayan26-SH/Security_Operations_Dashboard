package com.cybersecurity.dashboard.service;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String id) {
        super("Alert " + id + " was not found.");
    }
}
