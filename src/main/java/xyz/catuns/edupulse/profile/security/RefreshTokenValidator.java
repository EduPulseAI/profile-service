package xyz.catuns.edupulse.profile.security;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.catuns.edupulse.profile.domain.entity.RefreshToken;
import xyz.catuns.edupulse.profile.domain.repository.RefreshTokenRepository;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.provider.TokenValidator;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Transactional()
public class RefreshTokenValidator implements TokenValidator<RefreshToken> {

    private final RefreshTokenRepository repository;

    @Override
    public RefreshToken apply(Claims claims) {
        if (claims.getExpiration().before(new Date())) {
            throw new TokenValidationException("token has expired claims");
        }
        String identifier = (String) claims.get("identifier");
        return repository.findAllByIdentifierAndNotExpired(identifier)
                .stream().findFirst()
                .orElseThrow(() -> new TokenValidationException("No refresh token found"));
    }
}
