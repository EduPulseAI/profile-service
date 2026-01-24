package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.catuns.spring.jwt.domain.entity.RoleEntity;
import xyz.catuns.spring.jwt.domain.entity.UserEntity;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends UserEntity {

    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Override
    public Collection<? extends RoleEntity> getRoles() {
        return Collections.singletonList(role);
    }
}
