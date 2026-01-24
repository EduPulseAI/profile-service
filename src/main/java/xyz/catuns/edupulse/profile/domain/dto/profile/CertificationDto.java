package xyz.catuns.edupulse.profile.domain.dto.profile;

public record CertificationDto(
        String name,
        String issuer,
        String date,
        String logo
) {
}
