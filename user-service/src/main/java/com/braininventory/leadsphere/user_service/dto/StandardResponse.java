package com.braininventory.leadsphere.user_service.dto;




import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Best Practice: Don't send null fields to the client
public class StandardResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Object error;
    private LocalDateTime timestamp;
    private String path; // Helpful for debugging: tells the client which URL caused the error

    // Success static factory
    public static <T> StandardResponse<T> ok(T data, String message) {
        return StandardResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error static factory
    public static <T> StandardResponse<T> error(String message, Object errors, String path) {
        return StandardResponse.<T>builder()
                .success(false)
                .message(message)
                .error(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}