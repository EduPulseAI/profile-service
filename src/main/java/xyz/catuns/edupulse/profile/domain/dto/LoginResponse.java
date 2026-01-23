package xyz.catuns.edupulse.profile.domain.dto;

import xyz.catuns.spring.jwt.core.model.JwtToken;

public record LoginResponse(
        JwtToken token,
        UserResponse user
) {
}
