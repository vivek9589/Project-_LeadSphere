package com.braininventory.leadsphere.JWT_Auth_Service.feign;


import com.braininventory.leadsphere.JWT_Auth_Service.dto.UpdatePasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "user-service", url = "http://localhost:8081/sales-user")
@FeignClient(
        name = "user-service",
        contextId = "notificationServiceClient" // Add this unique ID
)
public interface NotificationClient {
    @PostMapping("/notifications/forgot-password")
    void sendForgotPasswordEmail(@RequestParam("email") String email,
                                 @RequestParam("token") String token);
}
