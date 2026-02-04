package com.braininventory.leadsphere.JWT_Auth_Service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    private String newPassword;
}

