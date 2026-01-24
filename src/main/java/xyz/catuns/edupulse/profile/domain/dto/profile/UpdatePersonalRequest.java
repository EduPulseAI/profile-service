package xyz.catuns.edupulse.profile.domain.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePersonalRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 3, message = "First name must contain at least 3 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, message = "Last name must contain at least 3 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Title is required")
        @Size(min = 3, message = "Title must contain at least 3 characters")
        String title,

        @NotBlank(message = "Location is required")
        @Size(min = 3, message = "Location must contain at least 3 characters")
        String location,

        String phone,

        String workingHours,

        boolean availableForWork
) {
}
