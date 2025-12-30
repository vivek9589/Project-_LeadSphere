package com.braininventory.leadsphere.lead_service.exception;

public class ExistByEmailAndCompany extends RuntimeException {
    public ExistByEmailAndCompany(String message) {
        super(message);
    }
}
