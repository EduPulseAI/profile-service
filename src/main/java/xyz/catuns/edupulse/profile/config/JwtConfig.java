package xyz.catuns.edupulse.profile.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import xyz.catuns.edupulse.profile.domain.entity.RefreshToken;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.repository.RefreshTokenRepository;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.spring.jwt.auth.AuthTokenProvider;
import xyz.catuns.spring.jwt.auth.provider.UsernamePwdAuthenticationProvider;
import xyz.catuns.spring.jwt.auth.service.UserEntityService;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.provider.SimpleTokenProvider;
import xyz.catuns.spring.jwt.core.provider.TokenGenerator;
import xyz.catuns.spring.jwt.core.provider.TokenProvider;
import xyz.catuns.spring.jwt.core.provider.TokenValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;


    @Bean
    TokenProvider<RefreshToken> refreshTokenProvider(
            TokenGenerator<RefreshToken> tokenGenerator,
            TokenValidator<RefreshToken> tokenValidator
    ) {
        return new SimpleTokenProvider<>(
                jwtProperties.getSecret(),
                Duration.ofDays(10),
                jwtProperties.getIssuer(),
                tokenGenerator,
                tokenValidator
        );
    }

    @Bean
    @Primary
    TokenProvider<Authentication> authTokenProvider() {
        return new AuthTokenProvider(jwtProperties);

    }


    @Bean
    UserEntityService<User> userEntityService(UserRepository repository) {
        return new UserEntityService<>(repository);
    }

    @Bean
    public AuthenticationProvider usernamePasswordAuthenticationProvider(
            UserEntityService<?> userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        return new UsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder);
    }
}
