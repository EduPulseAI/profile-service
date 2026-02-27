package xyz.catuns.edupulse.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.catuns.edupulse.profile.domain.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUsername(String username);
}
