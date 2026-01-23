package xyz.catuns.edupulse.profile.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password,
        @NotBlank String confirmPassword
) {
}
