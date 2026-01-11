package com.braininventory.leadsphere.JWT_Auth_Service.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
