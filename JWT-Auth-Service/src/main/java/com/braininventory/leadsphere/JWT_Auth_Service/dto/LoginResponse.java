package com.braininventory.leadsphere.JWT_Auth_Service.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private UserDto user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private Long id;
        private String email;
        private String role;
       // private List<String> permissions; // Added permissions list
    }
}