package com.braininventory.leadsphere.auth_service.service;

import com.braininventory.leadsphere.user_service.vo.LoginVO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate;

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginVO getUserByEmail(String email) {
        String url = "http://localhost:8081/sales-user/getBy/{email}"; // user-service endpoint
        return restTemplate.getForObject(url, LoginVO.class, email);
    }
}