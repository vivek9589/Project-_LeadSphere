package com.braininventory.leadsphere.lead_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data)
    {
        return new ApiResponse<>(200, message, data);
    }
}