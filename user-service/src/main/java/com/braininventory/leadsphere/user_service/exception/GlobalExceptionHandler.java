package com.braininventory.leadsphere.user_service.exception;

import com.braininventory.leadsphere.user_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
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

    // Handles unexpected system crashes
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(500)
                .body(new ApiResponse<>(500, "Internal Server Error: " + ex.getMessage()));
    }

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
}