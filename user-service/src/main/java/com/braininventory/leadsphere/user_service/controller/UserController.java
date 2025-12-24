package com.braininventory.leadsphere.user_service.controller;

import com.braininventory.leadsphere.user_service.dto.*;
import com.braininventory.leadsphere.user_service.service.UserService;
import com.braininventory.leadsphere.user_service.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController // Ensure this is present
@RequestMapping("/sales-user")
public class UserController {

    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    // Only users with 'user:create' OR the 'ROLE_ADMIN' can create
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('user:create', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> createSalesUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(201).body(userService.createSalesUser(userRequestDto));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('user:update', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> updateSalesUser(@PathVariable Long id,
                                                           @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateSalesUser(id, userRequestDto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Admin only for deletions
    public ResponseEntity<UserResponseDto> deleteSalesUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteSalesUser(id));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllSalesUsers() {
        try {
            // 1. Fetch the data from the service
            List<UserResponseDto> allSalesUsers = userService.getAllSalesUsers();

            // 2. Wrap the result in a successful ApiResponse
            ApiResponse<List<UserResponseDto>> response = new ApiResponse<>(
                    200,
                    "All sales users retrieved successfully",
                    allSalesUsers
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 3. Catch any error and throw the message into the 'message' field
            ApiResponse<List<UserResponseDto>> errorResponse = new ApiResponse<>(
                    500,
                    "Error retrieving users: " + e.getMessage(),
                    null
            );

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyAuthority('user:read', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> getSalesUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getSalesUserById(id));
    }

    @GetMapping("/getBy/{email}")
    @PreAuthorize("permitAll()")
    public LoginVO getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    // Registration is often public or restricted differently
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public String register(@RequestBody RegisterRequestDto registerRequestDto) {
        return userService.register(registerRequestDto);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> updatePassword(@PathVariable Long id,
                                                 @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request.getNewPassword());
        return ResponseEntity.ok("Password updated successfully");
    }



    @GetMapping("/isActive")
    public List<UserResponseDto> getAllActiveSalesUser()
    {
        return userService.getAllActiveSalesUser();
    }



}