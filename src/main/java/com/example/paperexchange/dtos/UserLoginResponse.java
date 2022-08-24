package com.example.paperexchange.dtos;

import java.time.Instant;

public record UserLoginResponse(UserDto user, String token, Instant expiry) {
}
