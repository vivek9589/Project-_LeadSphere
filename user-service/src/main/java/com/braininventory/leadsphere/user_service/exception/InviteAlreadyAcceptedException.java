package com.braininventory.leadsphere.user_service.exception;



public class InviteAlreadyAcceptedException extends BaseException {
    public InviteAlreadyAcceptedException() {
        super(400, "This invitation has already been accepted and cannot be used again.");
    }
}