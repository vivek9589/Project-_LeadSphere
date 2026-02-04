package com.braininventory.leadsphere.JWT_Auth_Service.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
