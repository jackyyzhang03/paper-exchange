package com.example.paperexchange.controllers;

import com.example.paperexchange.dtos.RegistrationRequest;
import com.example.paperexchange.dtos.UserDto;
import com.example.paperexchange.dtos.UserLoginResponse;
import com.example.paperexchange.dtos.VerificationRequest;
import com.example.paperexchange.entities.User;
import com.example.paperexchange.services.email.EmailVerificationService;
import com.example.paperexchange.services.security.JwtTokenService;
import com.example.paperexchange.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final JwtTokenService tokenService;

    @Autowired
    public AuthController(UserService userService, EmailVerificationService emailVerificationService, JwtTokenService tokenService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }


    @PostMapping("/login")
    public UserLoginResponse login(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(36000);
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        User user = userService.findUser(authentication.getName());
        UserDto userDto = new UserDto(user.getEmail(), user.isVerified());
        String token = tokenService.createToken(authentication.getName(), authorities, now, expiry);
        return new UserLoginResponse(userDto, token, expiry);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegistrationRequest registrationRequest) {
        userService.createUser(registrationRequest);
    }

    @GetMapping("/verification-email")
    public void sendEmail(Authentication authentication) {
        emailVerificationService.sendVerificationEmail(authentication.getName());
    }

    @PostMapping("/verify")
    public void verifyEmail(@RequestBody VerificationRequest verificationRequestDto, Authentication authentication) {
        emailVerificationService.verify(verificationRequestDto.verificationToken(), authentication.getName());
    }
}
