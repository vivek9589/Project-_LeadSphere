package com.braininventory.leadsphere.JWT_Auth_Service.service;

import com.braininventory.leadsphere.JWT_Auth_Service.dto.LoginResponse;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.AuthRequest;

public interface AuthService {


    LoginResponse login(AuthRequest req);

    void forgotPassword(String email);


}
