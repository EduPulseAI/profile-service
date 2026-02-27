package xyz.catuns.edupulse.profile.domain.dto.profile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateAboutRequest(
//        @NotBlank(message = "Bio is required")
//        @Size(max = 2000, message = "Bio must be less than 2000 characters")
//        String bio,

        @Valid
        List<String> summary
) {
}
