package com.braininventory.leadsphere.user_service.service.impl;

import com.braininventory.leadsphere.user_service.dto.*;
import com.braininventory.leadsphere.user_service.entity.Company;
import com.braininventory.leadsphere.user_service.entity.SocialLinks;
import com.braininventory.leadsphere.user_service.entity.User;
import com.braininventory.leadsphere.user_service.enums.Permission;
import com.braininventory.leadsphere.user_service.enums.Role;
import com.braininventory.leadsphere.user_service.exception.FileStorageException;
import com.braininventory.leadsphere.user_service.exception.ResourceNotFoundException;
import com.braininventory.leadsphere.user_service.feign.LeadServiceClient;
import com.braininventory.leadsphere.user_service.repository.UserRepository;
import com.braininventory.leadsphere.user_service.service.MailService;
import com.braininventory.leadsphere.user_service.service.UserService;
import com.braininventory.leadsphere.user_service.vo.LoginVO;
import com.braininventory.leadsphere.user_service.entity.Address;

import com.braininventory.leadsphere.user_service.vo.UserSummaryVo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Cloudinary cloudinary;

    // Inject the Feign Client
    @Autowired
    private LeadServiceClient leadServiceClient;


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
    @Transactional
    public UserResponse deleteSalesUser(Long id) {
        log.info("Attempting to delete user with ID: {}", id);

        // 1. Find user or throw our custom exception
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales User not found with id: " + id));

        // 2. Map to DTO before deleting from DB
        UserResponse response = modelMapper.map(user, UserResponse.class);

        // 3. Perform deletion
        userRepository.delete(user);

        log.info("Successfully deleted user with ID: {}", id);
        return response;
    }

    @Override
    public List<UserResponseDto> getAllSalesUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.SALES_USER)
                .map(user -> {
                    UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);

                    // Fetch target for each user
                    try {
                        StandardResponse<SalesTargetDTO> targetRes = leadServiceClient.getMonthlyTarget(user.getId());
                        if (targetRes != null && targetRes.isSuccess()) {
                            dto.setSalesTarget(targetRes.getData());
                        }
                    } catch (Exception e) {
                        log.warn("Target fetch failed for user ID: {}", user.getId());
                    }
                    return dto;
                })
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
        List<User> byIsActive = userRepository.findByIsActiveAndRole(true,Role.SALES_USER);

        return byIsActive.stream()
                .map(this ::convertToResponseDto)
                .collect(Collectors.toList());



    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserDetails(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserResponse response = modelMapper.map(user, UserResponse.class);
        attachTargetData(response); // Fetch target via Feign

        return response;
    }


    @Override
    @Transactional
    public UserResponse editUser(Long id, UserUpdateRequest updateRequest) {
        log.info("Updating user and potentially target for ID: {}", id);

        // 1. Update User Entity (Local DB)
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        modelMapper.map(updateRequest, existingUser);

        updateAddress(existingUser, updateRequest.getAddress());
        updateCompany(existingUser, updateRequest.getCompany());
        updateSocialLinks(existingUser, updateRequest.getSocial());

        userRepository.save(existingUser);

        // 2. Set Target via Feign (Lead Service)
        if (existingUser.getRole() == Role.SALES_USER && updateRequest.getSalesTarget() != null) {
            SalesTargetDTO targetDto = updateRequest.getSalesTarget();

            // --- NEW LOGIC: Default to Current Month/Year if null ---
            LocalDate now = LocalDate.now();
            if (targetDto.getTargetMonth() == null) {
                targetDto.setTargetMonth(now.getMonthValue());
            }
            if (targetDto.getTargetYear() == null) {
                targetDto.setTargetYear(now.getYear());
            }
            // -------------------------------------------------------

            try {
                leadServiceClient.setTarget(id, targetDto);
            } catch (Exception e) {
                log.error("Feign Error: Could not set target in Lead Service", e);
            }
        }

        // 3. Prepare Response and "Enrich" with latest target
        UserResponse response = modelMapper.map(existingUser, UserResponse.class);
        attachTargetData(response);

        return response;
    }
    // Helper method to keep logic simple
    private void attachTargetData(UserResponse response) {
        if (response.getRole() == Role.SALES_USER) {
            try {
                StandardResponse<SalesTargetDTO> targetRes = leadServiceClient.getMonthlyTarget(response.getId());
                if (targetRes != null && targetRes.isSuccess()) {
                    response.setSalesTarget(targetRes.getData());
                }
            } catch (Exception e) {
                log.warn("Lead Service target fetch failed for user {}", response.getId());
            }
        }
    }


    // --- HELPER METHODS ---

    private void updateAddress(User user, AddressDto dto) {
        if (dto == null) return;

        if (user.getAddress() == null) {
            user.setAddress(new Address());
        }

        Address address = user.getAddress();
        if (dto.getStreet() != null) address.setStreet(dto.getStreet());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getState() != null) address.setState(dto.getState());
        if (dto.getZipCode() != null) address.setZipCode(dto.getZipCode());
        if (dto.getCountry() != null) address.setCountry(dto.getCountry());
    }

    private void updateCompany(User user, CompanyDto dto) {
        if (dto == null) return;

        if (user.getCompany() == null) {
            user.setCompany(new Company());
        }

        Company company = user.getCompany();

        // 1. Check DTO for 'getName' (based on your JSON "name": "BI")
        // 2. Call Entity 'setName'
        if (dto.getName() != null) {
            company.setName(dto.getName());
        }

        if (dto.getPosition() != null) {
            company.setPosition(dto.getPosition());
        }

        if (dto.getJoinedDate() != null) {
            company.setJoinedDate(dto.getJoinedDate());
        }
    }
    private void updateSocialLinks(User user, SocialLinksDto dto) {
        if (dto == null) return;

        if (user.getSocial() == null) {
            user.setSocial(new SocialLinks());
        }

        SocialLinks social = user.getSocial();
        if (dto.getLinkedin() != null) social.setLinkedin(dto.getLinkedin());
        if (dto.getTwitter() != null) social.setTwitter(dto.getTwitter());
        if (dto.getWebsite() != null) social.setWebsite(dto.getWebsite());
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        log.info("Calculating total active users");
        return userRepository.countByIsActiveTrueAndRole(Role.SALES_USER);
    }


    @Override
    public List<UserSummaryVo> getActiveSalesUserSummaries() {
        return userRepository.findActiveSalesUserSummaries();
    }

    @Override
    public UserResponse updateAvatar(Long id, MultipartFile file) {
        log.info("Uploading avatar for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "avatars"));

            String url = (String) uploadResult.get("secure_url");
            user.setAvatar(url);
            userRepository.save(user);

            log.info("Successfully uploaded avatar for user ID: {}", id);

            return UserResponse.builder()
                    .id(user.getId())
                    .avatar(user.getAvatar())
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload avatar for user ID: {}", id, e);
            throw new FileStorageException("Could not upload file to Cloudinary", e);
        }
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