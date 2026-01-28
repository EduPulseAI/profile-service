package xyz.catuns.edupulse.profile.service;

import xyz.catuns.edupulse.profile.domain.dto.LoginRequest;
import xyz.catuns.edupulse.profile.domain.dto.AuthResponse;
import xyz.catuns.edupulse.profile.domain.dto.RefreshTokenRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
