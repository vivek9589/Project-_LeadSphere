package com.braininventory.leadsphere.user_service.exception;



public class DuplicateInviteException extends BaseException {
    public DuplicateInviteException(String email) {
        // Changed from 400 to 409
        super(409, "Already Exists: " + email);
    }
}