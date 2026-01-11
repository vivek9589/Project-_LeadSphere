package com.braininventory.leadsphere.JWT_Auth_Service.service.impl;

import com.braininventory.leadsphere.JWT_Auth_Service.dto.LoginResponse;
import com.braininventory.leadsphere.JWT_Auth_Service.dto.UpdatePasswordRequest;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.AuthRequest;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.LoginVO;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.ResetToken;
import com.braininventory.leadsphere.JWT_Auth_Service.exception.AuthException;
import com.braininventory.leadsphere.JWT_Auth_Service.feign.NotificationClient;
import com.braininventory.leadsphere.JWT_Auth_Service.feign.UserClient;
import com.braininventory.leadsphere.JWT_Auth_Service.repository.ResetTokenRepository;
import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final ResetTokenRepository tokenRepo;
    private final UserClient userClient;
    private final NotificationClient notificationClient;



    public AuthServiceImpl(ResetTokenRepository tokenRepo,
                           UserClient userClient,
                           NotificationClient notificationClient) {
        this.tokenRepo = tokenRepo;
        this.userClient = userClient;
        this.notificationClient = notificationClient;
    }


    @Override
    public void forgotPassword(String email) {
        LoginVO loginVO;

        // 1. Check if the email exists in the User Service
        try {
            loginVO = userClient.findByEmail(email);
        } catch (FeignException.NotFound e) {
            // Requirement: Explicitly notify if email is not found
            throw new AuthException("Email does not exist", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("User service communication error for email: {}", email, e);
            throw new AuthException("Unable to verify user at this time. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 2. Validate the returned data
        if (loginVO == null || loginVO.getId() == null) {
            log.error("User service returned empty data for email: {}", email);
            throw new AuthException("Account data is incomplete. Please contact support.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. Create and persist the reset token
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = ResetToken.builder()
                .userId(loginVO.getId())
                .token(token)
                .expiry(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        try {
            tokenRepo.save(resetToken);
        } catch (Exception e) {
            log.error("Database error while saving reset token", e);
            throw new AuthException("Failed to initiate password reset. Try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 4. Send the notification
        try {
            notificationClient.sendForgotPasswordEmail(email, token);
        } catch (Exception e) {
            log.error("Email notification failed for email: {}", email, e);
            throw new AuthException("Failed to send reset email. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    @Override
    public void resetPassword(String token, String newPassword) {
        // 1. Check if token exists
        ResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new AuthException("The reset link is invalid or has already been used.", HttpStatus.UNAUTHORIZED));

        // 2. Check if token is already used
        if (resetToken.isUsed()) {
            throw new AuthException("This reset link has already been used. Please request a new one.", HttpStatus.GONE);
        }

        // 3. Check if token is expired
        if (resetToken.getExpiry().isBefore(LocalDateTime.now())) {
            throw new AuthException("This reset link has expired. Password reset links are valid for 30 minutes.", HttpStatus.GONE);
        }

        try {
            // 4. Update password in User Service via Feign
            userClient.updatePassword(resetToken.getUserId(), new UpdatePasswordRequest(newPassword));

            // 5. Mark token as used only after successful password update
            resetToken.setUsed(true);
            tokenRepo.save(resetToken);
        } catch (Exception e) {
            throw new AuthException("Unable to update password at this time. Internal service error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}