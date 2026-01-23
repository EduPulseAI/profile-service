package xyz.catuns.edupulse.profile.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Email String email,
        @NotNull String password
) {
}
