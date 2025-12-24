package com.braininventory.leadsphere.user_service.dto;





import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message; // For Success
    private String error;   // For Failures
    private T data;

    // Constructor for Success (sets 'message')
    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Constructor for Error (sets 'error')
    public ApiResponse(int status, String error) {
        this.status = status;
        this.error = error;
    }
}