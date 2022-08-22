package com.example.paperexchange.auth;

import com.example.paperexchange.user.UserDto;

import java.time.Instant;

public record UserLoginDto(UserDto user, String token, Instant expiry) {
}
