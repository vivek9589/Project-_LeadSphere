package com.braininventory.leadsphere.user_service.exception;

public class InviteNotFoundException extends BaseException {
    public InviteNotFoundException(String token) {
        super(404, String.format("Invite with token [%s] was not found.", token));
    }
}