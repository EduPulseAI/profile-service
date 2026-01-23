package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.catuns.edupulse.profile.domain.dto.CreateUserRequest;
import xyz.catuns.edupulse.profile.domain.dto.UserResponse;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.mapper.UserMapper;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.edupulse.profile.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request, String role) {
        User newUser = mapper.toEntity(request, role);
        String pwd = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(pwd);
        newUser = repository.save(newUser);
        return mapper.toResponse(newUser);
    }
}
