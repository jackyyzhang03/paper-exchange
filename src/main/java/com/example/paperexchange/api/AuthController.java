package com.example.paperexchange.api;

import com.example.paperexchange.auth.JwtTokenService;
import com.example.paperexchange.auth.UserLoginDto;
import com.example.paperexchange.user.RegistrationRequest;
import com.example.paperexchange.user.User;
import com.example.paperexchange.user.UserDto;
import com.example.paperexchange.user.UserService;
import com.example.paperexchange.verification.VerificationRequest;
import com.example.paperexchange.verification.VerificationService;
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
    private final VerificationService verificationService;
    private final JwtTokenService tokenService;

    @Autowired
    public AuthController(UserService userService, VerificationService verificationService, JwtTokenService tokenService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.verificationService = verificationService;
    }


    @PostMapping("/login")
    public UserLoginDto login(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(36000);
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        User user = userService.findUser(authentication.getName());
        UserDto userDto = new UserDto(user.getEmail(), user.isVerified());
        String token = tokenService.createToken(authentication.getName(), authorities, now, expiry);
        return new UserLoginDto(userDto, token, expiry);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegistrationRequest registrationRequest) {
        userService.createUser(registrationRequest);
    }

    @GetMapping("/verification-email")
    public void sendEmail(Authentication authentication) {
        verificationService.sendVerificationEmail(authentication.getName());
    }

    @PostMapping("/verify")
    public void verifyEmail(@RequestBody VerificationRequest verificationRequest, Authentication authentication) {
        verificationService.verify(verificationRequest.verificationToken(), authentication.getName());
    }
}
