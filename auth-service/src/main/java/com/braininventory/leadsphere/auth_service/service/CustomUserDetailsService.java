package com.braininventory.leadsphere.auth_service.service;

import com.braininventory.leadsphere.user_service.vo.LoginVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    public CustomUserDetailsService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LoginVO user = userServiceClient.getUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword()) // BCrypt hash from user-service
                .roles("USER") // extend LoginVO to include roles if needed
                .build();
    }
}






/*

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private  UserDetailsRepository userDetailsRepository;


    /*
    public CustomUserDetailsService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    */

/*

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("UserName does not exist"));
    }

*/


/*
    private final RestTemplate restTemplate;

    @Value("${services.sales-user.base-url}")
    private String salesUserBaseUrl;

    @Autowired
    public CustomUserDetailsService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String url = String.format("%s/sales-user/getBy/%s", salesUserBaseUrl, email);

        // Call the endpoint and map directly to LoginVO
        LoginVO loginVO = restTemplate.getForObject(url, LoginVO.class);

        if (loginVO == null || loginVO.getEmail() == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Map LoginVO to Spring Security UserDetails
        return User.builder()
                .username(loginVO.getEmail())
                .password(loginVO.getPassword()) // must already be hashed in DB!
                .build();
    }


 */





