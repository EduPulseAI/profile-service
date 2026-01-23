package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @OneToOne(cascade = {CascadeType.ALL})
    private Profile profile;

    @OneToOne(cascade = {CascadeType.ALL})
    private Role role;

    @Override
    public Collection<? extends RoleEntity> getRoles() {
        return Collections.singletonList(role);
    }
}
