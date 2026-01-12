package com.braininventory.leadsphere.user_service.controller;

import com.braininventory.leadsphere.user_service.dto.*;
import com.braininventory.leadsphere.user_service.service.UserService;
import com.braininventory.leadsphere.user_service.vo.LoginVO;
import com.braininventory.leadsphere.user_service.vo.UserSummaryVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/sales-user")
public class UserController {

    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('user:create', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> createSalesUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(201).body(userService.createSalesUser(userRequestDto));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    public ResponseEntity<LoginVO> getUserByEmail(@PathVariable String email) {
        LoginVO loginVO = userService.findByEmail(email);

        if (loginVO == null) {
            // This triggers the FeignException.NotFound in your Auth Service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(loginVO);
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<StandardResponse<UserResponse>> deleteSalesUser(@PathVariable Long id) {

        UserResponse deletedUser = userService.deleteSalesUser(id);

        return ResponseEntity.ok(
                StandardResponse.ok(deletedUser, "User with ID " + id + " has been deleted successfully")
        );
    }


    @GetMapping("/active")
    public ResponseEntity<StandardResponse<List<UserSummaryVo>>> getActiveSalesUserSummaries() {
        List<UserSummaryVo> summaries = userService.getActiveSalesUserSummaries();

        return ResponseEntity.ok(
                StandardResponse.ok(summaries, "Active sales user summaries retrieved successfully")
        );
    }

    // upload profile pic
    @PostMapping("/profile/pic/{id}")
    public ResponseEntity<StandardResponse<Void>> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        userService.updateAvatar(id, file);

        return ResponseEntity.ok(
                StandardResponse.ok(null, "Profile picture updated successfully")
        );
    }

    @GetMapping("/profile/pic/{id}")
    public ResponseEntity<StandardResponse<String>> getProfilePicById(@PathVariable Long id,
                                                                      HttpServletRequest request) {
        log.info("Fetching avatar for userId={}", id);

        String avatarUrl = userService.getAvatarById(id);

        StandardResponse<String> response = StandardResponse.ok(
                avatarUrl,
                "Profile picture fetched successfully"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }






}