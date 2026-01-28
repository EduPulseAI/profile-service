package xyz.catuns.edupulse.profile.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.spring.jwt.auth.AuthTokenProvider;
import xyz.catuns.spring.jwt.auth.provider.UsernamePwdAuthenticationProvider;
import xyz.catuns.spring.jwt.auth.service.UserEntityService;
import xyz.catuns.spring.jwt.autoconfigure.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.provider.TokenProvider;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;


//    @Bean
//    RefreshTokenProvider getRefreshTokenProvider(RefreshTokenRepository repository) throws MissingSecretException {
//        RefreshTokenProvider provider = new RefreshTokenProvider(
//                jwtProperties.getSecret(),
//                Duration.ofDays(10),
//                repository
//        );
//
//        provider.setCustomizer((jwt, token) -> {
//            jwt.subject(token.getIdentifier());
//            jwt.issuer(jwtProperties.getIssuer());
//        });
//
//        return provider;
//    }

    @Bean
    @Primary
    TokenProvider<Authentication> authTokenProvider()  {
        return new AuthTokenProvider(
                jwtProperties.getSecret(),
                jwtProperties.getIssuer(),
                jwtProperties.getExpiration()
        );

    }

//    @Bean
//    ServiceTokenProvider defaultServiceTokenProvider() throws MissingSecretException {
//        return new ServiceTokenProvider(
//                jwtProperties.getSecret(),
//                jwtProperties.getExpiration()
//        );
//    }

    @Bean
    UserEntityService<User> userEntityService(UserRepository repository) {
        return new UserEntityService<>(repository);
    }

    @Bean
    public AuthenticationProvider usernamePasswordAuthenticationProvider(
            UserEntityService<?> userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        log.debug("Registering UsernamePwdAuthenticationProvider locally");
        return new UsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder);
    }
}
