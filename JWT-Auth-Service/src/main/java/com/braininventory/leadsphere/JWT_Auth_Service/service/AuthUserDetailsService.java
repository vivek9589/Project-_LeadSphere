package com.braininventory.leadsphere.JWT_Auth_Service.service;

import com.braininventory.leadsphere.JWT_Auth_Service.entity.LoginVO;
import com.braininventory.leadsphere.JWT_Auth_Service.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LoginVO user;
        try {
            // 1. Call the microservice
            user = userClient.findByEmail(email);
        } catch (feign.FeignException.NotFound e) {
            // 2. Handle specific 404 from User Service
            throw new UsernameNotFoundException("No user found with email: " + email);
        } catch (Exception e) {
            // 3. Handle service downtime (User Service is down)
            // We throw a specific error that our GlobalExceptionHandler can catch
            throw new org.springframework.security.authentication.InternalAuthenticationServiceException(
                    "User Service is temporarily unavailable. Please try again later.");
        }

        // 4. Double check the object is not null
        if (user == null) {
            throw new UsernameNotFoundException("User not found in the system");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // Handle null roles to prevent NullPointerException
        String roleName = user.getRole() != null ? user.getRole().toString() : "USER";
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        return new AuthUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    // Inner class so you don’t need a separate file
    public static class AuthUserPrincipal implements UserDetails {
        private final Long id;
        private final String email;
        private final String password;
        private final Collection<? extends GrantedAuthority> authorities;

        public AuthUserPrincipal(Long id, String email, String password,
                                 Collection<? extends GrantedAuthority> authorities) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
        }

        public Long getId() {
            return id;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() { return true; }

        @Override
        public boolean isAccountNonLocked() { return true; }

        @Override
        public boolean isCredentialsNonExpired() { return true; }

        @Override
        public boolean isEnabled() { return true; }
    }
}