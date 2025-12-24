package com.braininventory.leadsphere.user_service.exception;



// Throw this if the email service fails
public class EmailDeliveryException extends BaseException {
    public EmailDeliveryException(String email) {
        super(500, "Failed to send invitation email to: " + email);
    }
}