package xyz.catuns.edupulse.profile.domain.dto.profile;

import jakarta.validation.constraints.NotBlank;

public record EducationRequest(
        @NotBlank(message = "Degree is required")
        String degree,

        @NotBlank(message = "Institution is required")
        String institution,

        @NotBlank(message = "Year is required")
        String year,

        String logo
) {
}
