package com.braininventory.leadsphere.user_service.controller;

import com.braininventory.leadsphere.user_service.dto.*;
import com.braininventory.leadsphere.user_service.service.UserService;
import com.braininventory.leadsphere.user_service.vo.LoginVO;
import jakarta.validation.Valid;
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
    @PreAuthorize("hasAnyAuthority('ROLE_SALES_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> getSalesUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getSalesUserById(id));
    }

    @GetMapping("/getBy/{email}")
    @PreAuthorize("permitAll()")
    public LoginVO getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
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


    // standard follow
    @GetMapping("/detailsBy/{id}")
    public ResponseEntity<StandardResponse<UserResponse>> getUserById(@PathVariable("id") Long userId) {
        UserResponse response = userService.getUserDetails(userId);
        return ResponseEntity.ok(StandardResponse.ok(response, "User details retrieved successfully"));
    }

    @PatchMapping("/updateBy/{id}")
    public ResponseEntity<StandardResponse<UserResponse>> updateUser(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UserUpdateRequest updateRequest) {

        UserResponse response = userService.editUser(userId, updateRequest);
        return ResponseEntity.ok(StandardResponse.ok(response, "User updated successfully"));
    }

    @GetMapping("/active/count")
    public ResponseEntity<StandardResponse<Long>> getActiveUserCount() {
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(StandardResponse.ok(count, "Active user count retrieved"));
    }

    @DeleteMapping("/deleteBy/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // High-level security best practice
    public ResponseEntity<StandardResponse<UserResponse>> deleteSalesUser(@PathVariable Long id) {

        UserResponse deletedUser = userService.deleteSalesUser(id);

        return ResponseEntity.ok(
                StandardResponse.ok(deletedUser, "User with ID " + id + " has been deleted successfully")
        );
    }




}