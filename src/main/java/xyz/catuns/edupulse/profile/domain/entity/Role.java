package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.catuns.spring.jwt.domain.entity.RoleEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends RoleEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, unique = true)
    private RoleType type;

    public Role(RoleType type) {
        this.type = type;
        this.name = type.name();
    }

    @Override
    public String getName() {
        return type != null ? type.name() : this.name;
    }
}
