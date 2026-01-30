package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.catuns.edupulse.profile.domain.dto.LoginRequest;
import xyz.catuns.edupulse.profile.domain.dto.AuthResponse;
import xyz.catuns.edupulse.profile.domain.dto.RefreshTokenRequest;
import xyz.catuns.edupulse.profile.domain.entity.RefreshToken;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.mapper.UserMapper;
import xyz.catuns.edupulse.profile.domain.repository.RefreshTokenRepository;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.edupulse.profile.service.AuthService;
import xyz.catuns.spring.base.exception.controller.NotFoundException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.provider.TokenProvider;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    /* mappers */
    private final UserMapper userMapper;

    /* providers */
    private final AuthenticationProvider provider;
    private final TokenProvider<Authentication> authTokenProvider;
    private final TokenProvider<RefreshToken> refreshTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticate(request);
        JwtToken token = authTokenProvider.generate(auth);

        // Generate refresh token
        User user = (User) auth.getPrincipal();
        RefreshToken refreshToken = buildRefreshToken(user);

        // remove expired refresh tokens
        refreshTokenRepository.deleteAllByIdentifierAndExpiresBefore(
                user.getUsername(),
                Instant.now()
        );

        return new AuthResponse(
                token,
                userMapper.toResponse(user),
                refreshToken.getToken()
        );
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken validatedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new NotFoundException("No refresh token found"));
        if(validatedToken.isExpired()) {
            refreshTokenRepository.delete(validatedToken);
            throw new TokenValidationException("refresh token is expired");
        }

        // build user jwt token
        User user = userRepository.findByEmail(validatedToken.getIdentifier())
                .orElseThrow(() -> new BadCredentialsException("User not found by token identifier"));
        Authentication auth = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());
        JwtToken token = authTokenProvider.generate(auth);

        return new AuthResponse(
                token,
                userMapper.toResponse(user),
                validatedToken.getToken()
        );
    }

    private RefreshToken buildRefreshToken(User principal) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setIdentifier(principal.getUsername());

        JwtToken jwtToken = refreshTokenProvider.generate(refreshToken);
        refreshToken.setToken(jwtToken.value());
        refreshToken.setExpires(jwtToken.expiration());
        refreshToken.setIssuedAt(jwtToken.issuedAt());

        return refreshTokenRepository.save(refreshToken);
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
