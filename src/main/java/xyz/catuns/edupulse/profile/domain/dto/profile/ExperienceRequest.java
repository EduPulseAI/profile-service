package xyz.catuns.edupulse.profile.domain.dto.profile;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ExperienceRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Company is required")
        String company,

        @NotBlank(message = "Period is required")
        String period,

        String description,

        List<String> achievements,

        List<String> technologies
) {
}
