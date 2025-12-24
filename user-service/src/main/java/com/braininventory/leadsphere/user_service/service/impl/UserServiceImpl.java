package com.braininventory.leadsphere.user_service.service.impl;

import com.braininventory.leadsphere.user_service.dto.RegisterRequestDto;
import com.braininventory.leadsphere.user_service.dto.UserRequestDto;
import com.braininventory.leadsphere.user_service.dto.UserResponseDto;
import com.braininventory.leadsphere.user_service.entity.User;
import com.braininventory.leadsphere.user_service.enums.Permission;
import com.braininventory.leadsphere.user_service.enums.Role;
import com.braininventory.leadsphere.user_service.repository.UserRepository;
import com.braininventory.leadsphere.user_service.service.MailService;
import com.braininventory.leadsphere.user_service.service.UserService;
import com.braininventory.leadsphere.user_service.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createSalesUser(UserRequestDto userRequestDto) {
        User userEntity = new User();
        userEntity.setFirstName(userRequestDto.getFirstName());
        userEntity.setLastName(userRequestDto.getLastName());
        userEntity.setEmail(userRequestDto.getEmail());
        userEntity.setPassword("{noop}"+userRequestDto.getPassword());
        userEntity.setRole(Role.SALES_USER); // force role as SALES_USER

        User savedUser = userRepository.save(userEntity);
        return convertToResponseDto(savedUser);
    }

    @Override
    public UserResponseDto updateSalesUser(Long id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales User not found with id: " + id));

        existingUser.setFirstName(userRequestDto.getFirstName());
        existingUser.setLastName(userRequestDto.getLastName());
        existingUser.setEmail(userRequestDto.getEmail());
        existingUser.setPassword(userRequestDto.getPassword());
        existingUser.setRole(Role.SALES_USER);

        User updatedUser = userRepository.save(existingUser);
        return convertToResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto deleteSalesUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales User not found with id: " + id));

        UserResponseDto response = convertToResponseDto(user);
        userRepository.deleteById(id);
        return response;
    }

    @Override
    public List<UserResponseDto> getAllSalesUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.SALES_USER)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getSalesUserById(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.SALES_USER)
                .orElseThrow(() -> new RuntimeException("Sales User not found with id: " + id));
        return convertToResponseDto(user);
    }

    @Override
    public LoginVO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        System.out.println("current user " + user);
        if (user == null) {
            return null; // or throw custom exception
        }

        // Extract permissions from the role

        Set<String> permissions = user.getRole().getPermissions().stream()
                .map(Permission::getPermission)
                .collect(Collectors.toSet());

        LoginVO loginVO = new LoginVO();


        loginVO.setId(user.getId());
        loginVO.setEmail(user.getEmail());
        loginVO.setPassword(user.getPassword()); // must already be BCrypt hash in DB
        loginVO.setRole(user.getRole());
        loginVO.setPermissions(permissions);

        loginVO.setEnabled(true);

        return loginVO;
    }


    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Directly set plain text password
        user.setPassword("{noop}"+newPassword);
        userRepository.save(user);
    }



    @Override
    public List<UserResponseDto> getAllActiveSalesUser() {
        List<User> byIsActive = userRepository.findByIsActive(true);

        return byIsActive.stream()
                .map(this ::convertToResponseDto)
                .collect(Collectors.toList());



    }

    public String register(RegisterRequestDto registerRequestDto) {
        User userEntity = new User();

        userEntity.setFirstName(registerRequestDto.getName());
        userEntity.setMobileNo(registerRequestDto.getMobileNo());
        userEntity.setEmail(registerRequestDto.getEmail());

        // For testing: plain text with {noop}
        userEntity.setPassword("{noop}" + registerRequestDto.getPassword());

        // Default role USER
        userEntity.setRole(Role.SALES_USER);

        // userEntity.setPlanName(registerRequestDto.getPlanName());

        User savedUser = userRepository.save(userEntity);
        return "Registration Successful";
    }



    private UserResponseDto convertToResponseDto(User user) {
        if (user == null) return null;
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}