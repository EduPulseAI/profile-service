package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import xyz.catuns.spring.jwt.domain.entity.RoleEntity;

@Getter
@Setter
@Entity
@Table(name = "user_roles")
public class Role extends RoleEntity {
}
