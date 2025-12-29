package com.braininventory.leadsphere.lead_service.feign;



import com.braininventory.leadsphere.lead_service.config.FeignClientConfig;
import com.braininventory.leadsphere.lead_service.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


// Adjust 'name' and 'url' to your environment or use service discovery if available
//@FeignClient(name = "user-service", url = "http://localhost:8081/sales-user")
//@FeignClient(name = "user-service")

/*
@FeignClient(
        name = "user-service",
        contextId = "userServiceClient", // Add this
        url = "http://localhost:8081/sales-user"
)

 */

@FeignClient(
        name = "USER-SERVICE", // Matches the name in Eureka/Service Discovery
        contextId = "userServiceClient",
        configuration = FeignClientConfig.class // Links the JWT Interceptor
)
public interface UserClient {

    // The full path will be http://USER-SERVICE/sales-user/getById/{id}
    @GetMapping("/sales-user/getById/{id}")
    UserResponseDto getSalesUserById(@PathVariable("id") Long id);
}