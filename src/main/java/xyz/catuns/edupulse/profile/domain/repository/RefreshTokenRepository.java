package xyz.catuns.edupulse.profile.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.catuns.edupulse.profile.domain.entity.RefreshToken;
import xyz.catuns.spring.jwt.domain.repository.TokenEntityRepository;

import java.util.List;

public interface RefreshTokenRepository extends TokenEntityRepository<RefreshToken> {

    @Query("SELECT r FROM RefreshToken r " +
            "WHERE r.identifier = :identifier " +
            " AND r.expires >= CURRENT_TIMESTAMP ")
    List<RefreshToken> findAllByIdentifierAndNotExpired(@Param("identifier") String identifier);

    void deleteByToken(String token);

    boolean existsByToken(String token);
}
