package com.cybersecurity.dashboard.service;

public class InvalidAlertStateException extends RuntimeException {
    public InvalidAlertStateException(String id) {
        super("Alert " + id + " is not open and cannot be acknowledged.");
    }
}
