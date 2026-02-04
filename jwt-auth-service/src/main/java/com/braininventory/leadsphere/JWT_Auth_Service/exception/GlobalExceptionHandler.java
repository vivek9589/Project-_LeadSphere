package com.braininventory.leadsphere.JWT_Auth_Service.exception;

import com.braininventory.leadsphere.JWT_Auth_Service.dto.StandardResponse;
import jakarta.servlet.http.HttpServletRequest;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle CUSTOM Business Exceptions (Token expired, user not found, etc.)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<StandardResponse<Object>> handleCustomAuthException(AuthException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                StandardResponse.error(ex.getMessage(), "AUTH_ERROR", request.getRequestURI()),
                ex.getStatus()
        );
    }

    // 2. MERGED AUTHENTICATION HANDLER
    // Handles login failures, wrong passwords, and service downtime during auth
    @ExceptionHandler({
            AuthenticationException.class,
            BadCredentialsException.class,
            InternalAuthenticationServiceException.class
    })
    public ResponseEntity<StandardResponse<Object>> handleAuthenticationFailures(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        // This now picks up "Password is wrong" from the catch block above
        String message = ex.getMessage();
        String errorCode = "INVALID_CREDENTIALS";

        if (ex instanceof InternalAuthenticationServiceException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "User Service is down";
            errorCode = "SERVICE_UNAVAILABLE";
        }

        return new ResponseEntity<>(
                StandardResponse.error(message, errorCode, request.getRequestURI()),
                status
        );
    }
    // 3. Handle Permission Errors (Logged in but no rights)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse<Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                StandardResponse.error("You do not have permission to access this resource.", "FORBIDDEN_ACCESS", request.getRequestURI()),
                HttpStatus.FORBIDDEN
        );
    }

    // 4. Handle Validation Errors (Bad email format, empty fields, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(
                StandardResponse.error("Input validation failed", errors, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 5. Handle Request/JSON Malformation
    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<StandardResponse<Object>> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = "Malformed request or missing parameters.";
        if (ex instanceof MissingServletRequestParameterException m) {
            message = "Required parameter is missing: " + m.getParameterName();
        }

        return new ResponseEntity<>(
                StandardResponse.error(message, "BAD_REQUEST", request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 6. Handle Microservice/Feign Communication Errors
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<StandardResponse<Object>> handleFeignException(FeignException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.status()) != null ? HttpStatus.resolve(ex.status()) : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(
                StandardResponse.error("Internal service communication failed.", "MICROSERVICE_ERROR", request.getRequestURI()),
                status
        );
    }

    // 7. Handle Database Constraint Violations (Duplicate emails, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                StandardResponse.error("The data provided conflicts with existing records.", "DATA_CONFLICT", request.getRequestURI()),
                HttpStatus.CONFLICT
        );
    }

    // 8. FINAL SAFETY NET (Catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Object>> handleGeneralException(Exception ex, HttpServletRequest request) {
        // Log the actual exception locally for your debugging
        ex.printStackTrace();

        return new ResponseEntity<>(
                StandardResponse.error("An unexpected internal error occurred.", "INTERNAL_SERVER_ERROR", request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}