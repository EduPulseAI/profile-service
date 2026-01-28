package xyz.catuns.edupulse.profile.domain.repository;

import xyz.catuns.edupulse.profile.domain.entity.RefreshToken;
import xyz.catuns.spring.jwt.domain.repository.TokenEntityRepository;

public interface RefreshTokenRepository extends TokenEntityRepository<RefreshToken> {
}
