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
    @Autowired private AuthenticationManager authManager;
    @Autowired
    private JwtTokenService jwtService;

    @Autowired
    private  UserClient userClient;

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponse>> login(@RequestBody AuthRequest req) {

        // 1. Check if User exists (Manual check for specific "Email" error)
        LoginVO userVO;
        try {
            userVO = userClient.findByEmail(req.getEmail());
        } catch (FeignException.NotFound e) {
            // If User Service returns 404
            throw new AuthException("Email does not exist", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new AuthException("User service is currently unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Handle cases where service returns null instead of 404
        if (userVO == null) {
            throw new AuthException("Email does not exist", HttpStatus.NOT_FOUND);
        }

        // 2. If User exists, validate Password via AuthenticationManager
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // 3. Generate Token & Prepare Response
            String token = jwtService.generateToken(auth);
            AuthUserDetailsService.AuthUserPrincipal principal = (AuthUserDetailsService.AuthUserPrincipal) auth.getPrincipal();

            // Safe extraction of role
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");

            LoginResponse data = new LoginResponse(token, new LoginResponse.UserDto(principal.getId(), auth.getName(), role));

            return ResponseEntity.ok(StandardResponse.ok(data, "Login successful"));

        } catch (BadCredentialsException e) {
            // Specifically catch password failure
            throw new BadCredentialsException("Password is wrong");
        } catch (Exception e) {
            throw new AuthException(" Password is wrong ", HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping("/forgot-password")
    public ResponseEntity<StandardResponse<String>> forgotPassword(@RequestBody ForgetPasswordVO vo) {
        // If this method fails, GlobalExceptionHandler catches it and returns the error JSON
        authService.forgotPassword(vo.getEmail());

        // If it succeeds, return the StandardResponse.ok format
        return ResponseEntity.ok(
                StandardResponse.ok(null, "Password reset link has been sent to your registered email.")
        );
    }

    @PutMapping("/reset-password")
    public ResponseEntity<StandardResponse<String>> resetPassword(@RequestBody ResetPasswordVO vo) {
        authService.resetPassword(vo.getToken(), vo.getNewPassword());
        return ResponseEntity.ok(StandardResponse.ok(null, "Password updated successfully."));
    }

    @GetMapping("/health-check")
    public String healthCheck()
    {
        return "Auth services working fine";
    }



}