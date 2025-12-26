package com.braininventory.leadsphere.user_service.exception;

import com.braininventory.leadsphere.user_service.dto.ApiResponse;
import com.braininventory.leadsphere.user_service.dto.StandardResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handles Missing Email / Empty Email
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.status(400)
                .body(new ApiResponse<>(400,  ex.getMessage()));
    }

    // Handles Duplicate Email (Business Logic)
    @ExceptionHandler(DuplicateInviteException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(DuplicateInviteException ex) {
        return ResponseEntity.status(400)
                .body(new ApiResponse<>(400,  ex.getMessage()));
    }

    /*
    // Handles unexpected system crashes
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(500)
                .body(new ApiResponse<>(500, "Internal Server Error: " + ex.getMessage()));
    }


     */


    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(HandlerMethodValidationException ex) {
        // Get the first validation error message
        String errorMessage = ex.getAllErrors().get(0).getDefaultMessage();

        return ResponseEntity.status(400)
                .body(new ApiResponse<>(400, "Validation Error: " + errorMessage));
    }


    // 1. Handle when the parameter is missing from the request entirely
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(400)
                .body(new ApiResponse<>(400, " Email required"));
    }


    // Handles resource not found (e.g., Get User by ID fails)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(StandardResponse.error(ex.getMessage(), "NOT_FOUND", request.getRequestURI()));
    }

    // Handles validation errors (e.g., Edit User has invalid email)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.error("Validation failed", errors, request.getRequestURI()));
    }

    // Generic fallback for any other server errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        log.error("Internal Server Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // <-- FIXED
                .body(StandardResponse.error(
                        "An unexpected error occurred",
                        "INTERNAL_ERROR",
                        request.getRequestURI()
                ));
    }

}