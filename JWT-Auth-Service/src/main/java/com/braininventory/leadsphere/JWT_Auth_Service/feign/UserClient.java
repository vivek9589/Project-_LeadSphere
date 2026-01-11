package com.braininventory.leadsphere.JWT_Auth_Service.feign;


import com.braininventory.leadsphere.JWT_Auth_Service.dto.UpdatePasswordRequest;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.LoginVO;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Adjust 'name' and 'url' to your environment or use service discovery if available
//@FeignClient(name = "user-service", url = "http://localhost:8081/sales-user")
//@FeignClient(name = "user-service")
@FeignClient(
        name = "user-service",
        contextId = "userServiceClient", // Add this
        url = "http://localhost:8081/sales-user"
)
public interface UserClient {
    @GetMapping("/getBy/{email}")
    LoginVO findByEmail(@PathVariable("email") String email);

    @PutMapping("/{id}/password")
    void updatePassword(@PathVariable("id") Long userId,
                        @RequestBody UpdatePasswordRequest request);
}
