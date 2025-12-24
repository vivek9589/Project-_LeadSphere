package com.braininventory.leadsphere.user_service.exception;



public abstract class BaseException extends RuntimeException {
    private final int status;

    public BaseException(int status, String message) {
        super(message);
        this.status = status;
    }
    public int getStatus() { return status; }
}