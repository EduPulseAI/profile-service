package xyz.catuns.edupulse.profile.service;

import xyz.catuns.edupulse.profile.domain.dto.LoginRequest;
import xyz.catuns.edupulse.profile.domain.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
