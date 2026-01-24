package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record CredentialsDto(
        List<CertificationDto> certifications,
        List<EducationDto> education,
        List<String> skills
) {
}
