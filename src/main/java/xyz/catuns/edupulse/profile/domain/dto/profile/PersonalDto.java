package xyz.catuns.edupulse.profile.domain.dto.profile;

public record PersonalDto(
        String firstName,
        String lastName,
        String title,
        String location,
        String email,
        String phone
) {
}
