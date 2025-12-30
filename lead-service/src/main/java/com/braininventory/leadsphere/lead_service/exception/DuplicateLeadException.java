package com.braininventory.leadsphere.lead_service.exception;

public class DuplicateLeadException extends RuntimeException {
    public DuplicateLeadException(String message) {
        super(message);
    }
}
