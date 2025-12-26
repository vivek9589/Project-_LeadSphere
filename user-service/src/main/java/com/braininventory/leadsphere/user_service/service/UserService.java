package com.braininventory.leadsphere.user_service.service;


import com.braininventory.leadsphere.user_service.dto.*;
import com.braininventory.leadsphere.user_service.vo.LoginVO;

import java.util.List;


public interface UserService {

    // Admin operations on Sales Users
    UserResponseDto createSalesUser(UserRequestDto userRequestDto);
    UserResponseDto updateSalesUser(Long id, UserRequestDto userRequestDto);
    UserResponse deleteSalesUser(Long id);
    List<UserResponseDto> getAllSalesUsers();
    UserResponseDto getSalesUserById(Long id);
    LoginVO findByEmail(String email);

    public void updatePassword(Long userId, String newPassword);

    List<UserResponseDto> getAllActiveSalesUser();


    /// follow standard
    UserResponse getUserDetails(Long id);
    UserResponse editUser(Long id, UserUpdateRequest updateRequest);
    long countActiveUsers();

}

