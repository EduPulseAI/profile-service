package xyz.catuns.edupulse.profile.security;

import io.jsonwebtoken.JwtBuilder;
import org.springframework.stereotype.Component;
import xyz.catuns.edupulse.profile.domain.entity.RefreshToken;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.provider.TokenGenerator;

@Component
public class RefreshTokenGenerator implements TokenGenerator<RefreshToken> {
    @Override
    public void accept(JwtBuilder jwtBuilder, RefreshToken refreshToken) {
        String identifier = refreshToken.getIdentifier();
        if (identifier == null || identifier.isBlank()) {
            throw new TokenValidationException("refresh token identifier is empty");
        }
        jwtBuilder.claim("identifier", identifier);
    }
}
