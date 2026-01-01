package com.braininventory.leadsphere.lead_service.exception;

public class LeadCreationException extends RuntimeException {
    public LeadCreationException(String message) {
        super(message);
    }

    public LeadCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}