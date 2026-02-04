package com.braininventory.leadsphere.JWT_Auth_Service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    @NotBlank
    private String password;
    // getters and setters
}
