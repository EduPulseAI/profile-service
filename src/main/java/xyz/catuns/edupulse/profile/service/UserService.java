package xyz.catuns.edupulse.profile.service;

import xyz.catuns.edupulse.profile.domain.dto.CreateUserRequest;
import xyz.catuns.edupulse.profile.domain.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request, String role);
}
