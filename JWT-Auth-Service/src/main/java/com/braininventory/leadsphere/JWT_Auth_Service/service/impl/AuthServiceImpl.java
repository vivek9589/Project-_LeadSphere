package com.braininventory.leadsphere.JWT_Auth_Service.service.impl;

import com.braininventory.leadsphere.JWT_Auth_Service.dto.LoginResponse;
import com.braininventory.leadsphere.JWT_Auth_Service.dto.UpdatePasswordRequest;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.AuthRequest;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.LoginVO;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.ResetToken;
import com.braininventory.leadsphere.JWT_Auth_Service.exception.AuthException;
import com.braininventory.leadsphere.JWT_Auth_Service.feign.NotificationClient;
import com.braininventory.leadsphere.JWT_Auth_Service.feign.UserClient;
import com.braininventory.leadsphere.JWT_Auth_Service.jwt.JwtTokenService;
import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthService;
import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthUserDetailsService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final UserClient userClient;
    private final NotificationClient notificationClient;
    private final JwtTokenService jwtService;
    private final AuthenticationManager authManager;




    public AuthServiceImpl(
            UserClient userClient,
            NotificationClient notificationClient,
            JwtTokenService jwtService,
            AuthenticationManager authManager) {
        this.userClient = userClient;
        this.notificationClient = notificationClient;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }


    @Override
    public LoginResponse login(AuthRequest req) {
        log.info("Login attempt for email: {}", req.getEmail());

        // 1. Check if User exists
        LoginVO userVO;
        try {
            userVO = userClient.findByEmail(req.getEmail());
        } catch (FeignException.NotFound e) {
            log.warn("Email not found: {}", req.getEmail());
            throw new AuthException("Email does not exist", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("User service unavailable during login for email: {}", req.getEmail(), e);
            throw new AuthException("User service is currently unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (userVO == null) {
            log.warn("User service returned null for email: {}", req.getEmail());
            throw new AuthException("Email does not exist", HttpStatus.NOT_FOUND);
        }

        // 2. Authenticate password
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // 3. Generate JWT token
            String token = jwtService.generateToken(auth);
            AuthUserDetailsService.AuthUserPrincipal principal =
                    (AuthUserDetailsService.AuthUserPrincipal) auth.getPrincipal();

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");

            LoginResponse response = new LoginResponse(
                    token,
                    new LoginResponse.UserDto(principal.getId(), auth.getName(), role)
            );

            log.info("Login successful for email: {}", req.getEmail());
            return response;

        } catch (BadCredentialsException e) {
            log.warn("Invalid password for email: {}", req.getEmail());
            throw new BadCredentialsException("Password is wrong");
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", req.getEmail(), e);
            throw new AuthException("Password is wrong", HttpStatus.UNAUTHORIZED);
        }
    }



    @Override
    public void forgotPassword(String email) {
        LoginVO loginVO;

        // 1. Check if the email exists in the User Service
        try {
            loginVO = userClient.findByEmail(email);
        } catch (FeignException.NotFound e) {
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
            //tokenRepo.save(resetToken);
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
}