package xyz.catuns.edupulse.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.catuns.edupulse.profile.domain.entity.Role;
import xyz.catuns.edupulse.profile.domain.entity.RoleType;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByType(RoleType type);
}
