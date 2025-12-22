package com.braininventory.leadsphere.user_service.vo;


import lombok.Data;

@Data
public class ForgetPasswordVO {

    private String email;
    private String currentPassword;
    private String newPassword;
}
