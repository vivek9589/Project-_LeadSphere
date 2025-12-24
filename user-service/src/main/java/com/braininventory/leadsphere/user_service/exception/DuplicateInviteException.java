package com.braininventory.leadsphere.user_service.exception;

// Throw this if an invite for this email already exists
public class DuplicateInviteException extends BaseException {
    public DuplicateInviteException(String email) {
        super(400, "Already Exists: " + email);
    }
}