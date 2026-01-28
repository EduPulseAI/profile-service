package xyz.catuns.edupulse.profile.domain.dto;

import xyz.catuns.spring.jwt.core.model.JwtToken;

public record AuthResponse(
        JwtToken token,
        UserResponse user,
        String refreshToken
) {
}
