package com.braininventory.leadsphere.JWT_Auth_Service.controller;




import com.braininventory.leadsphere.JWT_Auth_Service.VO.ForgetPasswordVO;
import com.braininventory.leadsphere.JWT_Auth_Service.VO.ResetPasswordVO;
import com.braininventory.leadsphere.JWT_Auth_Service.dto.LoginResponse;
import com.braininventory.leadsphere.JWT_Auth_Service.dto.StandardResponse;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.AuthRequest;
import com.braininventory.leadsphere.JWT_Auth_Service.entity.LoginVO;
import com.braininventory.leadsphere.JWT_Auth_Service.exception.AuthException;
import com.braininventory.leadsphere.JWT_Auth_Service.exception.BadCredentialsException;
import com.braininventory.leadsphere.JWT_Auth_Service.feign.UserClient;
import com.braininventory.leadsphere.JWT_Auth_Service.jwt.JwtTokenService;
import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthService;
import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthUserDetailsService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;





@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponse>> login(@RequestBody AuthRequest req) {
        LoginResponse loginResponse = authService.login(req);
        return ResponseEntity.ok(StandardResponse.ok(loginResponse, "Login successful"));
    }




    @PutMapping("/forgot-password")
    public ResponseEntity<StandardResponse<String>> forgotPassword(@RequestBody ForgetPasswordVO vo) {

        authService.forgotPassword(vo.getEmail());
        return ResponseEntity.ok(
                StandardResponse.ok(null, "Password reset link has been sent to your registered email.")
        );
    }


    @GetMapping("/health-check")
    public String healthCheck()
    {
        return "Auth services working fine";
    }



}