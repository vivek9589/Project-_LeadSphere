package com.braininventory.leadsphere.user_service.controller;

import com.braininventory.leadsphere.user_service.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final MailService mailService;

    public NotificationController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> sendForgotPasswordEmail(@RequestParam String email,
                                                          @RequestParam String token) {
        mailService.sendForgetPasswordEmail(email, token);
        return ResponseEntity.ok("Email sent");
    }
}