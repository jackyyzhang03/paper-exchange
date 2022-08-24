package com.example.paperexchange.controllers;

import com.example.paperexchange.dtos.UserDto;
import com.example.paperexchange.entities.User;
import com.example.paperexchange.services.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDto getUser(Authentication authentication) {
        User user = userService.findUser(authentication.getName());
        return new UserDto(user.getEmail(), user.isVerified());
    }
}
