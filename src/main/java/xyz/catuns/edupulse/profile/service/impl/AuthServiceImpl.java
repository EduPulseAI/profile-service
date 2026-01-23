package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import xyz.catuns.edupulse.profile.domain.dto.LoginRequest;
import xyz.catuns.edupulse.profile.domain.dto.LoginResponse;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.mapper.UserMapper;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.edupulse.profile.service.AuthService;
import xyz.catuns.spring.base.exception.controller.BadRequestException;
import xyz.catuns.spring.jwt.auth.AuthTokenProvider;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.security.properties.JwtSecurityProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    /* mappers */
    private final UserMapper userMapper;

    /* providers */
    private final AuthenticationProvider provider;
    private final AuthTokenProvider authTokenProvider;

    private final UserRepository userRepository;


    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = this.authenticate(request);
        JwtToken token = authTokenProvider.generate(auth);
        User user = (User) auth.getPrincipal();
        return new LoginResponse(
                token,
                userMapper.toResponse(user)
        );
    }

    private Authentication authenticate(LoginRequest request) {
        return provider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                )
        );
    }
}
