package xyz.catuns.edupulse.profile.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.spring.jwt.auth.AuthTokenProvider;
import xyz.catuns.spring.jwt.auth.provider.UsernamePwdAuthenticationProvider;
import xyz.catuns.spring.jwt.auth.service.UserEntityService;
import xyz.catuns.spring.jwt.autoconfigure.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;

import java.util.Set;

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
    AuthTokenProvider getAuthTokenProvider() throws MissingSecretException {
        AuthTokenProvider provider = new AuthTokenProvider(
                jwtProperties.getSecret(),
                jwtProperties.getIssuer(),
                jwtProperties.getExpiration()
        ) {
            @Override
            public Authentication validate(String token) throws TokenValidationException {
                Claims claims = this.getClaims(token);
                String username = String.valueOf(claims.getSubject());
                String authorities = String.valueOf(claims.get("authorities"));
                if (username == null || username.isEmpty()) {
                    throw new TokenValidationException(null);
                }
                return new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
            }
        };

        provider.setCustomizer((jwt, auth) -> {
            Set<String> authoritiesList = AuthorityUtils.authorityListToSet(auth.getAuthorities());
            jwt.issuer(jwtProperties.getIssuer())
                    .subject(auth.getName())
                    .claim("user", auth.getName())
                    .claim("authorities", String.join(",", authoritiesList));
        });

        return provider;
    }

//    @Bean
//    ServiceTokenProvider defaultServiceTokenProvider() throws MissingSecretException {
//        return new ServiceTokenProvider(
//                jwtProperties.getSecret(),
//                jwtProperties.getExpiration()
//        );
//    }

    @Bean
    UserEntityService<User> getUserEntityService(UserRepository repository) {
        return new UserEntityService<>(repository);
    }

    @Bean
    public AuthenticationProvider getUsernamePasswordAuthenticationProvider(
            UserEntityService<?> userDetailsService,
            PasswordEncoder passwordEncoder) {
        log.debug("Registering UsernamePwdAuthenticationProvider locally");
        return new UsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder);
    }
}
