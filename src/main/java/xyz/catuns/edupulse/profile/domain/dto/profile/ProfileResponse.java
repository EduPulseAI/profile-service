package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record ProfileResponse(
        PersonalDto personal,
        AboutDto about,
        List<ExperienceItemDto> experience,
        CredentialsDto credentials,
        TechnicalSkillsDto technicalSkills
) {
}
