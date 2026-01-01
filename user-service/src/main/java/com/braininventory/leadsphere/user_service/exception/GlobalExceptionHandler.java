package com.braininventory.leadsphere.user_service.exception;

import com.braininventory.leadsphere.user_service.dto.StandardResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400 Bad Request → Custom invalid request
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<StandardResponse<Object>> handleInvalidRequest(
            InvalidRequestException ex,
            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                StandardResponse.error("Invalid request", ex.getMessage(), request.getRequestURI())
        );
    }

    // 400 Bad Request → Duplicate invite (business logic)
    @ExceptionHandler(DuplicateInviteException.class)
    public ResponseEntity<StandardResponse<Object>> handleDuplicate(
            DuplicateInviteException ex,
            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                StandardResponse.error("Duplicate invite", ex.getMessage(), request.getRequestURI())
        );
    }

    // 400 Bad Request → Handler method validation errors
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<StandardResponse<Object>> handleHandlerValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request) {
        String errorMessage = ex.getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(
                StandardResponse.error("Validation error", errorMessage, request.getRequestURI())
        );
    }

    // 400 Bad Request → Missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<StandardResponse<Object>> handleMissingParams(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                StandardResponse.error("Missing parameter", ex.getParameterName() + " is required", request.getRequestURI())
        );
    }

    // 400 Bad Request → Validation errors on DTO fields
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                StandardResponse.error("Validation failed", errors, request.getRequestURI())
        );
    }

    // 404 Not Found → Resource missing
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Object>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                StandardResponse.error("Resource not found", ex.getMessage(), request.getRequestURI())
        );
    }

    // 409 Conflict → Business rule violation
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<StandardResponse<Object>> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                StandardResponse.error("Conflict occurred", ex.getMessage(), request.getRequestURI())
        );
    }

    // 401 Unauthorized → Authentication issues
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardResponse<Object>> handleAuth(
            AuthenticationException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                StandardResponse.error("Authentication failed", ex.getMessage(), request.getRequestURI())
        );
    }

    // 403 Forbidden → Access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse<Object>> handleForbidden(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                StandardResponse.error("Access denied", ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<StandardResponse<Object>> handleFileStorage(FileStorageException ex, HttpServletRequest request) {
        log.error("File storage error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponse.error(ex.getMessage(), null, request.getRequestURI()));
    }


    // 500 Internal Server Error → Fallback for unexpected issues
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Internal Server Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                StandardResponse.error("Unexpected error occurred", ex.getMessage(), request.getRequestURI())
        );
    }
}