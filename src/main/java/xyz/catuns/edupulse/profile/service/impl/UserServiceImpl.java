package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.catuns.edupulse.profile.domain.dto.CreateUserRequest;
import xyz.catuns.edupulse.profile.domain.dto.UserResponse;
import xyz.catuns.edupulse.profile.domain.entity.Role;
import xyz.catuns.edupulse.profile.domain.entity.RoleType;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.mapper.UserMapper;
import xyz.catuns.edupulse.profile.domain.repository.RoleRepository;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.edupulse.profile.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request, String roleName) {
        RoleType roleType = RoleType.valueOf(roleName.toUpperCase());
        Role role = roleRepository.findByType(roleType)
                .orElseGet(() -> roleRepository.save(new Role(roleType)));

        User newUser = mapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(role);

        newUser = userRepository.save(newUser);
        return mapper.toResponse(newUser);
    }
}
