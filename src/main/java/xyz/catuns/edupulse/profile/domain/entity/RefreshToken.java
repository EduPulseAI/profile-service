package xyz.catuns.edupulse.profile.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import xyz.catuns.spring.jwt.domain.entity.TokenEntity;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends TokenEntity {
}
