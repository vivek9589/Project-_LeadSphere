package com.braininventory.leadsphere.user_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    private String newPassword;
}