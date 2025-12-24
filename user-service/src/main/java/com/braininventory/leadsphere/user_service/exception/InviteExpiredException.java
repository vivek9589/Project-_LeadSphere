package com.braininventory.leadsphere.user_service.exception;

public class InviteExpiredException extends BaseException {
    public InviteExpiredException(String email) {
        super(400, "Invitation for " + email + " has expired. Please request a new link.");
    }
}