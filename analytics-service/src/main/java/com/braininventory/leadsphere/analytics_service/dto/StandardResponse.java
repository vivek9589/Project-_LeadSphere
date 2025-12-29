package com.braininventory.leadsphere.analytics_service.dto;


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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponse<T> {


    private boolean success;
    private String message;
    private T data;
    private Object error;
    private LocalDateTime timestamp;
    private String path;


    public static <T> StandardResponse<T> ok(T data , String message)
    {
        return StandardResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static <T> StandardResponse<T> error(String message,Object error , String path)
    {
        return StandardResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

}
